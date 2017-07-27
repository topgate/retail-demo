import argparse
import apache_beam as beam
import apache_beam.options.pipeline_options as pipeline_options
import apache_beam.transforms.window as window


class MyOptions(pipeline_options.PipelineOptions):

    @classmethod
    def _add_argparse_args(cls, parser):
        parser.add_argument('--topic_receipt')
        parser.add_argument('--topic_console')
        parser.add_argument('--topic_trend')
        parser.add_argument('--topic_heatmap')


class SummaryCombineFn(beam.CombineFn):

    def create_accumulator(self):
        return {
            "storePriceSum": {},
            "storeQuantitySum": {},
            "productPriceSum": {},
            "productQuantitySum": {}
        }

    def add_input(self, accum, input):
        accum["storePriceSum"][input["storeCode"]] = accum["storePriceSum"].get(input["storeCode"], 0) + input["price"]
        accum["storeQuantitySum"][input["storeCode"]] = accum["storeQuantitySum"].get(input["storeCode"], 0) + input["quantity"]
        accum["productPriceSum"][input["productCode"]] = accum["productPriceSum"].get(input["productCode"], 0) + input["price"]
        accum["productQuantitySum"][input["productCode"]] = accum["productQuantitySum"].get(input["productCode"], 0) + input["quantity"]
        return accum

    def merge_accumulators(self, accumulators):
        from collections import Counter

        a1, a2, a3, a4 = Counter({}), Counter({}), Counter({}), Counter({})
        for accum in accumulators:
            a1 += Counter(accum["storePriceSum"])
            a2 += Counter(accum["storeQuantitySum"])
            a3 += Counter(accum["productPriceSum"])
            a4 += Counter(accum["productQuantitySum"])
        return {
            "storePriceSum": dict(a1),
            "storeQuantitySum": dict(a2),
            "productPriceSum": dict(a3),
            "productQuantitySum": dict(a4)
        }

    def extract_output(self, accum):
        return accum


class SummaryDoFn(beam.DoFn):

    def process(self, element):
        yield [{"productCode": key, "priceSum": value, "quantitySum": element["productQuantitySum"].get(key, 0)}
            for key, value in element["productPriceSum"].items()]
        yield beam.pvalue.TaggedOutput('store',
            [{"storeCode": key, "priceSum": value, "quantitySum": element["storeQuantitySum"].get(key, 0)}
                for key, value in element["storePriceSum"].items()])


def from_json(s):
    import json
    return json.loads(s)

def to_json(j):
    import json
    return json.dumps(j)

def filter_by_fix_rate(x):
    import random
    if x["storeCode"] == "001" or random.random() < 0.1:
        yield to_json(x)

def run(argv=None):
    options = MyOptions()
    with beam.Pipeline(options=options) as p:

        receipts = (p | 'read receipt json' >> beam.io.ReadStringsFromPubSub(options.topic_receipt)
                      | 'convert json to receipt' >> beam.Map(from_json))

        (receipts | 'filter receipt' >> beam.FlatMap(filter_by_fix_rate)
                  | 'write receipt'  >> beam.io.WriteStringsToPubSub(options.topic_console))

        results = (receipts | 'set window' >> beam.WindowInto(window.SlidingWindows(size=15, period=5))
                            | 'combine window' >> beam.CombineGlobally(SummaryCombineFn()).without_defaults()
                            | 'summary receipt' >> beam.ParDo(SummaryDoFn()).with_outputs('store', main='product'))

        (results.store | 'store summary to json' >> beam.Map(to_json)
                       | 'write heatmap' >> beam.io.WriteStringsToPubSub(options.topic_heatmap))
        (results.product | 'product summary to json' >> beam.Map(to_json)
                         | 'write trend' >> beam.io.WriteStringsToPubSub(options.topic_trend))

if __name__ == '__main__':
    run()

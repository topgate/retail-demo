package jp.co.topgate;

import static com.google.datastore.v1.client.DatastoreHelper.makeValue;

import com.google.cloud.dataflow.sdk.Pipeline;
import com.google.cloud.dataflow.sdk.io.TextIO;
import com.google.cloud.dataflow.sdk.io.datastore.DatastoreIO;
import com.google.cloud.dataflow.sdk.options.DataflowPipelineOptions;
import com.google.cloud.dataflow.sdk.options.Default;
import com.google.cloud.dataflow.sdk.options.DefaultValueFactory;
import com.google.cloud.dataflow.sdk.options.Description;
import com.google.cloud.dataflow.sdk.options.PipelineOptions;
import com.google.cloud.dataflow.sdk.options.PipelineOptionsFactory;
import com.google.cloud.dataflow.sdk.transforms.DoFn;
import com.google.cloud.dataflow.sdk.transforms.PTransform;
import com.google.cloud.dataflow.sdk.transforms.ParDo;
import com.google.cloud.dataflow.sdk.util.gcsfs.GcsPath;
import com.google.cloud.dataflow.sdk.values.PCollection;
import com.google.datastore.v1.Entity;
import com.google.datastore.v1.Key;
import com.google.datastore.v1.Key.Builder;
import com.google.datastore.v1.Key.PathElement;

public class StorageToDatastore {

	public interface StorageToDatastoreOptions extends DataflowPipelineOptions {
		@Description("Path of the file to read from")
		@Default.String("gs://retail-dataflow-demo/dataflow/resources/product_master.csv")
		String getInputFile();

		void setInputFile(String value);
	}

	static class CSVToEntityFn extends DoFn<String, Entity> {
		@Override
		public void processElement(ProcessContext c) {
			String[] columns = c.element().split(",");

			Builder builder = Key.newBuilder();
			PathElement pathElement = builder.addPathBuilder().setKind("Product").setName(columns[0]).build();
			Key key = builder.setPath(0, pathElement).build();

			Entity.Builder entityBuilder = Entity.newBuilder();
			entityBuilder.setKey(key);
			entityBuilder.getMutableProperties().put("ProductCode", makeValue(columns[0]).build());
			entityBuilder.getMutableProperties().put("CategoryCode", makeValue(columns[1]).build());
			entityBuilder.getMutableProperties().put("ProductName", makeValue(columns[2]).build());
			entityBuilder.getMutableProperties().put("Price", makeValue(Integer.valueOf(columns[3])).build());
			
			String imageURL = "";
			if (columns.length > 4) {
				imageURL = columns[4];
			}
			entityBuilder.getMutableProperties().put("ImageURL", makeValue(imageURL).build());
			Entity entity = entityBuilder.build();
			c.output(entity);
		}
	}

	public static class CSVToDatastore extends PTransform<PCollection<String>, PCollection<Entity>> {
		@Override
		public PCollection<Entity> apply(PCollection<String> lines) {

			PCollection<Entity> entities = lines.apply(ParDo.of(new CSVToEntityFn()));

			return entities;
		}
	}

	public static void main(String[] args) {
		StorageToDatastoreOptions options = PipelineOptionsFactory.fromArgs(args).withValidation()
				.as(StorageToDatastoreOptions.class);
		Pipeline p = Pipeline.create(options);

		p.apply(TextIO.Read.named("ReadLines").from(options.getInputFile())).apply(new CSVToDatastore())
				.apply(DatastoreIO.v1().write().withProjectId(options.getProject()));

		p.run();
	}
}

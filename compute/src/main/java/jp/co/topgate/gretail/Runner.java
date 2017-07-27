package jp.co.topgate.gretail;

import jp.co.topgate.gretail.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by nagai on 2017/05/15.
 */
public class Runner {

    private static final int INTERVAL_MILLI_SEC = 1000;
    private static final long START_DELAY = 0L;

    private static final Logger logger = LoggerFactory.getLogger(Runner.class);

    public class Task extends TimerTask {

        private final Sender sender;
        private final DummyGenerator generator;
        private final int doNumPerSec;

        public Task(Sender sender, int doNumPerSec, double lambda, double stddev, boolean useTurnAroundCategory) {
            this.sender = sender;
            this.generator = new DummyGenerator(lambda, stddev, useTurnAroundCategory);
            this.doNumPerSec = doNumPerSec;
        }

        public void run() {
            long start = Instant.now().toEpochMilli();
            for(int i=0; i<this.doNumPerSec; i++) {
                Order receipt = generator.generate();
                this.sender.send(receipt);
            }
            long end = Instant.now().toEpochMilli();
            logger.info("takes: " + (end - start) + "ms");
        }
    }

    public void run(String endpoint, int doNumPerSec, double lambda, double stddev, boolean useTurnAroundCategory) {
        Timer timer = new Timer();
        Sender sender = new Sender(endpoint);
        timer.schedule(new Task(sender, doNumPerSec, lambda, stddev, useTurnAroundCategory), START_DELAY, INTERVAL_MILLI_SEC);
    }

    public static void main(String[] args) {


        String endpoint = args[0];
        int doPerSec = Integer.valueOf(args[1]);
        double lambda = Double.valueOf(args[2]);
        double stddev = Double.valueOf(args[3]);
        boolean useTurnAroundCategory = Boolean.valueOf(args[4]);

/*
        String endpoint = "https://retail-dataflow-demo.appspot.com/api/1/order";
        int doPerSec = 10;
        double lambda = 0.01;
        double stddev = 40;
        boolean useTurnAroundCategory = true;
*/
        final Runner runner = new Runner();
        runner.run(endpoint, doPerSec, lambda, stddev, useTurnAroundCategory);
    }

}

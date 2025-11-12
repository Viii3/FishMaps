package fish.payara.fishmaps.azul.performance.batch;

import fish.payara.fishmaps.azul.performance.DurationLogger;
import jakarta.batch.api.listener.JobListener;
import jakarta.inject.Named;

@Named
public class BatchListener implements JobListener {
    private long startTime;
    
    @Override
    public void beforeJob () throws Exception {
        this.startTime = System.nanoTime();
    }

    @Override
    public void afterJob () throws Exception {
        long endTime = System.nanoTime();
        DurationLogger.log("Batch", endTime - startTime);
    }
}

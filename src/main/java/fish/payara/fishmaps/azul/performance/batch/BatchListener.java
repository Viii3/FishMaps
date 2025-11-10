package fish.payara.fishmaps.azul.performance.batch;

import jakarta.batch.api.listener.JobListener;
import jakarta.inject.Named;

import java.util.logging.Level;
import java.util.logging.Logger;

@Named
public class BatchListener implements JobListener {
    private long startTime;
    
    @Override
    public void beforeJob () throws Exception {
        this.startTime = System.nanoTime();
    }

    @Override
    public void afterJob () throws Exception {
        long time2 = System.nanoTime();
        Logger.getLogger(BatchListener.class.getName())
            .log(Level.INFO, "Batch task completed in " + (time2 - startTime) + " nanos.");
    }
}

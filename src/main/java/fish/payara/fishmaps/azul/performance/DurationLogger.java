package fish.payara.fishmaps.azul.performance;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DurationLogger {
    private static final Logger logger = Logger.getLogger(DurationLogger.class.getName());
    private static final List<PerformanceLog> logs = new ArrayList<>();
    private static final ConfluencePublisher publisher = new ConfluencePublisher();
    
    public static void publish () {
        try {
            publisher.publishResults(List.copyOf(logs));
            logs.clear();
        }
        catch (Exception e) {
            logger.log(Level.SEVERE, "Failed to publish results to Confluence: ", e);
        }
    }
    
    public static List<PerformanceLog> getLogs () {
        return List.copyOf(logs);
    }
    
    public static int size () {
        return logs.size();
    }
    
    public static void log (String module, int itemSize, long nanoseconds) {
        PerformanceLog log = new PerformanceLog(module, Optional.of(itemSize), nanoseconds);
        DurationLogger.log(log);
    }
    
    public static void log (String module, long nanoseconds) {
        PerformanceLog log = new PerformanceLog(module, Optional.empty(), nanoseconds);
        DurationLogger.log(log);
    }
    
    public static void log (PerformanceLog performanceLog) {
        logs.add(performanceLog);
        performanceLog.log();
    }

    public static void clear () {
        logs.clear();
    }
    
    public record PerformanceLog (String module, Optional<Integer> itemSize, long nanosecondDuration) {
        public void log () {
            String message;
            message = this.itemSize
                .map(size -> String.format("[%s]: processed %d objects in %dns", this.module, size, this.nanosecondDuration))
                .orElseGet(() -> String.format("[%s]: completed processing in %dns", this.module, this.nanosecondDuration));
            logger.log(Level.INFO, message);
        }
    }
}

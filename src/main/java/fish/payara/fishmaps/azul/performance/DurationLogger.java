package fish.payara.fishmaps.azul.performance;

import java.util.logging.Level;
import java.util.logging.Logger;

public class DurationLogger {
    private static final Logger logger = Logger.getLogger(DurationLogger.class.getName());
    
    public static void log (String module, int itemSize, long nanoseconds) {
        module = "[" + module + "]";
        logger.log(Level.INFO, module + ": processed " + itemSize + " objects in " + nanoseconds + "ns");
    }
}

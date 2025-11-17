package fish.payara.fishmaps.azul.performance;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Utility class to load environment variables from a .env file.
 * Variables are loaded into system properties and can be accessed via System.getProperty().
 */
public class EnvLoader {
    private static final String ENV_FILE = "../.env";
    private static boolean loaded = false;
    private static final Logger logger = Logger.getLogger(EnvLoader.class.getName());

    /**
     * Loads environment variables from the .env file into system properties.
     * This method is idempotent and will only load the variables once.
     */
    public static synchronized void load() {
        if (loaded) return;

        try {
            File envFile = Paths.get(ENV_FILE).toFile();
            if (envFile.exists()) {
                Properties props = new Properties();
                try (FileReader reader = new FileReader(envFile)) {
                    props.load(reader);
                }

                // Set each property as a system property if not already set
                props.forEach((key, value) -> {
                    String keyStr = key.toString().trim();
                    String valueStr = value.toString().trim();
                    if (System.getProperty(keyStr) == null) {
                        System.setProperty(keyStr, valueStr);
                    }
                });

                logger.log(Level.INFO, "Loaded environment variables from " + ENV_FILE);
            } else {
                logger.log(Level.WARNING, "Warning: .env file not found at " + envFile.getAbsolutePath());
            }
            loaded = true;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "Warning: Could not load .env file: ", e);
        }
    }
}
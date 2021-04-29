package org.openapi.validator;

import java.io.IOException;
import java.util.logging.FileHandler;

public class Logger {
    private static java.util.logging.Logger logger;

    public static java.util.logging.Logger getInstance() {
        if (logger == null) {
            logger = java.util.logging.Logger.getLogger(Validator.class.getName());
            FileHandler fileHandler = null;
            try {
                fileHandler = new FileHandler("application.log", true);
            } catch (IOException e) {
                logger.severe("Log dump file handler initialization failed:" + e.getMessage());
            }
            logger.addHandler(fileHandler);
        }
        return logger;
    }
}

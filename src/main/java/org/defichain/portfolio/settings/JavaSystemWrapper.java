package org.defichain.portfolio.settings;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class JavaSystemWrapper implements SystemWrapper{
    private final Logger logger = Logger.getLogger("root");
    @Override
    public String getUserDir() {
        return System.getProperty("user.dir");
    }

    @Override
    public String getAppData() {
        return System.getenv("APPDATA");
    }

    public void createPortfolioHomeDir(Path portfolioHomePath) throws IOException {
        if (!Files.exists(portfolioHomePath)) {
            try {
                Files.createDirectory(portfolioHomePath);
            } catch (IOException e) {
                logger.warning("Directory could not be created: "+ portfolioHomePath.getFileName());
                throw e;
            }
        }
    }

    @Override
    public void setupLogger(String logDirectory) throws IOException {
        FileHandler fh = new FileHandler(logDirectory + "log.txt");
        SimpleFormatter formatter = new SimpleFormatter();
        fh.setFormatter(formatter);
        logger.addHandler(fh);
    }
}

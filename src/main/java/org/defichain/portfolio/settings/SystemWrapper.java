package org.defichain.portfolio.settings;

import java.io.IOException;
import java.nio.file.Path;

interface SystemWrapper {
    String getUserDir();

    String getAppData();

    void createPortfolioHomeDir(Path portfolioHomePath) throws IOException;

    void setupLogger(String defiPortfolioHomeString) throws IOException;
}

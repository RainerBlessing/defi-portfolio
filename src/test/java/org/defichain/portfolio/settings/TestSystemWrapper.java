package org.defichain.portfolio.settings;

import java.nio.file.Path;

public class TestSystemWrapper implements SystemWrapper {
    @Override
    public String getUserDir() {
        return "/home/tester";
    }

    @Override
    public String getAppData() {
        return "C:/Users/tester/AppData/Roaming";
    }

    @Override
    public void createPortfolioHomeDir(Path portfolioHomePath) {

    }

    @Override
    public void setupLogger(String defiPortfolioHomeString) {

    }
}

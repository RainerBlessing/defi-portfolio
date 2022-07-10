package org.defichain.portfolio.settings;

import com.google.inject.Inject;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;
import java.util.logging.Logger;

import static org.defichain.portfolio.settings.SystemType.WINDOWS;

public class PortfolioSystem {

    private final SystemWrapper systemWrapper;

    private final SystemType systemType;

    private final Logger logger = Logger.getLogger("root");

    public PortfolioSystem(SystemWrapper systemWrapper, SystemType systemType) throws IOException {
        this.systemWrapper = systemWrapper;
        this.systemType = systemType;

        String defiPortfolioHomeString = getDefiPortfolioHome();
        Path portfolioHomePath = Paths.get(defiPortfolioHomeString);

        systemWrapper.createPortfolioHomeDir(portfolioHomePath);

        systemWrapper.setupLogger(defiPortfolioHomeString);
    }

    @Inject
    public PortfolioSystem(SystemWrapper systemWrapper) {
        this.systemWrapper = systemWrapper;

        String OS = System.getProperty("os.name", "generic").toLowerCase(Locale.ENGLISH);

        if (OS.contains("win")) {
            systemType = WINDOWS;
        } else if (OS.contains("mac")) {
            systemType = SystemType.OSX;
        } else {
            systemType = SystemType.LINUX;
        }
    }

    public String getDefiPortfolioHome() {
        return switch (systemType) {
            case LINUX, OSX -> systemWrapper.getUserDir() + "/PortfolioData/";
            case WINDOWS -> systemWrapper.getAppData().replace("\\", "/") + "/defi-portfolio/";
        };
    }
}

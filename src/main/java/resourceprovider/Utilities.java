package resourceprovider;

import com.google.inject.Inject;
import controllers.SettingsController;

import java.io.File;
import java.io.IOException;

public class Utilities {
    public static final String LIB_DIRECTORY = "lib";

    private SettingsController settingsController;

    @Inject
    public Utilities(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    public Process updatePortfolio() throws IOException {
        String path = LIB_DIRECTORY+"\\updatePortfolio.exe";
        String[] commands = {"cmd", "/c", "start", "\"Update Portfolio\"", path,settingsController.DEFI_PORTFOLIO_HOME,settingsController.PORTFOLIO_CONFIG_FILE_PATH};

        return Runtime.getRuntime().exec(commands);
    }

    public Process stockTokenPrices() throws IOException {
        String path = LIB_DIRECTORY+"\\StockTokenPrices.exe";
        String[] commands = {"cmd", "/c", "start", "\"Update Portfolio\"", path,settingsController.DEFI_PORTFOLIO_HOME};
        return Runtime.getRuntime().exec(commands);
    }

    public Process main(String strPortfolioDataPath) throws IOException{
        String path = LIB_DIRECTORY+ "\\main.exe";
        String[] commands = {"cmd", "/c", "start", "\"Merging data\"", path, settingsController.DEFI_PORTFOLIO_HOME.replace("/", "\\") + "transactionData.portfolio", strPortfolioDataPath};
        return Runtime.getRuntime().exec(commands);
    }
}

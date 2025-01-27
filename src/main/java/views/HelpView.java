package views;

import com.google.inject.Inject;
import controllers.SettingsController;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpView implements Initializable {
    @Inject
    private SettingsController settingsController;

    public Button btnClose;
    public AnchorPane anchorPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnClose.getTooltip().setText(settingsController.getTranslationValue("Close").toString());
        btnClose.setText(settingsController.getTranslationValue("Close").toString());
    }

    public void btnMailToCallback() {

        if (settingsController.getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://youtu.be/86xO3_oFXAQ"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://youtu.be/86xO3_oFXAQ").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }


    public void defichain() {
        if (settingsController.getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://defichain.com/"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://defichain.com/").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void defichainwiki() {
        if (settingsController.getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://defichain-wiki.com/wiki/DeFiChain-Portfolio"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://defichain-wiki.com/wiki/DeFiChain-Portfolio").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
    public void github() {
        if (settingsController.getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://github.com/DeFi-PortfolioManagement/defi-portfolio/blob/master/README.md"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://github.com/DeFi-PortfolioManagement/defi-portfolio/blob/master/README.md").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }

    public void btnTelegram()  {
        if (settingsController.getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://t.me/DeFiChainPortfolio"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://t.me/DeFiChainPortfolio").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }



    public void Close() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void defichainexplained() {
        if (settingsController.getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            try {
                if (Runtime.getRuntime().exec(new String[]{"which", "xdg-open"}).getInputStream().read() != -1) {
                    Runtime.getRuntime().exec(new String[]{"xdg-open", "https://www.defichain-income.com/"});
                } else {
                    System.out.println("xdg-open is not supported!");
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            try {
                Desktop.getDesktop().browse(new URL("https://www.defichain-income.com/").toURI());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
    }
}



package portfolio.views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import portfolio.controllers.SettingsController;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MissingTransactionView implements Initializable {
    @FXML
    public Button btnClose;
    public Button btnOpen;
    @FXML
    public CheckBox hCheckBox;
    @FXML
    public Label lblTransaction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblTransaction.setText(SettingsController.getInstance().translationList.getValue().get("MissingTransaction").toString());
        hCheckBox.setText(SettingsController.getInstance().translationList.getValue().get("DisclaimCheck").toString());
        btnClose.setText(SettingsController.getInstance().translationList.getValue().get("Close").toString());
        btnOpen.setText(SettingsController.getInstance().translationList.getValue().get("Open").toString());
    }

    public void btnClose() {
        SettingsController.getInstance().showMissingTransaction = !hCheckBox.isSelected();
        SettingsController.getInstance().saveSettings();
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void btnOpen(ActionEvent actionEvent) {
        if (SettingsController.getInstance().getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            File file = new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME);
            try {
                Runtime.getRuntime().exec(new String[]{"xdg-open", file.getAbsolutePath()});
            } catch (IOException e) {
                SettingsController.getInstance().logger.warning(e.toString());
            }
        } else {
            try {
                Desktop.getDesktop().open(new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME));
            } catch (IOException e) {
                SettingsController.getInstance().logger.warning(e.toString());
            }
        }
    }
}



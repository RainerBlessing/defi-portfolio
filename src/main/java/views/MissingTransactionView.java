package views;

import com.google.inject.Inject;
import controllers.SettingsController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MissingTransactionView implements Initializable {
    @Inject
    private SettingsController settingsController;
    
    @FXML
    public Button btnClose;
    public Button btnOpen;
    @FXML
    public CheckBox hCheckBox;
    @FXML
    public Label lblTransaction;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblTransaction.setText(settingsController.translationList.getValue().get("MissingTransaction").toString());
        hCheckBox.setText(settingsController.translationList.getValue().get("DisclaimCheck").toString());
        btnClose.setText(settingsController.translationList.getValue().get("Close").toString());
        btnOpen.setText(settingsController.translationList.getValue().get("Open").toString());
    }

    public void btnClose() {
        settingsController.showMissingTransaction = !hCheckBox.isSelected();
        settingsController.saveSettings();
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void btnOpen(ActionEvent actionEvent) {
        if (settingsController.getPlatform().equals("linux")) {
            // Workaround for Linux because "Desktop.getDesktop().browse()" doesn't work on some Linux implementations
            File file = new File(settingsController.DEFI_PORTFOLIO_HOME);
            try {
                Runtime.getRuntime().exec(new String[]{"xdg-open", file.getAbsolutePath()});
            } catch (IOException e) {
                settingsController.logger.warning(e.toString());
            }
        } else {
            try {
                Desktop.getDesktop().open(new File(settingsController.DEFI_PORTFOLIO_HOME));
            } catch (IOException e) {
                settingsController.logger.warning(e.toString());
            }
        }
    }
}



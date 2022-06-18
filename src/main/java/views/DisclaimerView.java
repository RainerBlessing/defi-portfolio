package views;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import controllers.SettingsController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

@Singleton
public class DisclaimerView implements Initializable {
    private SettingsController settingsController;
    public Button btnClose;
    @FXML
    public Button btnOpen;
    @FXML
    public CheckBox hCheckBox;
    @FXML
    public Label lblDisclaimer;

    @Inject
    public DisclaimerView(SettingsController settingsController) {
        this.settingsController = settingsController;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblDisclaimer.setText(settingsController.translationList.getValue().get("Disclaim").toString());
        hCheckBox.setText(settingsController.translationList.getValue().get("DisclaimCheck").toString());
        btnClose.setText(settingsController.translationList.getValue().get("Close").toString());
    }

    public void btnClose() {
        settingsController.showDisclaim = !hCheckBox.isSelected();
        settingsController.saveSettings();
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }


}



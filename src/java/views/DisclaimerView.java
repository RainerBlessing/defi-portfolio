package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import controllers.SettingsController;

import java.net.URL;
import java.util.ResourceBundle;

public class DisclaimerView implements Initializable {
    public Button btnClose;
    @FXML
    public Button btnOpen;
    @FXML
    public CheckBox hCheckBox;
    @FXML
    public Label lblDisclaimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblDisclaimer.setText(SettingsController.getInstance().translationList.getValue().get("Disclaim").toString());
        hCheckBox.setText(SettingsController.getInstance().translationList.getValue().get("DisclaimCheck").toString());
        btnClose.setText(SettingsController.getInstance().translationList.getValue().get("Close").toString());
    }

    public void btnClose() {
        SettingsController.getInstance().showDisclaim = !hCheckBox.isSelected();
        SettingsController.getInstance().saveSettings();
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }


}



package views;

import com.google.inject.Inject;
import controllers.SettingsController;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class RestartToolView implements Initializable {
    @Inject
    private SettingsController settingsController;
    public Button btnClose;
    @FXML
    public Label lblDisclaimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblDisclaimer.setText(settingsController.translationList.getValue().get("Restart").toString());
        btnClose.setText(settingsController.translationList.getValue().get("Close").toString());
    }

    public void btnClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }


}



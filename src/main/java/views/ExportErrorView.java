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

public class ExportErrorView implements Initializable {
    @Inject
    private SettingsController settingsController;
    public Button btnClose;
    @FXML
    public Label lblText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblText.setText(settingsController.getTranslationValue("CSVExportedError").toString());
        btnClose.setText(settingsController.getTranslationValue("Close").toString());
    }

    public void btnClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }


}



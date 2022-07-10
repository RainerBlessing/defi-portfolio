package views;

import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Inject;
import resourceprovider.CssProvider;
import controllers.SettingsController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NoAddressesWarningView implements Initializable {
    @Inject
    private SettingsController settingsController;
    public Stage AddressConfigStage;
    public Button btnClose;
    public Button btnOpenConfig;
    @FXML
    public Label lblText;
    @Inject
    private GuiceFXMLLoader fxmlLoader;
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblText.setText(settingsController.getTranslationValue("NoAddressFoundGoToConfig").toString());
        btnClose.setText(settingsController.getTranslationValue("Close").toString());
        btnOpenConfig.setText(settingsController.getTranslationValue("openAddressConfig").toString());
    }


    public void btnClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
    public void openConfig() throws IOException {
        this.btnClose();
        if (AddressConfigStage != null) AddressConfigStage.close();
        final SettingsView.Delta dragDelta = new SettingsView.Delta();
        Parent root = fxmlLoader.load(getClass().getResource("AddAddresses.fxml")).getRoot();
        Scene scene = new Scene(root);
        AddressConfigStage = new Stage();
        AddressConfigStage.initStyle(StageStyle.UNDECORATED);
        scene.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = AddressConfigStage.getX() - mouseEvent.getScreenX();
            dragDelta.y = AddressConfigStage.getY() - mouseEvent.getScreenY();
        });
        scene.setOnMouseDragged(mouseEvent -> {
            AddressConfigStage.setX(mouseEvent.getScreenX() + dragDelta.x);
            AddressConfigStage.setY(mouseEvent.getScreenY() + dragDelta.y);
        });
        AddressConfigStage.setScene(scene);
        AddressConfigStage.show();

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            AddressConfigStage.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            AddressConfigStage.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }
    static class Delta {
        double x, y;
    }


}



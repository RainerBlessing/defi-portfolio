package portfolio.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import portfolio.controllers.MainViewController;
import portfolio.controllers.SettingsController;
import portfolio.controllers.TransactionController;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NoAddressesWarningView implements Initializable {
    public Stage AddressConfigStage;
    public Button btnClose;
    public Button btnOpenConfig;
    @FXML
    public Label lblText;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        lblText.setText(SettingsController.getInstance().translationList.getValue().get("NoAddressFoundGoToConfig").toString());
        btnClose.setText(SettingsController.getInstance().translationList.getValue().get("Close").toString());
        btnOpenConfig.setText(SettingsController.getInstance().translationList.getValue().get("openAddressConfig").toString());
    }


    public void btnClose() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
    public void openConfig() throws IOException {
        this.btnClose();
        if (AddressConfigStage != null) AddressConfigStage.close();
        final SettingsView.Delta dragDelta = new SettingsView.Delta();
        Parent root = FXMLLoader.load(getClass().getResource("AddAddresses.fxml"));
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

        java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
        java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
        if (SettingsController.getInstance().selectedStyleMode.getValue().equals("Dark Mode")) {
            AddressConfigStage.getScene().getStylesheets().add(darkMode.toURI().toString());
        } else {
            AddressConfigStage.getScene().getStylesheets().add(lightMode.toURI().toString());
        }
    }
    static class Delta {
        double x, y;
    }


}



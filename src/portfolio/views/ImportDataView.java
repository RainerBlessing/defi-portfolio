package portfolio.views;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import portfolio.controllers.CheckConnection;
import portfolio.controllers.MainViewController;
import portfolio.controllers.SettingsController;
import portfolio.controllers.TransactionController;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Timer;

public class ImportDataView {

    @FXML
    public Button btnUpdateData,btnCakeCSV,btnWalletCSV,btnClose;
    @FXML
    public CheckBox cmbSaveDefault;
    public Stage NoAddressesWarningView;
    public Stage AddressConfigStage;

    public void btnUpdateDataPressed(){
        if(SettingsController.getInstance().listAddresses.size() > 0){
            TransactionController.getInstance().updateDatabase();
        }
        else{
            if (NoAddressesWarningView != null) NoAddressesWarningView.close();
            Parent root = null;
            final Delta dragDelta = new Delta();
            try {
                root = FXMLLoader.load(getClass().getResource("NoAddressesWarningView.fxml"));
            } catch (IOException ex) {
                ex.printStackTrace();
            }
            Scene scene = new Scene(root);
            NoAddressesWarningView = new Stage();
            NoAddressesWarningView.initStyle(StageStyle.UNDECORATED);
            scene.setOnMousePressed(mouseEvent -> {
                dragDelta.x = NoAddressesWarningView.getX() - mouseEvent.getScreenX();
                dragDelta.y = NoAddressesWarningView.getY() - mouseEvent.getScreenY();
            });
            scene.setOnMouseDragged(mouseEvent -> {
                NoAddressesWarningView.setX(mouseEvent.getScreenX() + dragDelta.x);
                NoAddressesWarningView.setY(mouseEvent.getScreenY() + dragDelta.y);
            });
            NoAddressesWarningView.setScene(scene);

            NoAddressesWarningView.show();

            File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
            File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
            if (SettingsController.getInstance().selectedStyleMode.getValue().equals("Dark Mode")) {
                NoAddressesWarningView.getScene().getStylesheets().add(darkMode.toURI().toString());
            } else {
                NoAddressesWarningView.getScene().getStylesheets().add(lightMode.toURI().toString());
            }
        }


        this.btnClosePressed();
    }
    public void btnCakeCSVPressed(){
        this.btnClosePressed();
        TransactionController.getInstance().importCakeCSV();
    }
    public void btnWalletCSVPressed(){
        this.btnClosePressed();
        TransactionController.getInstance().importWalletCSV();
    }
    public void btnClosePressed(){
        Stage stage = (Stage) btnUpdateData.getScene().getWindow();
        stage.close();
    }
    public void defaultChanged(){
        SettingsController.getInstance().saveSettings();
    }
    static class Delta {
        double x, y;
    }

    public void btnOpenAdressConfig() throws IOException {
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
        //AddressConfigStage.getIcons().add(new Image(new File(System.getProperty("user.dir").replace("\\","/") + "/defi-portfolio/src/icons/settings.png").toURI().toString()));
        //AddressConfigStage.setTitle(this.mainViewController.settingsController.translationList.getValue().get("Settings").toString());
        AddressConfigStage.setScene(scene);

//        ChangeListener<Number> widthListener = (observable, oldValue, newValue) -> {
//            double stageWidth = newValue.doubleValue();
//            AddressConfigStage.setX(mainAnchorPane.getScene().getWindow().getX() + mainAnchorPane.getScene().getWindow().getWidth() / 2 - stageWidth / 2);
//        };
//        ChangeListener<Number> heightListener = (observable, oldValue, newValue) -> {
//            double stageHeight = newValue.doubleValue();
//            AddressConfigStage.setY(mainAnchorPane.getScene().getWindow().getY() + mainAnchorPane.getScene().getWindow().getHeight() / 2 - stageHeight / 2);
//        };

        //     AddressConfigStage.widthProperty().addListener(widthListener);
        //   AddressConfigStage.heightProperty().addListener(heightListener);

        AddressConfigStage.setOnShown(e -> {
            //    AddressConfigStage.widthProperty().removeListener(widthListener);
            //   AddressConfigStage.heightProperty().removeListener(heightListener);
        });

        AddressConfigStage.show();

        java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
        java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
        if (SettingsController.getInstance().selectedStyleMode.getValue().equals("Dark Mode")) {
            AddressConfigStage.getScene().getStylesheets().add(darkMode.toURI().toString());
        } else {
            AddressConfigStage.getScene().getStylesheets().add(lightMode.toURI().toString());
        }
    }
}

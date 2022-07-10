package views;

import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import resourceprovider.CssProvider;
import controllers.SettingsController;
import controllers.TransactionController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

@Singleton
public class ImportDataView {
    private SettingsController settingsController;
    private TransactionController transactionController;
    @FXML
    public Button btnUpdateData,btnCakeCSV,btnWalletCSV,btnClose;
    @FXML
    public CheckBox cmbSaveDefault;
    public Stage NoAddressesWarningView;
    public Stage AddressConfigStage;
    @Inject
    private GuiceFXMLLoader fxmlLoader;
    @Inject
    public ImportDataView(SettingsController settingsController, TransactionController transactionController) {
        this.settingsController = settingsController;
        this.transactionController = transactionController;
    }

    public void btnUpdateDataPressed(){
        if(settingsController.listAddresses.size() > 0){
            transactionController.updateDatabase();
        }
        else{
            if (NoAddressesWarningView != null) NoAddressesWarningView.close();
            Parent root = null;
            final Delta dragDelta = new Delta();
            try {
                root = fxmlLoader.load(getClass().getResource("NoAddressesWarningView.fxml")).getRoot();
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

            if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
                NoAddressesWarningView.getScene().getStylesheets().add(CssProvider.DARK_MODE);
            } else {
                NoAddressesWarningView.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
            }
        }


        this.btnClosePressed();
    }
    public void btnCakeCSVPressed(){
        this.btnClosePressed();
        transactionController.importCakeCSV();
    }
    public void btnWalletCSVPressed(){
        this.btnClosePressed();
        transactionController.importWalletCSV();
    }
    public void btnClosePressed(){
        Stage stage = (Stage) btnUpdateData.getScene().getWindow();
        stage.close();
    }
    public void defaultChanged(){
        settingsController.saveSettings();
    }
    static class Delta {
        double x, y;
    }

    public void btnOpenAdressConfig() throws IOException {
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
        //AddressConfigStage.getIcons().add(new Image(new File(System.getProperty("user.dir").replace("\\","/") + "/defi-portfolio/src/icons/settings.png").toURI().toString()));
        //AddressConfigStage.setTitle(this.mainViewController.settingsController.getTranslationValue("Settings").toString());
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

        if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
            AddressConfigStage.getScene().getStylesheets().add(CssProvider.DARK_MODE);
        } else {
            AddressConfigStage.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
        }
    }
}

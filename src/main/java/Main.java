import com.cathive.fx.guice.GuiceApplication;
import com.cathive.fx.guice.GuiceFXMLLoader;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.name.Named;
import controllers.CssProvider;
import controllers.TransactionController;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import controllers.SettingsController;
import javafx.stage.StageStyle;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ResourceBundle;

public class Main extends GuiceApplication {
    @Inject
    private SettingsController settingsController;
    @Inject
    private GuiceFXMLLoader fxmlLoader;
    @Inject
    @Named("i18n-resources")
    private ResourceBundle resources;
    @Inject
    private TransactionController transactionController;

    @Override
    public void start(Stage stage) throws IOException {

        Parent root = null;
        // Main Window
        try {
            root = fxmlLoader.load(getClass().getResource("views/MainView.fxml"), resources).getRoot();
        } catch (IOException e) {
            settingsController.logger.warning("Exception occurred: " + e);
            e.printStackTrace();
        }
        assert root != null;
        Scene scene = new Scene(root);
        stage = new Stage();

        final Delta dragDelta = new Delta();
        stage.setTitle("DeFi-Portfolio " + settingsController.Version);
        stage.getIcons().add(new Image(new File( System.getProperty("user.dir") + "/icons/DefiIcon.png").toURI().toString()));
        stage.setScene(scene);
        stage.setMinHeight(700);
        stage.setMinWidth(1200);
        stage.show();
        // Stop Splashsccreen
        File file = new File(System.getProperty("user.dir")+"/PortfolioData/" +"splash.portfolio");
        if(file.exists())file.delete();

        stage.setOnCloseRequest(we -> {
            transactionController.stopServer();
            Platform.exit();
            System.exit(0);
        });

        // Disclaimer anzeigen
        if(settingsController.showDisclaim) {
            Parent rootDisclaimer = fxmlLoader.load(getClass().getResource("views/DisclaimerView.fxml"), resources).getRoot();
            Scene sceneDisclaimer = new Scene(rootDisclaimer);
            Stage stageDisclaimer = new Stage();
            stageDisclaimer.setTitle("DeFi-Portfolio Disclaimer");
            stageDisclaimer.setScene(sceneDisclaimer);
            stageDisclaimer.initStyle(StageStyle.UNDECORATED);
            sceneDisclaimer.setOnMousePressed(mouseEvent -> {
                // record a delta distance for the drag and drop operation.
                dragDelta.x = stageDisclaimer.getX() - mouseEvent.getScreenX();
                dragDelta.y = stageDisclaimer.getY() - mouseEvent.getScreenY();
            });
            sceneDisclaimer.setOnMouseDragged(mouseEvent -> {
                stageDisclaimer.setX(mouseEvent.getScreenX() + dragDelta.x);
                stageDisclaimer.setY(mouseEvent.getScreenY() + dragDelta.y);
            });
            stageDisclaimer.show();
            stageDisclaimer.setAlwaysOnTop(true);

            if (settingsController.selectedStyleMode.getValue().equals("Dark Mode")) {
                stageDisclaimer.getScene().getStylesheets().add(CssProvider.DARK_MODE);
            } else {
                stageDisclaimer.getScene().getStylesheets().add(CssProvider.LIGHT_MODE);
            }
        }
    }

    @Override
    public void init(List<Module> modules) throws Exception {
            modules.add(new AppModule());
    }

    static class Delta { double x, y; }

    public static void main(String[] args) {
        launch(args);
    }
}
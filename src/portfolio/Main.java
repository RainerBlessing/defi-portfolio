package portfolio;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import portfolio.controllers.SettingsController;
import portfolio.controllers.TransactionController;

import java.io.File;
import java.io.IOException;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {

        Parent root = null;
        // Main Window
        try {
            root = FXMLLoader.load(getClass().getResource("views/MainView.fxml"));
        } catch (IOException e) {
            SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
        }
        assert root != null;
        Scene scene = new Scene(root);
        stage = new Stage();

        final Delta dragDelta = new Delta();
        stage.setTitle("DeFi-Portfolio " + SettingsController.getInstance().Version);
        stage.getIcons().add(new Image(new File( System.getProperty("user.dir") + "/defi-portfolio/src/icons/DefiIcon.png").toURI().toString()));
        stage.setScene(scene);
        stage.setMinHeight(700);
        stage.setMinWidth(1200);
        stage.show();
        // Stop Splashsccreen
        File file = new File(System.getProperty("user.dir")+"/PortfolioData/" +"splash.portfolio");
        if(file.exists())file.delete();

        stage.setOnCloseRequest(we -> {
            TransactionController.getInstance().stopServer();
            Platform.exit();
            System.exit(0);
        });

        // Disclaimer anzeigen
        if(SettingsController.getInstance().showDisclaim) {
            Parent rootDisclaimer = FXMLLoader.load(getClass().getResource("views/DisclaimerView.fxml"));
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

            if (SettingsController.getInstance().selectedStyleMode.getValue().equals("Dark Mode")) {
                java.io.File darkMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/darkMode.css");
                stageDisclaimer.getScene().getStylesheets().add(darkMode.toURI().toString());
            } else {
                java.io.File lightMode = new File(System.getProperty("user.dir") + "/defi-portfolio/src/portfolio/styles/lightMode.css");
                stageDisclaimer.getScene().getStylesheets().add(lightMode.toURI().toString());
            }
        }
    }

    static class Delta { double x, y; }

    public static void main(String[] args) {
        launch(args);
    }
}
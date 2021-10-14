package portfolio.controllers;

import javafx.application.Platform;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.TimerTask;

public class TimerController extends TimerTask {
    MainViewController mainViewController;

    public TimerController(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
                    if (SettingsController.getInstance().runTimer) {
                        try {
                            HttpURLConnection connection = (HttpURLConnection) new URL("https://api.defichain.io/v1/getblockcount").openConnection();
                            String jsonText = "";
                            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                                jsonText = br.readLine();
                            } catch (Exception ex) {
                                SettingsController.getInstance().logger.warning("Exception occured: " + ex.toString());
                            }
                            JSONObject obj = (JSONObject) JSONValue.parse(jsonText);
                            if (obj.get("data") != null) {
                                if(Long.parseLong(mainViewController.strCurrentBlockLocally.getValue()) > Long.parseLong(obj.get("data").toString())){
                                    mainViewController.strCurrentBlockLocally.set(obj.get("data").toString());
                                }
                                mainViewController.strCurrentBlockOnBlockchain.set(obj.get("data").toString());
                            }
                        } catch (IOException e) {
                            SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
                        }


                    }
                }
        );
    }
}

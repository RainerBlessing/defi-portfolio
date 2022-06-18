package controllers;

import com.google.inject.Inject;
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

    private SettingsController settingsController;
    final MainViewController mainViewController;
    @Inject
    public TimerController(MainViewController mainViewController,SettingsController settingsController) {
        this.mainViewController = mainViewController;
        this.settingsController = settingsController;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
                    if (settingsController.runTimer) {
                        try {
                            HttpURLConnection connection = (HttpURLConnection) new URL("https://ocean.defichain.com/v0/mainnet/stats").openConnection();
                            String jsonText = "";
                            try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                                jsonText = br.readLine();
                            } catch (Exception ex) {
                                settingsController.logger.warning("Exception occurred: " + ex);
                            }
                            JSONObject obj = (JSONObject) JSONValue.parse(jsonText);
                            if (obj.get("data") != null) {
                                JSONObject data = (JSONObject) obj.get("data");
                                JSONObject count = (JSONObject) data.get("count");

                                if(Long.parseLong(mainViewController.strCurrentBlockLocally.getValue()) > Long.parseLong(count.get("blocks").toString())){
                                    mainViewController.strCurrentBlockLocally.set(count.get("blocks").toString());
                                }
                                mainViewController.strCurrentBlockOnBlockchain.set(count.get("blocks").toString());
                            }
                        } catch (IOException e) {
                            settingsController.logger.warning("Exception occurred: " + e);
                        }


                    }
                }
        );
    }
}

package views;

import com.google.inject.Inject;
import controllers.MainViewController;
import controllers.SettingsController;
import controllers.TransactionController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import models.Addresses;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.ResourceBundle;

public class AddAdressView implements Initializable {
    @Inject
    private SettingsController settingsController;
    @Inject
    private MainViewController mainViewController;
    public Button btnAddAddress;
    public TextField txtUserAddress;
    public Label lblAddress;
    public Label lblNoValidAddress;

    public Button btnClose;
    public Button btnClearList;
    public Button btnSaveAndClose;

    @FXML
    public TableView<Addresses> table;
    @FXML
    public TableColumn<Addresses,String> tableAddedAddresses;
    public final ObservableList<Addresses> listAdresses =
            FXCollections.observableArrayList();
    @Inject
    private TransactionController transactionController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadAddresses();

        this.lblAddress.setText(settingsController.translationList.getValue().get("address").toString());
        this.txtUserAddress.promptTextProperty().setValue(settingsController.translationList.getValue().get("typeYourAddress").toString());
        this.btnAddAddress.setText(settingsController.translationList.getValue().get("add").toString());
        this.tableAddedAddresses.setText(settingsController.translationList.getValue().get("addedAddresses").toString());
        this.btnClearList.setText(settingsController.translationList.getValue().get("removeEntry").toString());
        this.btnSaveAndClose.setText(settingsController.translationList.getValue().get("saveAndClose").toString());
        this.btnClose.setText(settingsController.translationList.getValue().get("Close").toString());
        this.lblNoValidAddress.setText(settingsController.translationList.getValue().get("noValidAddress").toString());
        table.setPlaceholder(new Label(""));

        lblNoValidAddress.setVisible(false);
        tableAddedAddresses.setCellValueFactory(new PropertyValueFactory<>("Address"));
        table.setItems(listAdresses);
    }

    public void addAddress(){
        if(!this.txtUserAddress.getText().isEmpty() && !this.checkDuplicate()){
            try {
                HttpURLConnection connection = (HttpURLConnection) new URL("https://ocean.defichain.com/v0/mainnet/address/"+this.txtUserAddress.getText()+"/balance").openConnection();
                try (BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
                    this.listAdresses.add(new Addresses(this.txtUserAddress.getText()));
                } catch (Exception ex) {

                    JSONObject obj = new JSONObject();
                    obj.put("query", "query {userByKey (key: \""+this.txtUserAddress.getText()+"\"){addresses}}");
                    byte[] postDataBytes = obj.toString().getBytes(StandardCharsets.UTF_8);

                    HttpURLConnection connectionGraph = (HttpURLConnection) new URL("https://graphql.defichain-income.com/graphql").openConnection();
                    connectionGraph.setDoOutput( true );
                    connectionGraph.setInstanceFollowRedirects( false );
                    connectionGraph.setRequestMethod( "POST" );
                    connectionGraph.setRequestProperty("Content-Type", "application/json");
                    try( DataOutputStream wr = new DataOutputStream( connectionGraph.getOutputStream())) {
                        wr.write( postDataBytes );
                    }


                    String jsonText="";
                    try (BufferedReader br = new BufferedReader(new InputStreamReader(connectionGraph.getInputStream()))) {
                        jsonText = br.readLine();
                    } catch (Exception exc) {
                        settingsController.logger.warning("Exception occurred: " + ex);
                    }

                    JSONObject response =   (JSONObject) JSONValue.parse(jsonText);
                    JSONObject data = (JSONObject)response.get("data");

                    if(!data.get("userByKey").toString().contains("null")){

                        JSONObject addresses = (JSONObject)data.get("userByKey");
                        List<String> addressList = (List) addresses.get("addresses");

                        for(String address : addressList){
                            this.listAdresses.add(new Addresses(address.replace(" ","")));
                        }
                    }else{

                    lblNoValidAddress.setVisible(true);
                    int delay = 5000;
                    ActionListener taskPerfomer = e -> lblNoValidAddress.setVisible(false);
                    new javax.swing.Timer(delay,taskPerfomer).start();

                    }
                }
            } catch (IOException e) {
                settingsController.logger.warning("Exception occurred: " + e);
            }
        }
        this.txtUserAddress.clear();
    }
    public boolean checkDuplicate(){
        for (Addresses listAdress : this.listAdresses) {
            if (listAdress.getAddress().equals(this.txtUserAddress.getText())) {
                return true;
            }
        }
        return false;
    }

    public void saveAddresses(){
        String savePath = settingsController.DEFI_PORTFOLIO_HOME + "Addresses.csv";
        FileWriter csvWriter;
        try {
            csvWriter = new FileWriter(savePath);
            for (Addresses listAdress : listAdresses) {
                csvWriter.append(listAdress.getAddress()).append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException ignored) {

        }

        // Copy to SettingsController
        settingsController.listAddresses.clear();
        for (Addresses listAdress : this.listAdresses) {
            settingsController.listAddresses.add(listAdress.getAddress());
        }
        transactionController.updateBalanceList();
        mainViewController.plotUpdate("Rewards");
        mainViewController.plotUpdate("Portfolio");
    }
    public void loadAddresses(){
        String savePath = settingsController.DEFI_PORTFOLIO_HOME + "Addresses.csv";
        File f = new File(savePath);
        if(f.exists() && !f.isDirectory()) {
            this.listAdresses.clear();
            try (BufferedReader br = new BufferedReader(new FileReader(savePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    this.listAdresses.add(new Addresses(line));
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void closeWindow(){
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void SaveAndClose(){
        this.saveAddresses();

        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void removeEntry(){
       this.listAdresses.remove(this.table.getSelectionModel().getSelectedItem());

     //   this.listAdresses.clear();
    }
}
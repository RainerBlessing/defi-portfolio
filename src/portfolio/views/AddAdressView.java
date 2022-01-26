package portfolio.views;
import javafx.animation.PauseTransition;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONValue;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import portfolio.controllers.MainViewController;
import portfolio.controllers.SettingsController;
import portfolio.controllers.TransactionController;
import portfolio.models.Addresses;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AddAdressView implements Initializable {

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
    public ObservableList<Addresses> listAdresses =
            FXCollections.observableArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadAddresses();

        this.lblAddress.setText(SettingsController.getInstance().translationList.getValue().get("address").toString());
        this.txtUserAddress.promptTextProperty().setValue(SettingsController.getInstance().translationList.getValue().get("typeYourAddress").toString());
        this.btnAddAddress.setText(SettingsController.getInstance().translationList.getValue().get("add").toString());
        this.tableAddedAddresses.setText(SettingsController.getInstance().translationList.getValue().get("addedAddresses").toString());
        this.btnClearList.setText(SettingsController.getInstance().translationList.getValue().get("removeEntry").toString());
        this.btnSaveAndClose.setText(SettingsController.getInstance().translationList.getValue().get("saveAndClose").toString());
        this.btnClose.setText(SettingsController.getInstance().translationList.getValue().get("Close").toString());
        this.lblNoValidAddress.setText(SettingsController.getInstance().translationList.getValue().get("noValidAddress").toString());
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
                    byte[] postDataBytes = obj.toString().getBytes("UTF-8");

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
                        SettingsController.getInstance().logger.warning("Exception occured: " + ex.toString());
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
                    ActionListener taskPerfomer = new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            lblNoValidAddress.setVisible(false);
                        }
                    };
                    new javax.swing.Timer(delay,taskPerfomer).start();

                    }
                }
            } catch (IOException e) {
                SettingsController.getInstance().logger.warning("Exception occured: " + e.toString());
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
        String savePath = SettingsController.getInstance().DEFI_PORTFOLIO_HOME + "Addresses.csv";
        FileWriter csvWriter;
        try {
            csvWriter = new FileWriter(savePath);
            for (Addresses listAdress : listAdresses) {
                csvWriter.append(listAdress.getAddress()).append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {

        }

        // Copy to SettingsController
        SettingsController.getInstance().listAddresses.clear();
        for (Addresses listAdress : this.listAdresses) {
            SettingsController.getInstance().listAddresses.add(listAdress.getAddress());
        }
        TransactionController.getInstance().updateBalanceList();
        MainViewController.getInstance().plotUpdate("Rewards");
        MainViewController.getInstance().plotUpdate("Portfolio");
    }
    public void loadAddresses(){
        String savePath = SettingsController.getInstance().DEFI_PORTFOLIO_HOME + "Addresses.csv";
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
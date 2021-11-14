package portfolio.views;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import portfolio.controllers.SettingsController;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class AddAdressView implements Initializable {

    public Button btnAddAddress;
    public TextField txtUserAddress;
    public Label lblAddress;
    public Label lblAddedAddresses;

    public Button btnClose;
    public Button btnClearList;
    public Button btnSaveAndClose;
    public TextArea txtArea;
    ArrayList<String> listAdresses = new ArrayList();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.loadAddresses();
        this.updateTextArea();

        this.lblAddress.setText(SettingsController.getInstance().translationList.getValue().get("address").toString());
        this.txtUserAddress.promptTextProperty().setValue(SettingsController.getInstance().translationList.getValue().get("typeYourAddress").toString());
        this.btnAddAddress.setText(SettingsController.getInstance().translationList.getValue().get("add").toString());
        this.lblAddedAddresses.setText(SettingsController.getInstance().translationList.getValue().get("addedAddresses").toString());
        this.txtArea.promptTextProperty().setValue(SettingsController.getInstance().translationList.getValue().get("noAddressAdded").toString());
        this.btnClearList.setText(SettingsController.getInstance().translationList.getValue().get("clearList").toString());
        this.btnSaveAndClose.setText(SettingsController.getInstance().translationList.getValue().get("saveAndClose").toString());
        this.btnClose.setText(SettingsController.getInstance().translationList.getValue().get("Close").toString());
    }

    public void addAddress(){
        if(!this.txtUserAddress.getText().isEmpty() && !this.listAdresses.contains(this.txtUserAddress.getText())){
            this.listAdresses.add(this.txtUserAddress.getText());
        }
        this.txtUserAddress.clear();
        this.updateTextArea();
    }

    public void updateTextArea(){
        if (this.listAdresses.size() == 0){
            this.txtArea.setText("");
        }
        else{
            StringBuilder addresses = new StringBuilder();
            for (String listAdress : listAdresses) {
                addresses.append(listAdress).append("\n");
            }
            txtArea.setText(addresses.toString());
        }
    }
    public void saveAddresses(){
        String savePath = SettingsController.getInstance().DEFI_PORTFOLIO_HOME + "Addresses.csv";
        FileWriter csvWriter;
        try {
            csvWriter = new FileWriter(savePath);
            for (String listAdress : listAdresses) {
                csvWriter.append(listAdress).append("\n");
            }
            csvWriter.flush();
            csvWriter.close();
        } catch (IOException e) {

        }

        // Copy to SettingsController
        SettingsController.getInstance().listAddresses.clear();
        for (String listAdress : this.listAdresses) {
            SettingsController.getInstance().listAddresses.add(listAdress);
        }
    }
    public void loadAddresses(){
        String savePath = SettingsController.getInstance().DEFI_PORTFOLIO_HOME + "Addresses.csv";
        File f = new File(savePath);
        if(f.exists() && !f.isDirectory()) {
            this.listAdresses.clear();
            try (BufferedReader br = new BufferedReader(new FileReader(savePath))) {
                String line;
                while ((line = br.readLine()) != null) {
                    this.listAdresses.add(line);
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

    public void clearList(){
        this.listAdresses.clear();
        this.updateTextArea();
    }
}



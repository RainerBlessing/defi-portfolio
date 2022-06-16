package views;

import javafx.event.ActionEvent;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import controllers.*;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;

public class UpdateView implements Initializable {

    public Button btnClose;
    public AnchorPane anchorPane;
    TransactionController transactionController  = TransactionController.getInstance();
    final UpdateController updateController = UpdateController.getInstance();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        btnClose.getTooltip().textProperty().bindBidirectional(updateController.strCloseText);
    }

    public void Close() {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }

    public void github(MouseEvent mouseEvent) {
    }
}



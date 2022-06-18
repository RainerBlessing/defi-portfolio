package views;

import controllers.DonateController;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.util.ResourceBundle;

public class DonateView implements Initializable {
    public Button btnCopy;
    public ImageView img;
    public Label donateText;
    public Button btnClose;
    public TextField donateTextField;
    public AnchorPane anchorPane;

    final DonateController donateController = DonateController.getInstance();

    public void  btnCopyPressed(){
        String myString = "dMswTqWd43S9Yu1m4LiX3QYPL2BAs7d37V";
        StringSelection stringSelection = new StringSelection(myString);
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        clipboard.setContents(stringSelection, null);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        this.donateText.textProperty().bindBidirectional(this.donateController.strDonateText);
        this.btnClose.textProperty().bindBidirectional(this.donateController.strBtnClose);
        this.donateTextField.setEditable(false);
    }

    public void Close(MouseEvent mouseEvent) {
        Stage stage = (Stage) btnClose.getScene().getWindow();
        stage.close();
    }
}



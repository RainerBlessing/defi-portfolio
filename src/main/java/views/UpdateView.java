package views;

import com.google.inject.Inject;
import controllers.TransactionController;
import controllers.UpdateController;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

public class UpdateView implements Initializable {

    public Button btnClose;
    public AnchorPane anchorPane;
    @Inject
    TransactionController transactionController;
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



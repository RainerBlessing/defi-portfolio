package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UpdateController {

    private static UpdateController OBJ;

    static {
        OBJ = new UpdateController();
    }
    public StringProperty strCloseText= new SimpleStringProperty();
    public static UpdateController getInstance() {
        return OBJ;
    }

}

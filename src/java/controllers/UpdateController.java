package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class UpdateController {

    private static final UpdateController OBJ;

    static {
        OBJ = new UpdateController();
    }
    public final StringProperty strCloseText= new SimpleStringProperty();
    public static UpdateController getInstance() {
        return OBJ;
    }

}

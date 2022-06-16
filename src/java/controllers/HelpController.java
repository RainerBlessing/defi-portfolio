package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class HelpController {

    private static final HelpController OBJ;

    static {
        OBJ = new HelpController();
    }
    public StringProperty strCloseText= new SimpleStringProperty();
    public static HelpController getInstance() {
        return OBJ;
    }


}

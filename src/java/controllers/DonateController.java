package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DonateController {
    private static DonateController OBJ;

    static {
        OBJ = new DonateController();
    }
    public StringProperty strDonateText = new SimpleStringProperty();
    public StringProperty strBtnClose = new SimpleStringProperty();


    public static DonateController getInstance() {
        return OBJ;
    }
}

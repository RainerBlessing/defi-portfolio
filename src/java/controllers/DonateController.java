package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class DonateController {
    private static final DonateController OBJ;

    static {
        OBJ = new DonateController();
    }
    public final StringProperty strDonateText = new SimpleStringProperty();
    public final StringProperty strBtnClose = new SimpleStringProperty();


    public static DonateController getInstance() {
        return OBJ;
    }
}

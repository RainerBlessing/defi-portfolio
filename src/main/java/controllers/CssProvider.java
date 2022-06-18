package controllers;

import java.io.File;

public class CssProvider {
    public static final String STYLES_DIRECTORY = "src/main/resources/styles/";
    public static final String DARK_MODE;
    public static final String LIGHT_MODE;

    static {
        LIGHT_MODE = new File(STYLES_DIRECTORY+"lightMode.css").toURI().toString();
        DARK_MODE = new File(STYLES_DIRECTORY+"darkMode.css").toURI().toString();
    }
}

package controllers;

import com.google.inject.Singleton;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
@Singleton
public class LocalisationProvider {

    public static final String ENGLISH = "English";
    public static final String DEUTSCH = "Deutsch";
    public static final String ESPANOL = "Espa\u00F1ol";
    public static final String BOKMAL = "Bokm\u00E5l";
    public static final String NEDERLANDS = "Nederlands";
    public static final String TRANSLATIONS_DIRECTORY = "src/main/resources/translations/";

    public JSONObject readLanguageFile(String language) throws IOException, ParseException {

        JSONParser jsonParser = new JSONParser();

        String fileName = TRANSLATIONS_DIRECTORY;

        switch (language) {
            case ENGLISH:
                fileName += "en.json";
                break;
            case DEUTSCH:
                fileName += "de.json";
                break;
            case ESPANOL:
                fileName += "es.json";
                break;
            case BOKMAL:
                fileName += "nb.json";
                break;
            case NEDERLANDS:
                fileName += "dut.json";
                break;
            default:
                fileName += "en.json";
                break;
        }

        FileReader reader = new FileReader(fileName);

        return (JSONObject)jsonParser.parse(reader);
    }
}

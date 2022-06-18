package controllers;

import com.google.inject.Inject;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.testng.annotations.*;

import java.io.IOException;

@Guice
public class LocalisationProviderTest {
    @Inject
    LocalisationProvider localisationProvider;
    @Test
    public void test() throws IOException, ParseException {
        JSONObject languageFile = localisationProvider.readLanguageFile(LocalisationProvider.ENGLISH);
        System.out.printf(languageFile.toString());
    }
}
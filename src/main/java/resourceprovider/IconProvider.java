package resourceprovider;

import javafx.scene.image.Image;

import java.io.File;

public class IconProvider {
    static final String  ICONS_DIRECTORY =  "src/main/resources/icons/";
    public static Image getImage(String filename){
        return new Image(new File(ICONS_DIRECTORY+filename).toURI().toString());
    }
}

import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import javafx.fxml.FXMLLoader;
import models.TransactionModelFactory;
import provider.FXMLLoaderProvider;
import resourceprovider.Utilities;

import java.util.ResourceBundle;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FXMLLoader.class).toProvider(FXMLLoaderProvider.class);

        bind(ResourceBundle.class).annotatedWith(Names.named("i18n-resources"))
                .toInstance(ResourceBundle.getBundle(Main.class.getName()));
        bind(TransactionModelFactory.class);
        bind(Utilities.class);
    }
}

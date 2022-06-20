import com.google.inject.AbstractModule;
import com.google.inject.name.Names;
import controllers.*;
import javafx.fxml.FXMLLoader;
import models.TransactionModel;
import models.TransactionModelFactory;
import provider.FXMLLoaderProvider;
import resourceprovider.LocalisationProvider;
import services.ExportService;
import views.DisclaimerView;
import views.ImportDataView;
import views.MainView;

import java.util.ResourceBundle;

public class AppModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(FXMLLoader.class).toProvider(FXMLLoaderProvider.class);

        bind(ResourceBundle.class).annotatedWith(Names.named("i18n-resources"))
                .toInstance(ResourceBundle.getBundle(Main.class.getName()));
        bind(TransactionModelFactory.class);
    }
}

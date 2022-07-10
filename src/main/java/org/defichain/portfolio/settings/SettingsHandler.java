package org.defichain.portfolio.settings;

import com.google.inject.Inject;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class SettingsHandler {
    private final String SETTING_FILE_NAME = "settings.properties";
    private final String settingsFilePath;

    public Settings getSettings() {
        return settings;
    }

    @Inject
    private Settings settings;

    public SettingsHandler(String defiPortfolioHomeDir) {
        this.settingsFilePath = defiPortfolioHomeDir + SETTING_FILE_NAME;
    }

    public Properties loadSettings() throws IOException {
        File f = new File(settingsFilePath);
        if (f.exists() && !f.isDirectory()) {
            File configFile = new File(settingsFilePath);
            Properties configProps = new Properties();
            try (FileInputStream i = new FileInputStream(configFile)) {
                configProps.load(i);
            }
            return configProps;
//            try {
//                if(configProps.getProperty("SelectedLanguage").contains("Espa")) {
//                    this.selectedLanguage.setValue("Espa\u00F1ol");
//                }
//                else if(configProps.getProperty("SelectedLanguage").contains("Bokm")){
//                    this.selectedLanguage.setValue("Bokm\u00E5l");
//                }else{
//                    this.selectedLanguage.setValue(configProps.getProperty("SelectedLanguage"));
//                }
//                this.selectedFiatCurrency.setValue(configProps.getProperty("SelectedFiatCurrency"));
//                this.selectedDecimal.setValue(configProps.getProperty("SelectedDecimal"));
//                this.selectedSeparator.setValue(configProps.getProperty("SelectedSeperator"));
//                this.selectedCoin.setValue(configProps.getProperty("SelectedCoin"));
//                this.selectedPlotCurrency.setValue(configProps.getProperty("SelectedPlotCurrency"));
//                this.selectedStyleMode.setValue(configProps.getProperty("SelectedStyleMode"));
//                this.exportCointracingVariant.setValue(configProps.getProperty("ExportCointrackinVariante"));
//                this.exportCSVVariant.setValue(configProps.getProperty("ExportCSVVariante"));
//                this.exportFrom.setValue(LocalDate.parse(configProps.getProperty("ExportFrom")));
//                this.exportTo.setValue(LocalDate.parse(LocalDate.now().toString()));
//                this.dateFrom.setValue(LocalDate.parse(configProps.getProperty("SelectedDate")));
//                if (!configProps.getProperty("LastUsedExportPath").equals(""))
//                    this.lastExportPath = configProps.getProperty("LastUsedExportPath");
//                this.showDisclaim = configProps.getProperty("ShowDisclaim").equals("true");
//                this.selectedLaunchDefid = configProps.getProperty("SelectedLaunchDefid").equals("true");
//                if (configProps.getProperty("SelectedLaunchSync") != null) {
//                    this.selectedLaunchSync = configProps.getProperty("SelectedLaunchSync").equals("true");
//                } else {
//                    this.selectedLaunchSync = false;
//                }
//                if(configProps.getProperty("SelectedSource") !=null){
//                    this.selectedSource.setValue(configProps.getProperty("SelectedSource"));
//                }else{
//                    this.selectedSource.setValue("Active Wallet");
//                }
//                if(configProps.getProperty("LastUpdate") !=null){
//                    this.lastUpdate.setValue(configProps.getProperty("LastUpdate"));
//                }else{
//                    this.lastUpdate.setValue("-");
//                }
//                if(configProps.getProperty("DefaultDataSource") !=null){
//                    this.selectedDefaultUpdateSource.setValue(configProps.getProperty("DefaultDataSource"));
//                }else{
//                    this.selectedDefaultUpdateSource.setValue("Show options");
//                }
//                this.showMissingTransaction = configProps.getProperty("MissingTransaction").equals("true");
//                this.lastWalletCSVImportPath = configProps.getProperty("LastWalletCSVImportPath");
//            } catch (Exception e) {
//                logger.warning("Exception occurred: " + e);
//                saveSettings();
//            }
        }
        return null;
    }
}
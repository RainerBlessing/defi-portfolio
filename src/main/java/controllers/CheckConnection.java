package controllers;

import com.google.inject.Inject;
import javafx.application.Platform;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TimerTask;

public class CheckConnection extends TimerTask {
    private final SettingsController settingsController;
    private final MainViewController mainViewController;

    @Inject
    public CheckConnection(MainViewController mainViewController,SettingsController settingsController) {
        this.mainViewController = mainViewController;
        this.settingsController = settingsController;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
                    if (settingsController.runCheckTimer) {
                        if (this.mainViewController.transactionController.checkRpc()) {
                            if (settingsController.errorBouncer < 30) {
                                System.out.println("Try to connect to Server");
                                settingsController.errorBouncer++;
                            } else {
                                settingsController.runCheckTimer = false;
                                settingsController.errorBouncer = 0;
                                File file = new File(settingsController.DEFI_PORTFOLIO_HOME + "update.portfolio");
                                if (file.exists()) file.delete();
                            }
                        } else {
                            if (!settingsController.selectedLaunchSync) {
                                settingsController.runCheckTimer = false;
                                settingsController.errorBouncer = 0;
                                this.mainViewController.btnUpdateDatabasePressed();
                                settingsController.updatePython = true;
                            }else{
                                int currentBlockCount = Integer.parseInt(this.mainViewController.transactionController.getBlockCountRpc());
                                int maxBlockCount = Integer.parseInt(this.mainViewController.transactionController.getBlockCount());
                                double progress = Math.floor(((double)currentBlockCount*10000.0/(double)maxBlockCount))/100.0;
                                if(progress > 100)progress=100;
                                if(currentBlockCount>maxBlockCount)currentBlockCount=maxBlockCount;
                                try {
                                    FileWriter myWriter = new FileWriter(settingsController.DEFI_PORTFOLIO_HOME + "update.portfolio");
                                    myWriter.write("<html><body>"+settingsController.getTranslationValue("SyncData") + progress+ "% <br>("+currentBlockCount+"/"+maxBlockCount+")</body></html>");
                                    myWriter.close();
                                } catch (IOException e) {
                                    settingsController.logger.warning("Could not write to update.portfolio."); }


                                settingsController.selectedLaunchSync = currentBlockCount<maxBlockCount;

                                if(!settingsController.selectedLaunchSync){
                                    settingsController.runCheckTimer = false;
                                    settingsController.errorBouncer = 0;
                                    this.mainViewController.btnUpdateDatabasePressed();
                                    settingsController.updatePython = true;
                                }

                            }
                        }
                    }else if(settingsController.updatePython){
//                        this.mainViewController.plotUpdate(this.mainViewController.mainView.tabPane.getSelectionModel().getSelectedItem().getId());
                        File file = new File(settingsController.DEFI_PORTFOLIO_HOME + "pythonUpdate.portfolio");
                        if (!file.exists()){
                            settingsController.updatePython = false;
                            mainViewController.finishedUpdate();
                        }
                    }
                }
        );
    }
}

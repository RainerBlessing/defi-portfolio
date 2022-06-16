package controllers;

import javafx.application.Platform;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TimerTask;

public class CheckConnection extends TimerTask {
    final MainViewController mainViewController;

    public CheckConnection(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
                    if (SettingsController.getInstance().runCheckTimer) {
                        if (this.mainViewController.transactionController.checkRpc()) {
                            if (SettingsController.getInstance().errorBouncer < 30) {
                                System.out.println("Try to connect to Server");
                                SettingsController.getInstance().errorBouncer++;
                            } else {
                                SettingsController.getInstance().runCheckTimer = false;
                                SettingsController.getInstance().errorBouncer = 0;
                                File file = new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + "update.portfolio");
                                if (file.exists()) file.delete();
                            }
                        } else {
                            if (!this.mainViewController.settingsController.selectedLaunchSync) {
                                SettingsController.getInstance().runCheckTimer = false;
                                SettingsController.getInstance().errorBouncer = 0;
                                this.mainViewController.btnUpdateDatabasePressed();
                                SettingsController.getInstance().updatePython = true;
                            }else{
                                int currentBlockCount = Integer.parseInt(this.mainViewController.transactionController.getBlockCountRpc());
                                int maxBlockCount = Integer.parseInt(this.mainViewController.transactionController.getBlockCount());
                                double progress = Math.floor(((double)currentBlockCount*10000.0/(double)maxBlockCount))/100.0;
                                if(progress > 100)progress=100;
                                if(currentBlockCount>maxBlockCount)currentBlockCount=maxBlockCount;
                                try {
                                    FileWriter myWriter = new FileWriter(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + "update.portfolio");
                                    myWriter.write("<html><body>"+SettingsController.getInstance().translationList.getValue().get("SyncData").toString() + progress+ "% <br>("+currentBlockCount+"/"+maxBlockCount+")</body></html>");
                                    myWriter.close();
                                } catch (IOException e) {
                                    SettingsController.getInstance().logger.warning("Could not write to update.portfolio."); }


                                this.mainViewController.settingsController.selectedLaunchSync = currentBlockCount<maxBlockCount;

                                if(!this.mainViewController.settingsController.selectedLaunchSync){
                                    SettingsController.getInstance().runCheckTimer = false;
                                    SettingsController.getInstance().errorBouncer = 0;
                                    this.mainViewController.btnUpdateDatabasePressed();
                                    SettingsController.getInstance().updatePython = true;
                                }

                            }
                        }
                    }else if(SettingsController.getInstance().updatePython){
                        this.mainViewController.plotUpdate(this.mainViewController.mainView.tabPane.getSelectionModel().getSelectedItem().getId());
                        File file = new File(SettingsController.getInstance().DEFI_PORTFOLIO_HOME + "pythonUpdate.portfolio");
                        if (!file.exists()){
                            SettingsController.getInstance().updatePython = false;
                            MainViewController.getInstance().finishedUpdate();
                            TransactionController.getInstance().stopServer();
                        }
                    }
                }
        );
    }
}

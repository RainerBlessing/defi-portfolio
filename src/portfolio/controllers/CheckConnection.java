package portfolio.controllers;

import javafx.application.Platform;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.TimerTask;

public class CheckConnection extends TimerTask {
    MainViewController mainViewController;

    public CheckConnection(MainViewController mainViewController) {
        this.mainViewController = mainViewController;
    }

    @Override
    public void run() {
        Platform.runLater(() -> {
                    if (SettingsController.getInstance().runCheckTimer) {
                        if (!this.mainViewController.transactionController.checkRpc()) {
                            if (SettingsController.getInstance().errorBouncer < 30) {
                                System.out.println("Try to connect to Server");
                                SettingsController.getInstance().errorBouncer++;
                            } else {
                                SettingsController.getInstance().runCheckTimer = false;
                                SettingsController.getInstance().errorBouncer = 0;
                                File file = new File(System.getProperty("user.dir".replace("\\","/")) + "/PortfolioData/" + "update.portfolio");
                                if (file.exists()) file.delete();
                            }
                        } else {
                            if (!this.mainViewController.settingsController.selectedLaunchSync) {
                                SettingsController.getInstance().runCheckTimer = false;
                                SettingsController.getInstance().errorBouncer = 0;
                                this.mainViewController.btnUpdateDatabasePressed();
                                this.mainViewController.plotUpdate(this.mainViewController.mainView.tabPane.getSelectionModel().getSelectedItem().getId());
                                File file = new File(System.getProperty("user.dir") + "/PortfolioData/" + "update.portfolio");
                                if (file.exists()) file.delete();
                            }else{
                                int currentBlockCount = Integer.parseInt(this.mainViewController.transactionController.getBlockCountRpc());
                                int maxBlockCount = Integer.parseInt(this.mainViewController.transactionController.getBlockCount());
                                double progress = Math.floor(((double)currentBlockCount*10000.0/(double)maxBlockCount))/100.0;
                                if(currentBlockCount>maxBlockCount)currentBlockCount=maxBlockCount;
                                if(SettingsController.getInstance().getPlatform().equals("mac")){
                                    try {
                                        FileWriter myWriter = new FileWriter(System.getProperty("user.dir") + "/PortfolioData/"+"update.portfolio");
                                        myWriter.write("<html><body>"+SettingsController.getInstance().translationList.getValue().get("SyncData").toString() + progress+ "% <br>("+currentBlockCount+"/"+maxBlockCount+")</body></html>");
                                        myWriter.close();
                                    } catch (IOException e) {
                                        SettingsController.getInstance().logger.warning("Could not write to update.portfolio."); }
                                }else{
                                    TransactionController.getInstance().jl.setText("<html><body>"+SettingsController.getInstance().translationList.getValue().get("SyncData").toString() + progress+ "% <br>("+currentBlockCount+"/"+maxBlockCount+")</body></html>");
                                }

                                this.mainViewController.settingsController.selectedLaunchSync = currentBlockCount<maxBlockCount;

                                if(!this.mainViewController.settingsController.selectedLaunchSync){
                                    SettingsController.getInstance().runCheckTimer = false;
                                    SettingsController.getInstance().errorBouncer = 0;
                                    this.mainViewController.btnUpdateDatabasePressed();
                                    this.mainViewController.plotUpdate(this.mainViewController.mainView.tabPane.getSelectionModel().getSelectedItem().getId());
                                    File file = new File(System.getProperty("user.dir") + "/PortfolioData/" + "update.portfolio");
                                    if (file.exists()) file.delete();
                                }

                            }
                        }
                    }
                }
        );
    }
}

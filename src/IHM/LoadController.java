package IHM;

import com.jfoenix.controls.JFXButton;
import data.Processus;
import factory.XMLReader;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by amelie on 13/03/17.
 */
public class LoadController {

    private final static Logger logger = LoggerUtility.getLogger(RootController.class);

    public Label errorLabel;

    @FXML
    private Label fileChosen;

    private String pathFile;

    private MainApp mainApp;

    /**
     * @param mainApp is the new main app
     */
    public synchronized void setMain(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Create a file choose to pick an XML file
     */
    public void chooseAction() {
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("XML files (*.xml)", "*.xml");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(this.mainApp.getPrimaryStage());
        fileChosen.setText(file.toString());
        logger.info("The XML file chosen is : " + file.toString());
        pathFile = file.getAbsolutePath();

    }

    /**
     * Load the XML file
     */
    public void loadAction() {
        if(pathFile!=null) {
            if (!pathFile.equals("")) {
                try {
                    XMLReader xmlReader = new XMLReader(pathFile);
                    xmlReader.buildData();
                    this.mainApp.setListOfProcess(xmlReader.getListOfProcess());
                    this.mainApp.setDefaultDimension(this.mainApp.getHeight(), this.mainApp.getWidth());
                } catch (NullPointerException e) {
                    errorLabel.setText("Une erreur est survenue dans le fichier XML merci de réessayer.");
                    logger.error("Error in the loadAction : " + e);
                }
                try {
                    this.mainApp.showListView();
                } catch (IOException e) {
                    logger.error("Error in the loadAction : " + e);
                }
            }
        }
    }

    /**
     * Goes back to the menu
     */
    public void backAction() {
        logger.info("Goes back to the menu. ");
        this.mainApp.setDefaultDimension(this.mainApp.getHeight(), this.mainApp.getWidth());
        this.mainApp.showMenuOverview();
    }
}

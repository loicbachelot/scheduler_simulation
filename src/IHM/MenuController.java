package IHM;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.stage.Stage;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.io.IOException;

public class MenuController {

    private final static Logger logger = LoggerUtility.getLogger(RootController.class);

    @FXML
    private JFXButton quitter;
    @FXML
    private JFXButton charger;

    private MainApp mainApp;

    public MenuController() {
    }


    @FXML
    /**
     * implementation of the close button
     */
    public void closeButtonAction() {
        logger.info("Simulation closed.");
        // get a handle to the stage
        Stage stage = (Stage) quitter.getScene().getWindow();
        // do what you have to do
        stage.close();
        System.exit(0);

    }

    @FXML
    /**
     * implementation of the creation button
     */
    public void createButtonAction() {
        try {
            this.mainApp.showCreationView();
        } catch (IOException e) {
            logger.error("createButtonAction error", e);
            e.printStackTrace();
        }
        logger.info("Creation of a new process.");
    }

    @FXML
    /**
     * implementation of the load button
     */
    public void loadButtonAction() {

        try {
            this.mainApp.showLoadView();
        } catch (IOException e) {
            logger.error("loadButtonAction error", e);
            e.printStackTrace();
        }
        logger.info("Loading a new XML file.");
    }

    @FXML
    /**
     * implementation of the vision button
     */
    public void seeListButtonAction() {
        try {
            this.mainApp.showListView();
        } catch (IOException e) {
            logger.error("listProcessButtonAction error", e);
            e.printStackTrace();
        }
        logger.info("Display the list of process.");
    }


    /**
     * @param mainApp is setting up
     */
    public void setMain(MainApp mainApp) {
        this.mainApp = mainApp;
    }

}

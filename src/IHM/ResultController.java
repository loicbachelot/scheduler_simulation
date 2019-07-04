package IHM;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.text.DecimalFormat;

/**
 * Created by amelie on 19/03/17.
 */
public class ResultController {

    private final static Logger logger = LoggerUtility.getLogger(RootController.class);

    private MainApp mainApp;

    @FXML
    private Label occupacyTime;
    @FXML
    private Label restitutionTime;
    @FXML
    private Label occupancyRate;
    @FXML
    private Label waitingTime;


    /**
     * @param mainApp is the new main app
     *                this method also initialize the label
     */
    public synchronized void setMain(MainApp mainApp) {
        this.mainApp = mainApp;
        occupacyTime.setText(String.valueOf(mainApp.getCurrentoccupancyTime()));
        restitutionTime.setText(String.valueOf(mainApp.getCurrentAverageReturnTime()));
        double d = mainApp.getCurrentoccupancyRate();
        DecimalFormat df = new DecimalFormat("#.##");
        occupancyRate.setText(String.valueOf(df.format(d)) + "%");
        waitingTime.setText(String.valueOf(mainApp.getCurrentAverageWaitingTime()));
    }

    /**
     * Goes back to the choice list
     */
    public void choiceAction() {
        logger.info("Goes back to the choice list. ");
        this.mainApp.setDefaultDimension(this.mainApp.getHeight(), this.mainApp.getWidth());
        try {
            this.mainApp.showTestView();
        } catch (IOException e) {
            logger.error("Error in the choiceAction : " + e);
        }
    }

}

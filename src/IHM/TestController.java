package IHM;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.TextInputDialog;
import javafx.stage.StageStyle;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.util.Optional;

/**
 * Created by amelie on 13/03/17.
 */
public class TestController {

    private final static Logger logger = LoggerUtility.getLogger(RootController.class);

    private MainApp mainApp;

    /**
     * @param mainApp is the new main app
     */
    public synchronized void setMain(MainApp mainApp) {
        this.mainApp = mainApp;
    }

    /**
     * Goes back to the menu
     */
    public void backAction() {
        logger.info("Goes back to the menu. ");
        this.mainApp.setDefaultDimension(this.mainApp.getHeight(), this.mainApp.getWidth());
        this.mainApp.showMenuOverview();
    }


    /**
     * Start testing the roundrobin
     */
    public void launchRoundRobin() {
        logger.info("Launch the roundRobin algo");
        int answer = -1;
        while (answer == -1) {
            answer = FxDialogs.showTextInput("Choisissez une valeur de quantum :", "Votre valeur : ", 0);

        }
        if (answer != 0)
            this.mainApp.showResultView(1, answer);
    }

    /**
     * start testing the SJF
     */
    public void launchSJF() {
        logger.info("Launch the SJF algo");
        this.mainApp.showResultView(2, 0);
    }

    /**
     * start testing the SRJF
     */
    public void launchSRJF() {
        logger.info("Launch the SRJF algo");
        this.mainApp.showResultView(3, 0);
    }

    /**
     * start testing the FIFO
     */
    public void launchFIFO() {
        logger.info("Launch the FIFO algo");
        this.mainApp.showResultView(4, 0);
    }

    /**
     * Class used to create a dialog
     */
    private static class FxDialogs {

        /**
         *
         * @param title is the title of the frame
         * @param message is the message seen on the frame
         * @param defaultValue is the default value of the quantum
         * @return the value that the user put
         */
        public static int showTextInput(String title, String message, int defaultValue) {
            TextInputDialog dialog = new TextInputDialog(String.valueOf(defaultValue));
            dialog.initStyle(StageStyle.UTILITY);
            dialog.setTitle("Choisir un quantum");
            dialog.setHeaderText(title);
            dialog.setContentText(message);

            Optional<String> result = dialog.showAndWait();
            if (result.isPresent()) {
                try {
                    return Integer.parseInt(result.get());
                } catch (NumberFormatException nfe) {
                    logger.error("Number required " + nfe);
                    return -1;
                }
            } else if (result.isPresent() && result.get().equals("")) {
                return -1;
            } else {
                return 0;
            }
        }

    }
}

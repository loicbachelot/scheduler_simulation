package IHM;

import log.LoggerUtility;
import org.apache.log4j.Logger;

public class RootController {

    private final static Logger logger = LoggerUtility.getLogger(RootController.class);


    private MainApp mainApp;

    public RootController() {
    }

    /**
     * @param mainApp init the mainApp
     */
    public void setMain(MainApp mainApp) {
        logger.info("App init OK !");
        this.mainApp = mainApp;
    }

}

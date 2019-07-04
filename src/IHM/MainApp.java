package IHM;


import core.Scheduler;
import data.Processus;
import factory.XMLReader;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

public class MainApp extends Application {

    private final static Logger logger = LoggerUtility.getLogger(MainApp.class);
    private Stage primaryStage;
    private BorderPane rootLayout;
    private RootController rootController;
    private Callable currentScreen;
    private Callable previousScreen;

    private ArrayList<Processus> listOfProcess;

    private Scheduler scheduler;

    private double currentOccupancyRate;
    private int currentOccupancyTime;
    private double currentAverageReturnTime;
    private double currentAverageWaitingTime;


    private double width;
    private double height;

    public static void main(String[] args) {
        launch(args);
    }


    /**
     * Starting method
     *
     * @param primaryStage stage were to start the application
     */
    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Projet d'OS");
        initRootLayout();
        showMenuOverview();
    }

    /**
     * Initializes the root layout.
     */
    public void initRootLayout() {
        try {

            // Load root layout from fxml file.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(MainApp.class.getResource("res/fxml/RootLayout.fxml"));
            rootLayout = loader.load();

            setRootController(loader.getController());
            getRootController().setMain(this);

            // Show the scene containing the root layout.
            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);
            primaryStage.show();
            logger.info("Scene init OK");
            width = primaryStage.getWidth();
            height = primaryStage.getHeight();
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("initRootLayout error : ", e);
        }
    }

    /**
     * Shows the person overview inside the root layout.
     */
    public void showMenuOverview() {
        try {
            // Load person overview.
            menuInit();

        } catch (IOException e) {
            e.printStackTrace();
            logger.error("showMenuOverview error", e);
        }
    }

    /**
     * Display the menu inside the root layout
     *
     * @throws IOException if the .jxml is not opened
     */
    public void menuInit() throws IOException {

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("res/fxml/Menu.fxml"));

        AnchorPane Overview = loader.load();

        // Set person overview into the center of root layout.
        rootLayout.setCenter(Overview);

        // Give the controller access to the main app.
        MenuController controller = loader.getController();
        controller.setMain(this);
        logger.info("Menu init OK");
    }


    /**
     * @return Stage primaryStage of the app
     */
    public Stage getPrimaryStage() {
        return primaryStage;
    }


    /**
     * @return RootController root controller of the app
     * @see RootController
     */
    public RootController getRootController() {
        return rootController;
    }

    /**
     * setter of the rootController
     *
     * @param rootController RootController to set
     * @see RootController
     */
    public void setRootController(RootController rootController) {
        this.rootController = rootController;
    }

    /**
     * set the creation view to main screen
     */
    public void showCreationView() throws IOException {

        primaryStage.setWidth(1100);
        primaryStage.setHeight(700);

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("res/fxml/Creation.fxml"));
        AnchorPane Overview = loader.load();

        // Set person overview into the center of root layout.
        rootLayout.setCenter(Overview);

        // Give the controller access to the main app.
        CreationController controller = loader.getController();
        controller.setMain(this);

    }

    /**
     * set the load view to main screen
     */
    public void showLoadView() throws IOException {

        primaryStage.setWidth(1000);
        primaryStage.setHeight(500);

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("res/fxml/Load.fxml"));
        AnchorPane Overview = loader.load();

        // Set person overview into the center of root layout.
        rootLayout.setCenter(Overview);

        // Give the controller access to the main app.
        LoadController controller = loader.getController();
        controller.setMain(this);

    }

    /**
     * set the test view to main screen
     */
    public void showTestView() throws IOException {

        primaryStage.setHeight(500);

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("res/fxml/Test.fxml"));
        AnchorPane Overview = loader.load();

        // Set person overview into the center of root layout.
        rootLayout.setCenter(Overview);

        // Give the controller access to the main app.
        TestController controller = loader.getController();
        controller.setMain(this);
    }

    /**
     * set the list of process view to main screen
     */
    public void showListView() throws IOException {

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("res/fxml/List.fxml"));
        AnchorPane Overview = loader.load();

        // Set person overview into the center of root layout.
        rootLayout.setCenter(Overview);

        // Give the controller access to the main app.
        ListController controller = loader.getController();
        controller.setMain(this);
    }

    /**
     * @return the width of the menu
     */
    public double getWidth() {
        return width;
    }

    /**
     * @return the height of the menu
     */
    public double getHeight() {
        return height;
    }


    /**
     * @param height is the default height
     * @param width  is the default width
     *               <p>
     *               It's used to set the deault dimension of the frame
     */
    public void setDefaultDimension(double height, double width) {
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);
    }

    /**
     * @return the list of process
     */
    public ArrayList<Processus> getListOfProcess() {
        return listOfProcess;
    }

    /**
     * @param listOfProcess is the new list of process
     */
    public void setListOfProcess(ArrayList<Processus> listOfProcess) {
        this.listOfProcess = listOfProcess;
    }

    public void showResultView(int algo, int quantum) {
        primaryStage.setWidth(1000);
        primaryStage.setHeight(1000);
        scheduler = new Scheduler(listOfProcess);
        switch (algo) {
            case 1: //Case RoundRobin
                scheduler.runRoundRobin(quantum);
                break;
            case 2: //Case SJF
                scheduler.runSJF();
                break;
            case 3: //Case SRJF
                scheduler.runSRJF();
                break;
            case 4: //Case FIFO
                scheduler.runFIFO();
                break;
        }
        currentAverageReturnTime = scheduler.getcurrentAverageReturnTime();
        currentAverageWaitingTime = scheduler.getcurrentAverageWaitingTime();
        currentOccupancyRate = scheduler.getcurrentOccupancyRate();
        currentOccupancyTime = scheduler.getcurrentOccupancyTime();

        FXMLLoader loader = new FXMLLoader(MainApp.class.getResource("res/fxml/Result.fxml"));
        AnchorPane Overview = null;
        try {
            Overview = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Set person overview into the center of root layout.
        rootLayout.setCenter(Overview);

        // Give the controller access to the main app.
        ResultController controller = loader.getController();
        controller.setMain(this);
    }

    public double getCurrentoccupancyRate() {
        return currentOccupancyRate;
    }

    public int getCurrentoccupancyTime() {
        return currentOccupancyTime;
    }

    public double getCurrentAverageReturnTime() {
        return currentAverageReturnTime;
    }

    public double getCurrentAverageWaitingTime() {
        return currentAverageWaitingTime;
    }
}

package IHM;

import com.jfoenix.controls.JFXButton;
import data.Processus;
import javafx.beans.InvalidationListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.util.Callback;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import javax.swing.*;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.*;

/**
 * Created by amelie on 14/03/17.
 */
public class ListController {

    private final static Logger logger = LoggerUtility.getLogger(RootController.class);

    @FXML
    private Label list;

    @FXML
    private GridPane grid;

    @FXML
    private TableView tableView;

    private MainApp mainApp;

    /**
     * @param mainApp is the new main app that contains the list of current processes
     */
    public synchronized void setMain(MainApp mainApp) {

        this.mainApp = mainApp;
        ArrayList<Processus> allProcesses = this.mainApp.getListOfProcess(); //Array list of all processes
        grid = (GridPane) list.getParent(); //get the grid where the table is showed

        int numberMaxUC = 0; //Number of UC so represent the number max of colums
        ArrayList<ArrayList<String>> staffArray = new ArrayList<>(); //Array filed with all the data to display
        ArrayList<String> columnTitle = new ArrayList<>(); //Array used to enter the first titles
        columnTitle.add("Name");
        columnTitle.add("Time");
        staffArray.add(columnTitle);
        if (allProcesses != null) {

            if (allProcesses.size() != 0) { //if the list of process is not empty display the table else display a label
                for (Processus currentProcess : allProcesses) { //flow all processes
                    ArrayList<String> processData = new ArrayList<>(); //create an array field with the current process data
                    processData.add(currentProcess.getName());
                    processData.add(String.valueOf(currentProcess.getTime()));
                    for (int a = 0; a < Math.max(currentProcess.getlistOfResource().size(), currentProcess.getListOfInOut().size()) * 2; a++) {
                        if (currentProcess.getlistOfResource().get(a) == null)
                            processData.add("0");
                        else
                            processData.add(String.valueOf(currentProcess.getlistOfResource().get(a)));
                        if (currentProcess.getListOfInOut().get(a) == null)
                            processData.add("0");
                        else
                            processData.add(String.valueOf(currentProcess.getListOfInOut().get(a)));
                    }
                    staffArray.add(processData); //adding the previous array to the biggest array to contains all data to display
                    if (currentProcess.getlistOfResource().size() > numberMaxUC || currentProcess.getListOfInOut().size() > numberMaxUC) //condition to count the number of columns
                        numberMaxUC = Math.max(currentProcess.getlistOfResource().size(), currentProcess.getListOfInOut().size());
                }
                for (int index = 0; index < numberMaxUC; index++) {
                    columnTitle.add("UC" + index);
                    columnTitle.add("ES" + index);
                }
                ObservableList<ArrayList> data = FXCollections.observableArrayList();
                data.addAll(staffArray);
                data.remove(0);
                TableView<ArrayList> table = new TableView<>();
                for (int i = 0; i < staffArray.get(0).size(); i++) {
                    TableColumn tc = new TableColumn(staffArray.get(0).get(i));
                    final int colNo = i;
                    tc.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<ArrayList, String>, ObservableValue<String>>() {
                        @Override
                        public ObservableValue<String> call(TableColumn.CellDataFeatures<ArrayList, String> p) {
                            if (p.getValue().size() > colNo)
                                return new SimpleStringProperty((String) p.getValue().get(colNo));
                            else {
                                return new SimpleStringProperty("");
                            }
                        }
                    });
                    tc.setPrefWidth(90);
                    table.getColumns().add(tc);
                }
                table.setItems(data);
                tableView = table;
                grid.getChildren().remove(list);
                grid.getChildren().add(table);
            }
        }
    }

    /**
     * Delete a process from the table
     */
    public void suppAction() {
        if (tableView.getItems().size() != 0) { //if there is a table
            int selectedIndex = tableView.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                ArrayList<Processus> allProcesses = this.mainApp.getListOfProcess();
                ArrayList elementToSupp = (ArrayList) tableView.getItems().get(selectedIndex);
                for (Processus currentProcess : allProcesses) {
                    if (currentProcess.getName().equals(elementToSupp.get(0))) { //flow the list and remove the process with the same name as selected
                        allProcesses.remove(currentProcess);
                        break;
                    }
                }
                tableView.getItems().remove(selectedIndex);
                this.mainApp.setListOfProcess(allProcesses);
            }
        }
        if (tableView.getItems().size() == 0) { //if the list is empty
            list.setText("La liste est vide");
            grid.getChildren().remove(tableView);
            if (!grid.getChildren().contains(list))
                grid.getChildren().add(list);
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

    /**
     * Launching the algos
     */
    public void launchAlgoAction() {
        if (this.mainApp.getListOfProcess() != null) {
            if (this.mainApp.getListOfProcess().size() != 0) {
                logger.info("Goes into the launch view. ");
                this.mainApp.setDefaultDimension(this.mainApp.getHeight(), this.mainApp.getWidth());
                try {
                    this.mainApp.showTestView();
                } catch (IOException e) {
                    logger.error("Error in the launchAlgoAction : " + e);
                    e.printStackTrace();
                }
            }
        }
    }

}

package IHM;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import data.Processus;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.WritableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Callback;
import log.LoggerUtility;
import org.apache.log4j.Logger;
import org.jfree.util.StringUtils;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by amelie on 13/03/17.
 */
public class CreationController {


    private final static Logger logger = LoggerUtility.getLogger(RootController.class);

    @FXML
    private TableView table;

    private MainApp mainApp;

    private int numberOfUC;

    private int indexData;

    @FXML
    private JFXTextField Name;

    @FXML
    private JFXTextField Time;

    @FXML
    private Label error;

    private ObservableList<ArrayList> data = FXCollections.observableArrayList();

    /**
     * @param mainApp is the new main app
     *                this method also initialize the table
     */
    public synchronized void setMain(MainApp mainApp) {
        this.mainApp = mainApp;
        numberOfUC = 0;
        indexData = 0;
        addAction();
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
     * method that parse the table and create a processus from the data
     */
    public void createProcessus() throws IOException {
        HashMap<Integer, Integer> listOfInOut = new HashMap<>();
        HashMap<Integer, Integer> listOfUC = new HashMap<>();
        int time = -1;
        int indexTMP = 0;
        ArrayList<String> tmp = (ArrayList<String>) table.getItems().get(0);
        for (int index = 0; index < tmp.size()/2; index++) {
            try {
                listOfUC.put(index, Integer.valueOf(tmp.get(indexTMP)));
            } catch (NumberFormatException nfe) {
                time = -1;
            }
            indexTMP++;
            try {
                listOfInOut.put(index, Integer.valueOf(tmp.get(indexTMP)));
            } catch (NumberFormatException nfe) {
                time = -1;
            }
            indexTMP++;
        }
        String name = Name.getText();
        if (!Time.getText().isEmpty())
            try {
                time = Integer.parseInt(Time.getText());
            } catch (NumberFormatException nfe) {
                time = -1;
            }
        if (name.isEmpty() || time < 0 || listOfInOut.size()==0|| listOfUC.size()==0) {
            logger.error("Error in the fields");
            error.setText("Erreur dans les champs veuillez remplir correctement svp");
        } else {
            Processus processus = new Processus(name, time, listOfInOut, listOfUC);
            ArrayList tmpListOfProcess = mainApp.getListOfProcess();
            tmpListOfProcess.add(processus);
            mainApp.setListOfProcess(tmpListOfProcess);
            logger.info("Goes to the list.");
            this.mainApp.setDefaultDimension(this.mainApp.getHeight(), this.mainApp.getWidth());
            this.mainApp.showListView();
        }
    }

    public void addAction() {
        ArrayList<ArrayList<String>> staffArray = new ArrayList<>(); //Array filed with all the data to display
        ArrayList<String> columnTitle = new ArrayList<>(); //Array used to enter the first titles
        columnTitle.add("UC" + (numberOfUC));
        columnTitle.add("ES" + (numberOfUC));
        staffArray.add(columnTitle); //adding the column to the data array
        ArrayList<String> columnAnswer = new ArrayList<>(); //Array used to enter the first titles
        columnAnswer.add("0");
        columnAnswer.add("0");
        staffArray.add(columnAnswer); //adding the column to the data array
        if (data.size() == 0)
            data.addAll(staffArray);
        else {
            ArrayList<String> tmpArray = data.get(0);
            tmpArray.add(staffArray.get(1).get(0));
            tmpArray.add(staffArray.get(1).get(1));
            data.set(0, tmpArray);
        }
        if (data.size() > 1) {
            data.remove(indexData);
        }
        table.setEditable(true); //allow the user to modify the values
        for (int i = 0; i < staffArray.get(0).size(); i++) {
            TableColumn tc = new TableColumn(staffArray.get(0).get(i)); //creating a column
            final int colNo = 2 * numberOfUC + i;
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
            tc.setCellFactory(TextFieldTableCell.<String>forTableColumn()); //allow user to enter values
            tc.setEditable(true);
            tc.setOnEditCommit(new EventHandler<TableColumn.CellEditEvent<String, String>>() {
                @Override
                public void handle(TableColumn.CellEditEvent<String, String> event) {
                    String newValue = event.getNewValue();
                    data.get(0).set(colNo, newValue);
                }
            });
            tc.setPrefWidth(90);
            table.getColumns().add(tc);
        }
        table.setItems(data);
        indexData++;
        numberOfUC++;
    }

}


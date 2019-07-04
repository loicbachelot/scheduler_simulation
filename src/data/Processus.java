package data;

import java.util.HashMap;

/**
 * Created by amelie on 11/03/17.
 */

public class Processus {
    private String name; //Name of the process
    private int time; //Time when the process starts
    private HashMap<Integer, Integer> listOfInOut; //regroup the inOut values
    private HashMap<Integer, Integer> listOfResource; //regroupe the ressources values
    private int currentStep; //Number of ressource and inOut done (index of the lists)
    private int currentOrder; //Used in fifo
    private int waitingTime; //Number of unit that the process is on wait
    private int startTime; //Copy of the time when the process starts to save it
    private int totalExecutionTime; //Total execution time is there is no waiting time
    /**
     * @param name            is the name of the process
     * @param time            is twhen the process starts
     * @param listOfInOut     is the list of the in/out phase
     * @param listOfResource is the list of the UC occupation
     */
    public Processus(String name, int time, HashMap<Integer, Integer> listOfInOut, HashMap<Integer, Integer> listOfResource) {
        this.name = name;
        this.time = time;
        this.listOfInOut = listOfInOut;
        this.listOfResource = listOfResource;
        currentStep = 0;
        currentOrder = 0;
        waitingTime = 0;
        startTime = time;
        totalExecutionTime = this.getTotalInOut() + this.getTotalUC();
    }

    /**
     * @return the String that represent the process
     */
    public String toString() {
        return name + " : temps de départ = " + time +
                ", Temps des ES = " + listOfInOut.toString() +
                ", Temps des UC = " + listOfResource.toString() + "\n";
    }

    /**
     * rest the process after a execution
     */
    public void resetProcess() {
        currentStep = 0;
        currentOrder = 0;
        waitingTime = 0;
        time = startTime;
    }

    /**
     * set the waiting time
     *
     * @param time time spent waiting
     */
    public void setWaitingTime(int time) {
        waitingTime += time;
    }

    /**
     * @return the name of the current process
     */
    public String getName() {
        return name;
    }

    /**
     * @param name is the new name of the process
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the time when the process starts
     */
    public int getTime() {
        return time;
    }

    /**
     * @param time is the new time when the process starts
     */
    public void setTime(int time) {
        this.time = time;
    }

    /**
     * @return the list of current in/out phase
     */
    public HashMap<Integer, Integer> getListOfInOut() {
        return listOfInOut;
    }

    /**
     * @param listOfInOut is the new in/out phase
     */
    public void setListOfInOut(HashMap<Integer, Integer> listOfInOut) {
        this.listOfInOut = listOfInOut;
    }

    /**
     * @return the list of ressources taken
     */
    public HashMap<Integer, Integer> getlistOfResource() {
        return listOfResource;
    }

    /**
     * @param listOfResource is the new list of ressources taken
     */
    public void setlistOfResource(HashMap<Integer, Integer> listOfResource) {
        this.listOfResource = listOfResource;
    }

    /**
     * @param i is the index of the ressource to get
     * @return the ressource that matches the index
     */
    public int getRessource(int i) {
        return getlistOfResource().get(i);
    }

    /**
     * @param i        is the index of the ressource that needs to be replace
     * @param newValue is the new value
     */
    public void setRessource(int i, int newValue) {
        listOfResource.replace(i, newValue);
    }

    /**
     * @param i is the index of the requested in out
     * @return the in out that matches the index
     */
    public int getInOut(int i) {
        return getListOfInOut().get(i);
    }

    /**
     * @return the currentStep
     */
    public int getCurrentStep() {
        return currentStep;
    }

    /**
     * @param currentStep is the new currentStep
     */
    public void setCurrentStep(int currentStep) {
        this.currentStep = currentStep;
    }

    /**
     * @return the currentOrder
     */
    public int getCurrentOrder() {
        return currentOrder;
    }

    /**
     * @param currentOrder is the new currentOrder
     */
    public void setCurrentOrder(int currentOrder) {
        this.currentOrder = currentOrder;
    }

    /**
     * @return the waiting time of the process
     */
    public int getWaitingTime() {
        return waitingTime;
    }

    /**
     * @return the total time that the process is executed
     */
    public double getExecutedTime() {
        return totalExecutionTime + this.getWaitingTime();
    }

    /**
     * @return the number of total UC
     */
    private int getTotalUC() {
        int result = 0;
        for (int i = 0; i < getlistOfResource().size(); i++) {//flow all the uc and add to each others
            result += getRessource(i);
        }
        return result;
    }

    /**
     * @return the number of total in out of a process
     */
    private int getTotalInOut() {
        int result = 0;
        for (int i = 0; i < getListOfInOut().size(); i++) { //flow all the in out and add to each others
            result += getInOut(i);
        }
        return result;
    }
}

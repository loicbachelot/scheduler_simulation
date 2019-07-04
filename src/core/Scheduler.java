package core;

import data.Processus;
import factory.XMLReader;
import log.LoggerUtility;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by loic on 11/03/17.
 */
public class Scheduler {
    private ArrayList<Processus> listOfProcess;
    private final static Logger logger = LoggerUtility.getLogger(XMLReader.class);
    private double currentOccupancyRate;
    private int currentOccupancyTime;
    private double currentAverageReturnTime;
    private double currentAverageWaitingTime;

    /**
     * This method create a new scheduler
     *
     * @param listOfProcess is the new listOfProcess
     */
    public Scheduler(ArrayList<Processus> listOfProcess) {
        this.listOfProcess = listOfProcess;
    }

    /**
     * rest the process list
     */
    public void restProcessList() {
        listOfProcess.clear();
    }

    /**
     * add a new process to the process list
     *
     * @param name           name of the process
     * @param time           begining time of the process
     * @param listOfInOut    list of the time spend for the in/out of the process
     * @param listOfResource list of the time of the ressource of the process
     */
    public void addProcess(String name, int time, HashMap<Integer, Integer> listOfInOut, HashMap<Integer, Integer> listOfResource) {
        listOfProcess.add(new Processus(name, time, listOfInOut, listOfResource));
    }

    /**
     * simulated FIFO scheduler with the actual list of process
     */
    public void runFIFO() {

        //init
        ArrayList<Processus> localProcess = new ArrayList<>(); //Creation of a local list of process
        for (Processus cpy : listOfProcess) { //Copy the list of process in the local list
            localProcess.add(cpy);
        }
        int size = listOfProcess.size(); //variable used to save the initial size of the list of process

        Processus executedProc = null; //ExecutedProc is the current process that is being execute
        Processus tmpProc = localProcess.get(0); //The tmpProc is the previous process executed

        //Variable we need to calcul
        double occupancyRate = 0;
        double averageWaitingTime = 0;
        double averageReturnTime = 0;
        int currentTime = 0;
        int occupancyTime = 0;
        int counter = 1;

        //beginning of the algo
        while (!localProcess.isEmpty()) { //As long as there are processes that are not done yet we keep going on

            //Determines the next process that will be executed
            for (Processus proc : localProcess) { //flow all remaining processes
                if (proc.getTime() <= currentTime) { //check if the currentTime of the process is below the currentTime
                    if (localProcess.size() == 1) { //if there is only one process left
                        executedProc = proc;
                    } else if (proc.getCurrentOrder() <= tmpProc.getCurrentOrder()) { //selection of the older process (FIFO selection)
                        executedProc = proc;
                        tmpProc = proc;
                    }
                }
            }

            //Check if the current process is assigned
            if (executedProc != null) {

                //execute the current ressource
                int tmpTime = 0;
                while (executedProc.getRessource(executedProc.getCurrentStep()) > tmpTime) { //As long as there is a resource
                    for (Processus proc : localProcess) {
                        if (proc.getTime() <= currentTime && !proc.equals(executedProc)) { //checking if there is another process waiting and set the new waiting time
                            proc.setWaitingTime(1);
                        }
                    }
                    currentTime++; //currentTime is updating at each loop
                    occupancyTime++; //occupancyTime is updating at each loop
                    tmpTime++;
                }

                executedProc.setCurrentStep(executedProc.getCurrentStep() + 1); //Update the currentStep to the next one (index of the lists of UC and inOut)

                if (executedProc.getCurrentStep() >= executedProc.getlistOfResource().size()) {//if it is the end of the process
                    averageReturnTime += executedProc.getExecutedTime(); //update the average return time by adding the time that the process took
                    localProcess.remove(executedProc); //remove the process from the list of process that needs to run
                }

                if (executedProc.getCurrentStep() <= executedProc.getListOfInOut().size()) { //If there is another inOut, it set the new time
                    executedProc.setTime(executedProc.getInOut(executedProc.getCurrentStep() - 1) + currentTime);
                }

                //put the process at the end of the list (fifo order)
                executedProc.setCurrentOrder(counter);
                counter++;
                executedProc = null;
            } else {
                currentTime++;
            }
        }
        //end of the algo

        //Calculation of the variables
        occupancyRate = ((double) occupancyTime / (double) currentTime) * 100;
        for (Processus proc : listOfProcess) {
            averageWaitingTime += proc.getWaitingTime();
        }
        averageWaitingTime = averageWaitingTime / size;
        averageReturnTime = averageReturnTime / size;

        //Updating the global variables
        currentAverageReturnTime = averageReturnTime;
        logger.trace("Current average return time : " + currentAverageReturnTime);
        currentAverageWaitingTime = averageWaitingTime;
        logger.trace("Current average waiting time : " + currentAverageWaitingTime);
        currentOccupancyRate = occupancyRate;
        logger.trace("Current occupancy rate : " + currentOccupancyRate);
        currentOccupancyTime = occupancyTime;
        logger.trace("Current occupancy time : " + currentOccupancyTime);

        restList(); //reset the list to the origin values so the user can test another algo
    }

    /**
     * simulated round robin scheduler with the actual list of process
     *
     * @param quantum size of the quantum, time allowed in the cpu
     */
    public void runRoundRobin(int quantum) {
        //init
        ArrayList<Processus> localProcess = new ArrayList<>(); //Creation of a local list of process
        for (Processus cpy : listOfProcess) { //Copy the list of process in the local list with new instances of process
            Processus tmp = new Processus(cpy.getName(), cpy.getTime(), (HashMap<Integer, Integer>) cpy.getListOfInOut().clone(), (HashMap<Integer, Integer>) cpy.getlistOfResource().clone());
            localProcess.add(tmp);
        }
        int size = listOfProcess.size(); //variable used to save the initial size of the list of process

        Processus executedProc = null; //ExecutedProc is the current process that is being execute
        Processus tmpProc = localProcess.get(0); //The tmpProc is the previous process executed

        //Variable we need to calcul
        double occupancyRate = 0;
        double averageWaitingTime = 0;
        double averageReturnTime = 0;
        int currentTime = 0;
        int occupancyTime = 0;
        int counter = 0;

        //beginning of the algo
        while (!localProcess.isEmpty()) {
            tmpProc = null;
            for (Processus proc : localProcess) {
                if (proc.getTime() <= currentTime) {
                    if (localProcess.size() == 1 || tmpProc == null) {
                        executedProc = proc;
                        tmpProc = proc;
                    } else if (proc.getCurrentOrder() <= tmpProc.getCurrentOrder()) { //selection of the older process (FIFO selection)
                        executedProc = proc;
                        tmpProc = proc;
                    }
                }
            }
            if (executedProc != null) {
                int tmpTime = 0;
                while (tmpTime < quantum && executedProc.getRessource(executedProc.getCurrentStep()) > 0) {
                    for (Processus proc : localProcess) {
                        if (proc.getTime() <= currentTime && !proc.equals(executedProc)) {//checking if there is another process waiting and set the new waiting time
                            proc.setWaitingTime(1);
                        }
                    }

                    occupancyTime++;
                    currentTime++;
                    tmpTime++;
                    executedProc.setTime(executedProc.getTime() + 1); //set the new process start time
                    executedProc.setRessource(executedProc.getCurrentStep(), executedProc.getRessource(executedProc.getCurrentStep()) - 1);//delete on resource time for the current process on the current step
                }
                //set the availability to the end of the process to the end of the in/out
                if (executedProc.getCurrentStep() < executedProc.getListOfInOut().size() && executedProc.getRessource(executedProc.getCurrentStep()) <= 0) {
                    executedProc.setTime(executedProc.getInOut(executedProc.getCurrentStep()) + currentTime);
                    executedProc.setCurrentStep(executedProc.getCurrentStep() + 1);
                } else if (executedProc.getCurrentStep() >= executedProc.getListOfInOut().size() && executedProc.getRessource(executedProc.getCurrentStep()) <= 0) {
                    averageReturnTime += executedProc.getExecutedTime(); //update the average return time by adding the time that the process took
                    averageWaitingTime += executedProc.getWaitingTime();
                    localProcess.remove(executedProc);
                }
                if (executedProc.getCurrentStep() >= executedProc.getlistOfResource().size()) {//if it is the end of the process
                    averageReturnTime += executedProc.getExecutedTime(); //update the average return time by adding the time that the process took
                    averageWaitingTime += executedProc.getWaitingTime();
                    localProcess.remove(executedProc);
                }
                //put the process at the end of the list
                executedProc.setCurrentOrder(counter);
                counter++;
                executedProc = null;
            } else {
                currentTime++;
            }
        }
        //end of the algo

        //Calculation of the variables
        occupancyRate = ((double) occupancyTime / (double) currentTime) * 100;
        for (Processus proc : listOfProcess) {
            averageWaitingTime += proc.getWaitingTime();
        }
        averageWaitingTime = averageWaitingTime / size;
        averageReturnTime = averageReturnTime / size;

        //Updating the global variables
        currentAverageReturnTime = averageReturnTime;
        logger.trace("Current average return time : " + currentAverageReturnTime);
        currentAverageWaitingTime = averageWaitingTime;
        logger.trace("Current average waiting time : " + currentAverageWaitingTime);
        currentOccupancyRate = occupancyRate;
        logger.trace("Current occupancy rate : " + currentOccupancyRate);
        currentOccupancyTime = occupancyTime;
        logger.trace("Current occupancy time : " + currentOccupancyTime);

        restList(); //reset the list to the origin values so the user can test another algo
    }

    /**
     * simulated SJF scheduler with the actual list of process
     */

    public void runSJF() {

        //init
        ArrayList<Processus> localProcess = new ArrayList<>(); //Creation of a local list of process
        for (Processus cpy : listOfProcess) { //Copy the list of process in the local list
            localProcess.add(cpy);
        }
        int size = listOfProcess.size(); //variable used to save the initial size of the list of process

        Processus executedProc = null; //ExecutedProc is the current process that is being execute
        Processus tmpProc = localProcess.get(0); //The tmpProc is the previous process executed

        //Variable we need to calcul
        double occupancyRate = 0;
        double averageWaitingTime = 0;
        double averageReturnTime = 0;
        int currentTime = 0;
        int occupancyTime = 0;

        //beginning of the algo
        while (!localProcess.isEmpty()) { //As long as there are processes that are not done yet we keep going on

            //Determines the next process that will be executed
            for (Processus proc : localProcess) { //flow all remaining processes
                if (proc.getTime() <= currentTime) { //check if the currentTime of the process is below the currentTime
                    if (localProcess.size() == 1) { //if there is only one process left
                        executedProc = proc;
                    } else if ((proc.getRessource(proc.getCurrentStep()) <= tmpProc.getRessource(tmpProc.getCurrentStep())) || (proc.getTime() < tmpProc.getTime())) {//shortest process selected
                        executedProc = proc;
                        tmpProc = proc;
                    }
                }
            }

            //Check if the current process is assigned
            if (executedProc != null) {

                //execute the current ressource
                int tmpTime = 0;
                while (executedProc.getRessource(executedProc.getCurrentStep()) > tmpTime) { //As long as there is a resource
                    for (Processus proc : localProcess) {
                        if (proc.getTime() <= currentTime && !proc.equals(executedProc)) { //checking if there is another process waiting
                            proc.setWaitingTime(1);
                        }
                    }
                    currentTime++; //currentTime is updating at each loop
                    occupancyTime++; //occupancyTime is updating at each loop
                    tmpTime++;
                }

                executedProc.setCurrentStep(executedProc.getCurrentStep() + 1); //Update the currentStep to the next one (index of the lists of UC and inOut)

                if (executedProc.getCurrentStep() >= executedProc.getlistOfResource().size()) {//if it is the end of the process
                    averageReturnTime += executedProc.getExecutedTime(); //update the average return time by adding the time that the process took
                    localProcess.remove(executedProc); //remove the process from the list of process that needs to run
                }

                if (executedProc.getCurrentStep() <= executedProc.getListOfInOut().size()) { //If there is another inOut, it set the new time
                    executedProc.setTime(executedProc.getInOut(executedProc.getCurrentStep() - 1) + currentTime);
                }

                executedProc = null;
            } else {
                currentTime++;
            }
        }
        //end of the algo

        //Calculation of the variables
        occupancyRate = ((double) occupancyTime / (double) currentTime) * 100;
        for (Processus proc : listOfProcess) {
            averageWaitingTime += proc.getWaitingTime();
        }
        averageWaitingTime = averageWaitingTime / size;
        averageReturnTime = averageReturnTime / size;

        //Updating the global variables
        currentAverageReturnTime = averageReturnTime;
        logger.trace("Current average return time : " + currentAverageReturnTime);
        currentAverageWaitingTime = averageWaitingTime;
        logger.trace("Current average waiting time : " + currentAverageWaitingTime);
        currentOccupancyRate = occupancyRate;
        logger.trace("Current occupancy rate : " + currentOccupancyRate);
        currentOccupancyTime = occupancyTime;
        logger.trace("Current occupancy time : " + currentOccupancyTime);

        restList(); //reset the list to the origin values so the user can test another algo
    }

    /**
     * simulated SRJF scheduler with the actual list of process
     */
    public void runSRJF() {
        //init
        ArrayList<Processus> localProcess = new ArrayList<>(); //Creation of a local list of process
        //localProcess = (ArrayList<Processus>) listOfProcess.clone();
        for (Processus cpy : listOfProcess) { //Copy the list of process in the local list with new instances of process
            Processus tmp = new Processus(cpy.getName(), cpy.getTime(), (HashMap<Integer, Integer>) cpy.getListOfInOut().clone(), (HashMap<Integer, Integer>) cpy.getlistOfResource().clone());
            localProcess.add(tmp);
        }
        int size = listOfProcess.size(); //variable used to save the initial size of the list of process

        Processus executedProc = null; //ExecutedProc is the current process that is being execute
        Processus tmpProc = localProcess.get(0); //The tmpProc is the previous process executed

        //Variable we need to calcul
        double occupancyRate = 0;
        double averageWaitingTime = 0;
        double averageReturnTime = 0;
        int currentTime = 0;
        int occupancyTime = 0;

        //beginning of the algo
        while (!localProcess.isEmpty()) {
            tmpProc = null;
            if (executedProc != null && executedProc.getTime() <= currentTime) {//test if the current executed process is the smallest and is not in in/out operation
                for (Processus proc : localProcess) {//chose the process to execute (the shortest)
                    if (proc.getTime() <= currentTime) {
                        if (proc.getRessource(proc.getCurrentStep()) < executedProc.getRessource(executedProc.getCurrentStep())) {//shortest process selected
                            executedProc = proc;
                        }
                    }
                }
            } else {//same tests but if there is no current process on the UC
                for (Processus proc : localProcess) {
                    if (proc.getTime() <= currentTime) {
                        if (localProcess.size() == 1 || tmpProc == null) {//if there is only only one process left in the list
                            executedProc = proc;
                            tmpProc = proc;
                        } else if (proc.getRessource(proc.getCurrentStep()) <= tmpProc.getRessource(tmpProc.getCurrentStep())) {//shortest process selected
                            executedProc = proc;
                            tmpProc = proc;
                        }
                    }
                }
            }
            if (executedProc != null) {//if there is a process
                //execution of the process over 1 unity of time and then verifying again it's steel the smallest
                for (Processus proc : localProcess) {
                    if (proc.getTime() <= currentTime && !proc.equals(executedProc)) {
                        proc.setWaitingTime(1);//set th waiting time of the others process that could be executed
                    }
                }
                occupancyTime++;
                currentTime++;
                executedProc.setTime(executedProc.getTime() + 1);
                executedProc.setRessource(executedProc.getCurrentStep(), executedProc.getRessource(executedProc.getCurrentStep()) - 1);
                if (executedProc.getRessource(executedProc.getCurrentStep()) <= 0) {
                    if (executedProc.getCurrentStep() < executedProc.getListOfInOut().size()) {
                        executedProc.setTime(currentTime + executedProc.getInOut(executedProc.getCurrentStep()));
                        executedProc.setCurrentStep(executedProc.getCurrentStep() + 1);
                        if (executedProc.getCurrentStep() > executedProc.getlistOfResource().size()) {
                            averageReturnTime += executedProc.getExecutedTime(); //update the average return time by adding the time that the process took
                            averageWaitingTime += executedProc.getWaitingTime();
                            localProcess.remove(executedProc);
                        }
                    } else {
                        averageReturnTime += executedProc.getExecutedTime(); //update the average return time by adding the time that the process took
                        averageWaitingTime += executedProc.getWaitingTime();
                        localProcess.remove(executedProc);
                    }
                    executedProc = null;
                }
            } else {
                currentTime++;
            }
        }
        //Calculation of the variables
        occupancyRate = ((double) occupancyTime / (double) currentTime) * 100;

        averageWaitingTime = averageWaitingTime / size;
        averageReturnTime = averageReturnTime / size;

        //Updating the global variables
        currentAverageReturnTime = averageReturnTime;
        logger.trace("Current average return time : " + currentAverageReturnTime);
        currentAverageWaitingTime = averageWaitingTime;
        logger.trace("Current average waiting time : " + currentAverageWaitingTime);
        currentOccupancyRate = occupancyRate;
        logger.trace("Current occupancy rate : " + currentOccupancyRate);
        currentOccupancyTime = occupancyTime;
        logger.trace("Current occupancy time : " + currentOccupancyTime);

        restList(); //reset the list to the origin values so the user can test another algo

    }

    /**
     * @return the current occupancy rate known thanks to the algo
     */
    public double getcurrentOccupancyRate() {
        return currentOccupancyRate;
    }

    /**
     * @return the current average occupancy time known thanks to the algo
     */
    public int getcurrentOccupancyTime() {
        return currentOccupancyTime;
    }

    /**
     * @return the current average return time known thanks to the algo
     */
    public double getcurrentAverageReturnTime() {
        return currentAverageReturnTime;
    }

    /**
     * @return the current average waiting time known thanks to the algo
     */
    public double getcurrentAverageWaitingTime() {
        return currentAverageWaitingTime;
    }

    /**
     * Reset all process the their default values
     */
    private void restList() {
        for (Processus tmp : listOfProcess) {
            tmp.resetProcess();
        }
    }


}

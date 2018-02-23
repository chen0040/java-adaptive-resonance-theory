package com.github.chen0040.art.falcon.simulation.utils;

/**
 * Created by memeanalytics on 30/12/15.
 */
public class SimulatorConfig {
    private int numAgents = 20; // the number of autonomous vehicles in the mine field per trial
    private int maxTrial = 2000; // the maximum number of trials (a trial is an attempt to navigate to the target from a random location) in a simulation run
    private int interval = 100; // the number of trials after which a report reading is created
    private int startRun;
    private String name = "mas";
    private int numRuns = 1;

    public int getMaxTrial() {
        return maxTrial;
    }

    public void setMaxTrial(int maxTrial) {
        this.maxTrial = maxTrial;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public int getNumAgents(){
        return numAgents;
    }

    public void setNumAgents(int numAgents){
        this.numAgents = numAgents;
    }

    public void setStartRun(int startRun) {
        this.startRun = startRun;
    }

    public int getStartRun() {
        return startRun;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getNumRuns() {
        return numRuns;
    }

    public void setNumRuns(int numRuns) {
        this.numRuns = numRuns;
    }
}

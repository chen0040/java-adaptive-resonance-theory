package com.github.chen0040.art.falcon.simulation.utils;

import java.io.Serializable;

/**
 * Created by memeanalytics on 25/12/15.
 */
public class SimulatorReportSection implements Serializable {
    private double successRate;
    private double hitMineRate;
    private double timeOutRate;
    private double conflictRate;
    private double normalizedSteps;
    private double numberCode;
    private int trial;


    public double getConflictRate() {
        return conflictRate;
    }

    public void setConflictRate(double conflictRate) {
        this.conflictRate = conflictRate;
    }

    public int getTrial() {

        return trial;
    }

    public void setTrial(int trial) {
        this.trial = trial;
    }

    public double getSuccessRate() {
        return successRate;
    }

    public void setSuccessRate(double successRate) {
        this.successRate = successRate;
    }

    public double getHitMineRate() {
        return hitMineRate;
    }

    public void setHitMineRate(double hitMineRate) {
        this.hitMineRate = hitMineRate;
    }

    public double getTimeOutRate() {
        return timeOutRate;
    }

    public void setTimeOutRate(double timeOutRate) {
        this.timeOutRate = timeOutRate;
    }

    public double getNormalizedSteps() {
        return normalizedSteps;
    }

    public void setNormalizedSteps(double normalizedSteps) {
        this.normalizedSteps = normalizedSteps;
    }

    public double getNumberCode() {
        return numberCode;
    }

    public void setNumberCode(double numberCode) {
        this.numberCode = numberCode;
    }

    @Override
    public String toString(){
        return "Trial " + trial
                + ": Success: " + successRate
                + "%  Hit Mine: " + hitMineRate
                + "%  Timeout: " + timeOutRate
                + "%  Conflict: " + conflictRate
                + "% NSteps: " + " " + normalizedSteps
                + " NCodes: " + numberCode;
    }
}

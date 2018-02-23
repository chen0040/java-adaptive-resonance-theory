package com.github.chen0040.art.falcon.simulation.utils;

import java.util.function.Consumer;

/**
 * Created by chen0469 on 10/1/2015 0001.
 */
public class SimulatorReport {


    private int numReadings;

    public int totalSteps;

    private SimulatorConfig config;

    public int success = 0;
    public int failure = 0;
    public int time_out = 0;

    public int total_step = 0;
    public int total_min_step = 0;
    public int conflict = 0;
    public int[] numCode;
    private String message = "";
    private String name="";

    private SimulatorReportSection sections[];

    private int rd = 0;

    public int getRun() {
        return run;
    }

    public void setRun(int run) {
        this.run = run;
    }

    public SimulatorConfig getConfig() {
        return config;

    }

    public void setConfig(SimulatorConfig config) {
        this.config = config;
    }

    private int run = 0;

    public SimulatorReportSection[] getSections() {
        return sections;
    }

    public void setSections(SimulatorReportSection[] sections) {
        this.sections = sections;
    }

    public SimulatorReport(SimulatorConfig config, int run, String name){
        this.name = name;
        this.run = run;
        this.config = config;
        numReadings = config.getMaxTrial() / config.getInterval() + 1;

        numCode = new int[config.getNumAgents()];

        sections = new SimulatorReportSection[numReadings];

        for (int i = 0; i < numReadings; i++) {
            sections[i] = new SimulatorReportSection();
        }
    }

    public String recordTrial(int trial, int step, Consumer<SimulatorReport> consumer){
        totalSteps += step;

        consumer.accept(this);


        int numAgents = config.getNumAgents();


        if (trial % config.getInterval() == 0) {
            int sample = config.getInterval();
            double success_rate = success * 100.0 / (sample * numAgents);
            double failure_rate = failure * 100.0 / (sample * numAgents);
            double time_out_rate = time_out * 100.0 / (sample * numAgents);
            double conflict_rate = conflict * 100.0 / (sample * numAgents);

            double remaining_rate = 1-success_rate-failure_rate-time_out_rate;
            if(remaining_rate > 0) {
                conflict_rate = remaining_rate;
            }

            double n_steps = total_min_step == 0 ? 0 : total_step / (double) total_min_step;
            double n_codes = numCode[0];

            sections[rd].setSuccessRate(success_rate);
            sections[rd].setHitMineRate(failure_rate);
            sections[rd].setTimeOutRate(time_out_rate);
            sections[rd].setNormalizedSteps(n_steps);
            sections[rd].setNumberCode(n_codes);
            sections[rd].setConflictRate(conflict_rate);
            sections[rd].setTrial(trial);

            message = sections[rd].toString();

            rd++;

            System.out.println(name+" >> " +message);

            success = 0;
            failure = 0;
            time_out = 0;
            conflict = 0;
            total_step = 0;
            total_min_step = 0;
        }

        return message;
    }
}

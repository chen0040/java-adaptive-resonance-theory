package com.github.chen0040.art.falcon.simulation.minefield;

import com.github.chen0040.art.falcon.simulation.minefield.env.MineField;

/**
 * Created by chen0469 on 10/2/2015 0002.
 */
public class MineFieldSimulatorProgress {
    private MineField mineField;
    private int trial;
    private int run;
    private int step;

    public MineFieldSimulatorProgress(int run, int trial, int step, MineField mineField){
        this.run = run;
        this.trial = trial;
        this.step = step;
        this.mineField = mineField;
    }

    public MineField getMineField() {
        return mineField;
    }

    public int getTrial() {
        return trial;
    }

    public int getRun() {
        return run;
    }

    public int getStep() {
        return step;
    }
}

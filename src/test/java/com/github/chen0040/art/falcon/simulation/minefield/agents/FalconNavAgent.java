package com.github.chen0040.art.falcon.simulation.minefield.agents;

import com.github.chen0040.art.falcon.simulation.minefield.env.MineField;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by chen0469 on 10/1/2015 0001.
 */
public abstract class FalconNavAgent {

    public static final int numAction = 5;

    protected double[] state;
    protected double reward;
    protected double[] actions;
    protected double[] newState;
    protected double prevReward;

    private int id;

    public int getId(){
        return id;
    }

    public abstract int selectValidAction(final MineField maze);
    public abstract void learn(final MineField maze);

    public void resetAction(){
        for(int i=0; i < numAction; ++i){
            actions[i] = 1 - actions[i];
        }
    }

    private int numState;
    private int numSonarInput;
    private int numAVSonarInput;
    private int numRangeInput;
    private int numBearingInput;

    public double getPrevReward(){
        return prevReward;
    }

    public FalconNavAgent(int id, int numSonarInput, int numAVSonarInput, int numBearingInput, int numRangeInput){
        this.id = id;
        numState = numAVSonarInput + numSonarInput + numBearingInput + numRangeInput;
        this.numAVSonarInput = numAVSonarInput;
        this.numSonarInput = numSonarInput;
        this.numRangeInput = numRangeInput;
        this.numBearingInput = numBearingInput;

        state = new double[numState];
        actions = new double[numAction];
        newState = new double[numState];
    }

    public void setState(double[] sonar, double[] av_sonar, int bearing, double range){
        int index =0;
        for(int i=0; i < numSonarInput / 2; ++i){
            state[index+i] = sonar[i];
            state[index+i+numSonarInput/2] = 1 - sonar[i];
        }
        index += numSonarInput;

        for(int i=0; i < numAVSonarInput / 2; ++i){
            state[index+i] = av_sonar[i];
            state[index+i+numAVSonarInput/2] = 1 - av_sonar[i];
        }
        index += numAVSonarInput;

        for(int i = 0; i < numBearingInput; ++i){
            state[index+i] = 0;
        }
        state[index+bearing] = 1.0;
        index += numBearingInput;

        for(int i = 0; i < numRangeInput / 2; ++i){
            state[index+i] = range;
            state[index+i+numRangeInput/2] = 1 - range;
        }
    }

    public void setNewState(double[] sonar, double[] av_sonar, int bearing, double range){
        int index =0;
        for(int i=0; i < numSonarInput / 2; ++i){
            newState[index+i] = sonar[i];
            newState[index+i+numSonarInput/2] = 1 - sonar[i];
        }
        index += numSonarInput;

        for(int i=0; i < numAVSonarInput / 2; ++i){
            newState[index+i] = av_sonar[i];
            newState[index+i+numAVSonarInput/2] = 1 - av_sonar[i];
        }
        index += numAVSonarInput;

        for(int i = 0; i < numBearingInput; ++i){
            newState[index+i] = 0;
        }
        newState[index+bearing] = 1.0;
        index += numBearingInput;

        for(int i = 0; i < numRangeInput / 2; ++i){
            newState[index+i] = range;
            newState[index+i+numRangeInput/2] = 1 - range;
        }
    }

    public void setReward(double immmediateReward){
        reward = immmediateReward;
    }

    public double getReward(){
        return reward;
    }

    public abstract int getNodeCount();

    public void setAction(int actionId){
        for(int i=0; i < numAction; ++i){
            actions[i] = 0;
        }
        actions[actionId] = 1;
    }

    public Set<Integer> getFeasibleActions(MineField maze){
        Set<Integer> feasibleActions = new HashSet<Integer>();

        for (int i = 0; i < numAction; i++) {
            if (maze.withinField(id, i - 2)) {   // valid action
                feasibleActions.add(i);
            }
        }

        return feasibleActions;
    }

    public void setPrevReward(double reward) {
        prevReward = reward;
    }
}

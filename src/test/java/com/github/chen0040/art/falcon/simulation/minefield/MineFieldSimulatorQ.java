package com.github.chen0040.art.falcon.simulation.minefield;

import com.github.chen0040.art.falcon.FalconConfig;
import com.github.chen0040.art.falcon.simulation.minefield.agents.FalconNavAgent;
import com.github.chen0040.art.falcon.simulation.minefield.agents.TDFalconNavAgent;

/**
 * Created by chen0469 on 10/1/2015 0001.
 */
public class MineFieldSimulatorQ extends MineFieldSimulator {

    public MineFieldSimulatorQ(MineFieldSimulatorConfig config, FalconConfig falconConfig){
        super(config, falconConfig);
    }

    @Override
    protected FalconNavAgent createAgent(int agentId){
        int numSonarInput = config.numSonarInput;
        int numAVSonarInput = config.numAVSonarInput;
        int numBearingInput = config.numBearingInput;
        int numRangeInput = config.numRangeInput;

        TDFalconNavAgent newAgent = new TDFalconNavAgent(falconConfig, agentId, numSonarInput, numAVSonarInput, numBearingInput, numRangeInput);
        newAgent.useImmediateRewardAsQ = false;

        if(config.isImmediateRewardProvided()){
            newAgent.setQGamma(0.5);
        } else {
            newAgent.setQGamma(0.9);
        }

        return newAgent;
    }

    public static void main(String[] args){
        MineFieldSimulatorConfig config = new MineFieldSimulatorConfig();
        config.setImmediateRewardProvided(false);
        config.setNumAgents(1);

        FalconConfig falconConfig = new FalconConfig();
        falconConfig.numAction = FalconNavAgent.numAction;
        falconConfig.numState = config.numState();
        falconConfig.numReward = 2;

        MineFieldSimulatorQ simulator = new MineFieldSimulatorQ(config, falconConfig);
        simulator.runSims();
    }
}

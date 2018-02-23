package com.github.chen0040.art.falcon.simulation.minefield;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.github.chen0040.art.falcon.FalconConfig;
import com.github.chen0040.art.falcon.simulation.minefield.agents.FalconNavAgent;
import com.github.chen0040.art.falcon.simulation.minefield.agents.TDFalconNavAgent;
import com.github.chen0040.art.falcon.simulation.minefield.env.MineField;
import com.github.chen0040.art.falcon.simulation.utils.SimulatorReport;

import java.io.*;
import java.util.function.Consumer;
import java.util.logging.Logger;

/**
 * Created by chen0469 on 10/1/2015 0001.
 */
public abstract class MineFieldSimulator {
    protected FalconNavAgent[] agents;
    protected MineField mineField;
    protected MineFieldSimulatorConfig config;
    protected FalconConfig falconConfig;
    protected boolean running = false;
    protected String message;

    public String getMessage() { return message; }

    private static Logger logger = Logger.getLogger(String.valueOf(MineFieldSimulator.class));

    public MineFieldSimulator(MineFieldSimulatorConfig config, FalconConfig falconConfig) {
        this.config = config;
        this.falconConfig = falconConfig;
        mineField = new MineField(config.getMineFieldSize(), config.getNumMines(), config.getNumAgents());

        int numAgents = config.getNumAgents();
        agents = new FalconNavAgent[numAgents];
        for (int i = 0; i < numAgents; ++i) {
            agents[i] = createAgent(i);
        }
    }

    protected void logInfo(String message) {
        //logger.info(message);
    }

    protected void logWarning(String message) {
        //logger.warning(message);
    }

    public FalconConfig getFalconConfig(){
        return falconConfig;
    }

    public MineFieldSimulatorConfig getConfig(){
        return config;
    }

    public boolean senseActSense(int agentId, boolean last, boolean provideImmediateReward) {
        int action;
        double r;
        do {
            double[] this_Sonar = mineField.getSonar(agentId);
            double[] this_AVSonar = mineField.getAVSonar(agentId);

            int this_bearing = (8 + mineField.getTargetBearing(agentId) - mineField.getCurrentBearing(agentId)) % 8;
            double this_targetRange = mineField.getTargetRange(agentId);

            agents[agentId].setState(this_Sonar, this_AVSonar, this_bearing, this_targetRange);

            logInfo("Sense and Search for an Action:");

            action = agents[agentId].selectValidAction(mineField);

            if (action == -1) {   // No valid action; deadend, backtrack
                logWarning("*** No valid action, backtracking ***");
                mineField.turn(agentId, 4);
            }

        } while (action == -1);

        logInfo("Performing the Action:");

        double v = mineField.move(agentId, action - 2);          // actual movement, aco direction is from -2 to 2

        if (v != -1) {  // if valid move
            if (last == true && provideImmediateReward == false)  //run out of time (without immediate reward)
                r = 0.0;
            else
                r = mineField.getReward(agentId, provideImmediateReward);
        } else {   // invalid move
            r = 0.0;
            System.out.println("*** Invalid action " + action + " taken *** ");
        }

        if (r == 1.0) logInfo("Success");

        if (action != -1) {
            double[] this_Sonar = mineField.getSonar(agentId);
            double[] this_AVSonar = mineField.getAVSonar(agentId);

            int this_bearing = (8 + mineField.getTargetBearing(agentId) - mineField.getCurrentBearing(agentId)) % 8;
            double this_targetRange = mineField.getTargetRange(agentId);

            agents[agentId].setNewState(this_Sonar, this_AVSonar, this_bearing, this_targetRange);
            agents[agentId].setAction(action);    // set action
            agents[agentId].setReward(r);

            return true;
        }

        return false;
    }

    public SimulatorReport[] runSims(){
        return runSims(null);
    }

    public SimulatorReport[] runSims(Consumer<MineFieldSimulatorProgress> progressChanged) {
        running = true;

        message = "Simulation Started";

        SimulatorReport[] reports = new SimulatorReport[config.getNumRuns()];
        for (int runIndex = 0; runIndex < config.getNumRuns(); runIndex++) {
            int run = runIndex + config.getStartRun();
            if(!running) break;

            SimulatorReport rpt = runSim(run, progressChanged);

            String dirpath = System.getProperty("user.home")+"/"+config.getName();
            File dir = new File(dirpath);

            if(!dir.exists()){
                dir.mkdirs();
            }

            String filepath = dirpath+"/"+String.format("%02d", run)+".json";

            System.out.println("Persisting: "+filepath);

            String json = JSON.toJSONString(rpt, SerializerFeature.BrowserCompatible);

            try {
                BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filepath)));
                writer.write(json);
                writer.flush();
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            reports[runIndex] = rpt;

        }
        return reports;
    }

    private boolean doStep(int step){
        //!aco.endState(Target_Moving)

        // the code below is required for the RFALCON to behave correctly
        for(int agt = 0; agt < config.getNumAgents(); ++agt) {
            agents[agt].setPrevReward(mineField.getReward(agt, config.isImmediateRewardProvided()));
        }

        boolean lastFlag = step == (config.getMaxStep() - 1);

        int activeAgentCount = 0;
        for (int agt = 0; agt < config.getNumAgents(); agt++) {
            if(!running) break;
            if (!mineField.isActive(agt))
                continue;

            if(config.targetMoving) mineField.moveTarget();

            doStep(agt, lastFlag);
            activeAgentCount++;
        }

        if(activeAgentCount == 0) return false;

        for(int agt = 0; agt < config.getNumAgents(); ++agt) {
            mineField.checkConflict(agt);
        }

        afterStep();

        return true;
    }

    protected void afterStep(){

    }

    public void doStep(int agentId, boolean last) {
        boolean acted = senseActSense(agentId, last, config.isImmediateRewardProvided());

        if(acted) {
            agents[agentId].learn(mineField);
        }
    }


    protected abstract FalconNavAgent createAgent(int agentId);

    private SimulatorReport runSim(int run, Consumer<MineFieldSimulatorProgress> progressChanged){
        SimulatorReport report = new SimulatorReport(config, run, "MineField-TD-FALCON");

        int numAgents = config.getNumAgents();

        agents = new FalconNavAgent[numAgents];
        for (int i = 0; i < numAgents; ++i) {
            agents[i] = createAgent(i);
        }

        for (int trial = 1; trial <= config.getMaxTrial(); ++trial) {
            if(!running) break;
            mineField.refreshMaze(config.getMineFieldSize(), config.getNumMines(), config.getNumAgents());

            for (int i = 0; i < config.getNumAgents(); ++i) {
                agents[i].setPrevReward(0);
            }

            int step = 0;
            for(; step < config.getMaxStep(); ++step) {
                if(!running) break;
                if(!doStep(step)) break;
                if(progressChanged != null){
                    MineFieldSimulatorProgress progress = new MineFieldSimulatorProgress(run, trial, step, mineField);
                    progressChanged.accept(progress);
                    try{
                        Thread.sleep(config.getUiInterval());
                    }catch(InterruptedException ie){

                    }
                }
            }

            int step_final = step;

            message = report.recordTrial(trial, step_final, (rpt)->{
                FalconNavAgent[] agents = this.agents;
                MineField maze = this.mineField;

                for (int agt = 0; agt < numAgents; agt++) {
                    rpt.numCode[agt] = agents[agt].getNodeCount();
                    if (maze.isHitTarget(agt)) {
                        rpt.success++;
                        rpt.total_step += step_final;
                        rpt.total_min_step += maze.getMinStep(agt);
                    } else if (step_final == config.getMaxStep())
                        rpt.time_out++;
                    else if (maze.isConflicting(agt))
                        rpt.conflict++;
                    else
                        rpt.failure++;
                }
            });

            for (int i=0; i < numAgents; ++i){
                if(agents[i] instanceof TDFalconNavAgent) {
                    ((TDFalconNavAgent)agents[i]).decayQEpsilon();
                }
            }
        }

        return report;
    }

    public void stop(){
        running = false;
    }

    public int getNumAgents() {
        return config.getNumAgents();
    }

    public MineField getMineField(){
        return mineField;
    }

    public static void main(String[] args){
        MineFieldSimulatorConfig config = new MineFieldSimulatorConfig();
        config.setImmediateRewardProvided(false);


        FalconConfig falconConfig = new FalconConfig();
        falconConfig.numAction = FalconNavAgent.numAction;
        falconConfig.numState = config.numState();
        falconConfig.numReward = 2;


        MineFieldSimulator simulator = CommandLineUtils.procCommandLines(args, config, falconConfig);
        simulator.runSims();
    }
}

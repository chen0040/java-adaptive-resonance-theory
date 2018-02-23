package com.github.chen0040.art.falcon.simulation.minefield;

import com.github.chen0040.art.falcon.FalconConfig;
import org.apache.commons.cli.*;

/**
 * Created by memeanalytics on 30/12/15.
 */
public class CommandLineUtils {
    public static MineFieldSimulator procCommandLines(String[] args, MineFieldSimulatorConfig simConfig, FalconConfig aiConfig){
        Options options = new Options();
        options.addOption("s", "size", true, "specify the number of nodes in the traffic network");
        options.addOption("c", "numAgents", true, "specify the number of agents");
        options.addOption("t", "targetMove", true, "specify whether the target is moving");
        options.addOption("m", "falconMode", true, "specify whether the falcon mode is Q-Learn or SARSA");
        options.addOption("f", "TDFALCON", true, "specify whether the agent is TD-FALCON");
        options.addOption("b", "bounded", true, "specify whether TD-FALCON Q is bounded");
        options.addOption("h", "head", true, "specify the starting index of simulation");
        options.addOption("g", "num", true, "specify the number of simulations to run");
        options.addOption("n", "name", true, "specify the folder name of the simulation results");
        options.addOption("j", "jMainClass", true, "specify the main class to run");

        int numSims = 30;
        int startSim = 0;

        String name="minefield";

        String falconMode = "qlearn";
        CommandLineParser parser = new DefaultParser();
        try {
            CommandLine line = parser.parse( options, args );
            String s = line.getOptionValue("s");
            int size = 32;
            if(s != null) {
                try {
                    size = Integer.parseInt(s);
                } catch (NumberFormatException ex) {
                    size = 32;
                }
            }

            simConfig.setMineFieldSize(size);

            int numAgents = 20;
            String c = line.getOptionValue("c");
            if(c != null) {
                try {
                    numAgents = Integer.parseInt(c);
                } catch (NumberFormatException ex) {
                    numAgents = 20;
                }
            }

            simConfig.setNumAgents(numAgents);

            boolean targetMoving = false;
            String t = line.getOptionValue("t");
            if(t != null) {
                try{
                    targetMoving = Integer.parseInt(t) == 1;
                } catch(NumberFormatException ex) {
                    targetMoving = false;
                }
            }

            simConfig.targetMoving = targetMoving;

            falconMode = line.getOptionValue("m");

            boolean tdf = false;
            String f = line.getOptionValue("f");
            if(f != null) {
                try{
                    tdf = Integer.parseInt(f) == 1;
                } catch(NumberFormatException ex) {
                    tdf = false;
                }
            }
            simConfig.applyTDFALCON = tdf;

            boolean bounded = false;
            String b = line.getOptionValue("b");
            if(b != null) {
                try{
                    bounded = Integer.parseInt(b) == 1;
                } catch (NumberFormatException ex) {
                    bounded = false;
                }
            }

            aiConfig.isBounded = bounded;

            String h = line.getOptionValue("h");
            if(h != null) {
                try{
                    startSim = Integer.parseInt(h);
                } catch (NumberFormatException ex) {
                    startSim = 0;
                }
            }

            String g = line.getOptionValue("g");
            if(g != null){
                try{
                    numSims = Integer.parseInt(g);
                } catch (NumberFormatException ex) {
                    numSims = 30;
                }
            }

            String n = line.getOptionValue("n");
            if(n != null) {
                name = n;
            }

        }
        catch( ParseException exp ) {
            System.err.println( "Parsing failed.  Reason: " + exp.getMessage() );
        }

        if(falconMode == null) {
            falconMode = "qlearn";
        }



        simConfig.setNumRuns(numSims);
        simConfig.setStartRun(startSim);

        System.out.println("Start: " + startSim + "\tNo. of runs: "+numSims);
        simConfig.setName(name);


        if(falconMode.equalsIgnoreCase("sarsa")) {
            return new MineFieldSimulatorSarsa(simConfig, aiConfig);
        } else if(falconMode.equalsIgnoreCase("qlearn")) {
            return new MineFieldSimulatorSarsa(simConfig, aiConfig);
        } else if(falconMode.equalsIgnoreCase("sarsalambda")) {
            return new MineFieldSimulatorSarsaLambda(simConfig, aiConfig);
        } else if(falconMode.equalsIgnoreCase("r")) {
            return new MineFieldSimulatorR(simConfig, aiConfig);
        } else {
            return new MineFieldSimulatorQLambda(simConfig, aiConfig);
        }
    }
}

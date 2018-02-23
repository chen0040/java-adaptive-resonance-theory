package com.github.chen0040.art.falcon;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by chen0469 on 9/28/2015 0028.
 *
 * FALCON A Fusion Architecture for Learning Cognition and Navigation
 */
public class Falcon {
    public ArrayList<FalconNode> nodes = new ArrayList<FalconNode>();
    public FalconConfig config;

    public Falcon(FalconConfig config) {
        this.config = config;
    }

    public int numReward(){
        return config.numReward;
    }

    public int numAction(){
        return config.numAction;
    }

    public int learn(double[] inputs, int actionTaken, double reward){
        double[] rewards = new double[numReward()];
        rewards[0] = clamp(reward, 0, 1);
        rewards[1] = 1 - rewards[0];

        return learn(inputs, actionTaken, rewards);
    }

    public int learn(double[] inputs, double[] actionTaken, double reward){
        double[] rewards = new double[numReward()];
        rewards[0] = clamp(reward, 0, 1);
        rewards[1] = 1 - rewards[0];

        return learn(inputs, actionTaken, rewards);
    }

    protected double clamp(double val, double minVal, double maxVal){
        if(val < minVal){
            val = minVal;
        }
        else if(val > maxVal){
            val = maxVal;
        }

        return val;
    }

    public int learn(double[] inputs, int actionTaken, double[] rewards){
        double[] actions = new double[numAction()];
        for(int i=0; i < numAction(); ++i){
            actions[i] = 0;
        }
        actions[actionTaken] = 1;
        return learn(inputs, actions, rewards);
    }

    public int learn(double[] inputs, double[] actions, double[] rewards){
        double[] choiceValues = computeChoiceValues(nodes, inputs, actions, null, config);
        return learn(inputs, actions, rewards, choiceValues);
    }

    public int learn(double[] inputs, double[] actions, double[] rewards, double[] choiceValues){
        double[] rhos = new double[3];
        rhos[0] = config.rho_inputs;
        rhos[1] = config.rho_actions;
        rhos[2] = config.rho_rewards;

        int J = -1;
        boolean newNode = true;
        for(int i=0; i < nodes.size(); ++i) {
            J = compete(choiceValues);
            if(J == -1) break;

            if (J != -1) {
                FalconNode nodeJ = nodes.get(J);
                if (nodeJ.isVigilanceConstraintSatisfied(inputs, actions, rewards, rhos)) {
                    nodeJ.learnTemplate(inputs, actions, rewards, config);
                    newNode = false;
                    break;
                } else {
                    if(nodeJ.isPerfectMismatch(inputs)){
                        nodeJ.overwrite(inputs, actions, rewards, config);
                        newNode = false;
                        break;
                    }
                    else {
                        choiceValues[J] = -1;
                        rhos = nodeJ.raiseVigilance(inputs, actions, rewards, rhos, config);
                    }
                }
            }else{
                break;
            }
        }

        if(newNode){
            FalconNode node = new FalconNode(inputs, actions, rewards);
            nodes.add(node);
            onNewNode(node);
            J = nodes.size()-1;
        }
        return J;
    }

    protected void onNewNode(FalconNode node){

    }

    public int selectActionId(double[] inputs, QValueProvider maze){
        return selectDirectionActionId(inputs);
    }

    public int selectActionId(double[] inputs) {
        QValueProvider provider = null;
        return selectActionId(inputs, provider);
    }

    public int selectActionId(double[] inputs, Set<Integer> feasibleActions, QValueProvider maze){
       return selectDirectionActionId(inputs, feasibleActions);
    }

    public int selectActionId(double[] inputs, Set<Integer> feasibleActions){
        QValueProvider provider = null;
        return selectActionId(inputs, feasibleActions, provider);
    }

    public double[] searchAction(double[] inputs) {

        double[] choiceValues = computeChoiceValues(nodes, inputs, null, null, config);

        int J = compete(choiceValues);
        if (J != -1) {
            FalconNode nodeJ = nodes.get(J);
            double[] output_actions = nodeJ.weight_actions.clone();
            return output_actions;
        }

        return null;
    }

    public int compete(double[] choiceValues){
        double maxChoiceValue = Double.NEGATIVE_INFINITY;
        int J = -1;
        for(int j=0; j < choiceValues.length; ++j){
            if(choiceValues[j] > maxChoiceValue){
                maxChoiceValue = choiceValues[j];
                J = j;
            }
        }

        onChoiceCompeted(J);

        return J;
    }

    protected void onChoiceCompeted(int J){

    }


    public static double[] computeChoiceValues(List<FalconNode> nodes, double[] inputs, double[] actions, double[] rewards, FalconConfig config){
        int nodeCount = nodes.size();
        double[] choiceValues = new double[nodeCount];
        for(int i=0; i < nodeCount; ++i){
            choiceValues[i] = nodes.get(i).computeChoiceValue(inputs, actions, rewards, config);
        }
        return choiceValues;
    }

    public int selectDirectionActionId(double[] inputs, Set<Integer> feasibleActions) {
        double[] actions = searchAction(inputs);

        if(actions == null) {
            List<Integer> actionList = new ArrayList<Integer>(feasibleActions);
            if(actionList.size() > 0){
                return actionList.get(((int)Math.random() * actionList.size()));
            }
        }

        int selectedAction = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        for(Integer actionId : feasibleActions){

            if(actions[actionId] > maxValue){
                maxValue = actions[actionId];
                selectedAction = actionId;
            }
        }

        return selectedAction;
    }

    public int selectDirectionActionId(double[] inputs){
        double[] actions = searchAction(inputs);

        int numAction =  numAction();

        if(actions == null){
            return ((int)Math.random() * numAction);
        }

        int selectedAction = -1;
        double maxValue = Double.NEGATIVE_INFINITY;
        for(int actionId=0; actionId < numAction; ++actionId){
            if(actions[actionId] > maxValue){
                maxValue = actions[actionId];
                selectedAction = actionId;
            }
        }

        return selectedAction;
    }

    public FalconConfig getConfig() {
        return config;
    }
}

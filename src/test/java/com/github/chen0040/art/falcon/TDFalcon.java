package com.github.chen0040.art.falcon;

import java.util.*;

/**
 * Created by chen0469 on 9/28/2015 0028.
 * @brief TD-Falcon is based on "Self-organizing neural architectures and cooperative learning in multiagent environment"
 */
public class TDFalcon extends Falcon {
    public double QEpsilon = 0.50000; // parameter for the epsilon-greedy action selection strategy
    public double QEpsilonDecay = 0.00050;
    public double minQEpsilon   = 0.00500;

    private Random random = new Random();
    public double QGamma = 0.9; // discount factor in Q-learning (0.9 for delayed reward; 0.5 for immediate reward)
    public double QAlpha = 0.5; // learning rate in Q-learning


    public TDMethod method = TDMethod.QLearn;

    public TDFalcon(FalconConfig config)
    {
        super(config);
    }

    public TDFalcon(FalconConfig config, TDMethod method){
        super(config);
        this.method = method;
    }

    public void decayQEpsilon(){
        if (QEpsilon > minQEpsilon)
            QEpsilon -= QEpsilonDecay;
    }

    private int getRandomActionId(Set<Integer> feasibleActions){
        if(feasibleActions.size() == 0) return -1;
        List<Integer> actions = new ArrayList<Integer>();
        for(Integer actionId : feasibleActions){
            actions.add(actionId);
        }
        return actions.get(random.nextInt(actions.size()));
    }

    @Override
    public int selectActionId(double[] state, QValueProvider maze){
        int selectedAction = -1;
        // implement QEpsilon-greedy action selection policy
        if(random.nextDouble() <= 1 - QEpsilon + QEpsilon / numAction()){
            selectedAction = getActionIdWithMaxQ(state, maze);
        }else{
            selectedAction = getRandomActionId();
        }

        return selectedAction;
    }

    private int getRandomActionId(){
        return random.nextInt(numAction());
    }

    private int getActionIdWithMaxQ(double[] state, QValueProvider maze){
        int selectedActionId = -1;
        double maxQ = Double.NEGATIVE_INFINITY;

        for(int actionId = 0; actionId < numAction(); ++actionId){
            QValue Q_guessed = maze != null ? maze.queryQValue(state, actionId, true) : QValue.Invalid();
            double Q = searchQ(state, actionId, Q_guessed);
            if(Q > maxQ){
                maxQ = Q;
                selectedActionId = actionId;
            }
        }

        return selectedActionId;
    }

    private int getActionIdWithMaxQ(double[] state, Set<Integer> feasibleActions, QValueProvider maze){
        int selectedActionId = -1;
        double maxQ = Double.NEGATIVE_INFINITY;

        for(Integer actionId : feasibleActions){
            QValue Q_guessed = maze != null ? maze.queryQValue(state, actionId, true) : QValue.Invalid();
            double Q = searchQ(state, actionId, Q_guessed);
            if(Q > maxQ){
                maxQ = Q;
                selectedActionId = actionId;
            }
        }

        return selectedActionId;
    }

    @Override
    public int selectActionId(double[] state, Set<Integer> feasibleActions, QValueProvider maze){
        int selectedAction = -1;
        // implement QEpsilon-greedy action selection policy
        if(random.nextDouble() <= 1 - QEpsilon + QEpsilon / feasibleActions.size()){
            selectedAction = getActionIdWithMaxQ(state, feasibleActions, maze);
        }else{
            selectedAction = getRandomActionId(feasibleActions); //getActionWithSoftMaxQ(state, feasibleActions);
        }

        return selectedAction;
    }

    public int learnQ(double[] currentState, int actionTaken, double[] newState, double immediateReward, QValueProvider maze){
        return learnQ(currentState, actionTaken, newState, null, immediateReward, maze);
    }

    public int learnQ(double[] currentState, int actionTaken, double[] newState, Set<Integer> feasibleActionsAtNewState, double immediateReward, QValueProvider maze){
        int actionCount = numAction();
        double[] actions = new double[actionCount];
        for(int i=0; i < actionCount; ++i){
            actions[i] = 0;
        }
        actions[actionTaken] = 1;
        return learnQ(currentState, actions, newState, feasibleActionsAtNewState, immediateReward, maze);
    }

    protected double[] dummyRewards(){
        int rewardSize = numReward();
        double[] rewards = new double[rewardSize];
        for(int i=0; i < rewardSize; ++i){
            rewards[i] = 1;
        }
        return rewards;
    }

    public int learnQ(double[] currentState, double[] actionTaken, double[] newState, Set<Integer> feasibleActionAtNewState, double immediateReward, QValueProvider maze){
        double[] rewards = updateQValue(currentState, actionTaken, newState, feasibleActionAtNewState, immediateReward, maze);
        return learn(currentState, actionTaken, rewards);
    }

    protected double getTDError(double oldQ, double[] newState, Set<Integer> feasibleActionsAtNewState, double immediateReward, QValueProvider qValueProvider){

        double td_error = 0;


        if(method == TDMethod.QLearn) {
            double maxQ = searchMaxQ(newState, feasibleActionsAtNewState, qValueProvider);
            td_error = immediateReward + QGamma * maxQ - oldQ;
        } else if(method == TDMethod.Sarsa) {
            int newActionId = selectActionId(newState, feasibleActionsAtNewState, qValueProvider);
            double nextQ = config.initialQ;
            if(newActionId != -1){
                QValue Q_injected = qValueProvider != null ? qValueProvider.queryQValue(newState, newActionId, true) : QValue.Invalid();
                nextQ = searchQ(newState, newActionId, Q_injected);
            }

            td_error = immediateReward + QGamma * nextQ - oldQ;
        }

        return td_error;
    }

    protected double[] updateQValue(double[] oldState, double[] actionTaken, double[] newState, Set<Integer> feasibleActionsAtNewState, double immediateReward, QValueProvider maze){
        double newQ = 0;
        boolean isQProvided = false;
        if(maze != null){
            QValue QValue = maze.queryQValue(oldState, actionTaken, false);
            if(QValue.isValid()){
                newQ = QValue.getValue();
                isQProvided = true;
            }
        }

        if(!isQProvided) {
            double oldQ = searchQ(oldState, actionTaken, QValue.Invalid());

            double td_error = getTDError(oldQ, newState, feasibleActionsAtNewState, immediateReward, maze);

            double deltaQ = QAlpha * td_error;

            if(config.isBounded){
                deltaQ = deltaQ * (1-oldQ);
            }

            newQ = oldQ + deltaQ;
        }

        newQ = clamp(newQ, 0, 1);

        double[] rewards = new double[numReward()];
        rewards[0] = newQ;
        rewards[1] = 1 - newQ;
        return rewards;
    }

    private double searchMaxQ(double[] state, Set<Integer> feasibleActionsAtState, QValueProvider maze){
        int actionCount = numAction();
        if(feasibleActionsAtState == null){
            feasibleActionsAtState = new HashSet<Integer>();
            for(int actionId = 0; actionId < actionCount; ++actionId){
                feasibleActionsAtState.add(actionId);
            }
        }

        boolean valid = false;
        double maxQ = Double.NEGATIVE_INFINITY;
        for(Integer actionId : feasibleActionsAtState){
            QValue Q_injected = maze != null ? maze.queryQValue(state, actionId, true) : QValue.Invalid();
            double Q = searchQ(state, actionId, Q_injected);
            if(Q > maxQ){
                maxQ = Q;
                valid = true;
            }
        }

        if(!valid) {
            maxQ = config.initialQ;
        }

        return maxQ;

    }

    private double searchQ(double[] state, int actionId, QValue Q_guessed){
        int actionCount = numAction();
        double[] actions = new double[actionCount];
        for(int i=0; i < actionCount; ++i){
            actions[i] = 0;
        }
        actions[actionId] = 1;

        return searchQ(state, actions, Q_guessed);
    }

    private double searchQ(double[] state, double[] actions, QValue Q_guessed){
        if(Q_guessed.isValid()){
            return Q_guessed.getValue();
        } else {
            double[] choiceValues = computeChoiceValues(nodes, state, actions, null, config);

            int J = compete(choiceValues);
            if(J != -1){
                FalconNode nodeJ = nodes.get(J);

                double[] rewards = dummyRewards();
                rewards = nodeJ.fuzzyAND(rewards, nodeJ.weight_rewards);
                return readQ(rewards);
            }
            return config.initialQ;
        }

    }

    protected static double readQ(double[] rewards){
        return rewards[0]; // / sum(rewards);
    }

    private static double sum(double[] rewards){
        double sum = 0;
        for(int i=0; i < rewards.length; ++i){
            sum += rewards[i];
        }
        return sum;
    }
}

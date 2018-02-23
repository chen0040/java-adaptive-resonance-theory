package com.github.chen0040.art.falcon;

import java.util.Set;
import java.util.logging.Logger;

/**
 * Created by chen0469 on 10/2/2015 0002.
 */
public class TDLambdaFalcon extends TDFalcon {
    private double lambda = 0.9;
    private EligibilityTraceUpdateMode traceUpdateMode = EligibilityTraceUpdateMode.ReplaceTrace;
    private static Logger logger = Logger.getLogger(String.valueOf(TDLambdaFalcon.class));
    private boolean parallel = false;

    public TDLambdaFalcon(FalconConfig config) {
        super(config);
    }

    public TDLambdaFalcon(FalconConfig config, TDMethod method) {
        super(config, method);
    }

    public EligibilityTraceUpdateMode getTraceUpdateMode() {
        return traceUpdateMode;
    }

    @Override
    public int learnQ(double[] oldState, double[] actionTaken, double[] newState, Set<Integer> feasibleActionsAtNewState, double immediateReward, QValueProvider maze){

        QValue Q_injected = maze == null ? QValue.Invalid() : maze.queryQValue(oldState, actionTaken, false);


        if(!Q_injected.isValid()) {
            Tuple2<Integer, Double> kv = searchQNode(oldState, actionTaken, QValue.Invalid());

            int J = kv.getKey();
            double oldQ = kv.getValue();

            if(J == -1){
                J = super.learnQ(oldState, actionTaken, newState, feasibleActionsAtNewState, immediateReward, maze);
            } else {
                FalconNode nodeJ = nodes.get(J);
                nodeJ.e += 1;

                double td_error = getTDError(oldQ, newState, feasibleActionsAtNewState, immediateReward, maze);

                for (int stateIndex = 0; stateIndex < nodes.size(); stateIndex++) {
                    FalconNode node = nodes.get(stateIndex);
                    double[] state = node.weight_inputs;

                    oldQ = readQ(node.weight_rewards);

                    double deltaQ = QAlpha * td_error * node.e;

                    if (config.isBounded) {
                        deltaQ = deltaQ * (1 - oldQ);
                    }

                    double newQ = oldQ + deltaQ;
                    newQ = clamp(newQ, 0, 1);

                    double[] rewards = new double[numReward()];
                    rewards[0] = newQ;
                    rewards[1] = 1 - newQ;

                    if (J == stateIndex) {
                        nodeJ.learnTemplate(state, actionTaken, rewards, config);
                        nodes.get(J).e *= QGamma * lambda;
                    } else {
                        node.learnTemplate(state, node.weight_actions, rewards, config);

                        Tuple2<Integer, double[]> kv2 = searchActionNode(state);
                        double[] action2 = kv2.getValue();
                        int jprime = kv2.getKey();

                        if (jprime != -1) {
                            if (equals(action2, actionTaken)) {
                                nodes.get(jprime).e *= QGamma * lambda;
                            } else {
                                nodes.get(jprime).e = 0;
                            }
                        }
                    }
                }
            }

            return J;
        } else {
            double newQ = Q_injected.getValue();
            double[] rewards = new double[numReward()];
            rewards[0] = newQ;
            rewards[1] = 1 - newQ;

            return learn(oldState, actionTaken, rewards);
        }
    }

    protected boolean equals(double[] actionsA, double[] actionsB){
        int actionId1 = getActionId(actionsA);
        int actionId2 = getActionId(actionsB);
        return actionId1 == actionId2;
    }

    protected int getActionId(double[] actions){
        double maxValue = Double.NEGATIVE_INFINITY;
        int actionId = -1;
        for(int i=0; i < actions.length; ++i){
            if(actions[i] > maxValue){
                maxValue = actions[i];
                actionId = i;
            }
        }
        return actionId;
    }

    public Tuple2<Integer, double[]> searchActionNode(double[] inputs) {

        double[] choiceValues = computeChoiceValues(nodes, inputs, null, null, config);

        int J = compete(choiceValues);
        if (J != -1) {
            FalconNode nodeJ = nodes.get(J);
            double[] output_actions = nodeJ.weight_actions.clone();
            return new Tuple2<Integer, double[]>(J, output_actions);
        }

        return new Tuple2<Integer, double[]>(-1, null);
    }

    private Tuple2<Integer, Double> searchQNode(double[] state, double[] actions, QValue Q_injected) {
        double[] choiceValues = computeChoiceValues(nodes, state, actions, null, config);

        double[] rhos = new double[3];
        rhos[0] = config.rho_inputs;
        rhos[1] = config.rho_actions;
        rhos[2] = config.rho_rewards;

        double[] rewards = dummyRewards();

        int J = -1;
        for(int j = 0; j < nodes.size(); ++j) {
            J = compete(choiceValues);
            if (J == -1) break;

            FalconNode nodeJ = nodes.get(J);

            if(nodeJ.isVigilanceConstraintSatisfied(state, actions, rewards, rhos)){
                choiceValues[J] = -1;
                J = -1;
                rhos = nodeJ.raiseVigilance(state, actions, rewards, rhos, config);
            }
        }

        if(J != -1){
            FalconNode nodeJ = nodes.get(J);

            double q;
            if (Q_injected.isValid()) {
                q = Q_injected.getValue();
            } else {
                rewards = nodeJ.weight_rewards.clone();
                q = readQ(rewards);
            }
            return new Tuple2<Integer, Double>(J, q);
        }

        return new Tuple2<Integer, Double>(-1, Q_injected.isValid() ? Q_injected.getValue() : config.initialQ);

    }

    public void setTraceUpdateMode(EligibilityTraceUpdateMode traceUpdateMode) {
        this.traceUpdateMode = traceUpdateMode;
    }

    public double getLambda(){
        return lambda;
    }

    public void setLambda(double lambda){
        this.lambda = lambda;
    }
}

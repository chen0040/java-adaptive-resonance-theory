package com.github.chen0040.art.falcon;

/**
 * Created by chen0469 on 10/1/2015 0001.
 */
public class RFalcon extends Falcon {

    private double initConfidence = (double)0.5;
    private double reinforce_rate = (double)0.5;
    private double penalize_rate = (double)0.2;
    private double decay_rate = (double)0.0005;
    private double threshold = (double)0.01;
    private int    capacity = 9999;
    private double[]   confidence;
    private int J = -1;

    public RFalcon(FalconConfig config) {
        super(config);
    }

    @Override
    public int learn(double[] state, double[] actions, double[] rewards){
        double[] choiceValues = computeChoiceValues(nodes, state, null, null, config);
        return learn(state, actions, rewards, choiceValues);
    }

    @Override
    protected void onNewNode(FalconNode node){
        int numCode = nodes.size();

        double[] new_confidence = new double[numCode];
        for (int j=0; j < numCode-1; j++)
            new_confidence[j] = confidence[j];
        new_confidence[numCode-1] = initConfidence;
        confidence = new_confidence;

        J = numCode-1;
    }

    @Override
    public void onChoiceCompeted(int J){
        this.J = J;
    }

    public void reinforce () {
        confidence[J] += (1.0-confidence[J]) * reinforce_rate;
    }

    public void penalize () {
        confidence[J] -= confidence[J] * penalize_rate;
    }

    public void decay () {
        int numCode = nodes.size();
        for (int j=0; j < numCode; j++)
            confidence[j] -= confidence[j] * decay_rate;
    }
}

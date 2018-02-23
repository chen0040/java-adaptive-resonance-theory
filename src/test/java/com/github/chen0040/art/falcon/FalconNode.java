package com.github.chen0040.art.falcon;

/**
 * Created by chen0469 on 9/29/2015 0029.
 */

public class FalconNode
{
    public double[] weight_inputs;
    public double[] weight_actions;
    public double[] weight_rewards;

    public double e = 0; // eligibility trace

    public FalconNode(double[] inputs, double[] actions, double[] rewards){
        weight_inputs = inputs.clone();
        weight_actions = actions.clone();
        weight_rewards = rewards.clone();
    }

    public double computeChoiceValue(double[] inputs, double[] actions, double[] rewards, FalconConfig config){
        double choiceValue_inputs = inputs == null ? 0 : computeChoiceValue(inputs, weight_inputs, config.alpha_inputs, config.artType);
        double choiceValue_actions = actions == null ? 0 : computeChoiceValue(actions, weight_actions, config.alpha_actions, config.artType);
        double choiceValue_rewards = rewards == null ? 0 : computeChoiceValue(rewards, weight_rewards, config.alpha_rewards, config.artType);

        return choiceValue_inputs * config.gamma_inputs + choiceValue_actions * config.gamma_actions + choiceValue_rewards * config.gamma_rewards;
    }

    public double computeChoiceValue(double[] x, double[] weights, double alpha, FalconConfigType choiceFunctionType){
        if(choiceFunctionType == FalconConfigType.FuzzyART) {
            return norm(fuzzyAND(x, weights)) / (alpha + norm(weights));
        } else {
            return norm(arrayTimes(x, weights)) / x.length;
        }
    }

    public double[] arrayTimes(double[] x, double[] weights){
        int len = x.length;
        double[] y = new double[len];
        for(int i=0; i < len; ++i){
            y[i] = x[i] * weights[i];
        }
        return y;
    }

    public double norm(double[] x){
        double sum = 0;
        for(int i=0; i < x.length; ++i){
            sum += x[i];
        }
        return sum;
    }

    public double[] fuzzyAND(double[] x1, double[] x2){
        int len = x1.length;
        double[] result = new double[len];
        for(int i=0; i < len; ++i){
            result[i] = fuzzyAND(x1[i], x2[i]);
        }
        return result;
    }

    public double fuzzyAND(double x1, double x2){
        return Math.min(x1, x2);
    }

    public boolean isVigilanceConstraintSatisfied(double[] inputs, double[] actions, double[] rewards, double[] rhos){
        return computeMatchValue(inputs, weight_inputs) >= rhos[0]
                && computeMatchValue(actions, weight_actions) >= rhos[1]
                && computeMatchValue(rewards, weight_rewards) >= rhos[2];
    }

    public double[] raiseVigilance(double[] inputs, double[] actions, double[] rewards, double[] rhos, FalconConfig config){
        double[] new_rhos = rhos.clone();
        double matchValue = computeMatchValue(inputs, weight_inputs);
        if(matchValue > rhos[0]){
            new_rhos[0] = Math.min(matchValue + config.rho_epsilon, 1);
        }
        return new_rhos;
    }

    public boolean isPerfectMismatch(double[] inputs){
        return computeMatchValue(inputs, weight_inputs) == 1;
    }

    public void overwrite(double[] inputs, double[] actions, double[] rewards, FalconConfig config){
        weight_inputs = inputs.clone();
        weight_actions = actions.clone();
        weight_rewards = rewards.clone();
    }

    public void learnTemplate(double[] inputs, double[] actions, double[] rewards, FalconConfig config){
        updateWeights(inputs, weight_inputs, config.beta_inputs, config.artType);
        updateWeights(actions, weight_actions, config.beta_actions, config.artType);
        updateWeights(rewards, weight_rewards, config.beta_rewards, config.artType);
    }

    public void updateWeights(double[] x, double[] weights, double beta, FalconConfigType artType){
        int len = x.length;

        if(artType == FalconConfigType.FuzzyART) {
            double[] x_fuzzyAND_weights = fuzzyAND(x, weights);
            for (int i = 0; i < len; ++i) {
                weights[i] = (1 - beta) * weights[i] + beta * x_fuzzyAND_weights[i];
            }
        }else {
            for(int i = 0; i < len; ++i) {
                weights[i] = (1 - beta) * weights[i] + beta * x[i];
            }
        }
    }

    public double computeMatchValue(double[] x, double[] weights){

        boolean isNull = true;
        for(int i=0; i < x.length; ++i){
            if(x[i] != 0) {
                isNull = false;
            }
        }
        if(isNull) return 1;

        double denominator = norm(x);
        if(denominator == 0) return 1;
        return norm(fuzzyAND(x, weights)) / denominator;
    }
}


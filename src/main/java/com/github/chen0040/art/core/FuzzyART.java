package com.github.chen0040.art.core;

/**
 * Created by xschen on 21/8/15.
 */
public class FuzzyART extends ART1 {

    public FuzzyART(int inputCount, int initialNeuronCount) {
        super(inputCount, initialNeuronCount);
    }

    public FuzzyART(){
        super();
    }
    
    @Override
    protected double choice_function(double[] x, int j){
        double[] W_j = weights.get(j);
        double sum = 0;
        double sum2 = 0;
        for (int i = 0; i < x.length; ++i){
            sum += Math.abs(Math.min(x[i], W_j[i])); // norm1(fuzzy and)
            sum2 += Math.abs(W_j[i]); //  norm1
        }

        return sum / (alpha + sum2);
    }

    @Override
    protected double match_function(double[] x, int j){
        double[] W_j = weights.get(j);
        double sum = 0;
        double sum2 = 0;
        for (int i = 0; i < x.length; ++i){
            sum += Math.abs(Math.min(x[i], W_j[i])); // norm1(fuzzy and)
            sum2 += Math.abs(x[i]); //  norm1
        }

        return sum / sum2;
    }
}

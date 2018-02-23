package com.github.chen0040.art.falcon;

/**
 * Created by chen0469 on 9/29/2015 0029.
 */
public class FalconConfig {
    public int numState = 5;
    public int numAction = 5;
    public int numReward = 2;

    public double alpha_inputs = 0.1; // choice function parameter for inputs (a.k.a states)
    public double alpha_actions = 0.1; // choice function parameter for actions
    public double alpha_rewards = 0.1; // choice function parameter for rewards

    public double beta_inputs = 1; // learning rate for inputs (a.k.a states)
    public double beta_actions = 1; // learning rate for actions
    public double beta_rewards = 1; // learning rate for rewards

    public double rho_inputs = 0.2; // baseline vigilance parameter for inputs (a.k.a states)
    public double rho_actions = 0.2; // baseline vigilance parameter for actions
    public double rho_rewards = 0.5; // baseline vigilance parameter for rewards

    public double rho_epsilon = 0.001;

    public double gamma_inputs = 1; // contribution parameter for inputs (a.k.a states)
    public double gamma_actions = 1; // contribution parameter for actions
    public double gamma_rewards = 1; // contribution parameter for rewards

    public double initialQ = 0.5;

    public FalconConfigType artType = FalconConfigType.FuzzyART;

    public boolean isBounded = true;

}

package com.github.chen0040.art.core;

import java.util.ArrayList;
import java.util.List;

public class ART1 implements Cloneable {

	protected double alpha; // choice parameter
	protected double rho; // resonance threshold
	protected double beta; // learning rate
	protected List<double[]> weights;
	protected List<Double> activation_values;
	protected int inputCount;


	public ART1(){
		weights = new ArrayList<double[]>();
		activation_values = new ArrayList<Double>();
	}

	public ART1(int inputCount, int initialNeuronCount) {
		this.inputCount = inputCount;
		weights = new ArrayList<double[]>();
		activation_values = new ArrayList<Double>();

		for(int i=0; i < initialNeuronCount; ++i){
			addNode();
		}
	}

	public double getAlpha() {
		return alpha;
	}

	public void setAlpha(double alpha) {
		this.alpha = alpha;
	}

	public double getRho() {
		return rho;
	}

	public void setRho(double rho) {
		this.rho = rho;
	}

	public double getBeta() {
		return beta;
	}

	public void setBeta(double beta) {
		this.beta = beta;
	}

	public List<double[]> getWeights() {
		return weights;
	}

	public void setWeights(List<double[]> weights) {
		this.weights = weights;
	}

	public List<Double> getActivation_values() {
		return activation_values;
	}

	public void setActivation_values(List<Double> activation_values) {
		this.activation_values = activation_values;
	}

	public int getInputCount() {
		return inputCount;
	}

	public void setInputCount(int inputCount) {
		this.inputCount = inputCount;
	}

	public void copy(ART1 rhs){

		alpha = rhs.alpha;
		rho = rhs.rho;
		beta = rhs.beta;
		weights.clear();
		activation_values.clear();
		inputCount = rhs.inputCount;

		for(int i=0; i < rhs.weights.size(); ++i){
			weights.add(rhs.weights.get(i).clone());
		}

		for(int i=0; i < rhs.activation_values.size(); ++i){
			activation_values.add(rhs.activation_values.get(i));
		}
	}

	@Override
	public Object clone() throws CloneNotSupportedException {
		ART1 clone = (ART1)super.clone();
		clone.copy(this);
		return clone;
	}

	public void addNode(){
		double[] neuron = new double[inputCount];
		for(int i=0; i < inputCount; ++i){
			neuron[i] = 1;
		}
		weights.add(neuron);
		activation_values.add(0.0);
	}

	public void addNode(double[] x){
		double[] neuron = new double[inputCount];
		for(int i=0; i < inputCount; ++i){
			neuron[i] = x[i];
		}
		weights.add(neuron);
		activation_values.add(0.0);
	}

	protected double choice_function(double[] x, int j){
		double[] W_j = weights.get(j);
		double sum = 0;
		double sum2 = 0;
		for(int i=0; i < x.length; ++i){
			sum += Math.abs(x[i] * W_j[i]); // norm1
			sum2 += Math.abs(W_j[i]); //  norm1
		}

		return sum / (alpha + sum2);
	}

	protected int template_with_max_activation_value(){
		int C = getNodeCount();
		double max_activation_value = 0;
		int template_selected = -1;
		for(int i=0; i < C; ++i){
			double activation_value = activation_values.get(i);
			if(activation_value > max_activation_value){
				max_activation_value = activation_value;
				template_selected = i;
			}
		}
		return template_selected;
	}

	public int getNodeCount(){
		return weights.size();
	}

	protected double match_function(double[] x, int j){
		double[] W_j = weights.get(j);
		double sum = 0;
		double sum2 = 0;
		for(int i=0; i < x.length; ++i){
			sum += Math.abs(x[i] * W_j[i]); // norm1
			sum2 += Math.abs(x[i]); //  norm1
		}

		return sum / sum2;
	}

	protected void update_node(double[] x, int j){
		double[] W_j = weights.get(j);

		for(int i=0; i < x.length; ++i){
			W_j[i] = (1 - beta) * W_j[i] + beta * W_j[i] * x[i];
		}
	}

	public int train(double[] x){
		return simulate(x, true);
	}

	public int simulate(double[] x, boolean can_create_new_node){
		boolean new_node = can_create_new_node;
		int C = getNodeCount();

		int winner = -1;

		if(can_create_new_node) {
			for (int i = 0; i < C; ++i) {
				double activation_value = choice_function(x, i);
				activation_values.set(i, activation_value);
			}

			for (int i = 0; i < C; ++i) {
				int J = template_with_max_activation_value();
				if (J == -1) break;

				double match_value = match_function(x, J);
				if (match_value > rho) {
					update_node(x, J);
					winner = J;
					new_node = false;
					break;
				} else {
					activation_values.set(J, 0.0);
				}
			}

			if (new_node) {
				addNode(x);
				winner = getNodeCount() - 1;
			}
		}else{
			double max_match_value = Double.NEGATIVE_INFINITY;
			int J = -1;
			for (int j = 0; j < C; ++j) {
				double match_value = match_function(x, j);
				if(max_match_value < match_value){
					max_match_value = match_value;
					J = j;
				}
			}
			winner = J;
		}

		return winner;
	}
}

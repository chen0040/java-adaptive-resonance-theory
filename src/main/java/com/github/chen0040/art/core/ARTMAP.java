package com.github.chen0040.art.core;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by xschen on 23/8/15.
 */
public class ARTMAP extends FuzzyART {
    private List<String> labels;
    private double epsilon = 0.00001;

    public void setEpsilon(double epsilon) {
        this.epsilon = epsilon;
    }

    public ARTMAP(int inputCount) {
        super(inputCount, 0);
        labels = new ArrayList<>();
    }

    public ARTMAP(){
        super();
        labels = new ArrayList<>();
    }


    public String simulate(double[] x, String label, boolean can_create_new_node){
        boolean new_node = can_create_new_node;
        int C = getNodeCount();

        String winner = label;

        if(label != null && !labels.contains(label)){
            addNode(x);
            labels.add(label);
        }
        else {
            if(label == null){
                can_create_new_node = false;
            }

            if (can_create_new_node) {
                for (int j = 0; j < C; ++j) {
                    double activation_value = choice_function(x, j);
                    activation_values.set(j, activation_value);
                }

                for (int j = 0; j < C; ++j) {
                    int J = template_with_max_activation_value();
                    if (J == -1) break;

                    String labelJ = labels.get(J);
                    if (!labelJ.equals(label)) {
                        rho = match_function(x, J) + epsilon;
                    }

                    double match_value = match_function(x, J);
                    if (match_value > rho) {
                        update_node(x, J);
                        new_node = false;
                        break;
                    } else {
                        activation_values.set(J, 0.0);
                    }
                }

                if (new_node) {
                    addNode(x);
                    labels.add(label);
                }
            } else {
                double max_match_value = 0;
                int J = -1;
                for (int j = 0; j < C; ++j) {
                    double match_value = match_function(x, j);
                    if (max_match_value < match_value) {
                        max_match_value = match_value;
                        J = j;
                    }
                }
                winner = labels.get(J);
            }
        }

        return winner;
    }
}

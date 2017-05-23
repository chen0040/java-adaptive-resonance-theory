package com.github.chen0040.art.clustering;


import com.github.chen0040.art.core.ART1;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.data.utils.transforms.Standardization;


/**
 * Created by xschen on 21/8/15.
 */
public class ART1Clustering implements Cloneable {
    private ART1 net;
    private int initialNodeCount = 1;
    private boolean allowNewNodeInPrediction = false;
    private Standardization inputNormalization;

    private double alpha = 0.1; // choice parameter
    private double rho0 = 0.9; // base resonance threshold
    private double beta = 0.3; // learning rate

    @Override
    public Object clone() throws CloneNotSupportedException {
        ART1Clustering clone = (ART1Clustering)super.clone();
        clone.copy(this);

        return clone;
    }

    public void copy(ART1Clustering rhs2) throws CloneNotSupportedException {

        net = rhs2.net == null ? null : (ART1)rhs2.net.clone();
        initialNodeCount = rhs2.initialNodeCount;
        allowNewNodeInPrediction = rhs2.allowNewNodeInPrediction;
        inputNormalization = rhs2.inputNormalization == null ? null : (Standardization)rhs2.inputNormalization.clone();
    }

    public ART1Clustering(){

    }


    public int transform(DataRow tuple) {
        return simulate(tuple, allowNewNodeInPrediction);
    }

    public void fit(DataFrame batch) {

        int dimension = batch.row(0).toArray().length;
        inputNormalization = new Standardization(batch);

        net=new ART1(dimension, initialNodeCount);
        net.setAlpha(alpha);
        net.setBeta(beta);
        net.setRho(rho0);

        int m = batch.rowCount();
        for(int i=0; i < m; ++i) {
            DataRow tuple = batch.row(i);
            simulate(tuple, true);
        }
    }

    public int simulate(DataRow tuple, boolean can_create_new_node){
        double[] x = tuple.toArray();
        x = inputNormalization.standardize(x);
        double[] y = binarize(x);
        int clusterId = net.simulate(y, can_create_new_node);
        tuple.setCategoricalTargetCell("predicted", String.format("%d", clusterId));
        return clusterId;
    }

    private double[] binarize(double[] x){
        double[] y = new double[x.length];
        for(int i=0; i < x.length; ++i){
            if(x[i] > 0){
                y[i] = 1;
            }else{
                y[i] = 0;
            }
        }

        return y;
    }

}

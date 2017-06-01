package com.github.chen0040.art.clustering;


import com.github.chen0040.art.core.FuzzyART;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.data.utils.transforms.ComplementaryCoding;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


/**
 * Created by xschen on 21/8/15.
 */
@Getter
@Setter
public class FuzzyARTClustering implements Cloneable {

    @Setter(AccessLevel.NONE)
    private FuzzyART net;

    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private int initialNodeCount = 1;
    @Setter(AccessLevel.NONE)
    @Getter(AccessLevel.NONE)
    private boolean allowNewNodeInPrediction = false;

    @Setter(AccessLevel.NONE)
    private ComplementaryCoding inputNormalization;

    private double alpha = 0.1;
    private double beta = 0.2;
    private double rho = 0.7;

    public void copy(FuzzyARTClustering that) throws CloneNotSupportedException {

        net = that.net == null ? null : (FuzzyART)that.net.clone();
        initialNodeCount = that.initialNodeCount;
        allowNewNodeInPrediction = that.allowNewNodeInPrediction;
        inputNormalization = that.inputNormalization == null ? null : (ComplementaryCoding)that.inputNormalization.clone();
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        FuzzyARTClustering clone = (FuzzyARTClustering)super.clone();
        clone.copy(this);

        return clone;
    }

    public FuzzyARTClustering(){

    }


    public int transform(DataRow tuple) {
        return simulate(tuple, allowNewNodeInPrediction);
    }

    public void transform(DataFrame dataFrame) {
        for(int i=0; i <dataFrame.rowCount(); ++i){
            int clusterId = transform(dataFrame.row(i));
            dataFrame.row(i).setCategoricalTargetCell("cluster", "" + clusterId);
        }
    }

    public DataFrame fitAndTransform(DataFrame dataFrame) {
        fit(dataFrame);
        dataFrame = dataFrame.makeCopy();
        transform(dataFrame);
        return dataFrame;
    }

    public void fit(DataFrame batch) {

        inputNormalization = new ComplementaryCoding(batch);
        int dimension = batch.row(0).toArray().length * 2; // times 2 due to complementary coding

        net=new FuzzyART(dimension, initialNodeCount);
        net.setAlpha(alpha);
        net.setBeta(beta);
        net.setRho(rho);

        int m = batch.rowCount();
        for(int i=0; i < m; ++i) {
            DataRow tuple = batch.row(i);
            simulate(tuple, true);
        }
    }

    public int simulate(DataRow tuple, boolean can_create_node){
        double[] x = tuple.toArray();
        x = inputNormalization.normalize(x);
        return net.simulate(x, can_create_node);
    }
}

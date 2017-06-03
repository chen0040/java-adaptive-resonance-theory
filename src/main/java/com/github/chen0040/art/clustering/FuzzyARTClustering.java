package com.github.chen0040.art.clustering;


import com.github.chen0040.art.core.FuzzyART;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.data.utils.transforms.ComplementaryCoding;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;


/**
 * Created by xschen on 21/8/15.
 */
@Getter
@Setter
public class FuzzyARTClustering {

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

    private int maxClusterCount = -1;

    private double alpha = 0.1;
    private double beta = 0.2;
    private double rho = 0.7;

    private Set<Integer> clusterIds = new HashSet<>();

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
        boolean create_node = true;
        for(int i=0; i < m; ++i) {
            DataRow tuple = batch.row(i);
            int clusterId = simulate(tuple, create_node);

            if(maxClusterCount > 0 && !clusterIds.contains(clusterId) && clusterIds.size() >= maxClusterCount-1){
                create_node = false;
            }

            clusterIds.add(clusterId);
        }
    }

    public int simulate(DataRow tuple, boolean can_create_node){
        double[] x = tuple.toArray();
        x = inputNormalization.normalize(x);
        return net.simulate(x, can_create_node);
    }
}

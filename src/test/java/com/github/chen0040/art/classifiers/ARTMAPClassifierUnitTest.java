package com.github.chen0040.art.classifiers;


import com.github.chen0040.art.utils.FileUtils;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.frame.DataRow;
import org.testng.annotations.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import static org.testng.Assert.*;


/**
 * Created by xschen on 23/5/2017.
 */
public class ARTMAPClassifierUnitTest {

   @Test
   public void TestHeartScale() throws FileNotFoundException {
      InputStream inputStream = FileUtils.getResource("heart_scale");

      DataFrame batch = DataQuery.libsvm().from(inputStream).build();

      batch.unlock();
      for(int i=0; i < batch.rowCount(); ++i){
         DataRow row = batch.row(i);
         row.setCategoricalTargetCell("category-label", "" + row.target());
      }
      batch.lock();


      double best_alpha=0, best_beta=0, best_rho_base=0;
      double predictionAccuracy = 0;
      int nodeCount = batch.rowCount();

      for(double alpha = 8; alpha < 10; alpha += 0.1) {
         for(double beta = 0; beta < 0.5; beta += 0.1) {
            for(double rho = 0.01; rho < 0.05; rho += 0.1){
               ARTMAPClassifier svc = new ARTMAPClassifier();

               svc.setAlpha(alpha);
               svc.setBeta(beta);
               svc.setRho0(rho);

               svc.fit(batch);

               int correctnessCount = 0;
               for(int i = 0; i < batch.rowCount(); ++i){
                  DataRow tuple = batch.row(i);
                  String predicted_label = svc.transform(tuple);
                  //System.out.println("predicted: "+predicted_label+"\texpected: "+tuple.getLabelOutput());
                  correctnessCount += (predicted_label.equals(tuple.categoricalTarget()) ? 1 : 0);
               }

               double accuracy = (correctnessCount * 100 / batch.rowCount());
               if((accuracy > predictionAccuracy && nodeCount / (double)svc.nodeCount() > 0.8) || (accuracy / predictionAccuracy > 0.85 && nodeCount > svc.nodeCount())){
                  best_alpha = alpha;
                  best_beta = beta;
                  best_rho_base = rho;
                  predictionAccuracy = accuracy;
                  nodeCount = svc.nodeCount();
               }

               //System.out.println("Prediction accuracy: " + (correctnessCount * 100 / batch.size()));
            }
         }
      }

      System.out.println("QAlpha: "+best_alpha + "\tbeta: "+best_beta+"\trho_base: "+best_rho_base);
      System.out.println("accuracy: "+predictionAccuracy);
      System.out.println("nodeCount: "+nodeCount);
      System.out.println("dbSize: "+batch.rowCount());



   }
}

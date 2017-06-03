package com.github.chen0040.art.clustering;


import com.github.chen0040.art.utils.FileUtils;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataQuery;
import com.github.chen0040.data.frame.DataRow;
import com.github.chen0040.data.frame.Sampler;
import com.github.chen0040.data.image.ImageDataFrameFactory;
import com.sun.scenario.effect.ImageData;
import org.testng.annotations.Test;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.testng.Assert.*;


/**
 * Created by xschen on 23/5/2017.
 */
public class FuzzyARTClusteringUnitTest {

   private Random rand = new Random();


   @Test
   public void test_image_segmentation() throws IOException {
      BufferedImage img= ImageIO.read(FileUtils.getResource("1.jpg"));

      DataFrame batch = ImageDataFrameFactory.dataFrame(img);

      FuzzyARTClustering cluster = new FuzzyARTClustering();
      cluster.fit(batch);

      List<Integer> classColors = new ArrayList<Integer>();
      for(int i=0; i < 5; ++i){
         for(int j=0; j < 5; ++j){
            classColors.add(ImageDataFrameFactory.get_rgb(255, rand.nextInt(255), rand.nextInt(255), rand.nextInt(255)));
         }
      }

      BufferedImage segmented_image = new BufferedImage(img.getWidth(), img.getHeight(), img.getType());
      for(int x=0; x < img.getWidth(); x++)
      {
         for(int y=0; y < img.getHeight(); y++)
         {
            int rgb = img.getRGB(x, y);

            DataRow tuple = ImageDataFrameFactory.getPixelTuple(x, y, rgb);

            int clusterIndex = cluster.transform(tuple);

            rgb = classColors.get(clusterIndex % classColors.size());

            segmented_image.setRGB(x, y, rgb);
         }
      }
   }

   private static Random random = new Random();

   public static double rand(){
      return random.nextDouble();
   }

   public static double rand(double lower, double upper){
      return rand() * (upper - lower) + lower;
   }

   public static double randn(){
      double u1 = rand();
      double u2 = rand();
      double r = Math.sqrt(-2.0 * Math.log(u1));
      double theta = 2.0 * Math.PI * u2;
      return r * Math.sin(theta);
   }


   // unit testing based on example from http://scikit-learn.org/stable/auto_examples/svm/plot_oneclass.html#
   @Test
   public void testSimple(){


      DataQuery.DataFrameQueryBuilder schema = DataQuery.blank()
              .newInput("c1")
              .newInput("c2")
              .newOutput("designed")
              .end();

      Sampler.DataSampleBuilder negativeSampler = new Sampler()
              .forColumn("c1").generate((name, index) -> randn() * 0.3 + (index % 2 == 0 ? 2 : 4))
              .forColumn("c2").generate((name, index) -> randn() * 0.3 + (index % 2 == 0 ? 2 : 4))
              .forColumn("designed").generate((name, index) -> 0.0)
              .end();

      Sampler.DataSampleBuilder positiveSampler = new Sampler()
              .forColumn("c1").generate((name, index) -> rand(-4, -2))
              .forColumn("c2").generate((name, index) -> rand(-2, -4))
              .forColumn("designed").generate((name, index) -> 1.0)
              .end();

      DataFrame data = schema.build();

      data = negativeSampler.sample(data, 200);
      data = positiveSampler.sample(data, 200);

      System.out.println(data.head(10));

      FuzzyARTClustering algorithm = new FuzzyARTClustering();
      algorithm.setMaxClusterCount(2);

      DataFrame learnedData = algorithm.fitAndTransform(data);

      for(int i = 0; i < learnedData.rowCount(); ++i){
         DataRow tuple = learnedData.row(i);
         String clusterId = tuple.getCategoricalTargetCell("cluster");
         System.out.println("learned: " + clusterId +"\tknown: "+tuple.target());
      }


   }
}

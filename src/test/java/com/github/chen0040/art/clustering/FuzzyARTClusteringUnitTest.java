package com.github.chen0040.art.clustering;


import com.github.chen0040.art.utils.FileUtils;
import com.github.chen0040.data.frame.DataFrame;
import com.github.chen0040.data.frame.DataRow;
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
}

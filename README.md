# java-adaptive-resonance-theory
Package provides java implementation of algorithms in the field of adaptive resonance theory (ART) 

[![Build Status](https://travis-ci.org/chen0040/java-adaptive-resonance-theory.svg?branch=master)](https://travis-ci.org/chen0040/java-adaptive-resonance-theory) [![Coverage Status](https://coveralls.io/repos/github/chen0040/java-adaptive-resonance-theory/badge.svg?branch=master)](https://coveralls.io/github/chen0040/java-adaptive-resonance-theory?branch=master) 

# Install

Add the following dependency to your POM file:

```xml
<dependency>
  <groupId>com.github.chen0040</groupId>
  <artifactId>java-adaptive-resonance-theory</artifactId>
  <version>1.0.2</version>
</dependency>
```

# Features

Algorithms included:

* ART1
* FuzzyART
* ARTMAP

Applications included:

* Clustering (FuzzyART, ART1)
* Multi-class Classification (ARTMAP)

# Usage

### Multi-class Classification using ARTMAP

To create and train a ARTMAP classifier:

```java
ARTMAPClassifier classifier = new ARTMAPClassifier();
clasifier.fit(trainingData);
```

The "trainingData" is a data frame which holds data rows with labeled output (Please refers to this [link](https://github.com/chen0040/java-data-frame) to find out how to store data into a data frame)

To predict using the trained ARTMAP classifier:

```java
String predicted_label = classifier.transform(dataRow);
```

The detail on how to use this can be found in the unit testing codes. Below is a complete sample codes of classifying on the libsvm-formatted heart-scale data:

```java
InputStream inputStream = new FileInputStream("heart_scale");
DataFrame dataFrame = DataQuery.libsvm().from(inputStream).build();

// as the dataFrame obtained thus far has numeric output instead of labeled categorical output, the code below performs the categorical output conversion
dataFrame.unlock();
for(int i=0; i < dataFrame.rowCount(); ++i){
 DataRow row = dataFrame.row(i);
 row.setCategoricalTargetCell("category-label", "" + row.target());
}
dataFrame.lock();

double alpha = 9.89;
double beta = 0.3;
double rho = 0.01;
classifier.setAlpha(alpha);
classifier.setBeta(beta);
classifier.setRho0(rho);

classifier.fit(dataFrame);

for(int i = 0; i < dataFrame.rowCount(); ++i){
  DataRow tuple = dataFrame.row(i);
  String predicted_label = classifier.transform(tuple);
  System.out.println("predicted: "+predicted_label+"\tactual: "+tuple.categoricalTarget());
}

```

### Image Segmentation (Clustering) using FuzzyART

The following sample code shows how to use FuzzyART to perform image segmentation:

```java
BufferedImage img= ImageIO.read(FileUtils.getResource("1.jpg"));

DataFrame dataFrame = ImageDataFrameFactory.dataFrame(img);

FuzzyARTClustering cluster = new FuzzyARTClustering();
cluster.fit(dataFrame);

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

    DataRow tuple = ImageDataFrameFactory.getPixelTuple(dataFrame, rgb);

    int clusterIndex = cluster.transform(tuple);

    rgb = classColors.get(clusterIndex % classColors.size());

    segmented_image.setRGB(x, y, rgb);
 }
}
```





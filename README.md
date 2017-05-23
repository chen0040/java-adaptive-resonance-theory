# java-adaptive-resonance-theory
Package provides java implementation of algorithms in the field of adaptive resonance theory (ART) 

[![Build Status](https://travis-ci.org/chen0040/java-adaptive-resonance-theory.svg?branch=master)](https://travis-ci.org/chen0040/java-adaptive-resonance-theory) [![Coverage Status](https://coveralls.io/repos/github/chen0040/java-adaptive-resonance-theory/badge.svg?branch=master)](https://coveralls.io/github/chen0040/java-adaptive-resonance-theory?branch=master) 

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

The "trainingData" is a data frame which holds data rows with labeled output (Please refers to this link to find out how to store data into a data frame)

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





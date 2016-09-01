[![Build Status](https://travis-ci.org/tudarmstadt-lt/AB-Sentiment.svg?branch=master)](https://travis-ci.org/tudarmstadt-lt/AB-Sentiment) [![Release](https://jitpack.io/v/tudarmstadt-lt/AB-Sentiment.svg)](https://jitpack.io/#tudarmstadt-lt/AB-Sentiment)
# Aspect-Based Sentiment Analysis

##  Overview
This software package performs aspect-based sentiment analysis. It can analyze documents and identify aspect targets, their aspect category and their relevance. For usage in live systems, it features a relevance classifier to filter irrelevant documents.
JavaDoc documentation is available on the [documentation page](http://tudarmstadt-lt.github.io/AB-Sentiment/doc/).

## Quickstart

* create a new project
* add the jitpack Maven dependency
```
	<repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>
```
* add the AB-Sentiment dependency
```
	<dependency>
	    <groupId>com.github.tudarmstadt-lt</groupId>
	    <artifactId>AB-Sentiment</artifactId>
	    <version>-SNAPSHOT</version>
	</dependency>
```
* copy models into your project home
* create a Java class for analysis:
```
import tudarmstadt.lt.ABSentiment.type.Result;
import tudarmstadt.lt.ABSentiment.AbSentiment;

public class MyClass {

    public static void main(String[] args) {
        AbSentiment analyzer = new AbSentiment();

        Result result = analyzer.analyzeText("This is the input string");

        // get Sentiment of text
        System.out.println(result.getSentiment());
        System.out.println(result.getSentimentScore());

    }
}
```
* analyze aspects and sentiment :)


## Licence
This software is published under the Apache Software Licence 2.0
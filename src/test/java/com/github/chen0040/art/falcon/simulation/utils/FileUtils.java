package com.github.chen0040.art.falcon.simulation.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import static java.util.stream.Collectors.toList;

/**
 * Created by memeanalytics on 12/8/15.
 */
public class FileUtils {
    public static List<String> getLines(String fileName) {

        List<String> lines = null;

        //Get file from resources folder
        ClassLoader classLoader = FileUtils.class.getClassLoader();


        try(BufferedReader reader = new BufferedReader(new InputStreamReader(classLoader.getResourceAsStream(fileName)))){
            lines = reader.lines().collect(toList());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return lines;

    }

    public static File getResourceFile(String filename) {
        //Get file from resources folder
        ClassLoader classLoader = FileUtils.class.getClassLoader();

        return new File(classLoader.getResource(filename).getFile());
    }
}

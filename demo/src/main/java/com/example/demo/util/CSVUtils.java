package com.example.demo.util;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.example.demo.model.Person;


public class CSVUtils {

    public static final String CSV_SEPARATOR = ",";
    public static final int NUM_PERSON_COLUMNS = 3;

    private CSVUtils() {
    }

    public static Person fromPersonString(String[] personData) {
        if (personData.length != NUM_PERSON_COLUMNS) {
            return null;
        }
        return new Person(Long.valueOf(personData[0]), personData[1], personData[2]);
    }

    public static String toPersonString(Person person) {
        List<String> values = Arrays.asList(person.getPersonId().toString(), person.getFirstName(), person.getLastName());
        boolean first = true;
        StringBuilder sb = new StringBuilder();
        for (String value : values) {
            if (first)
                first = false;
            else
                sb.append(CSV_SEPARATOR);
            sb.append(value);
        }
        sb.append("\n");
        return sb.toString();
    }


    public static File getFile() {
        try {
            File myObj = new File("D:\\trainning\\Java-SpringBatch\\demo\\src\\main\\resources\\output.csv");
            if (myObj.createNewFile()) {
                System.out.println("File created: " + myObj.getName());
            } else {
                System.out.println("File already exists.");
            }
            return myObj;
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return null;
    }
}

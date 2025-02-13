package com.efrei.autograder;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.net.URL;
import java.util.Enumeration;

public class AutogradingJsonGenerator {

    public static void main(String[] args) {
        // Get all test classes from the magasin package
        List<Class<?>> testClasses = findTestClasses("magasin");
        
        // Generate the autograding.json content
        String jsonContent = generateAutogradingJson(testClasses);

        // Write the content to autograding.json
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(".github/classroom/autograding.json"))) {
            writer.write(jsonContent);
            System.out.println("autograding.json generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static List<Class<?>> findTestClasses(String packageName) {
        List<Class<?>> testClasses = new ArrayList<>();
        try {
            ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
            String path = packageName.replace('.', '/');
            Enumeration<URL> resources = classLoader.getResources(path);
            
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                File directory = new File(resource.getFile());
                
                if (directory.exists()) {
                    File[] files = directory.listFiles();
                    if (files != null) {
                        for (File file : files) {
                            String fileName = file.getName();
                            if (fileName.endsWith("Test.class")) {
                                String className = packageName + "." + fileName.substring(0, fileName.length() - 6);
                                Class<?> testClass = Class.forName(className);
                                testClasses.add(testClass);
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return testClasses;
    }

    private static String generateAutogradingJson(List<Class<?>> testClasses) {
        StringBuilder jsonBuilder = new StringBuilder();
        jsonBuilder.append("{\n  \"tests\": [\n");

        List<String> allTestEntries = new ArrayList<>();

        // Generate JSON entries for each test class
        for (Class<?> testClass : testClasses) {
            Method[] methods = testClass.getDeclaredMethods();
            String className = testClass.getName();

            // Collect test method names for this class
            for (Method method : methods) {
                if (method.isAnnotationPresent(org.junit.jupiter.api.Test.class)) {
                    StringBuilder testEntry = new StringBuilder();
                    testEntry.append("    {\n")
                            .append("      \"name\": \"")
                            .append(className)
                            .append(".")
                            .append(method.getName())
                            .append("\",\n")
                            .append("      \"setup\": \"\",\n")
                            .append("      \"run\": \"./gradlew test --tests ")
                            .append(className)
                            .append(".")
                            .append(method.getName())
                            .append("\",\n")
                            .append("      \"input\": \"\",\n")
                            .append("      \"output\": \"PASSED\",\n")
                            .append("      \"comparison\": \"included\",\n")
                            .append("      \"timeout\": 10,\n")
                            .append("      \"points\": 1\n")
                            .append("    }");
                    
                    allTestEntries.add(testEntry.toString());
                }
            }
        }

        // Join all test entries with commas
        jsonBuilder.append(String.join(",\n", allTestEntries));
        jsonBuilder.append("\n  ]\n}");
        
        return jsonBuilder.toString();
    }
}
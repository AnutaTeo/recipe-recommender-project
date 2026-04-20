package com.example.recipe_recommender.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Component
public class XmlValidationRunner implements CommandLineRunner {

    private final XmlValidator xmlValidator;

    public XmlValidationRunner(XmlValidator xmlValidator) {
        this.xmlValidator = xmlValidator;
    }

    @Override
    public void run(String... args) {
        File recipesXml = getDataFile("recipes.xml");
        File recipesXsd = getDataFile("recipes.xsd");
        File usersXml = getDataFile("users.xml");
        File usersXsd = getDataFile("users.xsd");

        boolean recipesValid = xmlValidator.validate(
                recipesXml.getAbsolutePath(),
                recipesXsd.getAbsolutePath()
        );

        boolean usersValid = xmlValidator.validate(
                usersXml.getAbsolutePath(),
                usersXsd.getAbsolutePath()
        );

        System.out.println("Recipes XML valid: " + recipesValid);
        System.out.println("Users XML valid: " + usersValid);
    }

    private File getDataFile(String fileName) {
        File baseDir = new File(System.getProperty("user.dir"));

        if (!new File(baseDir, "src/main/resources/data").exists()) {
            baseDir = new File(baseDir, "recipe-recommender");
        }

        return new File(baseDir, "src/main/resources/data/" + fileName);
    }
}
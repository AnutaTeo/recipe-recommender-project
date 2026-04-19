package com.example.recipe_recommender.runner;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class XmlValidationRunner implements CommandLineRunner {

    private final XmlValidator xmlValidator;

    public XmlValidationRunner(XmlValidator xmlValidator) {
        this.xmlValidator = xmlValidator;
    }

    @Override
    public void run(String... args) {
        String base = System.getProperty("user.dir") + "/src/main/resources/data/";

        boolean recipesValid = xmlValidator.validate(
                base + "recipes.xml",
                base + "recipes.xsd"
        );

        boolean usersValid = xmlValidator.validate(
                base + "users.xml",
                base + "users.xsd"
        );

        System.out.println("Recipes XML valid: " + recipesValid);
        System.out.println("Users XML valid: " + usersValid);
    }
}
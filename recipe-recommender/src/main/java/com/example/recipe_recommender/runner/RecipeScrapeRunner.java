package com.example.recipe_recommender.runner;

import com.example.recipe_recommender.model.Recipe;
import com.example.recipe_recommender.service.RecipeScraperService;
import com.example.recipe_recommender.service.RecipeXmlWriterService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

//@Component
public class RecipeScrapeRunner implements CommandLineRunner {

    private final RecipeScraperService recipeScraperService;
    private final RecipeXmlWriterService recipeXmlWriterService;

    public RecipeScrapeRunner(RecipeScraperService recipeScraperService,
                              RecipeXmlWriterService recipeXmlWriterService) {
        this.recipeScraperService = recipeScraperService;
        this.recipeXmlWriterService = recipeXmlWriterService;
    }

    @Override
    public void run(String... args) throws Exception {
        List<Recipe> recipes = recipeScraperService.scrapeRecipes();

        if (recipes.size() < 20) {
            throw new IllegalStateException(
                    "Scraping produced fewer than 20 recipes. Found: " + recipes.size()
            );
        }

        String outputPath = System.getProperty("user.dir") + "/src/main/resources/data/recipes.xml";
        recipeXmlWriterService.writeRecipesToXml(recipes, outputPath);

        System.out.println("Successfully scraped and saved " + recipes.size() + " recipes.");
        System.out.println("XML saved at: " + outputPath);

    }
}
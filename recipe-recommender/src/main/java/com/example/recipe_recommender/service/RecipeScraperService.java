package com.example.recipe_recommender.service;

import com.example.recipe_recommender.model.Recipe;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class RecipeScraperService {

    private static final String BBC_URL =
            "https://www.bbcgoodfood.com/recipes/collection/budget-autumn";

    private static final List<String> CUISINE_POOL = Arrays.asList(
            "Italian", "Asian", "Mexican", "Indian", "French",
            "Mediterranean", "European", "American", "Middle Eastern", "British"
    );

    private static final List<String> DIFFICULTY_POOL = Arrays.asList(
            "Beginner", "Intermediate", "Advanced"
    );

    public List<Recipe> scrapeRecipes() throws IOException {
        Document document = Jsoup.connect(BBC_URL)
                .userAgent("Mozilla/5.0")
                .timeout(15000)
                .get();

        Set<String> uniqueTitles = extractTitles(document);

        List<Recipe> recipes = new ArrayList<>();
        int index = 1;

        for (String title : uniqueTitles) {
            Recipe recipe = new Recipe();
            recipe.setId("r" + index);
            recipe.setTitle(cleanTitle(title));
            recipe.setCuisineTypes(generateTwoDistinctCuisines(index));
            recipe.setDifficultyLevel(generateOneDifficulty(index));
            recipes.add(recipe);
            index++;
        }

        return recipes;
    }

    private Set<String> extractTitles(Document document) {
        Set<String> titles = new LinkedHashSet<>();

        Elements links = document.select("a[href]");

        for (Element link : links) {
            String text = link.text().trim();

            if (isValidRecipeTitle(text)) {
                titles.add(text);
            }
        }

        return titles;
    }

    private boolean isValidRecipeTitle(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }

        if (text.length() < 4 || text.length() > 120) {
            return false;
        }

        String lower = text.toLowerCase();

        if (lower.contains("subscribe")
                || lower.contains("back to")
                || lower.contains("newsletter")
                || lower.contains("recipes")
                || lower.contains("healthy eating")
                || lower.contains("budget")
                || lower.contains("learn more")
                || lower.contains("whatsapp")
                || lower.contains("download our app")
                || lower.contains("good food")
                || lower.contains("premium piece of content")) {
            return false;
        }

        boolean looksLikeRecipeLink = lower.contains("pasta")
                || lower.contains("hash")
                || lower.contains("stroganoff")
                || lower.contains("risotto")
                || lower.contains("soup")
                || lower.contains("curry")
                || lower.contains("flatbreads")
                || lower.contains("casserole")
                || lower.contains("beans")
                || lower.contains("fusilli")
                || lower.contains("burgers")
                || lower.contains("tagliatelle")
                || lower.contains("shells")
                || lower.contains("salad")
                || lower.contains("pie")
                || lower.contains("stew")
                || lower.contains("roast")
                || lower.contains("bake")
                || lower.contains("gratin")
                || lower.contains("crumble")
                || lower.contains("chilli")
                || lower.contains("tart");

        boolean startsWithCapital = Character.isUpperCase(text.charAt(0));

        return looksLikeRecipeLink && startsWithCapital;
    }

    private String cleanTitle(String rawTitle) {
        return rawTitle.trim();
    }

    private List<String> generateTwoDistinctCuisines(int seed) {
        Random random = new Random(seed * 31L + 7);
        List<String> cuisines = new ArrayList<>();

        while (cuisines.size() < 2) {
            String cuisine = CUISINE_POOL.get(random.nextInt(CUISINE_POOL.size()));
            if (!cuisines.contains(cuisine)) {
                cuisines.add(cuisine);
            }
        }

        return cuisines;
    }

    private String generateOneDifficulty(int seed) {
        Random random = new Random(seed * 17L + 3);
        return DIFFICULTY_POOL.get(random.nextInt(DIFFICULTY_POOL.size()));
    }
}
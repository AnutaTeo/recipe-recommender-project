package com.example.recipe_recommender.controller;

import com.example.recipe_recommender.model.Recipe;
import com.example.recipe_recommender.service.RecipeService;
import com.example.recipe_recommender.service.XPathService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
@Controller
public class RecipeController {

    private final RecipeService recipeService;
    private final XPathService xPathService;

    public RecipeController(RecipeService recipeService, XPathService xPathService) {
        this.recipeService = recipeService;
        this.xPathService = xPathService;
    }

    @GetMapping("/recipes")
    public String showRecipes(Model model) {
        List<Recipe> recipes = recipeService.getAllRecipes();
        model.addAttribute("recipes", recipes);
        return "recipes";
    }

    @GetMapping("/recipes/add")
    public String showAddRecipeForm(Model model) {
        model.addAttribute("recipe", new Recipe());
        return "add-recipe";
    }

    @PostMapping("/recipes/add")
    public String addRecipe(@ModelAttribute Recipe recipe, Model model) {
        String validationError = recipeService.validateRecipe(recipe);

        if (validationError != null) {
            model.addAttribute("error", validationError);
            model.addAttribute("recipe", recipe);
            return "add-recipe";
        }

        recipeService.addRecipe(recipe);
        return "redirect:/recipes";
    }

    @GetMapping("/recipes/recommend/skill")
    public String recommendBySkill(Model model) {
        List<Recipe> recommendedRecipes = xPathService.getRecipesRecommendedBySkillLevel();
        String userSkillLevel = xPathService.getFirstUserSkillLevel();

        model.addAttribute("recipes", recommendedRecipes);
        model.addAttribute("userSkillLevel", userSkillLevel);

        return "recommendations-skill";
    }

    @GetMapping("/recipes/recommend/full")
    public String recommendBySkillAndCuisine(Model model) {
        List<Recipe> recommendedRecipes = xPathService.getRecipesRecommendedBySkillAndCuisine();
        String userSkillLevel = xPathService.getFirstUserSkillLevel();
        String preferredCuisine = xPathService.getFirstUserPreferredCuisine();

        model.addAttribute("recipes", recommendedRecipes);
        model.addAttribute("userSkillLevel", userSkillLevel);
        model.addAttribute("preferredCuisine", preferredCuisine);

        return "recommendations-full";
    }
}
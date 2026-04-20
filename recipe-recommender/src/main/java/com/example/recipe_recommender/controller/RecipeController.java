package com.example.recipe_recommender.controller;

import com.example.recipe_recommender.model.Recipe;
import com.example.recipe_recommender.model.User;
import com.example.recipe_recommender.service.RecipeService;
import com.example.recipe_recommender.service.UserService;
import com.example.recipe_recommender.service.XPathService;
import com.example.recipe_recommender.service.XslService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
public class RecipeController {

    private final RecipeService recipeService;
    private final XPathService xPathService;
    private final XslService xslService;
    private final UserService userService;

    public RecipeController(RecipeService recipeService,
                            XPathService xPathService,
                            XslService xslService,
                            UserService userService) {
        this.recipeService = recipeService;
        this.xPathService = xPathService;
        this.xslService = xslService;
        this.userService = userService;
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

    @GetMapping("/recipes/xsl")
    public String showRecipesWithXsl(Model model) {
        String userSkillLevel = xPathService.getFirstUserSkillLevel();

        String transformedHtml = xslService.transformRecipesXmlToHtml(userSkillLevel);

        model.addAttribute("transformedHtml", transformedHtml);
        return "xsl-recipes";
    }

    @GetMapping("/recipes/{id}")
    public String showRecipeDetails(@PathVariable String id, Model model) {
        Recipe recipe = xPathService.getRecipeById(id);

        if (recipe == null) {
            model.addAttribute("errorMessage", "Recipe not found.");
            return "recipe-details";
        }

        model.addAttribute("recipe", recipe);
        return "recipe-details";
    }

    @GetMapping("/recipes/filter/cuisine")
    public String showCuisineFilterPage() {
        return "cuisine-filter";
    }

    @PostMapping("/recipes/filter/cuisine")
    public String filterByCuisine(@RequestParam String cuisineType, Model model) {
        List<Recipe> filteredRecipes = xPathService.getRecipesByCuisineType(cuisineType);

        model.addAttribute("recipes", filteredRecipes);
        model.addAttribute("selectedCuisine", cuisineType);

        return "cuisine-filter";
    }

    @GetMapping("/recipes/xsl/select")
    public String showXslUserSelection(Model model) {
        List<User> users = userService.getAllUsers();
        model.addAttribute("users", users);
        return "xsl-user-select";
    }

    @PostMapping("/recipes/xsl/select")
    public String showRecipesWithSelectedUser(@RequestParam("userId") String userId, Model model) {
        User selectedUser = userService.getUserById(userId);

        if (selectedUser == null) {
            model.addAttribute("errorMessage", "Selected user was not found.");
            model.addAttribute("users", userService.getAllUsers());
            return "xsl-user-select";
        }

        String transformedHtml = xslService.transformRecipesXmlToHtml(selectedUser.getCookingSkillLevel());

        model.addAttribute("transformedHtml", transformedHtml);
        model.addAttribute("selectedUserName", selectedUser.getName() + " " + selectedUser.getSurname());
        model.addAttribute("selectedUserSkill", selectedUser.getCookingSkillLevel());

        return "xsl-recipes-selected";
    }
}
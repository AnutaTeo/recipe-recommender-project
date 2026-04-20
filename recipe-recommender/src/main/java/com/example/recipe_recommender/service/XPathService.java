package com.example.recipe_recommender.service;

import com.example.recipe_recommender.model.Recipe;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class XPathService {

    public String getFirstUserSkillLevel() {
        try {
            Document document = loadUsersDocument();
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expression = "/users/user[1]/cookingSkillLevel/text()";
            return xpath.evaluate(expression, document).trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getFirstUserPreferredCuisine() {
        try {
            Document document = loadUsersDocument();
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expression = "/users/user[1]/preferredCuisineType/text()";
            return xpath.evaluate(expression, document).trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public List<Recipe> getRecipesRecommendedBySkillLevel() {
        List<Recipe> recipes = new ArrayList<>();

        try {
            String skillLevel = getFirstUserSkillLevel();

            Document document = loadRecipesDocument();
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expression = "/recipes/recipe[difficultyLevel='" + skillLevel + "']";
            NodeList nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

            recipes = convertNodeListToRecipes(nodeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recipes;
    }

    public List<Recipe> getRecipesRecommendedBySkillAndCuisine() {
        List<Recipe> recipes = new ArrayList<>();

        try {
            String skillLevel = getFirstUserSkillLevel();
            String preferredCuisine = getFirstUserPreferredCuisine();

            Document document = loadRecipesDocument();
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expression = "/recipes/recipe[difficultyLevel='" + skillLevel +
                    "' and cuisineTypes/cuisineType='" + preferredCuisine + "']";

            NodeList nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

            recipes = convertNodeListToRecipes(nodeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recipes;
    }

    public Recipe getRecipeById(String recipeId) {
        try {
            Document document = loadRecipesDocument();
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expression = "/recipes/recipe[@id='" + recipeId + "']";
            Node node = (Node) xpath.evaluate(expression, document, XPathConstants.NODE);

            if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
                return convertElementToRecipe((Element) node);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<Recipe> getRecipesByCuisineType(String cuisineType) {
        List<Recipe> recipes = new ArrayList<>();

        try {
            Document document = loadRecipesDocument();
            XPath xpath = XPathFactory.newInstance().newXPath();

            String expression = "/recipes/recipe[cuisineTypes/cuisineType='" + cuisineType + "']";
            NodeList nodeList = (NodeList) xpath.evaluate(expression, document, XPathConstants.NODESET);

            recipes = convertNodeListToRecipes(nodeList);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recipes;
    }

    private Document loadUsersDocument() throws Exception {
        ClassPathResource resource = new ClassPathResource("data/users.xml");
        InputStream inputStream = resource.getInputStream();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        document.getDocumentElement().normalize();

        return document;
    }

    private Document loadRecipesDocument() throws Exception {
        ClassPathResource resource = new ClassPathResource("data/recipes.xml");
        InputStream inputStream = resource.getInputStream();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(inputStream);
        document.getDocumentElement().normalize();

        return document;
    }

    private List<Recipe> convertNodeListToRecipes(NodeList nodeList) {
        List<Recipe> recipes = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                recipes.add(convertElementToRecipe((Element) node));
            }
        }

        return recipes;
    }

    private Recipe convertElementToRecipe(Element recipeElement) {
        Recipe recipe = new Recipe();
        recipe.setId(recipeElement.getAttribute("id"));
        recipe.setTitle(getTagValue("title", recipeElement));

        List<String> cuisineTypes = new ArrayList<>();
        NodeList cuisineNodes = recipeElement.getElementsByTagName("cuisineType");
        for (int j = 0; j < cuisineNodes.getLength(); j++) {
            cuisineTypes.add(cuisineNodes.item(j).getTextContent());
        }
        recipe.setCuisineTypes(cuisineTypes);

        recipe.setDifficultyLevel(getTagValue("difficultyLevel", recipeElement));

        return recipe;
    }

    private String getTagValue(String tagName, Element parent) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}
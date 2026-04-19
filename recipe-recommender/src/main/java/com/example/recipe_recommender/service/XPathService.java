package com.example.recipe_recommender.service;

import com.example.recipe_recommender.model.Recipe;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class XPathService {

    private final String recipesPath = System.getProperty("user.dir") + "/src/main/resources/data/recipes.xml";
    private final String usersPath = System.getProperty("user.dir") + "/src/main/resources/data/users.xml";

    public String getFirstUserSkillLevel() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            InputSource inputSource = new InputSource(new File(usersPath).getAbsolutePath());

            String expression = "/users/user[1]/cookingSkillLevel/text()";
            return xpath.evaluate(expression, inputSource).trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public String getFirstUserPreferredCuisine() {
        try {
            XPath xpath = XPathFactory.newInstance().newXPath();
            InputSource inputSource = new InputSource(new File(usersPath).getAbsolutePath());

            String expression = "/users/user[1]/preferredCuisineType/text()";
            return xpath.evaluate(expression, inputSource).trim();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    public List<Recipe> getRecipesRecommendedBySkillLevel() {
        List<Recipe> recipes = new ArrayList<>();

        try {
            String skillLevel = getFirstUserSkillLevel();

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(recipesPath));
            document.getDocumentElement().normalize();

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

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(new File(recipesPath));
            document.getDocumentElement().normalize();

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

    private List<Recipe> convertNodeListToRecipes(NodeList nodeList) {
        List<Recipe> recipes = new ArrayList<>();

        for (int i = 0; i < nodeList.getLength(); i++) {
            Node node = nodeList.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element recipeElement = (Element) node;

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

                recipes.add(recipe);
            }
        }

        return recipes;
    }

    private String getTagValue(String tagName, Element parent) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}
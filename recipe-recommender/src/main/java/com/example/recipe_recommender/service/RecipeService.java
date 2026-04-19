package com.example.recipe_recommender.service;

import com.example.recipe_recommender.model.Recipe;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        try {
            String filePath = System.getProperty("user.dir") + "/src/main/resources/data/recipes.xml";
            File xmlFile = new File(filePath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            document.getDocumentElement().normalize();

            NodeList recipeNodes = document.getElementsByTagName("recipe");

            for (int i = 0; i < recipeNodes.getLength(); i++) {
                Node node = recipeNodes.item(i);

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

        } catch (Exception e) {
            e.printStackTrace();
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
package com.example.recipe_recommender.service;

import com.example.recipe_recommender.model.Recipe;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class RecipeService {

    public List<Recipe> getAllRecipes() {
        List<Recipe> recipes = new ArrayList<>();

        try {
            Document document = loadRecipesDocument();
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

                    if (cuisineTypes.size() >= 2) {
                        recipe.setCuisineType1(cuisineTypes.get(0));
                        recipe.setCuisineType2(cuisineTypes.get(1));
                    }

                    recipe.setDifficultyLevel(getTagValue("difficultyLevel", recipeElement));

                    recipes.add(recipe);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return recipes;
    }

    public String validateRecipe(Recipe recipe) {
        if (recipe.getTitle() == null || recipe.getTitle().trim().isEmpty()) {
            return "Title is required.";
        }

        if (recipe.getCuisineType1() == null || recipe.getCuisineType1().trim().isEmpty()) {
            return "First cuisine type is required.";
        }

        if (recipe.getCuisineType2() == null || recipe.getCuisineType2().trim().isEmpty()) {
            return "Second cuisine type is required.";
        }

        if (recipe.getCuisineType1().trim().equalsIgnoreCase(recipe.getCuisineType2().trim())) {
            return "The two cuisine types must be different.";
        }

        if (recipe.getDifficultyLevel() == null || recipe.getDifficultyLevel().trim().isEmpty()) {
            return "Difficulty level is required.";
        }

        List<String> allowedDifficulties = List.of("Beginner", "Intermediate", "Advanced");
        if (!allowedDifficulties.contains(recipe.getDifficultyLevel())) {
            return "Difficulty level must be Beginner, Intermediate, or Advanced.";
        }

        return null;
    }

    public void addRecipe(Recipe recipe) {
        try {
            File xmlFile = getDataFile("recipes.xml");

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();

            Element recipeElement = document.createElement("recipe");
            recipeElement.setAttribute("id", generateNextRecipeId(document));

            Element titleElement = document.createElement("title");
            titleElement.setTextContent(recipe.getTitle().trim());
            recipeElement.appendChild(titleElement);

            Element cuisineTypesElement = document.createElement("cuisineTypes");

            Element cuisine1 = document.createElement("cuisineType");
            cuisine1.setTextContent(recipe.getCuisineType1().trim());
            cuisineTypesElement.appendChild(cuisine1);

            Element cuisine2 = document.createElement("cuisineType");
            cuisine2.setTextContent(recipe.getCuisineType2().trim());
            cuisineTypesElement.appendChild(cuisine2);

            recipeElement.appendChild(cuisineTypesElement);

            Element difficultyElement = document.createElement("difficultyLevel");
            difficultyElement.setTextContent(recipe.getDifficultyLevel().trim());
            recipeElement.appendChild(difficultyElement);

            root.appendChild(recipeElement);

            saveDocument(document, xmlFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Document loadRecipesDocument() throws Exception {
        File xmlFile = getDataFile("recipes.xml");

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.parse(xmlFile);
        document.getDocumentElement().normalize();

        return document;
    }

    private File getDataFile(String fileName) {
        File baseDir = new File(System.getProperty("user.dir"));

        if (!new File(baseDir, "src/main/resources/data").exists()) {
            baseDir = new File(baseDir, "recipe-recommender");
        }

        File file = new File(baseDir, "src/main/resources/data/" + fileName);
        System.out.println("Resolved file path: " + file.getAbsolutePath());
        return file;
    }

    private String generateNextRecipeId(Document document) {
        NodeList recipeNodes = document.getElementsByTagName("recipe");
        int maxId = 0;

        for (int i = 0; i < recipeNodes.getLength(); i++) {
            Node node = recipeNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element recipeElement = (Element) node;
                String id = recipeElement.getAttribute("id");

                if (id != null && id.startsWith("r")) {
                    try {
                        int numericPart = Integer.parseInt(id.substring(1));
                        if (numericPart > maxId) {
                            maxId = numericPart;
                        }
                    } catch (NumberFormatException ignored) {
                    }
                }
            }
        }

        return "r" + (maxId + 1);
    }

    private void saveDocument(Document document, File xmlFile) throws Exception {
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();

        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(xmlFile);
        transformer.transform(source, result);
    }

    private String getTagValue(String tagName, Element parent) {
        NodeList nodeList = parent.getElementsByTagName(tagName);
        if (nodeList.getLength() > 0) {
            return nodeList.item(0).getTextContent();
        }
        return "";
    }
}
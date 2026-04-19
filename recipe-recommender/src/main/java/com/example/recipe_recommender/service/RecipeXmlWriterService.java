package com.example.recipe_recommender.service;

import com.example.recipe_recommender.model.Recipe;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@Service
public class RecipeXmlWriterService {

    public void writeRecipesToXml(List<Recipe> recipes, String outputPath) throws Exception {
        Path path = Path.of(outputPath);
        Files.createDirectories(path.getParent());

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();

        Element root = document.createElement("recipes");
        document.appendChild(root);

        for (Recipe recipe : recipes) {
            Element recipeElement = document.createElement("recipe");
            recipeElement.setAttribute("id", recipe.getId());

            Element titleElement = document.createElement("title");
            titleElement.setTextContent(recipe.getTitle());
            recipeElement.appendChild(titleElement);

            Element cuisineTypesElement = document.createElement("cuisineTypes");
            for (String cuisine : recipe.getCuisineTypes()) {
                Element cuisineElement = document.createElement("cuisineType");
                cuisineElement.setTextContent(cuisine);
                cuisineTypesElement.appendChild(cuisineElement);
            }
            recipeElement.appendChild(cuisineTypesElement);

            Element difficultyElement = document.createElement("difficultyLevel");
            difficultyElement.setTextContent(recipe.getDifficultyLevel());
            recipeElement.appendChild(difficultyElement);

            root.appendChild(recipeElement);
        }

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "4");

        DOMSource source = new DOMSource(document);
        StreamResult result = new StreamResult(path.toFile());
        transformer.transform(source, result);
    }
}
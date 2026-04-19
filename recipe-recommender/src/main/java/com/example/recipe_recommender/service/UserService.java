package com.example.recipe_recommender.service;

import com.example.recipe_recommender.model.User;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final String filePath = System.getProperty("user.dir") + "/src/main/resources/data/users.xml";

    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();

        try {
            File xmlFile = new File(filePath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            document.getDocumentElement().normalize();

            NodeList userNodes = document.getElementsByTagName("user");

            for (int i = 0; i < userNodes.getLength(); i++) {
                Node node = userNodes.item(i);

                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element userElement = (Element) node;

                    User user = new User();
                    user.setId(userElement.getAttribute("id"));
                    user.setName(getTagValue("name", userElement));
                    user.setSurname(getTagValue("surname", userElement));
                    user.setCookingSkillLevel(getTagValue("cookingSkillLevel", userElement));
                    user.setPreferredCuisineType(getTagValue("preferredCuisineType", userElement));

                    users.add(user);
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public String validateUser(User user) {
        if (user.getName() == null || user.getName().trim().isEmpty()) {
            return "Name is required.";
        }

        if (user.getSurname() == null || user.getSurname().trim().isEmpty()) {
            return "Surname is required.";
        }

        if (user.getCookingSkillLevel() == null || user.getCookingSkillLevel().trim().isEmpty()) {
            return "Cooking skill level is required.";
        }

        List<String> allowedLevels = List.of("Beginner", "Intermediate", "Advanced");
        if (!allowedLevels.contains(user.getCookingSkillLevel())) {
            return "Cooking skill level must be Beginner, Intermediate, or Advanced.";
        }

        if (user.getPreferredCuisineType() == null || user.getPreferredCuisineType().trim().isEmpty()) {
            return "Preferred cuisine type is required.";
        }

        return null;
    }

    public void addUser(User user) {
        try {
            File xmlFile = new File(filePath);

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document document = builder.parse(xmlFile);

            document.getDocumentElement().normalize();

            Element root = document.getDocumentElement();

            Element userElement = document.createElement("user");
            userElement.setAttribute("id", generateNextUserId(document));

            Element nameElement = document.createElement("name");
            nameElement.setTextContent(user.getName().trim());
            userElement.appendChild(nameElement);

            Element surnameElement = document.createElement("surname");
            surnameElement.setTextContent(user.getSurname().trim());
            userElement.appendChild(surnameElement);

            Element skillElement = document.createElement("cookingSkillLevel");
            skillElement.setTextContent(user.getCookingSkillLevel().trim());
            userElement.appendChild(skillElement);

            Element cuisineElement = document.createElement("preferredCuisineType");
            cuisineElement.setTextContent(user.getPreferredCuisineType().trim());
            userElement.appendChild(cuisineElement);

            root.appendChild(userElement);

            saveDocument(document, xmlFile);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String generateNextUserId(Document document) {
        NodeList userNodes = document.getElementsByTagName("user");
        int maxId = 0;

        for (int i = 0; i < userNodes.getLength(); i++) {
            Node node = userNodes.item(i);

            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element userElement = (Element) node;
                String id = userElement.getAttribute("id");

                if (id != null && id.startsWith("u")) {
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

        return "u" + (maxId + 1);
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
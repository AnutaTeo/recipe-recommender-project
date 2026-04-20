package com.example.recipe_recommender.runner;

import org.springframework.stereotype.Component;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

@Component
public class XmlValidator {

    public boolean validate(String xmlPath, String xsdPath) {
        try {
            File xmlFile = new File(xmlPath);
            File xsdFile = new File(xsdPath);

            System.out.println("Checking XML: " + xmlFile.getAbsolutePath());
            System.out.println("Checking XSD: " + xsdFile.getAbsolutePath());
            System.out.println("XML exists: " + xmlFile.exists());
            System.out.println("XSD exists: " + xsdFile.exists());

            SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(xsdFile);
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(xmlFile));

            System.out.println("VALID: " + xmlPath);
            return true;

        } catch (SAXException | IOException e) {
            System.out.println("VALIDATION ERROR in: " + xmlPath);
            System.out.println("Reason: " + e.getMessage());
            return false;
        }
    }
}
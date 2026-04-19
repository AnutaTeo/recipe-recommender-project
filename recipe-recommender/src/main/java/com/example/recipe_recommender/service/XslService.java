package com.example.recipe_recommender.service;

import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.charset.StandardCharsets;

@Service
public class XslService {

    public String transformRecipesXmlToHtml(String userSkillLevel) {
        try {
            ClassPathResource xmlResource = new ClassPathResource("data/recipes.xml");
            ClassPathResource xslResource = new ClassPathResource("data/recipes.xsl");

            InputStream xmlInput = xmlResource.getInputStream();
            InputStream xslInput = xslResource.getInputStream();

            TransformerFactory factory = TransformerFactory.newInstance();
            Source xslSource = new StreamSource(xslInput);
            Transformer transformer = factory.newTransformer(xslSource);

            transformer.setParameter("userSkillLevel", userSkillLevel);

            Source xmlSource = new StreamSource(xmlInput);
            StringWriter writer = new StringWriter();

            transformer.transform(xmlSource, new StreamResult(writer));

            return writer.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "<pre style='color:red; white-space:pre-wrap;'>" +
                    e.toString().replace("<", "&lt;").replace(">", "&gt;") +
                    "</pre>";
        }
    }
}
package com.semanticweb.bookrecommendation.service;

import org.apache.jena.rdf.model.*;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileOutputStream;

@Service
public class RdfService {

    private static final String FILE_PATH = "src/main/resources/data/book_recommendation.rdf";
    private static final String NS = "http://example.org/bookrec#";

    public void addBook(String bookName, String author, String theme, String readingLevel) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream(FILE_PATH), null);

        Resource book = model.createResource(NS + bookName.replaceAll("\\s+", ""));
        Property hasAuthor = model.createProperty(NS + "hasAuthor");
        Property hasTheme = model.createProperty(NS + "hasTheme");
        Property suitableForLevel = model.createProperty(NS + "suitableForLevel");

        Resource themeRes = model.createResource(NS + theme);
        Resource levelRes = model.createResource(NS + readingLevel);

        book.addProperty(hasAuthor, author);
        book.addProperty(hasTheme, themeRes);
        book.addProperty(suitableForLevel, levelRes);

        FileOutputStream out = new FileOutputStream(FILE_PATH);
        model.write(out, "RDF/XML-ABBREV");
        out.close();
    }

    public void modifyReadingLevel(String bookName, String newLevel) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream(FILE_PATH), null);

        Resource book = model.getResource(NS + bookName.replaceAll("\\s+", ""));
        Property suitableForLevel = model.createProperty(NS + "suitableForLevel");
        Resource levelRes = model.createResource(NS + newLevel);

        book.removeAll(suitableForLevel);
        book.addProperty(suitableForLevel, levelRes);

        FileOutputStream out = new FileOutputStream(FILE_PATH);
        model.write(out, "RDF/XML-ABBREV");
        out.close();
    }

    public java.util.List<String> getAllBooks() throws Exception {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream(FILE_PATH), null);

        java.util.List<String> books = new java.util.ArrayList<>();
        Property suitableForLevel = model.createProperty(NS + "suitableForLevel");

        ResIterator iter = model.listSubjectsWithProperty(suitableForLevel);
        while (iter.hasNext()) {
            Resource res = iter.nextResource();
            if (res.getLocalName() != null) {
                books.add(res.getLocalName());
            }
        }
        return books;
    }

    public java.util.List<String> getBookThemes(String bookName) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream(FILE_PATH), null);

        Resource book = model.getResource(NS + bookName);
        Property hasTheme = model.createProperty(NS + "hasTheme");

        java.util.List<String> themes = new java.util.ArrayList<>();
        StmtIterator iter = book.listProperties(hasTheme);
        while (iter.hasNext()) {
            themes.add(iter.nextStatement().getObject().asResource().getLocalName());
        }
        return themes;
    }

    public String getBookReadingLevel(String bookName) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream(FILE_PATH), null);

        Resource book = model.getResource(NS + bookName);
        Property suitableForLevel = model.createProperty(NS + "suitableForLevel");

        Statement stmt = book.getProperty(suitableForLevel);
        return stmt != null ? stmt.getObject().asResource().getLocalName() : "Unknown";
    }

    public String getBookAuthor(String bookName) throws Exception {
        Model model = ModelFactory.createDefaultModel();
        model.read(new FileInputStream(FILE_PATH), null);

        Resource book = model.getResource(NS + bookName);
        Property hasAuthor = model.createProperty(NS + "hasAuthor");

        Statement stmt = book.getProperty(hasAuthor);
        return stmt != null ? stmt.getString() : "Unknown Author";
    }
}
package com.semanticweb.bookrecommendation.service;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.embedding.onnx.allminilml6v2.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.googleai.GoogleAiGeminiChatModel;
import dev.langchain4j.store.embedding.EmbeddingMatch;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import org.apache.jena.rdf.model.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.FileInputStream;
import java.util.List;

@Service
public class ChatbotService {

    @Value("${gemini.api.key}")
    private String apiKey;

    private EmbeddingStore<TextSegment> vectorDatabase;
    private EmbeddingModel embeddingModel;
    private ChatLanguageModel llm;

    private static final String FILE_PATH = "src/main/resources/data/book_recommendation.rdf";
    private static final String NS = "http://example.org/bookrec#";

    @PostConstruct
    public void init() {
        embeddingModel = new AllMiniLmL6V2EmbeddingModel();

        vectorDatabase = new InMemoryEmbeddingStore<>();

        llm = GoogleAiGeminiChatModel.builder()
                .apiKey(apiKey)
                .modelName("gemini-2.5-flash")
                .build();

        buildVectorDatabase();
    }

    private void buildVectorDatabase() {
        try {
            Model model = ModelFactory.createDefaultModel();
            model.read(new FileInputStream(FILE_PATH), null);

            Property suitableForLevel = model.getProperty(NS + "suitableForLevel");
            Property hasTheme = model.getProperty(NS + "hasTheme");
            Property hasAuthor = model.getProperty(NS + "hasAuthor");

            ResIterator iter = model.listSubjectsWithProperty(suitableForLevel);
            while (iter.hasNext()) {
                Resource book = iter.nextResource();
                String name = book.getLocalName();
                String level = book.getProperty(suitableForLevel).getObject().asResource().getLocalName();

                StringBuilder themes = new StringBuilder();
                StmtIterator themeIter = book.listProperties(hasTheme);
                while (themeIter.hasNext()) {
                    themes.append(themeIter.nextStatement().getObject().asResource().getLocalName()).append(" ");
                }

                String author = book.hasProperty(hasAuthor) ? book.getProperty(hasAuthor).getString() : "Unknown";

                String bookContext = String.format("The book '%s' is written by '%s'. Its themes/genres are %s. It is suitable for a '%s' reading level.",
                        name, author, themes.toString().trim(), level);

                TextSegment segment = TextSegment.from(bookContext);
                Embedding embedding = embeddingModel.embed(segment).content();
                vectorDatabase.add(embedding, segment);
            }
            System.out.println("SUCCES: Vector Database is populated with the books from XML!");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String askQuestion(String userQuery) {
        Embedding queryEmbedding = embeddingModel.embed(userQuery).content();

        List<EmbeddingMatch<TextSegment>> relevantDocs = vectorDatabase.findRelevant(queryEmbedding, 3, 0.4);

        StringBuilder contextBuilder = new StringBuilder();
        for (EmbeddingMatch<TextSegment> match : relevantDocs) {
            contextBuilder.append(match.embedded().text()).append("\n");
        }

        String prompt = "You are a helpful AI book assistant. " +
                "Answer the user's question based STRICTLY on the Database Information below. " +
                "If the answer is not present in the Database Information, reply exactly with: 'Sorry! I do not have this information.' " +
                "Do not repeat the user's question and do not make up answers.\n\n" +
                "Database Information:\n" + contextBuilder.toString() + "\n\n" +
                "User Question: " + userQuery;

        return llm.generate(prompt);
    }
}
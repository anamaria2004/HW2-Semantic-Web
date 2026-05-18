# Semantic Web Book Recommendation System & AI Chatbot

github link: https://github.com/anamaria2004/HW2-Semantic-Web/edit/main/README.md

A modern, full-stack Book Recommendation System that bridges traditional **Semantic Web technologies** (RDF, OWL, SPARQL) with **Generative AI** (RAG pipeline via Google Gemini). 

This project was built to demonstrate how semantic data can be queried logically and consumed by Large Language Models to provide accurate, hallucination-free, and highly contextual user recommendations.

## Key Features

* **RDF Knowledge Graph:** Full CRUD capabilities reading and writing directly to a persistent `book_recommendation.rdf` file using the Apache Jena API.
* **OWL Ontology:** Defines a clear logical structure (`owl:Class`, `owl:ObjectProperty`) mapping relationships between Users, Books, Themes, Authors, and Reading Levels.
* **SPARQL Recommendation Engine:** Uses complex SPARQL queries to extract data and match user profiles with suitable books.
* **Contextual RAG Chatbot:** A floating, context-aware AI widget integrated into the UI. It features dynamic conversation starters based on the current page (asking about a specific author when viewing a book).
* **Hallucination-Free AI:** Powered by **LangChain4j** and an In-Memory Vector Database, the system converts RDF data into mathematical embeddings. Gemini AI strictly answers based on the semantic context provided, actively preventing hallucinations.
* **Responsive Web Interface:** Built with Spring Boot and Thymeleaf, featuring a clean layout to browse books, view semantic relationships, and add new entries.

---

## Technology Stack

| Component | Technology / Library |
| :--- | :--- |
| **Backend Framework** | Java 17, Spring Boot |
| **Semantic Web API** | Apache Jena |
| **LLM Orchestration** | LangChain4j |
| **AI Model** | Google Gemini API |
| **Frontend** | HTML5, CSS3, JavaScript, Thymeleaf |
| **Ontology Visualization**| GraphDB |

---

## Architecture & Important Files

* `src/main/resources/data/book_recommendation.rdf` - The persistent RDF database containing all instances (Books, Users, Themes).
* `src/main/resources/data/book_ontology.owl` - The semantic structure defining domains, ranges, and properties.
* `sparql_owl.txt` - Contains the 5 core SPARQL queries used for data extraction and recommendation logic.
* `ontologie_graphdb.png` - Visual representation of the OWL ontology exported from GraphDB.
* `com/.../service/RdfService.java` - Handles all direct interactions, parsing, and writing with the Apache Jena Model.
* `com/.../service/ChatbotService.java` - Contains the Retrieval-Augmented Generation (RAG) pipeline, embedding store generation, and Gemini API integration.

---

## How to Run the Project

**1. Clone the repository:**
```bash
git clone https://github.com/anamaria2004/HW2-Semantic-Web.git
cd HW2-Semantic-Web
```

**2. Configure the AI API Key:**
For security reasons, the Google Gemini API key is not hardcoded. 
* Open `src/main/resources/application.properties`.
* Replace the placeholder with your actual Gemini API key:
  `gemini.api.key=KEY_PLACEHOLDER`

**3. Run the application:**
Use Maven to build and run the Spring Boot application:
```bash
mvn spring-boot:run
```

**4. Access the Interface:**
Open your browser and navigate to: `http://localhost:8080`

---

## Contributors

###  Comeaga Ana-Maria (Backend & AI Architecture)
* **Project Setup:** Initialized the Spring Boot framework, dependency mapping, and structured the internal data layers.
* **Semantic Database Core:** Developed the primary components of `RdfService.java` utilizing the Apache Jena API to fetch, parse, and build model instances.
* **Knowledge Engineering:** Conceptualized and built the `book_ontology.owl` core semantics, classes, and objective relationships.
* **AI Retrieval Pipeline:** Designed the entire Retrieval-Augmented Generation (RAG) backend engine in `ChatbotService.java` powered by LangChain4j and in-memory vector indexing.
* **Data Persistence Layer:** Finalized the automated, persistent writing system translating runtime database modifications cleanly back into the raw RDF repository file.

###  Popa Ruxandra-Georgiana (Frontend Integration & Semantic Inquiries)
* **Routing Controllers:** Formulated the programmatic routing and data model mappings inside `BookController.java` to support Thymeleaf synchronization.
* **User Interface Design:** Programmed the entire UI core structure (`index.html`, `book-info.html`) ensuring responsiveness, table structuring, and data input validation.
* **Graph Architecture:** Evaluated and exported the system's structure using GraphDB, creating full semantic diagrams and troubleshooting data synchronization limits.
* **Semantic Inquiries:** Created and tested 5 distinct and advanced SPARQL queries (`sparql_owl.txt`), optimizing execution times across user recommendation benchmarks.
* **AI Chat Widget & REST API:** Programmed the modular floating chatbot UI, handling JavaScript-driven contextual actions and setting up `ChatController.java` endpoints.

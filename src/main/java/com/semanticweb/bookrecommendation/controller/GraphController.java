package com.semanticweb.bookrecommendation.controller;

import edu.uci.ics.jung.algorithms.layout.FRLayout;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.graph.DirectedSparseMultigraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;
import edu.uci.ics.jung.visualization.VisualizationImageServer;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

@RestController
public class GraphController {

    @PostMapping("/upload-rdf")
    public ResponseEntity<byte[]> handleFileUpload(@RequestParam("file") MultipartFile file) {
        try {
            Model model = ModelFactory.createDefaultModel();
            model.read(file.getInputStream(), null);

            Graph<String, String> graph = new DirectedSparseMultigraph<>();
            StmtIterator iter = model.listStatements();
            int edgeId = 0;

            while (iter.hasNext()) {
                Statement stmt = iter.nextStatement();

                String subject = stmt.getSubject().getLocalName() != null ? stmt.getSubject().getLocalName() : "SubiectNecunoscut";
                String predicate = stmt.getPredicate().getLocalName();
                String object = stmt.getObject().isResource() && stmt.getObject().asResource().getLocalName() != null
                        ? stmt.getObject().asResource().getLocalName()
                        : stmt.getObject().toString();

                graph.addVertex(subject);
                graph.addVertex(object);
                graph.addEdge(predicate + " #" + (edgeId++), subject, object, EdgeType.DIRECTED);
            }

            Layout<String, String> layout = new FRLayout<>(graph);
            layout.setSize(new Dimension(800, 600));

            VisualizationImageServer<String, String> vv = new VisualizationImageServer<>(layout, new Dimension(1000, 800));
            vv.setBackground(Color.WHITE);
            vv.getRenderContext().setVertexLabelTransformer(new ToStringLabeller());
            vv.getRenderContext().setEdgeLabelTransformer(edge -> edge.split(" #")[0]); // ascundem ID-ul unic

            BufferedImage image = new BufferedImage(1000, 800, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2d = image.createGraphics();
            g2d.setColor(Color.WHITE);
            g2d.fillRect(0, 0, 1000, 800);
            vv.paint(g2d);
            g2d.dispose();

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            byte[] imageBytes = baos.toByteArray();

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.IMAGE_PNG);
            return new ResponseEntity<>(imageBytes, headers, HttpStatus.OK);

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
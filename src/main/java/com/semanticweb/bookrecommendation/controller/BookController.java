package com.semanticweb.bookrecommendation.controller;

import com.semanticweb.bookrecommendation.service.RdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class BookController {

    @Autowired
    private RdfService rdfService;

    @GetMapping("/")
    public String index(Model model) {
        try {
            model.addAttribute("books", rdfService.getAllBooks());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "index";
    }

    @GetMapping("/book/{bookName}")
    public String viewBookInfo(@PathVariable String bookName, Model model) {
        try {
            model.addAttribute("bookName", bookName);
            model.addAttribute("themes", rdfService.getBookThemes(bookName));
            model.addAttribute("level", rdfService.getBookReadingLevel(bookName));
            model.addAttribute("author", rdfService.getBookAuthor(bookName));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "book-info";
    }

    @PostMapping("/add-book")
    public String handleAddBook(@RequestParam String bookName, @RequestParam String author, @RequestParam String theme, @RequestParam String readingLevel) {
        try { rdfService.addBook(bookName, author, theme, readingLevel); } catch (Exception e) {}
        return "redirect:/";
    }

    @PostMapping("/modify-book")
    public String handleModifyBook(@RequestParam String bookName, @RequestParam String newReadingLevel) {
        try { rdfService.modifyReadingLevel(bookName, newReadingLevel); } catch (Exception e) {}
        return "redirect:/";
    }
}
package com.anayonzem.project_management_app.controller;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class StructuredOpController {

    // The chat client is injected via the constructor, which is used to interact with an external chat API
    private final ChatClient chatClient;

    // Constructor that initializes the chatClient object
    public StructuredOpController(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    // Endpoint to generate a simple text response with book details as a string
    @GetMapping("/text")
    public String getBook() {
        // Calls the chat client to generate a list of 5 books in a plain text format
        return chatClient.prompt("Generate  5 book name with title, author,publicationYear and ISBN.")
                .call() // Make the API call
                .content(); // Extract the content (the response as a string)
    }

    // Endpoint to generate a single Book object with detailed information
    @GetMapping("/bookOP")
    public Book getBookObject() {
        // Calls the chat client to generate one book's details (title, author, publication year, ISBN)
        // Returns the book as a Book entity
        return chatClient.prompt("Generate  1 book name with title, author, publicationYear and ISBN.")
                .call() // Make the API call
                .entity(Book.class); // Deserialize the response into a Book object
    }

    // Endpoint to generate a list of Book objects (5 books)
    @GetMapping("/bookList")
    public List<Book> getBookList() {
        // Calls the chat client to generate a list of 5 books with details
        // The response is deserialized into a list of Book objects using a ParameterizedTypeReference
        return chatClient.prompt("Generate  5 book name with title, author, publicationYear and ISBN.")
                .call() // Make the API call
                .entity(new ParameterizedTypeReference<List<Book>>() {
                }); // Deserialize the response into a List<Book>
    }

    // Endpoint to generate a map of books, where each book is keyed by a string (e.g., "numbers")
    @GetMapping("/bookMap")
    public Map<String, Book> getBookMap() {
        // Calls the chat client to generate a map of books (keyed by "numbers") with details
        // The response is deserialized into a Map<String, Book> using a ParameterizedTypeReference
        return chatClient.prompt("Generate 5 book detail with title, author, publicationYear and ISBN, under the key name 'numbers'")
                .call() // Make the API call
                .entity(new ParameterizedTypeReference<Map<String, Book>>() {
                }); // Deserialize the response into a Map<String, Book>
    }

    @GetMapping("/bookListGen")
    public Flux<Book> storytWithStream(@RequestParam(defaultValue = "Generate  15 book name with title, author,publicationYear and ISBN") String message) {
        // Creates a BeanOutputConverter to convert the response into a List<Book> from the API
        var converter = new BeanOutputConverter<>(new ParameterizedTypeReference<List<Book>>() {
        });

        // Sends a prompt to the chat client, passing a dynamic message for the book details
        // The message can be customized via a query parameter
        Flux<String> flux = this.chatClient.prompt()
                .user(u -> u.text("""
                                  %s
                                  {format}
                                """.formatted(message)) // Insert the dynamic message here
                        .param("format", converter.getFormat())) // Pass any additional parameters, such as format
                .stream() // Stream the response content
                .content(); // Get the content (raw text response)

        // Collect the streamed content into a single string
        String content = flux.collectList().block().stream().collect(Collectors.joining());

        // Convert the content (raw string) into a List<Book> using the converter
        List<Book> actorFilms = converter.convert(content);

        // Return the list of books as a Flux<Book> to stream them asynchronously
        return Flux.fromIterable(actorFilms); // Convert the List<Book> into a Flux stream
    }
}


package com.demo.stl.aijokes.ask;

import com.demo.stl.aijokes.JokeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class AskController {

    Logger logger = LoggerFactory.getLogger(AskController.class);

    private final ChatClient aiClient;
    private final VectorStore vectorStore;

    @Value("classpath:/rag-prompt-template.st")
    private Resource ragPromptTemplate;

    @Autowired
    public AskController(ChatClient aiClient, VectorStore vectorStore){
        this.aiClient = aiClient;
        this.vectorStore = vectorStore;
    }


    @GetMapping("/ask")
    public Answer ask(@RequestParam ("question") String question){
        List<Document> documents = vectorStore.similaritySearch(SearchRequest.query(question).withTopK(2));

        List<String> contentList = documents.stream().map(Document::getContent).toList();
        PromptTemplate promptTemplate = new PromptTemplate(ragPromptTemplate);
        Map<String, Object> promptParameters = new HashMap<>();
        promptParameters.put("input", question);
        promptParameters.put("documents", String.join("\n", contentList));

        Prompt prompt = promptTemplate.create(promptParameters);
        ChatResponse response = aiClient.call(prompt);
        return new Answer(response.getResult().getOutput().getContent());
    }
}

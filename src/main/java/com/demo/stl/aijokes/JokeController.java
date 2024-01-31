package com.demo.stl.aijokes;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
import org.springframework.ai.chat.ChatResponse;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.parser.BeanOutputParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class JokeController {

    Logger logger = LoggerFactory.getLogger(JokeController.class);

    private final ChatClient chatClient;

    private String promptTemplate;

    @Autowired
    public JokeController(ChatClient chatClient,
                          @Value("${app.promptTemplate}") String promptTemplate) {
        this.chatClient = chatClient;
        this.promptTemplate = promptTemplate;
    }

    @GetMapping("/joke")
    public JokeResponse tellJoke(@RequestParam ("subject") String subject){
        logger.info("tellJoke");
        logger.info("promptTemplate: " + promptTemplate);
        BeanOutputParser<JokeResponse> parser = new BeanOutputParser<>(JokeResponse.class);
        String format = parser.getFormat();
        PromptTemplate pt = new PromptTemplate(promptTemplate);
        String renderedPrompt = pt.render(Map.of("subject",subject, "format", format));
        try{
            logger.info(renderedPrompt);
            return parser.parse(chatClient.call(renderedPrompt));
        }catch (Exception e){
            logger.error(e.getMessage());
            return new JokeResponse("error", "no joke today");
        }
    }

    @GetMapping("/joke2")
    public JokeResponse tellJoke2 (@RequestParam ("subject") String subject){
        logger.info("tellJoke2");
        logger.info("promptTemplate: " + promptTemplate);
        BeanOutputParser<JokeResponse> parser = new BeanOutputParser<>(JokeResponse.class);
        String format = parser.getFormat();
        PromptTemplate pt = new PromptTemplate(promptTemplate);
        Prompt renderedPrompt = pt.create(Map.of("subject",subject, "format", format));
        try{
            logger.info(renderedPrompt.toString());
            ChatResponse chatResponse = chatClient.call(renderedPrompt);
            Usage usage = chatResponse.getMetadata().getUsage();
            logger.info(usage.toString());
            return parser.parse(chatResponse.getResult().getOutput().getContent());
        }catch (Exception e){
            logger.error(e.getMessage());
            return new JokeResponse("error", "no joke today");
        }
    }
}

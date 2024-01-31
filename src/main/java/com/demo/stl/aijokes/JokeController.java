package com.demo.stl.aijokes;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.ChatClient;
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
        logger.info("promptTemplate: " + promptTemplate);
        BeanOutputParser<JokeResponse> parser = new BeanOutputParser<>(JokeResponse.class);
        String format = parser.getFormat();
        logger.info(format);
        PromptTemplate pt = new PromptTemplate(promptTemplate);
        String renderedPrompt = pt.render(Map.of("subject",subject, "format", format));
        try{
            return parser.parse(chatClient.call(renderedPrompt));
        }catch (Exception e){
            logger.error(e.getMessage());
            System.out.println(e);
            return new JokeResponse("error", "no joke today");
        }

    }
}

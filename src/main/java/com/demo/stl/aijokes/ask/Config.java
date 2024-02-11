package com.demo.stl.aijokes.ask;

import com.demo.stl.aijokes.JokeController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.openai.OpenAiEmbeddingClient;
import org.springframework.ai.openai.OpenAiEmbeddingOptions;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@Configuration
public class Config {

    Logger logger = LoggerFactory.getLogger(Config.class);

    @Value("${app.vectorstore.path:/tmp/vectorstore.json}")
    private String vectorStorePath;

    @Value("${app.documentResource}")
    private String documentResource;

    @Value("${spring.ai.openai.api-key}")
    private String OPENAI_API_KEY;

    @Value("${app.documentResource}")
    private Resource resource;

    @Bean
    public EmbeddingClient embeddingClient(){
        OpenAiApi openAiApi = new OpenAiApi(OPENAI_API_KEY);
        return  new OpenAiEmbeddingClient(openAiApi)
                .withDefaultOptions(OpenAiEmbeddingOptions.builder().withModel("text-embedding-3-small").build());
    }

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingClient embeddingClient) throws IOException, URISyntaxException {
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingClient);
        File vectorStoreFile = new File (vectorStorePath);
        resource.getFilename();
        if (vectorStoreFile.exists()){
            logger.info("vectorStoreFile exists, reusing existing " + vectorStoreFile.getAbsolutePath());
            simpleVectorStore.load(vectorStoreFile);
        }else {
            logger.info("generating new vectorStoreFile from resource " + resource.getURI());
            TikaDocumentReader documentReader = new TikaDocumentReader(resource);
            List<Document> documents = documentReader.get();
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(documents);
            simpleVectorStore.add(splitDocuments);
            simpleVectorStore.save(vectorStoreFile);
        }
        return simpleVectorStore;
    }

}

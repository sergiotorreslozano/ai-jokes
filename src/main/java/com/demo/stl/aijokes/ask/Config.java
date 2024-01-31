package com.demo.stl.aijokes.ask;

import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.util.List;

@Configuration
public class Config {


    @Value("${app.vectorstore.path://tmp/vectorstore.json}")
    private String vectorStorePath;

    @Value("${app.documentResource}")
    private String documentResource;

    @Bean
    public SimpleVectorStore simpleVectorStore(EmbeddingClient embeddingClient){
        SimpleVectorStore simpleVectorStore = new SimpleVectorStore(embeddingClient);
        File vectorStoreFile = new File (vectorStorePath);
        if (vectorStoreFile.exists()){
            simpleVectorStore.load(vectorStoreFile);
        }else {
            TikaDocumentReader documentReader = new TikaDocumentReader(documentResource);
            List<Document> documents = documentReader.get();
            TextSplitter textSplitter = new TokenTextSplitter();
            List<Document> splitDocuments = textSplitter.apply(documents);
            simpleVectorStore.add(splitDocuments);
            simpleVectorStore.save(vectorStoreFile);
        }
        return simpleVectorStore;
    }

}

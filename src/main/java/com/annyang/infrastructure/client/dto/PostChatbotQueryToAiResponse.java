package com.annyang.infrastructure.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class PostChatbotQueryToAiResponse {
    String answer;
    String error;

    @JsonProperty("retrieved_documents")
    List<RetrievedDocument> retrievedDocuments;

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class RetrievedDocument {
        Chunk chunk;
        Double similarity;

        @JsonProperty("chunk_id")
        String chunkId;

        @Getter
        @AllArgsConstructor
        @NoArgsConstructor
        public static class Chunk {
            Integer id;
            String content;
            List<String> keywords;
            String source;
        }
    }
}

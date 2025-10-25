package com.research.assistant.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.research.assistant.dto.GeminiResponse;
import com.research.assistant.dto.ResearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;

@Service
public class ResearchService {

    @Value("${gemini.api.url}")
    private String geminiApiUrl;

    @Value("${gemini.api.key}")
    private String geminiApiKey;

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    public ResearchService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper=objectMapper;
    }

    public String processContent(ResearchRequest request) {
        //build the prompt
        String prompt = buildPrompt(request);
        //query the AI model API
        Map<String, Object> requestBody = Map.of(
                "contents", new Object[]{
                        Map.of("parts", new Object[]{
                                Map.of("text", prompt)
                        })
                }
        );
        //Parse the response
        String response = webClient.post()
                .uri(geminiApiUrl+geminiApiKey)
                .bodyValue(requestBody)
                .retrieve()
                .bodyToMono(String.class)
                .block();
        String responseText = extractTextFromResponse(response);
        //return res
        return responseText;
    }

    private String extractTextFromResponse(String response) {
        try{
            GeminiResponse geminiResponse = objectMapper.readValue(response, GeminiResponse.class);
            if(geminiResponse.getCandidates() != null && !geminiResponse.getCandidates().isEmpty()){
                GeminiResponse.Candidate firstCandidate = geminiResponse.getCandidates().get(0);
                if(firstCandidate.getContent() != null && firstCandidate.getContent().getParts()!=null && !firstCandidate.getContent().getParts().isEmpty()){
                    return firstCandidate.getContent().getParts().get(0).getText();
                }
            }
        }catch(Exception e){
            return "Pasring error : "+e.getMessage();
        }
        return "No Content found!";
    }

    private String buildPrompt(ResearchRequest request){
        StringBuilder prompt = new StringBuilder();
        switch(request.getOperation()){
            case "summarize":
                prompt.append("Summarize the following content clearly and concisely.\n" +
                        "Focus on the key ideas, avoid repetition, and preserve important details.\n" +
                        "Use bullet points if suitable.");
                break;

            case "suggest":
                prompt.append("Read the following content carefully: Based on your understanding, suggest improvements, alternative ideas, missing points, or ways to make it clearer and more engaging.\n" +
                        "Focus on maintaining the original intent but enhancing its clarity, structure, and impact.\n" +
                        "Present your suggestions as a numbered list with short explanations.");
                break;
            default :
                throw new IllegalArgumentException("unknown operation : "+request.getOperation());
        }
        prompt.append(request.getContent());
        return prompt.toString();
    }
}

/*
The @Value annotation in Spring is used to inject values into fields from:application.properties / application.yml,environment variables,system properties - value("${}")

objectMapper.readValue(response, GeminiResponse.class) -> It converts (deserializes) the JSON string into a GeminiResponse Java object.
 */
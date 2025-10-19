package com.research.assistant.dto;
//class ResearchRequest represents an incoming API payload (request body)
import lombok.Data;

@Data //generates common methods for a class @Getter @Setter @ToString...etc
public class ResearchRequest {
    private String content;
    private String operation;
}

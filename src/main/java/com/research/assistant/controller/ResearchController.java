package com.research.assistant.controller;

import com.research.assistant.dto.ResearchRequest;
import com.research.assistant.service.ResearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/research")
@CrossOrigin(origins = "*")
@AllArgsConstructor
public class ResearchController {
    private final ResearchService researchService;

    @PostMapping("/process")
    public ResponseEntity<String> processContent(@RequestBody ResearchRequest request){
        String result = researchService.processContent(request);
        return ResponseEntity.ok(result);
    }
}

/*
@RequestBody is a Spring MVC annotation used to bind the HTTP request body (typically JSON) to a Java object.
step1- Client sends JSON in the request body {contents:__, operation:__}
step2- Spring automatically converts this JSON into a ResearchRequest object using an HttpMessageConverter (usually Jackson).

ResponseEntity.ok(result) --> It creates an HTTP 200 OK response with the result object (or string) as the response body.
 */

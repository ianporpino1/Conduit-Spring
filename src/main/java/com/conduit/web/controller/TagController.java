package com.conduit.web.controller;

import com.conduit.application.service.TagService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class TagController {
    private  final TagService tagService;

    public TagController(TagService tagService) {
        this.tagService = tagService;
    }

    @GetMapping("/tags")
    public ResponseEntity<Map<String, List<String>>> getTags(){
        List<String> tags = tagService.getTags();

        Map<String, List<String>> response = new HashMap<>();
        response.put("tags", tags);
        return ResponseEntity.ok(response);
    }
}

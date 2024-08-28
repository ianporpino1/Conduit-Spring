package com.conduit.application.service;

import com.conduit.domain.model.Tag;
import com.conduit.domain.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class TagService {
    private final TagRepository tagRepository;
    
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag createTagFromString(String tagName) {
        return tagRepository.findByName(tagName)
                .orElseGet(() -> {
                    Tag newTag = new Tag();
                    newTag.setName(tagName);
                    return tagRepository.save(newTag);
                });
    }
    
    public List<String> getTags(){
        return tagRepository.findAllTags()
                .orElseThrow(()-> new RuntimeException("tag not found"))
                .stream()
                .map(Tag::getName) 
                .toList();
    }
}

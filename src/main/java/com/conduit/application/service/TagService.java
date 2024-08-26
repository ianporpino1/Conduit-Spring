package com.conduit.application.service;

import com.conduit.domain.model.Tag;
import com.conduit.domain.repository.TagRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TagService {
    private final TagRepository tagRepository;
    
    public TagService(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    @Transactional
    public Tag createTagFromString(String tagName) {
        Optional<Tag> existingTag = tagRepository.findByName(tagName);
        if (existingTag.isPresent()) {
            return existingTag.get();
        } else {
            Tag newTag = new Tag();
            newTag.setName(tagName);
            return tagRepository.save(newTag);
        }
    }
}

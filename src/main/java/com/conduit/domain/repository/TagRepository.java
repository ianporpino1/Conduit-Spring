package com.conduit.domain.repository;

import com.conduit.domain.model.Tag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TagRepository extends JpaRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
    default Optional<List<Tag>> findAllTags() {
        List<Tag> tags = findAll();
        return tags.isEmpty() ? Optional.empty() : Optional.of(tags);
    }
}

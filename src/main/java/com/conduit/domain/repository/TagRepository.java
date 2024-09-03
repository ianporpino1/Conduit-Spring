package com.conduit.domain.repository;

import com.conduit.domain.model.Tag;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends ListCrudRepository<Tag, Long> {
    Optional<Tag> findByName(String name);
}

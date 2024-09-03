package com.conduit.domain.repository;

import com.conduit.domain.model.User;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends ListCrudRepository<User, Long> {
    
    Optional<User> findUserByEmail(String email);
    Optional<User> findUserByUsername(String username);
}

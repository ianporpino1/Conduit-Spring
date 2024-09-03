package com.conduit.domain.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.util.HashSet;
import java.util.Set;


@Data
@NoArgsConstructor
@Table(name = "users")
public class User {
    
    @Id
    private Long id;
    private String email;
    private  String password;
    private String username;

    @MappedCollection(idColumn = "user_id", keyColumn = "followee_id")
    private Set<UserFollowing> followings = new HashSet<>();
    
    private String bio;
    
    private String image;
    
}

package com.conduit.domain.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Data
@NoArgsConstructor
@Table(name = "users")
public class User {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(unique = true, length = 100, nullable = false)
    private String email;
    
    private  String password;

    @Column(unique = true, length = 100, nullable = false)
    private String username;
    
    @ManyToMany
    private List<User> followedBy;
    
    @ManyToMany
    private List<User> following;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;
    
    private String bio;
    
    private String image;
    
}

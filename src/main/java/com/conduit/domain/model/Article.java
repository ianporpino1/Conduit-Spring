package com.conduit.domain.model;

import com.conduit.application.dto.article.ArticleRequestDTO;
import jakarta.persistence.*;
import lombok.Data;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "author_id")
    private User author;
    
    private String slug;

    @Column(nullable = false)
    private String title;
    
    private String description;

    private String body;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(
            name = "article_tags", 
            joinColumns = @JoinColumn(name = "article_id"), 
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private List<Tag> tagList;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private Instant updatedAt;
    
    @ManyToMany
    @JoinTable(
            name = "user_favorites",
            joinColumns = @JoinColumn(name = "article_id"),
            inverseJoinColumns = @JoinColumn(name = "user_id")
    )
    private List<User> favoritedBy = new ArrayList<>();

    @OneToMany(mappedBy = "article", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Comment> comments;

    public void addFavorite(User user) {
        if (!this.favoritedBy.contains(user)) {
            this.favoritedBy.add(user);
            user.getFavoritedArticles().add(this);
        }
    }

    public void removeFavorite(User user) {
        if (this.favoritedBy.remove(user)) {
            user.getFavoritedArticles().remove(this);
        }
    }

    public void update(String title, String description, String body) {
        boolean isModified = false;
        
        if (title != null && !title.equals(this.title)) {
            this.title = title;
            this.slug = createSlug(title);
            isModified = true;
        }
        if (description != null) {
            this.description = description;
            isModified = true;
        }
        if (body != null) {
            this.body = body;
            isModified = true;
        }
        if(isModified){
            this.updatedAt = Instant.now();
        }
    }
    
    

    public void addTag(Tag tag) {
        if (!this.tagList.contains(tag)) {
            this.tagList.add(tag);
        }
    }

    public void removeTag(Tag tag) {
        this.tagList.remove(tag);
    }

    public boolean isFavoritedBy(User user) {
        return this.favoritedBy.contains(user);
    }

    public int getFavoritesCount() {
        return this.favoritedBy.size();
    }

    private String createSlug(String title) {
        return title.toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
    }
  
}

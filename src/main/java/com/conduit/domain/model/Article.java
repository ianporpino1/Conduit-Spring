package com.conduit.domain.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.MappedCollection;
import org.springframework.data.relational.core.mapping.Table;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
@Table("articles")
public class Article {

    @Id
    private Long id;
    
    private Long authorId;
    
    private String slug;

    private String title;
    
    private String description;

    private String body;

    @MappedCollection(idColumn = "article_id")
    private Set<ArticleTag> tags = new HashSet<>();
    
    @CreatedDate
    private Instant createdAt;
    
    @LastModifiedDate
    private Instant updatedAt;
    
    @MappedCollection(idColumn = "article_id")
    private Set<UserFavoriteArticle> favoritedBy = new HashSet<>();
    
    @MappedCollection(idColumn = "article_id")
    private Set<Comment> comments;
    
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
    
//    public boolean isFavoritedBy(UserRef user) {
//        return this.favoritedBy.contains(user);
//    }
//
//    public int getFavoritesCount() {
//        return this.favoritedBy.size();
//    }

    private String createSlug(String title) {
        return title.toLowerCase().replaceAll("[^a-z0-9\\s-]", "").replaceAll("\\s+", "-");
    }
  
}

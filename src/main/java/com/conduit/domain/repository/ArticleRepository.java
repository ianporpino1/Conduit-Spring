package com.conduit.domain.repository;

import com.conduit.domain.model.Article;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {

    @Query("SELECT DISTINCT a FROM Article a " +
            "LEFT JOIN a.tagList t " +
            "LEFT JOIN a.favoritedBy u " +
            "WHERE (:tag IS NULL OR t.name = :tag) " +
            "AND (:author IS NULL OR a.author.username = :author) " +
            "AND (:favorited IS NULL OR EXISTS (SELECT 1 FROM a.favoritedBy f WHERE f.username = :favorited)) " +
            "ORDER BY a.createdAt DESC")
    Page<Article> findArticles(@Param("tag") String tag,
                               @Param("author") String author,
                               @Param("favorited") String favorited,
                               Pageable pageable);
    

    @Query("SELECT COUNT(a) FROM Article a WHERE a.author.id IN :authorIds")
    Long countArticlesByFollowingUsers(@Param("authorIds") List<Long> authorIds);


    @Query("SELECT a FROM Article a WHERE a.author.id IN :authorIds ORDER BY a.createdAt DESC")
    Page<Article> findArticlesByFollowingUsersIds(@Param("authorIds") List<Long> authorIds, Pageable pageable);
    
    Optional<Article> findArticleBySlug(String slug);

    Optional<Article> findBySlug(String slug);
    
}

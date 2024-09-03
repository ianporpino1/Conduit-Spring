package com.conduit.domain.repository;

import com.conduit.domain.model.Article;
import com.conduit.domain.model.ArticleTag;
import com.conduit.domain.model.UserFavoriteArticle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface ArticleRepository extends PagingAndSortingRepository<Article, Long>, 
                                            ListCrudRepository<Article,Long> {

    @Query("SELECT DISTINCT a.* FROM articles a " +
            "LEFT JOIN article_tags at ON a.id = at.article_id " +
            "LEFT JOIN tags t ON at.tag_id = t.id " +
            "LEFT JOIN user_favorites_articles uf ON a.id = uf.article_id " +
            "LEFT JOIN users u ON uf.user_id = u.id " +
            "WHERE (:tagId IS NULL OR t.id = :tagId) " +
            "AND (:authorId IS NULL OR a.author_id = :authorId) " +
            "AND (:favoritedId IS NULL OR EXISTS (SELECT 1 FROM user_favorites_articles uf WHERE uf.user_id = :favoritedId)) " +
            "ORDER BY a.created_at DESC " +
            "LIMIT :limit OFFSET :offset")
    List<Article> findAllArticlesByFilters(@Param("tagId") Long tagId,
                               @Param("authorId") Long authorId,
                               @Param("favoritedId") Long favoritedId,
                               @Param("limit") int limit,
                               @Param("offset") int offset);
    
}

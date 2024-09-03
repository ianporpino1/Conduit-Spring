package com.conduit.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Setter
@Getter
@Table("user_favorites_articles")
public class UserFavoriteArticle {
    static UserFavoriteArticle of(Long articleId) {

        UserFavoriteArticle userFavoriteArticle = new UserFavoriteArticle();
        userFavoriteArticle.setArticleId(articleId);

        return userFavoriteArticle;
    }

    Long articleId;

}

package com.conduit.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Table("article_tags")
@Getter
@Setter
public class ArticleTag {
    public static ArticleTag of(Long tagId) {

        ArticleTag articleTag = new ArticleTag();
        articleTag.setTagId(tagId);

        return articleTag;
    }

    private Long tagId;
}

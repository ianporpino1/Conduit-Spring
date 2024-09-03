package com.conduit.domain.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.List;


@Data
@Table("tags")
public class Tag {

    @Id
    private Long id;

    private String name;
    
}

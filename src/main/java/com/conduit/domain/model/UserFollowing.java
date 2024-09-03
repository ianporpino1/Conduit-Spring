package com.conduit.domain.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("user_following")
@Getter
@Setter
public class UserFollowing {
    

    @Column("followee_id")
    private Long followeeId;


    public static UserFollowing of(Long followeeId) {
        UserFollowing userFollowing = new UserFollowing();
        userFollowing.setFolloweeId(followeeId);
        return userFollowing;
    }
}

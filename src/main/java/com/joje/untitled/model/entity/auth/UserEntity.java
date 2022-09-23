package com.joje.untitled.model.entity.auth;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.management.relation.Role;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TB_USER")
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userNo;

    private String userId;
    private String userName;
    private String password;
    private LocalDateTime regDate;
    private boolean enabled;

    @JsonIgnore
    @Builder.Default
    @ManyToMany
    @JoinTable(
            name = "TB_USER_ROLE",
            joinColumns = @JoinColumn(name = "USER_NO"),
            inverseJoinColumns = @JoinColumn(name = "ROLE_ID")
    )
    private List<RoleEntity> roles = new ArrayList<>();

}

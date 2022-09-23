package com.joje.untitled.model.entity.auth;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.joje.untitled.common.constants.RoleType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.List;

@Builder
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "TB_ROLE")
public class RoleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long roleId;

    @Enumerated(EnumType.STRING)
    private RoleType roleName;

    public RoleEntity(RoleType role) {
        this.roleId = role.getRoleId();
        this.roleName = role;
    }

    @JsonBackReference
    @ManyToMany(mappedBy = "roles")
    private List<UserEntity> users;

}

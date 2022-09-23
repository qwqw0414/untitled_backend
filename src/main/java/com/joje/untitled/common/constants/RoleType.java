package com.joje.untitled.common.constants;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RoleType {

    ROLE_ADMIN(1),
    ROLE_MANAGER(2),
    ROLE_USER(3);

    private final long roleId;

}

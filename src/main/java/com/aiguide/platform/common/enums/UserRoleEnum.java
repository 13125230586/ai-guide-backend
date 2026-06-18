package com.aiguide.platform.common.enums;

import lombok.Getter;

@Getter
public enum UserRoleEnum {
    TOURIST("TOURIST", "游客"),
    ADMIN("ADMIN", "管理员");

    private final String value;
    private final String desc;

    UserRoleEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static boolean isValid(String value) {
        for (UserRoleEnum e : values()) {
            if (e.value.equals(value)) return true;
        }
        return false;
    }
}

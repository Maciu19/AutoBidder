package com.maciu19.autobidder.api.utils;

import com.maciu19.autobidder.api.model.enums.EnumString;
import jakarta.annotation.Nullable;

public class EnumUtils {

    @Nullable
    public static <T extends Enum<T> & EnumString> T fromId(Class<T> enumClass, String id) {
        for (T enumConst : enumClass.getEnumConstants()) {
            if (enumConst.getId().equalsIgnoreCase(id)) {
                return enumConst;
            }
        }

        return null;
    }
}

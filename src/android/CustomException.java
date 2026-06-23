package io.capawesome.cordova.plugins.agesignals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class CustomException extends Exception {

    @Nullable
    private final String code;

    public CustomException(@Nullable String code, @NonNull String message) {
        super(message);
        this.code = code;
    }

    @Nullable
    public String getCode() {
        return code;
    }
}

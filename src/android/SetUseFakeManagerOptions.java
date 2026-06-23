package io.capawesome.cordova.plugins.agesignals.options;

import org.json.JSONObject;

public class SetUseFakeManagerOptions {

    private final boolean useFake;

    public SetUseFakeManagerOptions(JSONObject options) {
        this.useFake = options.optBoolean("useFake", false);
    }

    public boolean getUseFake() {
        return useFake;
    }
}

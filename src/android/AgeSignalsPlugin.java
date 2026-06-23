package io.capawesome.cordova.plugins.agesignals;

import androidx.annotation.NonNull;
import io.capawesome.cordova.plugins.agesignals.options.SetNextAgeSignalsExceptionOptions;
import io.capawesome.cordova.plugins.agesignals.options.SetNextAgeSignalsResultOptions;
import io.capawesome.cordova.plugins.agesignals.options.SetUseFakeManagerOptions;
import io.capawesome.cordova.plugins.agesignals.results.CheckAgeSignalsResult;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AgeSignalsPlugin extends CordovaPlugin {

    private static final String ERROR_UNKNOWN_ERROR = "An unknown error occurred.";
    private static final String ERROR_UNIMPLEMENTED = "This method is not available on this platform.";

    private AgeSignals implementation;

    @Override
    protected void pluginInitialize() {
        implementation = new AgeSignals(this);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        switch (action) {
            case "checkAgeSignals":
                checkAgeSignals(callbackContext);
                return true;
            case "checkEligibility":
                callbackContext.error(ERROR_UNIMPLEMENTED);
                return true;
            case "setUseFakeManager":
                setUseFakeManager(getJSONObjectArg(args, 0), callbackContext);
                return true;
            case "setNextAgeSignalsResult":
                setNextAgeSignalsResult(getJSONObjectArg(args, 0), callbackContext);
                return true;
            case "setNextAgeSignalsException":
                setNextAgeSignalsException(getJSONObjectArg(args, 0), callbackContext);
                return true;
            default:
                return false;
        }
    }

    private void checkAgeSignals(@NonNull CallbackContext callbackContext) {
        cordova.getThreadPool().execute(() -> {
            implementation.checkAgeSignals(new AgeSignals.ResultCallback<CheckAgeSignalsResult>() {
                @Override
                public void success(@NonNull CheckAgeSignalsResult result) {
                    callbackContext.success(result.toJSONObject());
                }

                @Override
                public void error(@NonNull Exception exception) {
                    reject(callbackContext, exception);
                }
            });
        });
    }

    private void setUseFakeManager(@NonNull JSONObject options, @NonNull CallbackContext callbackContext) {
        cordova.getThreadPool().execute(() -> {
            try {
                SetUseFakeManagerOptions parsedOptions = new SetUseFakeManagerOptions(options);
                implementation.setUseFakeManager(parsedOptions, new AgeSignals.EmptyResultCallback() {
                    @Override
                    public void success() {
                        callbackContext.success();
                    }

                    @Override
                    public void error(@NonNull Exception exception) {
                        reject(callbackContext, exception);
                    }
                });
            } catch (Exception exception) {
                reject(callbackContext, exception);
            }
        });
    }

    private void setNextAgeSignalsResult(@NonNull JSONObject options, @NonNull CallbackContext callbackContext) {
        cordova.getThreadPool().execute(() -> {
            try {
                SetNextAgeSignalsResultOptions parsedOptions = new SetNextAgeSignalsResultOptions(options);
                implementation.setNextAgeSignalsResult(parsedOptions, new AgeSignals.EmptyResultCallback() {
                    @Override
                    public void success() {
                        callbackContext.success();
                    }

                    @Override
                    public void error(@NonNull Exception exception) {
                        reject(callbackContext, exception);
                    }
                });
            } catch (Exception exception) {
                reject(callbackContext, exception);
            }
        });
    }

    private void setNextAgeSignalsException(@NonNull JSONObject options, @NonNull CallbackContext callbackContext) {
        cordova.getThreadPool().execute(() -> {
            try {
                SetNextAgeSignalsExceptionOptions parsedOptions = new SetNextAgeSignalsExceptionOptions(options);
                implementation.setNextAgeSignalsException(parsedOptions, new AgeSignals.EmptyResultCallback() {
                    @Override
                    public void success() {
                        callbackContext.success();
                    }

                    @Override
                    public void error(@NonNull Exception exception) {
                        reject(callbackContext, exception);
                    }
                });
            } catch (Exception exception) {
                reject(callbackContext, exception);
            }
        });
    }

    @NonNull
    private JSONObject getJSONObjectArg(@NonNull JSONArray args, int index) throws JSONException {
        if (args.length() <= index || args.isNull(index)) {
            return new JSONObject();
        }
        return args.getJSONObject(index);
    }

    private void reject(@NonNull CallbackContext callbackContext, @NonNull Exception exception) {
        String message = exception.getMessage();
        if (message == null) {
            message = ERROR_UNKNOWN_ERROR;
        }

        if (exception instanceof CustomException) {
            CustomException customException = (CustomException) exception;
            if (customException.getCode() != null) {
                callbackContext.error(customException.getCode() + ": " + message);
                return;
            }
        }

        callbackContext.error(message);
    }
}

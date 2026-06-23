package io.capawesome.cordova.plugins.agesignals;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.gms.tasks.Task;
import com.google.android.play.agesignals.AgeSignalsManager;
import com.google.android.play.agesignals.AgeSignalsManagerFactory;
import com.google.android.play.agesignals.AgeSignalsRequest;
import com.google.android.play.agesignals.AgeSignalsResult;
import com.google.android.play.agesignals.testing.FakeAgeSignalsManager;
import io.capawesome.cordova.plugins.agesignals.options.SetNextAgeSignalsExceptionOptions;
import io.capawesome.cordova.plugins.agesignals.options.SetNextAgeSignalsResultOptions;
import io.capawesome.cordova.plugins.agesignals.options.SetUseFakeManagerOptions;
import io.capawesome.cordova.plugins.agesignals.results.CheckAgeSignalsResult;
import org.apache.cordova.CordovaPlugin;

public class AgeSignals {

    @NonNull
    private final CordovaPlugin plugin;

    private boolean useFakeManager = false;

    @Nullable
    private FakeAgeSignalsManager fakeManager = null;

    public AgeSignals(@NonNull CordovaPlugin plugin) {
        this.plugin = plugin;
    }

    public interface ResultCallback<T> {
        void success(@NonNull T result);

        void error(@NonNull Exception exception);
    }

    public interface EmptyResultCallback {
        void success();

        void error(@NonNull Exception exception);
    }

    public void checkAgeSignals(@NonNull ResultCallback<CheckAgeSignalsResult> callback) {
        AgeSignalsManager manager;
        if (useFakeManager && fakeManager != null) {
            manager = fakeManager;
        } else {
            manager = AgeSignalsManagerFactory.create(plugin.cordova.getActivity());
        }

        AgeSignalsRequest request = AgeSignalsRequest.builder().build();

        Task<AgeSignalsResult> task = manager.checkAgeSignals(request);
        task.addOnSuccessListener(ageSignalsResult -> {
            try {
                CheckAgeSignalsResult result = new CheckAgeSignalsResult(ageSignalsResult);
                callback.success(result);
            } catch (Exception exception) {
                callback.error(exception);
            }
        });
        task.addOnFailureListener(exception -> callback.error(mapErrorCodeToException(exception)));
    }

    public void setUseFakeManager(@NonNull SetUseFakeManagerOptions options, @NonNull EmptyResultCallback callback) {
        try {
            this.useFakeManager = options.getUseFake();
            if (this.useFakeManager) {
                this.fakeManager = new FakeAgeSignalsManager();
            } else {
                this.fakeManager = null;
            }
            callback.success();
        } catch (Exception exception) {
            callback.error(exception);
        }
    }

    public void setNextAgeSignalsResult(@NonNull SetNextAgeSignalsResultOptions options, @NonNull EmptyResultCallback callback) {
        try {
            if (!useFakeManager || fakeManager == null) {
                throw CustomExceptions.FAKE_MANAGER_NOT_ENABLED;
            }
            AgeSignalsResult result = options.buildAgeSignalsResult();
            fakeManager.setNextAgeSignalsResult(result);
            callback.success();
        } catch (Exception exception) {
            callback.error(exception);
        }
    }

    public void setNextAgeSignalsException(@NonNull SetNextAgeSignalsExceptionOptions options, @NonNull EmptyResultCallback callback) {
        try {
            if (!useFakeManager || fakeManager == null) {
                throw CustomExceptions.FAKE_MANAGER_NOT_ENABLED;
            }
            com.google.android.play.agesignals.AgeSignalsException exception = options.buildAgeSignalsException();
            fakeManager.setNextAgeSignalsException(exception);
            callback.success();
        } catch (Exception exception) {
            callback.error(exception);
        }
    }

    @NonNull
    private Exception mapErrorCodeToException(@NonNull Exception exception) {
        if (!(exception instanceof com.google.android.gms.common.api.ApiException)) {
            return exception;
        }

        com.google.android.gms.common.api.ApiException apiException = (com.google.android.gms.common.api.ApiException) exception;
        int statusCode = apiException.getStatusCode();

        switch (statusCode) {
            case 25000:
                return CustomExceptions.API_NOT_AVAILABLE;
            case 25001:
                return CustomExceptions.PLAY_STORE_NOT_FOUND;
            case 25002:
                return CustomExceptions.NETWORK_ERROR;
            case 25003:
                return CustomExceptions.PLAY_SERVICES_NOT_FOUND;
            case 25004:
                return CustomExceptions.CANNOT_BIND_TO_SERVICE;
            case 25005:
                return CustomExceptions.PLAY_STORE_VERSION_OUTDATED;
            case 25006:
                return CustomExceptions.PLAY_SERVICES_VERSION_OUTDATED;
            case 25007:
                return CustomExceptions.CLIENT_TRANSIENT_ERROR;
            case 25008:
                return CustomExceptions.APP_NOT_OWNED;
            case 25009:
                return CustomExceptions.INTERNAL_ERROR;
            case 25010:
                return CustomExceptions.SDK_VERSION_OUTDATED;
            default:
                return exception;
        }
    }
}

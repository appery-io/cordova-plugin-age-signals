package io.capawesome.cordova.plugins.agesignals.options;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.google.android.play.agesignals.AgeSignalsResult;
import com.google.android.play.agesignals.model.AgeSignalsVerificationStatus;
import io.capawesome.cordova.plugins.agesignals.CustomExceptions;
import io.capawesome.cordova.plugins.agesignals.enums.UserStatus;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.json.JSONObject;

public class SetNextAgeSignalsResultOptions {

    @NonNull
    private final UserStatus userStatus;

    @Nullable
    private final Integer ageLower;

    @Nullable
    private final Integer ageUpper;

    @Nullable
    private final String mostRecentApprovalDate;

    @Nullable
    private final String installId;

    public SetNextAgeSignalsResultOptions(@NonNull JSONObject options) throws Exception {
        this.userStatus = getUserStatusFromOptions(options);
        this.ageLower = options.has("ageLower") && !options.isNull("ageLower") ? options.getInt("ageLower") : null;
        this.ageUpper = options.has("ageUpper") && !options.isNull("ageUpper") ? options.getInt("ageUpper") : null;
        this.mostRecentApprovalDate = options.has("mostRecentApprovalDate") && !options.isNull("mostRecentApprovalDate")
            ? options.getString("mostRecentApprovalDate")
            : null;
        this.installId = options.has("installId") && !options.isNull("installId")
            ? options.getString("installId")
            : null;
    }

    @NonNull
    public AgeSignalsResult buildAgeSignalsResult() throws Exception {
        AgeSignalsResult.Builder builder = AgeSignalsResult.builder();

        Integer verificationStatus = mapUserStatusToVerificationStatus(this.userStatus);
        builder.setUserStatus(verificationStatus);

        if (this.ageLower != null) {
            builder.setAgeLower(this.ageLower);
        }
        if (this.ageUpper != null) {
            builder.setAgeUpper(this.ageUpper);
        }
        if (this.mostRecentApprovalDate != null) {
            Date date = parseDateString(this.mostRecentApprovalDate);
            builder.setMostRecentApprovalDate(date);
        }
        if (this.installId != null) {
            builder.setInstallId(this.installId);
        }

        return builder.build();
    }

    @NonNull
    private static UserStatus getUserStatusFromOptions(@NonNull JSONObject options) throws Exception {
        String userStatusString = options.optString("userStatus", null);
        if (userStatusString == null) {
            throw new Exception(CustomExceptions.USER_STATUS_MISSING.getMessage());
        }
        try {
            return UserStatus.valueOf(userStatusString);
        } catch (IllegalArgumentException exception) {
            throw new Exception("Invalid userStatus: " + userStatusString);
        }
    }

    @Nullable
    private Integer mapUserStatusToVerificationStatus(@NonNull UserStatus userStatus) {
        switch (userStatus) {
            case VERIFIED:
                return AgeSignalsVerificationStatus.VERIFIED;
            case SUPERVISED:
                return AgeSignalsVerificationStatus.SUPERVISED;
            case SUPERVISED_APPROVAL_PENDING:
                return AgeSignalsVerificationStatus.SUPERVISED_APPROVAL_PENDING;
            case SUPERVISED_APPROVAL_DENIED:
                return AgeSignalsVerificationStatus.SUPERVISED_APPROVAL_DENIED;
            case UNKNOWN:
                return AgeSignalsVerificationStatus.UNKNOWN;
            case DECLARED:
                return AgeSignalsVerificationStatus.DECLARED;
            case EMPTY:
            default:
                return null;
        }
    }

    @NonNull
    private Date parseDateString(@NonNull String dateString) throws Exception {
        try {
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            Date date = format.parse(dateString);
            if (date == null) {
                throw new Exception("Failed to parse date: " + dateString);
            }
            return date;
        } catch (Exception exception) {
            throw new Exception("Invalid date format. Expected yyyy-MM-dd, got: " + dateString);
        }
    }
}

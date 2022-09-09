package or.kr.ajouhosp.stroke.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build.VERSION_CODES;
import android.preference.PreferenceManager;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.annotation.StringRes;

import or.kr.ajouhosp.stroke.CameraSource;
import or.kr.ajouhosp.stroke.CameraSource.SizePair;
import or.kr.ajouhosp.stroke.R;
import com.google.android.gms.common.images.Size;
import com.google.common.base.Preconditions;
import com.google.mlkit.vision.pose.PoseDetectorOptionsBase;
import com.google.mlkit.vision.pose.accurate.AccuratePoseDetectorOptions;
import com.google.mlkit.vision.pose.defaults.PoseDetectorOptions;

public class PreferenceUtils {
    private static final int POSE_DETECTOR_PERFORMANCE_MODE_FAST = 1;

    static void saveString(Context context, @StringRes int prefKeyId, @Nullable String value) {
        PreferenceManager.getDefaultSharedPreferences(context)
                .edit()
                .putString(context.getString(prefKeyId), value)
                .apply();
    }

    @Nullable
    public static SizePair getCameraPreviewSizePair(Context context, int cameraId) {
        Preconditions.checkArgument(
                cameraId == CameraSource.CAMERA_FACING_BACK
                        || cameraId == CameraSource.CAMERA_FACING_FRONT);
        String previewSizePrefKey;
        String pictureSizePrefKey;
        if (cameraId == CameraSource.CAMERA_FACING_BACK) {
            previewSizePrefKey = context.getString(R.string.pref_key_rear_camera_preview_size);
            pictureSizePrefKey = context.getString(R.string.pref_key_rear_camera_picture_size);
        } else {
            previewSizePrefKey = context.getString(R.string.pref_key_front_camera_preview_size);
            pictureSizePrefKey = context.getString(R.string.pref_key_front_camera_picture_size);
        }

        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
            return new CameraSource.SizePair(
                    Size.parseSize(sharedPreferences.getString(previewSizePrefKey, null)),
                    Size.parseSize(sharedPreferences.getString(pictureSizePrefKey, null)));
        } catch (Exception e) {
            return null;
        }
    }
    @RequiresApi(VERSION_CODES.LOLLIPOP)
    @Nullable
    public static android.util.Size getCameraXTargetResolution(Context context) {
        String prefKey = context.getString(R.string.pref_key_camerax_target_resolution);
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        try {
            return android.util.Size.parseSize(sharedPreferences.getString(prefKey, null));
        } catch (Exception e) {
            return null;
        }
    }
    public static PoseDetectorOptionsBase getPoseDetectorOptionsForLivePreview(Context context) {
        int performanceMode =
                getModeTypePreferenceValue(
                        context,
                        R.string.pref_key_live_preview_pose_detection_performance_mode,
                        POSE_DETECTOR_PERFORMANCE_MODE_FAST);
        if (performanceMode == POSE_DETECTOR_PERFORMANCE_MODE_FAST) {
            return new PoseDetectorOptions.Builder()
                    .setDetectorMode(PoseDetectorOptions.STREAM_MODE)
                    .build();
        } else {
            return new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.STREAM_MODE)
                    .build();
        }
    }
    public static PoseDetectorOptionsBase getPoseDetectorOptionsForStillImage(Context context) {
        int performanceMode =
                getModeTypePreferenceValue(
                        context,
                        R.string.pref_key_still_image_pose_detection_performance_mode,
                        POSE_DETECTOR_PERFORMANCE_MODE_FAST);
        if (performanceMode == POSE_DETECTOR_PERFORMANCE_MODE_FAST) {
            return new PoseDetectorOptions.Builder()
                    .setDetectorMode(PoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();
        } else {
            return new AccuratePoseDetectorOptions.Builder()
                    .setDetectorMode(AccuratePoseDetectorOptions.SINGLE_IMAGE_MODE)
                    .build();
        }
    }
    public static boolean shouldShowPoseDetectionInFrameLikelihoodLivePreview(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKey =
                context.getString(R.string.pref_key_live_preview_pose_detector_show_in_frame_likelihood);
        return sharedPreferences.getBoolean(prefKey, false);
    }

    public static boolean shouldShowPoseDetectionInFrameLikelihoodStillImage(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKey =
                context.getString(R.string.pref_key_still_image_pose_detector_show_in_frame_likelihood);
        return sharedPreferences.getBoolean(prefKey, false);
    }
    private static int getModeTypePreferenceValue(
            Context context, @StringRes int prefKeyResId, int defaultValue) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKey = context.getString(prefKeyResId);
        return Integer.parseInt(sharedPreferences.getString(prefKey, String.valueOf(defaultValue)));
    }
    public static boolean isCameraLiveViewportEnabled(Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String prefKey = context.getString(R.string.pref_key_camera_live_viewport);
        return sharedPreferences.getBoolean(prefKey, false);
    }

    private PreferenceUtils() {}
}
package indubitables.config.util;

import com.acmerobotics.dashboard.config.Config;

import indubitables.pedroPathing.localization.Pose;

@Config
public class FieldConstants {

    public enum RobotStart {
        BLUE_BUCKET,
        BLUE_OBSERVATION,
        RED_BUCKET,
        RED_OBSERVATION
    }

    public static final Pose blueBucketStartPose = new Pose(7.5, 78.75, Math.toRadians(180));
    public static final Pose blueObservationStartPose = new Pose(8, 36, Math.toRadians(180));
    public static final Pose redBucketStartPose = new Pose(144-8, 79.5, 0);
    public static final Pose redObservationStartPose = new Pose(144-8, 36, 0);

    // Blue Preload Poses
    public static final Pose blueBucketPreloadPose = new Pose(22, 78.375, Math.toRadians(180));

    // Blue Bucket Sample Poses
    public static final Pose blueBucketLeftSamplePose = new Pose(22, 110, 0);
    public static final Pose blueBucketLeftSampleControlPose = new Pose(20, 96);
    public static final Pose blueBucketMidSamplePose = new Pose(22, 116, 0);
    public static final Pose blueBucketMidSampleControlPose = new Pose(20, 86);
    public static final Pose blueBucketRightSamplePose = new Pose(22, 122, 0);
    public static final Pose blueBucketRightSampleControlPose = new Pose(20, 96);

    public static final Pose blueBucketScorePose = new Pose(20, 128, Math.toRadians(-45));

    public static final Pose blueBucketParkPose = new Pose(62, 97.75, Math.toRadians(90));
    public static final Pose blueBucketParkControlPose = new Pose(60.25, 123.5);


}
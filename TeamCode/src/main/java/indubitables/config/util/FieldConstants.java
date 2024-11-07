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
    public static final Pose blueObservationStartPose = new Pose(7.5, 65.25, Math.toRadians(180));
    public static final Pose redBucketStartPose = new Pose(144-blueBucketStartPose.getX(), blueBucketStartPose.getY(), 0);
    public static final Pose redObservationStartPose = new Pose(144-blueObservationStartPose.getX(), blueObservationStartPose.getY(), 0);

    // Preload Poses
    public static final Pose blueBucketPreloadPose = new Pose(30.25, 78.375, Math.toRadians(180));
    public static final Pose blueObservationPreloadPose = new Pose(30.25, 63.625, Math.toRadians(180));
    public static final Pose redBucketPreloadPose = new Pose(144-blueBucketPreloadPose.getX(), blueBucketPreloadPose.getY(), 0);
    public static final Pose redObservationPreloadPose = new Pose(144-blueObservationPreloadPose.getX(), blueObservationPreloadPose.getY(), 0);

    // Blue Bucket Sample Poses
    public static final Pose blueBucketLeftSamplePose = new Pose(22, 110, 0);
    public static final Pose blueBucketLeftSampleControlPose = new Pose(20, 96);
    public static final Pose blueBucketMidSamplePose = new Pose(22, 116, 0);
    public static final Pose blueBucketMidSampleControlPose = new Pose(20, 86);
    public static final Pose blueBucketRightSamplePose = new Pose(22, 122, 0);
    public static final Pose blueBucketRightSampleControlPose = new Pose(20, 96);

    // Blue Observation Poses
    public static final Pose blueObservationElement1Pose = new Pose(20, 96, Math.toRadians(180));
    public static final Pose blueObservationElement1ControlPose = new Pose(20, 110);
    public static final Pose blueObservationElement2Pose = new Pose(20, 86, Math.toRadians(180));
    public static final Pose blueObservationElement2ControlPose = new Pose(20, 96);
    public static final Pose blueObservationElement3Pose = new Pose(20, 96, Math.toRadians(180));
    public static final Pose blueObservationElement3ControlPose = new Pose(20, 110);
    public static final Pose blueObservationSpecimenSetPose = new Pose(21, 25.5, Math.toRadians(0));
    public static final Pose blueObservationSpecimenPickupPose = new Pose(17.25, 25.5, Math.toRadians(0));
    public static final Pose blueObservationSpecimenPickup2Pose = new Pose(17.25, 25.5, Math.toRadians(0));
    public static final Pose blueObservationSpecimen1Pose = new Pose(28, 68.625, Math.toRadians(180));
    public static final Pose blueObservationSpecimen2Pose = new Pose(28.25, 71.625, Math.toRadians(180));
    public static final Pose blueObservationSpecimen3Pose = new Pose(30.25, 76.625, Math.toRadians(180));
    public static final Pose blueObservationParkPose = new Pose(16, 48, 0);
    public static final Pose blueObservationParkControlPose = new Pose(20, 50);


    public static final Pose blueBucketScorePose = new Pose(20, 128, Math.toRadians(-45));

    public static final Pose blueBucketParkPose = new Pose(62, 97.75, Math.toRadians(90));
    public static final Pose blueBucketParkControlPose = new Pose(60.25, 123.5);


}
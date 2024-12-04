package indubitables.config.util;

import com.acmerobotics.dashboard.config.Config;

import indubitables.pedroPathing.localization.Pose;

@Config
public class FieldConstants {

    public enum RobotStart {
        BUCKET,
        OBSERVATION,
    }

    // Bucket Poses
    public static final Pose bucketStartPose = new Pose(7.5, 78.75, Math.toRadians(180));
    public static final Pose bucketPreloadPose = new Pose(29.25, 78.375, Math.toRadians(180));

    public static final Pose bucketLeftSamplePose = new Pose(22, 110, 0);
    public static final Pose bucketLeftSampleControlPose = new Pose(20, 96);
    public static final Pose bucketMidSamplePose = new Pose(22, 116, 0);
    public static final Pose bucketMidSampleControlPose = new Pose(20, 86);
    public static final Pose bucketRightSamplePose = new Pose(22, 122, 0);
    public static final Pose bucketRightSampleControlPose = new Pose(20, 96);

    public static final Pose bucketScorePose = new Pose(20, 128, Math.toRadians(-45));

    public static final Pose bucketParkPose = new Pose(62, 97.75, Math.toRadians(90));
    public static final Pose bucketParkControlPose = new Pose(60.25, 123.5);

    // Observation Poses
    public static final Pose observationStartPose = new Pose(7.5, 65.25, Math.toRadians(180));
    public static final Pose observationPreloadPose = new Pose(38.5, 68, Math.toRadians(180));

    public static final Pose observationSpecimenSetPose = new Pose(12, 35, Math.toRadians(180));

    public static final Pose observationSpecimenPickupPose = new Pose(6.5, 35, Math.toRadians(180));
    public static final Pose observationSpecimenPickup2Pose = new Pose(6.5, 35, Math.toRadians(180));
    public static final Pose observationSpecimenPickup3Pose = new Pose(6.5, 35, Math.toRadians(180));
    public static final Pose observationSpecimenPickup4Pose = new Pose(6.5, 35, Math.toRadians(180));

    public static final Pose observationSpecimen1Pose = new Pose(38, 72.25, Math.toRadians(180));
    public static final Pose observationSpecimen2Pose = new Pose(38, 76.75, Math.toRadians(180));
    public static final Pose observationSpecimen3Pose = new Pose(38, 80.25, Math.toRadians(180));
    public static final Pose observationSpecimen4Pose = new Pose(38, 84.25, Math.toRadians(180));

    public static final Pose observationParkPose = new Pose(12, 30, Math.toRadians(180));



}
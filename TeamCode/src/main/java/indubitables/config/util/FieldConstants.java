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
    public static Pose bucketStartPose = new Pose(7.5, 78.75, Math.toRadians(180));
    public static Pose bucketPreloadPose = new Pose(29.25, 78.375, Math.toRadians(180));

    public static Pose bucketLeftSamplePose = new Pose(22, 110, 0);
    public static Pose bucketLeftSampleControlPose = new Pose(20, 96);
    public static Pose bucketMidSamplePose = new Pose(22, 116, 0);
    public static Pose bucketMidSampleControlPose = new Pose(20, 86);
    public static Pose bucketRightSamplePose = new Pose(22, 122, 0);
    public static Pose bucketRightSampleControlPose = new Pose(20, 96);

    public static Pose bucketScorePose = new Pose(20, 128, Math.toRadians(-45));

    public static Pose bucketParkPose = new Pose(62, 97.75, Math.toRadians(90));
    public static Pose bucketParkControlPose = new Pose(60.25, 123.5);

    // Observation Poses
    public static Pose observationStartPose = new Pose(7.5, 65.25, Math.toRadians(180));
    public static Pose observationPreloadPose = new Pose(38.5, 68, Math.toRadians(180));

    public static Pose observationSpecimenSetPose = new Pose(12, 35, Math.toRadians(180));

    public static Pose observationSpecimenPickupPose = new Pose(6.75, 37, Math.toRadians(180));
    public static Pose observationSpecimenPickup2Pose = new Pose(6.75, 37, Math.toRadians(180));
    public static Pose observationSpecimenPickup3Pose = new Pose(6.75, 37, Math.toRadians(180));
    public static Pose observationSpecimenPickup4Pose = new Pose(6.75, 37, Math.toRadians(180));

    public static Pose observationSpecimen1Pose = new Pose(38, 76, Math.toRadians(180));
    public static Pose observationSpecimen2Pose = new Pose(38, 79.75, Math.toRadians(180));
    public static Pose observationSpecimen3Pose = new Pose(38, 82.25, Math.toRadians(180));
    public static Pose observationSpecimen4Pose = new Pose(38, 85.25, Math.toRadians(180));

    public static Pose observationParkPose = new Pose(24, 44, Math.toRadians(215));



}
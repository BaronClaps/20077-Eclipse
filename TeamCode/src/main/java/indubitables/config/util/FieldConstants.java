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
    public static Pose bucketStartPose = new Pose(7.5, 78.75, Math.toRadians(270));

    public static Pose bucketLeftSampleGrabPose = new Pose(35.1, 120.8, Math.toRadians(0));
    public static Pose bucketMidSampleGrabPose = new Pose(34.8, 131.4, Math.toRadians(0));
    public static Pose bucketRightSampleGrabPose = new Pose(45.4, 129.4, Math.toRadians(90));

    public static Pose bucketScorePose = new Pose(16, 128, Math.toRadians(315));

    public static Pose bucketParkPose = new Pose(60.7, 95.6, Math.toRadians(90));

    // Observation Poses
    public static Pose observationStartPose = new Pose(7.5, 65.25, Math.toRadians(180));
    public static Pose observationPreloadPose = new Pose(38.5, 75, Math.toRadians(180));

    public static Pose observationSpecimenSetPose = new Pose(12, 35, Math.toRadians(180));

    public static Pose observationSpecimenPickupPose = new Pose(6.5, 35, Math.toRadians(180));
    public static Pose observationSpecimenPickup2Pose = new Pose(6.5, 35, Math.toRadians(180));
    public static Pose observationSpecimenPickup3Pose = new Pose(6.5, 35, Math.toRadians(180));
    public static Pose observationSpecimenPickup4Pose = new Pose(6.5, 35, Math.toRadians(180));

    public static Pose observationSpecimen1Pose = new Pose(38, 74, Math.toRadians(180));
    public static Pose observationSpecimen2Pose = new Pose(38, 72, Math.toRadians(180));
    public static Pose observationSpecimen3Pose = new Pose(38, 69, Math.toRadians(180));
    public static Pose observationSpecimen4Pose = new Pose(38, 66, Math.toRadians(180));

    public static Pose observationParkPose = new Pose(24, 44, Math.toRadians(215));

    public static Pose teleopStartPose = new Pose(24,44,Math.toRadians(215));

    public static Pose humanPlayerPose = new Pose(7,40, Math.PI);



}
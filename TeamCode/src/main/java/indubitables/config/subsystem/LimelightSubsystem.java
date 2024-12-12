package indubitables.config.subsystem;

import androidx.annotation.NonNull;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.io.Console;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import indubitables.pedroPathing.follower.Follower;
import indubitables.pedroPathing.localization.Pose;
import indubitables.pedroPathing.pathGeneration.MathFunctions;

public class LimelightSubsystem {

    public enum limelightState {
        yellow,
        red,
        blue,
        aprilTag,
        none
    }

        // Camera field of view and resolution (adjust based on your camera's specs)
        private static final double CAMERA_FOV_HORIZONTAL = 54.5; // in degrees
        private static final double CAMERA_FOV_VERTICAL = 42; // in degrees
        private static final int CAMERA_RESOLUTION_WIDTH = 960; // in pixels
        private static final int CAMERA_RESOLUTION_HEIGHT = 720; // in pixels

        private static final double INCHES_PER_PIXEL_HORIZONTAL = CAMERA_FOV_HORIZONTAL / CAMERA_RESOLUTION_WIDTH;
        private static final double INCHES_PER_PIXEL_VERTICAL = CAMERA_FOV_VERTICAL / CAMERA_RESOLUTION_HEIGHT;

        private double cameraOffsetX, cameraOffsetY, cameraHeadingOffset;

        private Telemetry telemetry;
        private Follower follower;
        private List<Sample> detectedSamples;

        public limelightState state;
        private Limelight3A limelight;
        private LLResult result;

        private Pose targetPose;

        private int pipeline = 0;

        public LimelightSubsystem(HardwareMap hardwareMap, Telemetry telemetry, Follower follower, Pose targetPose) {
            this.telemetry = telemetry;
            this.follower = follower;
            this.targetPose = targetPose;
            this.cameraOffsetX = -3;
            this.cameraOffsetY = -3;
            this.cameraHeadingOffset = Math.toRadians(180);
            limelight = hardwareMap.get(Limelight3A.class, "limelight");
            limelight.setPollRateHz(100); // per sec
        }

        public void init() {
            limelight.start();
            limelight.pipelineSwitch(pipeline);

            update();

            List<LLResultTypes.DetectorResult> detections = result.getDetectorResults();
            detectedSamples.clear();

            for (LLResultTypes.DetectorResult detection : detections) {
                String className = detection.getClassName(); // What was detected

                // Convert target position in pixels to inches relative to the camera
                double targetXInches = detection.getTargetXPixels() * INCHES_PER_PIXEL_HORIZONTAL;
                double targetYInches = detection.getTargetYPixels() * INCHES_PER_PIXEL_VERTICAL;

                // Calculate the field-relative position of the target
                Pose cameraPose = getCameraPose(follower.getPose(), cameraOffsetX, cameraOffsetY, cameraHeadingOffset);
                double fieldX = cameraPose.getX() + targetXInches * Math.cos(cameraPose.getHeading()) -
                        targetYInches * Math.sin(cameraPose.getHeading());
                double fieldY = cameraPose.getY() + targetXInches * Math.sin(cameraPose.getHeading()) +
                        targetYInches * Math.cos(cameraPose.getHeading());

                detectedSamples.add(new Sample(className, fieldX, fieldY));
                telemetry.addData(className, "at (" + fieldX + ", " + fieldY + ") inches");
            }

            sortSamplesByDistance();

            telemetry.addData("Detected Samples", detectedSamples.toString());
            telemetry.update();
        }

        public void start() {
            limelight.start();
            limelight.pipelineSwitch(pipeline);
        }

        public void switchPipeline(limelightState state) {
            switch (state) {
                case yellow:
                    pipeline = 0;
                    break;
                case red:
                    pipeline = 1;
                    break;
                case blue:
                    pipeline = 2;
                    break;
                case aprilTag:
                    pipeline = 3;
                    break;
            }

            limelight.pipelineSwitch(pipeline);

            if (state == limelightState.none) {
                limelight.stop();
            }
        }

        public void update() {
            result = limelight.getLatestResult();
        }

    /**
     * Calculates the field-relative position of a camera based on its offset and heading
     * from the robot center.
     *
     * @param robotPose The current pose of the robot.
     * @param cameraOffsetX The x offset of the camera from the robot center (inches).
     * @param cameraOffsetY The y offset of the camera from the robot center (inches).
     * @param cameraHeadingOffset The heading offset of the camera relative to the robot's front (radians).
     * @return A new Pose representing the camera's position and heading on the field.
     */
    public Pose getCameraPose(Pose robotPose, double cameraOffsetX, double cameraOffsetY, double cameraHeadingOffset) {

        // Rotate the camera's position offset to field space
        double rotatedX = cameraOffsetX * Math.cos(robotPose.getHeading()) - cameraOffsetY * Math.sin(robotPose.getHeading());
        double rotatedY = cameraOffsetX * Math.sin(robotPose.getHeading()) + cameraOffsetY * Math.cos(robotPose.getHeading());

        // Add the rotated offsets to the robot's position
        double cameraFieldX = robotPose.getX() + rotatedX;
        double cameraFieldY = robotPose.getY() + rotatedY;

        // Calculate the camera's field-relative heading (in radians)
        double cameraFieldHeading = MathFunctions.normalizeAngle(robotPose.getHeading() + cameraHeadingOffset);

        // Return the camera's field-relative pose
        return new Pose(cameraFieldX, cameraFieldY, cameraFieldHeading);
    }

    /**
     * Sorts the detected samples by their distance to the targetPose.
     * The closer the sample is to the targetPose, the higher it is in the list.
     */
    private void sortSamplesByDistance() {
        detectedSamples.sort((sample1, sample2) -> {
            double distance1 = calculateDistance(sample1.x, sample1.y, targetPose.getX(), targetPose.getY());
            double distance2 = calculateDistance(sample2.x, sample2.y, targetPose.getX(), targetPose.getY());
            return Double.compare(distance1, distance2); // Ascending order
        });
    }

    /**
     * Calculates the Euclidean distance between two points.
     *
     * @param x1 The x-coordinate of the first point.
     * @param y1 The y-coordinate of the first point.
     * @param x2 The x-coordinate of the second point.
     * @param y2 The y-coordinate of the second point.
     * @return The Euclidean distance between the two points.
     */
    private double calculateDistance(double x1, double y1, double x2, double y2) {
        return Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

}

    class Sample implements Comparable<Sample> {
        String name;
        double x;
        double y;

        public Sample(String name, double x, double y) {
            this.name = name;
            this.x = x;
            this.y = y;
        }

        @Override
        public int compareTo(Sample o) {
            return 0;
        }

        @NonNull
        @Override
        public String toString() {
            return name + " at (" + x + ", " + y + ")";
        }
    }


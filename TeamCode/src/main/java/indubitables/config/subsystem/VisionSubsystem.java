package indubitables.config.subsystem;

import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

import indubitables.pedroPathing.follower.Follower;

public class VisionSubsystem {

    public enum limelightState {
        yellow,
        red,
        blue,
        aprilTag,
        none
    }

    private Telemetry telemetry;

    public limelightState state;
    private Limelight3A limelight;
    private LLResult result;

    private int pipeline = 0;
    private double x = 0;
    private double y = 0;

    private DcMotor lf,rf,lb,rb;


    public VisionSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        limelight = hardwareMap.get(Limelight3A.class, "limelight");
        limelight.setPollRateHz(100); // per sec

        lf = hardwareMap.get(DcMotor.class, "leftFront");
        rf = hardwareMap.get(DcMotor.class, "rightFront");
        lb = hardwareMap.get(DcMotor.class, "leftRear");
        rb = hardwareMap.get(DcMotor.class, "rightRear");
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

    /*public Pose aprilTagPose(Pose currentPose, int id) {
        switchPipeline(limelightState.aprilTag);
        List<LLResultTypes.FiducialResult> fiducials = result.getFiducialResults();
        for (LLResultTypes.FiducialResult fiducial : fiducials) {
            if (fiducial.getFiducialId() == id) {
                x = detection.getTargetXDegrees();
                y = detection.getTargetYDegrees();
                double StrafeDistance_3D = fiducial.getRobotPoseTargetSpace().getY();
            }
        }
        return new Pose(x, y, heading);
    }*/

    public void updateColor() {
        update();
        if (result != null) {
            telemetry.addData("tx", result.getTx());
            telemetry.addData("ty", result.getTy());
        }
    }

    public void driveAlign(double power) {
        if(result.getTx() >= 1) {
            strafeLeft(power);
        } else if(result.getTx() <= -1) {
            strafeLeft(-power);
        } else {
            strafeLeft(0);
        }
    }

    public void update() {
        result = limelight.getLatestResult();
    }

    public void strafeLeft(double left) {
        double leftFrontPower = -left;
        double rightFrontPower = left;
        double leftBackPower = left;
        double rightBackPower = -left;

        // Normalize wheel powers to be less than 1.0
        double max = Math.max(Math.abs(leftFrontPower), Math.abs(rightFrontPower));
        max = Math.max(max, Math.abs(leftBackPower));
        max = Math.max(max, Math.abs(rightBackPower));

        if (max > 1.0) {
            leftFrontPower /= max;
            rightFrontPower /= max;
            leftBackPower /= max;
            rightBackPower /= max;
        }

        // Send powers to the wheels.
        lf.setPower(leftFrontPower);
        rf.setPower(rightFrontPower);
        lb.setPower(leftBackPower);
        rb.setPower(rightBackPower);
    }

}
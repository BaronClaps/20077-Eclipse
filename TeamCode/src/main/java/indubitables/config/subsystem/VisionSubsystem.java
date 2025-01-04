package indubitables.config.subsystem;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.List;

import indubitables.config.vision.SampleDetectionPipeline;
import indubitables.pedroPathing.follower.Follower;

@Config
public class VisionSubsystem {

    private Telemetry telemetry;
    private HardwareMap hardwareMap;
    private double degrees;

    private OpenCvCamera controlHubCam;  // Use OpenCvCamera class from FTC SDK
    private static final int CAMERA_WIDTH = 640; // width  of wanted camera resolution
    private static final int CAMERA_HEIGHT = 360; // height of wanted camera resolution

    private SampleDetectionPipeline pipeline;
    private IntakeSubsystem intake;
    private String color;


    public VisionSubsystem(HardwareMap hardwareMap, Telemetry telemetry, String color, IntakeSubsystem intake) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;
        this.intake = intake;
        this.color = color;
    }

    public void init() {
        FtcDashboard dashboard = FtcDashboard.getInstance();
        telemetry = new MultipleTelemetry(this.telemetry, dashboard.getTelemetry());
        FtcDashboard.getInstance().startCameraStream(controlHubCam, 30);

        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());

        // Use OpenCvCameraFactory class from FTC SDK to create camera instance
        controlHubCam = OpenCvCameraFactory.getInstance().createWebcam(
                hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);


        pipeline = new SampleDetectionPipeline(telemetry);

        controlHubCam.setPipeline(pipeline);

        controlHubCam.openCameraDevice();
        controlHubCam.startStreaming(CAMERA_WIDTH, CAMERA_HEIGHT, OpenCvCameraRotation.UPSIDE_DOWN);

        pipeline.setSelectedColor(color);
    }

    public void clawAlign() {
        telemetry.addData("stone degrees", pipeline.getSelectedStoneDegrees());
        intake.rotateDegrees(-pipeline.getSelectedStoneDegrees());
    }

    public void setBlue() {
        pipeline.setSelectedColor("Blue");
    }

    public void setRed() {
        pipeline.setSelectedColor("Red");
    }

    public void setYellow() {
        pipeline.setSelectedColor("Yellow");
    }

}
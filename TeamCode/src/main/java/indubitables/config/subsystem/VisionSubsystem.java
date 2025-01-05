package indubitables.config.subsystem;

import android.graphics.Color;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.config.Config;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.NormalizedColorSensor;
import com.qualcomm.robotcore.hardware.NormalizedRGBA;

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
    private NormalizedColorSensor cSensor;
    private float[] hsvValues = new float[3];


    // Define the HSV ranges for each color
    int[] redBottom1 = {0, 100, 50};
    int[] redTop1 = {10, 255, 255};
    int[] redBottom2 = {160, 100, 50};
    int[] redTop2 = {180, 255, 255};

    int[] blueBottom = {100, 100, 50};
    int[] blueTop = {140, 255, 255};

    int[] yellowBottom = {20, 100, 50};
    int[] yellowTop = {30, 255, 255};


    public VisionSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.hardwareMap = hardwareMap;
        this.telemetry = telemetry;

       // cSensor = hardwareMap.get(RevColorSensorV3.class, "colorSensor");
    }

    public void init() {
       // telemetry.addData("color sensor status", cSensor.getDeviceName());

    }

    public boolean sampleDetected() {
        NormalizedRGBA color = cSensor.getNormalizedColors();

        Color.colorToHSV(color.toColor(), hsvValues);

        return isBlue(hsvValues) || isRed(hsvValues) || isYellow(hsvValues);
    }

    // Function to check if the HSV value corresponds to red
    public boolean isRed(float[] hsvValues) {
        // Check both red ranges
        boolean inFirstRange = hsvValues[0] >= redBottom1[0] && hsvValues[0] <= redTop1[0] &&
                hsvValues[1] >= redBottom1[1] && hsvValues[1] <= redTop1[1] &&
                hsvValues[2] >= redBottom1[2] && hsvValues[2] <= redTop1[2];

        boolean inSecondRange = hsvValues[0] >= redBottom2[0] && hsvValues[0] <= redTop2[0] &&
                hsvValues[1] >= redBottom2[1] && hsvValues[1] <= redTop2[1] &&
                hsvValues[2] >= redBottom2[2] && hsvValues[2] <= redTop2[2];

        return inFirstRange || inSecondRange;
    }

    // Function to check if the HSV value corresponds to blue
    public boolean isBlue(float[] hsvValues) {
        return hsvValues[0] >= blueBottom[0] && hsvValues[0] <= blueTop[0] &&
                hsvValues[1] >= blueBottom[1] && hsvValues[1] <= blueTop[1] &&
                hsvValues[2] >= blueBottom[2] && hsvValues[2] <= blueTop[2];
    }

    // Function to check if the HSV value corresponds to yellow
    public boolean isYellow(float[] hsvValues) {
        return hsvValues[0] >= yellowBottom[0] && hsvValues[0] <= yellowTop[0] &&
                hsvValues[1] >= yellowBottom[1] && hsvValues[1] <= yellowTop[1] &&
                hsvValues[2] >= yellowBottom[2] && hsvValues[2] <= yellowTop[2];
    }

    public void telemetry() {
        /*telemetry.addLine()
                .addData("Hue", "%.3f", hsvValues[0])
                .addData("Saturation", "%.3f", hsvValues[1])
                .addData("Value", "%.3f", hsvValues[2]);

        telemetry.addData("isBlue", isBlue(hsvValues));
        telemetry.addData("isRed", isRed(hsvValues));
        telemetry.addData("isYellow", isYellow(hsvValues));*/
    }
}


   /* private Telemetry telemetry;
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
    }*/


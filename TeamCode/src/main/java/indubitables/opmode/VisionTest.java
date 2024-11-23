package indubitables.opmode;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import indubitables.config.subsystem.VisionSubsystem;

@TeleOp (name = "visiontest")
public class VisionTest extends OpMode {
    VisionSubsystem vision;
    @Override
    public void init() {
        vision = new VisionSubsystem(hardwareMap, telemetry);
        vision.switchPipeline(VisionSubsystem.limelightState.blue);
    }

    @Override
    public void start() {
        vision.start();
    }

    @Override
    public void loop() {
        vision.updateColor();

        vision.driveAlign(gamepad1.left_stick_x);
    }
}

package indubitables.opmode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import indubitables.config.subsystem.LimelightSubsystem;
import indubitables.config.util.FieldConstants;
import indubitables.config.util.RobotConstants;
import indubitables.pedroPathing.localization.Pose;

@TeleOp
@Config
public class LimelightTest extends OpMode {

    private LimelightSubsystem limelight;

    @Override
    public void init() {
        limelight = new LimelightSubsystem(hardwareMap, telemetry, FieldConstants.observationStartPose, new Pose(36,72, Math.toRadians(0)));
        limelight.init();
    }

    @Override
    public void loop() {

    }
}

package indubitables.opmode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.Gamepad;

import indubitables.config.subsystem.IntakeSubsystem;
import indubitables.config.subsystem.VisionSubsystem;
import indubitables.pedroPathing.follower.Follower;

@Config
@TeleOp(name="visionTest", group="b")
public class VisionTest extends OpMode {

    private IntakeSubsystem intake;
    private IntakeSubsystem.GrabState intakeGrabState;
    private IntakeSubsystem.RotateState intakeRotateState;
    private IntakeSubsystem.PivotState intakePivotState;

    private VisionSubsystem vision;

    private Follower follower;
    private Gamepad currentGamepad2 = new Gamepad(), previousGamepad2 = new Gamepad();

    @Override
    public void init() {
        intake = new IntakeSubsystem(hardwareMap, telemetry, intakeGrabState, intakeRotateState, intakePivotState);
        follower = new Follower(hardwareMap);
        intake.init();
        vision = new VisionSubsystem(hardwareMap, telemetry);
        vision.init();
    }

    @Override
    public void start() {
        follower.startTeleopDrive();
        intake.start();
    }

    @Override
    public void loop() {
        previousGamepad2.copy(currentGamepad2);
        currentGamepad2.copy(gamepad2);

        if(gamepad1.x) {
            intake.transfer();
        }

        if(gamepad1.y) {
            intake.ground();
        }
        if(gamepad1.right_bumper) {
            intake.close();
        } else if (gamepad1.left_bumper) {
            intake.open();
        }

        follower.setTeleOpMovementVectors(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x, true);
        follower.update();


        //  telemetry.addData("armState", arm.state);
        intake.telemetry();
        telemetry.update();
    }
}

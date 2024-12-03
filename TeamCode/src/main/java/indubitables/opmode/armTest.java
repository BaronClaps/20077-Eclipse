package indubitables.opmode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import indubitables.config.subsystem.OuttakeSubsystem;
import indubitables.pedroPathing.follower.Follower;

@Config
@TeleOp(name="armTest", group="a")
public class armTest extends OpMode {

    private ArmSubsystem arm;
    private OuttakeSubsystem claw;
    private OuttakeSubsystem.ClawGrabState clawGrabState;
    private OuttakeSubsystem.ClawPivotState clawPivotState;
    private ArmSubsystem.ArmState armState;
    private Follower follower;

    @Override
    public void init() {
        arm = new ArmSubsystem(hardwareMap, armState);
        claw = new OuttakeSubsystem(hardwareMap, clawGrabState, clawPivotState);
        follower = new Follower(hardwareMap);
        arm.init();
        claw.init();
    }

    @Override
    public void start() {
        follower.startTeleopDrive();
    }

    @Override
    public void loop() {
        if(gamepad1.x) {
            claw.transfer();
            arm.transfer();
        }

        if(gamepad1.y) {
            claw.score();
            arm.score();
        }
        if(gamepad1.right_bumper) {
            claw.close();
        } else if (gamepad1.left_bumper) {
            claw.open();
        }

        follower.setTeleOpMovementVectors(-gamepad1.left_stick_y, -gamepad1.left_stick_x, -gamepad1.right_stick_x, true);
        follower.update();


      //  telemetry.addData("armState", arm.state);
        telemetry.addData("left", arm.left.getPosition());
        telemetry.addData("right", arm.right.getPosition());
        telemetry.addData("leftPivot", claw.leftRotate.getPosition());
        telemetry.addData("rightPivot", claw.rightRotate.getPosition());
        telemetry.addData("grab", claw.grab.getPosition());
        telemetry.update();
    }
}

package indubitables.opmode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import indubitables.config.subsystem.ArmSubsystem;
import indubitables.config.subsystem.ClawSubsystem;
import indubitables.config.util.RobotConstants;

@Config
@TeleOp(name="armTest", group="a")
public class ArmTest extends OpMode {

    private ArmSubsystem arm;
    private ClawSubsystem claw;
    private ClawSubsystem.ClawGrabState clawGrabState;
    private ClawSubsystem.ClawPivotState clawPivotState;
    private ArmSubsystem.ArmState armState;

    @Override
    public void init() {
        arm = new ArmSubsystem(hardwareMap, armState);
        claw = new ClawSubsystem(hardwareMap, clawGrabState, clawPivotState);
        arm.init();
        claw.init();
    }

    @Override
    public void loop() {
        if(gamepad1.x) {
            claw.pivot.setPosition(RobotConstants.clawScore);
            arm.left.setPosition(RobotConstants.armScoring);
            arm.right.setPosition(RobotConstants.armScoring);
        }

        if(gamepad1.y) {
            arm.left.setPosition(RobotConstants.armTransfer);
            arm.right.setPosition(RobotConstants.armTransfer);
        }

        if(gamepad1.a) {
            arm.left.setPosition(RobotConstants.armInit);
            arm.right.setPosition(RobotConstants.armInit);
            claw.pivot.setPosition(RobotConstants.clawScore);
        }

        if(gamepad1.b) {
            arm.left.setPosition(RobotConstants.armInit);
            arm.right.setPosition(RobotConstants.armInit);
            claw.pivot.setPosition(RobotConstants.clawSpecimenGrab);
        }

        if(gamepad1.right_bumper) {
            claw.close();
        } else if (gamepad1.left_bumper) {
            claw.open();
        }


      //  telemetry.addData("armState", arm.state);
        telemetry.addData("left", arm.left.getPosition());
        telemetry.addData("right", arm.right.getPosition());
        telemetry.addData("pivot", claw.pivot.getPosition());
    }
}

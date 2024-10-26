package indubitables.opmode;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import indubitables.config.subsystem.ArmSubsystem;
import indubitables.config.util.RobotConstants;

@Config
@TeleOp(name="armTest", group="a")
public class ArmTest extends OpMode {

    private ArmSubsystem arm;
    private ArmSubsystem.ArmState armState;

    @Override
    public void init() {
        arm = new ArmSubsystem(hardwareMap, armState);
        arm.init();
    }

    @Override
    public void loop() {
        if(gamepad1.x)
            //arm.setState(ArmSubsystem.ArmState.SCORING);
            arm.left.setPosition(RobotConstants.armScoring);
            arm.right.setPosition(RobotConstants.armScoring);

        if(gamepad1.y)
            arm.left.setPosition(RobotConstants.armTransfer);
            arm.right.setPosition(RobotConstants.armTransfer);


      //  telemetry.addData("armState", arm.state);
        telemetry.addData("left", arm.left.getPosition());
        telemetry.addData("right", arm.right.getPosition());
    }
}

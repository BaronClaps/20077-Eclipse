package indubitables.config.subsystem;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import indubitables.config.util.action.Actions;
import indubitables.config.util.action.RunAction;
import indubitables.config.util.RobotConstants;


public class ArmSubsystem {

    public enum ArmState {
        TRANSFER, SCORING
    }

    private Servo left, right;
    private ArmState state;
    public RunAction toTransfer, toScoring;

    public ArmSubsystem(HardwareMap hardwareMap, ArmState state) {
        left = hardwareMap.get(Servo.class, "leftArm");
        right = hardwareMap.get(Servo.class, "rightArm");
        this.state = state;

        toTransfer = new RunAction(this::toTransfer);
        toScoring = new RunAction(this::toScoring);
    }

    // State //
    public void setState(ArmState armState) {
        if (armState == ArmState.TRANSFER) {
            left.setPosition(RobotConstants.armTransfer);
            right.setPosition(RobotConstants.armTransfer);
            this.state = ArmState.TRANSFER;
        } else if (armState == ArmState.SCORING) {
            left.setPosition(RobotConstants.armScoring);
            right.setPosition(RobotConstants.armScoring);
            this.state = ArmState.SCORING;
        }
    }

    public void switchState() {
        if (state == ArmState.TRANSFER) {
            setState(ArmState.SCORING);
        } else if (state == ArmState.SCORING) {
            setState(ArmState.TRANSFER);
        }
    }

    // Preset //

    public void toTransfer() {
        setState(ArmState.TRANSFER);
    }

    public void toScoring() {
        setState(ArmState.SCORING);
    }

    // Util //
    public void setPos(double armPos) {
        left.setPosition(armPos);
        right.setPosition(armPos);
    }

    public double getPos() {
        return left.getPosition();
    }

    // Init + Start //
    public void init() {
        Actions.runBlocking(toTransfer);
    }

    public void start() {
        Actions.runBlocking(toTransfer);
    }

}
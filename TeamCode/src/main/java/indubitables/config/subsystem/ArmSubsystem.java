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
    public ArmState state;
    public RunAction toTransfer, toScoring;

    public ArmSubsystem(HardwareMap hardwareMap, ArmState state) {
        left = hardwareMap.get(Servo.class, "leftArm");
        right = hardwareMap.get(Servo.class, "rightArm");
        this.state = state;

        toTransfer = new RunAction(this::transfer);
        toScoring = new RunAction(this::score);
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

    public void transfer() {
        setState(ArmState.TRANSFER);
    }

    public void score() {
        setState(ArmState.SCORING);
    }

    // Util //
    public void setPos(double armPos) {
        left.setPosition(armPos);
        right.setPosition(armPos);
    }

    // Init + Start //
    public void init() {
        transfer();
    }

    public void start() {
        transfer();
    }

}
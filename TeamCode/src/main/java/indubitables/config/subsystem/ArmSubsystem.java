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

    private Servo arm;
    private ArmState state;
    public RunAction toTransfer, toScoring;

    public ArmSubsystem(HardwareMap hardwareMap, ArmState state) {
        arm = hardwareMap.get(Servo.class, "arm");
        this.state = state;

        toTransfer = new RunAction(this::toTransfer);
        toScoring = new RunAction(this::toScoring);
    }

    // State //
    public void setState(ArmState armState) {
        if (armState == ArmState.TRANSFER) {
            arm.setPosition(RobotConstants.armTransfer);
            this.state = ArmState.TRANSFER;
        } else if (armState == ArmState.SCORING) {
            arm.setPosition(RobotConstants.armScoring);
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
        arm.setPosition(armPos);
    }

    public double getPos() {
        return arm.getPosition();
    }

    // Init + Start //
    public void init() {
        Actions.runBlocking(toTransfer);
    }

    public void start() {
        Actions.runBlocking(toTransfer);
    }

}
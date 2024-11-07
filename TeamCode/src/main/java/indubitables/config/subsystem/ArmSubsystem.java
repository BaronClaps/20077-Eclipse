package indubitables.config.subsystem;

import static indubitables.config.util.RobotConstants.*;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import indubitables.config.util.action.RunAction;


public class ArmSubsystem {

    public enum ArmState {
        TRANSFER, SCORING, SPECIMEN, CHAMBER, INIT, SPECIMENGRAB, SPECIMENCSCORE
    }

    public Servo left, right;
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
            left.setPosition(armTransfer);
            right.setPosition(armTransfer);
            this.state = ArmState.TRANSFER;
        } else if (armState == ArmState.SCORING) {
            left.setPosition(armScoring);
            right.setPosition(armScoring);
            this.state = ArmState.SCORING;
        } else if (armState == ArmState.SPECIMEN) {
            left.setPosition(armSpecimen);
            right.setPosition(armSpecimen);
            this.state = ArmState.SPECIMEN;
        } else if (armState == ArmState.CHAMBER) {
            left.setPosition(armChamber);
            right.setPosition(armChamber);
            this.state = ArmState.CHAMBER;
        } else if (armState == ArmState.INIT) {
            left.setPosition(armInit);
            right.setPosition(armInit);
            this.state = ArmState.INIT;
        } else if (armState == ArmState.SPECIMENGRAB) {
            left.setPosition(armSpecimenGrab);
            right.setPosition(armSpecimenGrab);
            this.state = ArmState.SPECIMENGRAB;
        } else if (armState == ArmState.SPECIMENCSCORE) {
            left.setPosition(armSpecimenScore);
            right.setPosition(armSpecimenScore);
            this.state = ArmState.SPECIMENCSCORE;
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

    public void specimen() {
        setState(ArmState.SPECIMEN);
    }

    public void specimenGrab() {
        setState(ArmState.SPECIMENGRAB);
    }

    public void specimenScore() {
        setState(ArmState.SPECIMENCSCORE);
    }

    public void chamber() {
        setState(ArmState.CHAMBER);
    }

    public void initArm() {setState(ArmState.INIT);}

    // Util //
    public void setPos(double armPos) {
        left.setPosition(armPos);
        right.setPosition(armPos);
    }

    // Init + Start //
    public void init() {
        initArm();
    }

    public void start() {
        transfer();
    }

}
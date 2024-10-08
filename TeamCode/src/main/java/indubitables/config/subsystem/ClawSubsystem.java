package indubitables.config.subsystem;
import static indubitables.config.util.RobotConstants.*;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import indubitables.config.util.RobotConstants;
import indubitables.config.util.action.Actions;
import indubitables.config.util.action.RunAction;

/** @author Baron Henderson
 * @version 1.0 | 7/1/24
 */

public class ClawSubsystem {

    public enum ClawGrabState {
        CLOSED, OPEN
    }

    public enum ClawPivotState {
        TRANSFER, SCORE, SPECIMEN, CHAMBER, SPECIMEN_SCORE
    }

    public Servo grab, pivot;
    public ClawGrabState grabState;
    public ClawPivotState pivotState;
    public RunAction open, close, transfer, score, specimen, specimenScore;

    public ClawSubsystem(HardwareMap hardwareMap, ClawGrabState clawGrabState, ClawPivotState clawPivotState) {
        grab = hardwareMap.get(Servo.class, "clawGrab");
        pivot = hardwareMap.get(Servo.class, "clawPivot");
        this.grabState = clawGrabState;
        this.pivotState = clawPivotState;

        open = new RunAction(this::open);
        close = new RunAction(this::close);
        transfer = new RunAction(this::transfer);
        score = new RunAction(this::score);
        specimen = new RunAction(this::specimen);
        specimenScore = new RunAction(this::specimenScore);
    }

    public void setPivotState(ClawPivotState state) {
        if (state == ClawPivotState.TRANSFER) {
            pivot.setPosition(clawTransfer);
            this.pivotState = ClawPivotState.TRANSFER;
        } else if (state == ClawPivotState.SCORE) {
            pivot.setPosition(clawScore);
            this.pivotState = ClawPivotState.SCORE;
        } else if (state == ClawPivotState.SPECIMEN) {
            pivot.setPosition(clawSpecimen);
            this.pivotState = ClawPivotState.SPECIMEN;
        } else if (state == ClawPivotState.CHAMBER) {
            pivot.setPosition(clawChamber);
            this.pivotState = ClawPivotState.CHAMBER;
        } else if (state == ClawPivotState.SPECIMEN_SCORE) {
            pivot.setPosition(clawSpecimenScore);
            this.pivotState = ClawPivotState.SPECIMEN_SCORE;
        }
    }

    public void switchPivotState() {
        if (pivotState == ClawPivotState.TRANSFER) {
            setPivotState(ClawPivotState.SCORE);
        } else if (pivotState == ClawPivotState.SCORE) {
            setPivotState(ClawPivotState.TRANSFER);
        }
    }

    public void setGrabState(ClawGrabState clawGrabState) {
        if (clawGrabState == ClawGrabState.CLOSED) {
            grab.setPosition(clawClose);
            this.grabState = ClawGrabState.CLOSED;
        } else if (clawGrabState == ClawGrabState.OPEN) {
            grab.setPosition(clawOpen);
            this.grabState = ClawGrabState.OPEN;
        }
    }

    public void switchGrabState() {
        if (grabState == ClawGrabState.CLOSED) {
            setGrabState(ClawGrabState.OPEN);
        } else if (grabState == ClawGrabState.OPEN) {
            setGrabState(ClawGrabState.CLOSED);
        }
    }

    public void open() {
        setGrabState(ClawGrabState.OPEN);
    }

    public void close() {
        setGrabState(ClawGrabState.CLOSED);
    }

    public void transfer() {
        setPivotState(ClawPivotState.TRANSFER);
    }

    public void score() {
        setPivotState(ClawPivotState.SCORE);
    }

    public void specimen() {
        setPivotState(ClawPivotState.SPECIMEN);
    }

    public void chamber() {
        setPivotState(ClawPivotState.CHAMBER);
    }

    public void specimenScore() {
        setPivotState(ClawPivotState.SPECIMEN_SCORE);
    }

    public void init() {
        close();
        transfer();
    }

    public void start() {
        close();
        transfer();
    }



}
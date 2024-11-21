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
        TRANSFER, SCORE, INIT, SPECIMENGRAB, SPECIMENSCORE
    }

    public Servo grab, leftPivot, rightPivot;
    public ClawGrabState grabState;
    public ClawPivotState pivotState;
    public RunAction open, close, transfer, score, specimen;

    public ClawSubsystem(HardwareMap hardwareMap, ClawGrabState clawGrabState, ClawPivotState clawPivotState) {
        grab = hardwareMap.get(Servo.class, "clawGrab");
        leftPivot = hardwareMap.get(Servo.class, "leftPivot");
        rightPivot = hardwareMap.get(Servo.class, "rightPivot");
        this.grabState = clawGrabState;
        this.pivotState = clawPivotState;

        open = new RunAction(this::open);
        close = new RunAction(this::close);
        transfer = new RunAction(this::transfer);
        score = new RunAction(this::score);
    }

    public void setPivotState(ClawPivotState state) {
        if (state == ClawPivotState.TRANSFER) {
            leftPivot.setPosition(clawTransfer);
            rightPivot.setPosition(clawTransfer);
            this.pivotState = ClawPivotState.TRANSFER;
        } else if (state == ClawPivotState.SCORE) {
            leftPivot.setPosition(clawLeftScore);
            rightPivot.setPosition(clawRightScore);
            this.pivotState = ClawPivotState.SCORE;
        } else if (state == ClawPivotState.INIT) {
            leftPivot.setPosition(clawInit);
            rightPivot.setPosition(clawInit);
            this.pivotState = ClawPivotState.INIT;
        } else if (state == ClawPivotState.SPECIMENGRAB) {
            leftPivot.setPosition(clawSpecimenGrab);
            rightPivot.setPosition(clawSpecimenGrab);
            this.pivotState = ClawPivotState.SPECIMENGRAB;
        } else if (state == ClawPivotState.SPECIMENSCORE) {
            leftPivot.setPosition(clawLeftSpecimenScore);
            rightPivot.setPosition(clawRightSpecimenScore);
            this.pivotState = ClawPivotState.SPECIMENSCORE;
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

    public void specimenGrab() {
        setPivotState(ClawPivotState.SPECIMENGRAB);
    }

    public void specimenScore() {
        setPivotState(ClawPivotState.SPECIMENSCORE);
    }

    public void initClaw() {
        setPivotState(ClawPivotState.INIT);
    }

    public void init() {
        close();
        initClaw();
    }

    public void start() {
        close();
        transfer();
    }
}
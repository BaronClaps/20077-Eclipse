package indubitables.config.subsystem;

import static indubitables.config.util.RobotConstants.*;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;


/** @author Baron Henderson
 * @version 1.0 | 12/3/24
 */

public class OuttakeSubsystem {

    public enum GrabState {
        CLOSED, OPEN
    }

    public enum RotateState {
        TRANSFER, SCORE, INIT, SPECIMENGRAB, SPECIMENSCORE
    }
    
    public enum PivotState {
        TRANSFER, SCORE, INIT, SPECIMENGRAB, SPECIMENSCORE
    }

    public Servo grab, leftRotate, rightRotate, leftPivot, rightPivot;
    public GrabState grabState;
    public RotateState rotateState;
    public PivotState pivotState;
    private Telemetry telemetry;

    public OuttakeSubsystem(HardwareMap hardwareMap, Telemetry telemetry, GrabState grabState, RotateState rotateState, PivotState pivotState) {
        grab = hardwareMap.get(Servo.class, "oG");
        leftRotate = hardwareMap.get(Servo.class, "oLR");
        rightRotate = hardwareMap.get(Servo.class, "oRR");
        leftPivot = hardwareMap.get(Servo.class, "oLP");
        rightPivot = hardwareMap.get(Servo.class, "oRP");
        this.telemetry = telemetry;
        this.grabState = grabState;
        this.rotateState = rotateState;
        this.pivotState = pivotState;
    }

    public void setRotateState(RotateState state) {
        if (state == RotateState.TRANSFER) {
            leftRotate.setPosition(outtakeRotateTransfer);
            rightRotate.setPosition(outtakeRotateTransfer);
            this.rotateState = RotateState.TRANSFER;
        } else if (state == RotateState.SCORE) {
            leftRotate.setPosition(outtakeRotateLeftScore);
            rightRotate.setPosition(outtakeRotateRightScore);
            this.rotateState = RotateState.SCORE;
        } else if (state == RotateState.INIT) {
            leftRotate.setPosition(outtakeRotateInit);
            rightRotate.setPosition(outtakeRotateInit);
            this.rotateState = RotateState.INIT;
        } else if (state == RotateState.SPECIMENGRAB) {
            leftRotate.setPosition(outtakeRotateSpecimenGrab);
            rightRotate.setPosition(outtakeRotateSpecimenGrab);
            this.rotateState = RotateState.SPECIMENGRAB;
        } else if (state == RotateState.SPECIMENSCORE) {
            leftRotate.setPosition(outtakeRotateLeftSpecimenScore);
            rightRotate.setPosition(outtakeRotateRightSpecimenScore);
            this.rotateState = RotateState.SPECIMENSCORE;
        }
    }

    public void setGrabState(GrabState grabState) {
        if (grabState == GrabState.CLOSED) {
            grab.setPosition(outtakeGrabClose);
            this.grabState = GrabState.CLOSED;
        } else if (grabState == GrabState.OPEN) {
            grab.setPosition(outtakeGrabOpen);
            this.grabState = GrabState.OPEN;
        }
    }

    public void switchGrabState() {
        if (grabState == GrabState.CLOSED) {
            setGrabState(GrabState.OPEN);
        } else if (grabState == GrabState.OPEN) {
            setGrabState(GrabState.CLOSED);
        }
    }

    public void setPivotState(PivotState pivotState) {
        if (pivotState == PivotState.TRANSFER) {
            leftPivot.setPosition(outtakePivotTransfer);
            rightPivot.setPosition(outtakePivotTransfer);
            this.pivotState = PivotState.TRANSFER;
        } else if (pivotState == PivotState.SCORE) {
            leftPivot.setPosition(outtakePivotScore);
            rightPivot.setPosition(outtakePivotScore);
            this.pivotState = PivotState.SCORE;
        } else if (pivotState == PivotState.INIT) {
            leftPivot.setPosition(outtakePivotInit);
            rightPivot.setPosition(outtakePivotInit);
            this.pivotState = PivotState.INIT;
        } else if (pivotState == PivotState.SPECIMENGRAB) {
            leftPivot.setPosition(outtakePivotSpecimenGrab);
            rightPivot.setPosition(outtakePivotSpecimenGrab);
            this.pivotState = PivotState.SPECIMENGRAB;
        } else if (pivotState == PivotState.SPECIMENSCORE) {
            leftPivot.setPosition(outtakePivotSpecimenScore);
            rightPivot.setPosition(outtakePivotSpecimenScore);
            this.pivotState = PivotState.SPECIMENSCORE;
        }
    }

    public void open() {
        setGrabState(GrabState.OPEN);
    }

    public void close() {
        setGrabState(GrabState.CLOSED);
    }

    public void transfer() {
        setRotateState(RotateState.TRANSFER);
        setPivotState(PivotState.TRANSFER);
        setGrabState(GrabState.OPEN);
    }

    public void score() {
        setRotateState(RotateState.SCORE);
        setPivotState(PivotState.SCORE);
        setGrabState(GrabState.CLOSED);
    }

    public void specimenGrab() {
        setRotateState(RotateState.SPECIMENGRAB);
        setPivotState(PivotState.SPECIMENGRAB);
        setGrabState(GrabState.OPEN);
    }

    public void specimenScore() {
        setRotateState(RotateState.SPECIMENSCORE);
        setPivotState(PivotState.SPECIMENSCORE);
        setGrabState(GrabState.CLOSED);
    } 

    public void init() {
        setPivotState(PivotState.INIT);
        setRotateState(RotateState.INIT);
        setGrabState(GrabState.CLOSED);
    }

    public void start() {
        setPivotState(PivotState.INIT);
        setRotateState(RotateState.INIT);
        setGrabState(GrabState.CLOSED);
    }

    public Telemetry.Line telemetry() {
        return

    }
}
package indubitables.config.subsystem;

import static indubitables.config.util.RobotConstants.*;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;


/** @author Baron Henderson
 * @version 1.0 | 12/3/24
 */

public class IntakeSubsystem {

    public enum GrabState {
        CLOSED, OPEN
    }

    public enum RotateState {
        TRANSFER, GROUND, INIT, SPECIMENGRAB, SPECIMENSCORE
    }

    public enum PivotState {
        TRANSFER, GROUND, INIT, SPECIMENGRAB, SPECIMENSCORE
    }

    public Servo grab, leftRotate, rightRotate, leftPivot, rightPivot;
    public GrabState grabState;
    public RotateState rotateState;
    public PivotState pivotState;
    private Telemetry telemetry;

    public IntakeSubsystem(HardwareMap hardwareMap, Telemetry telemetry, GrabState grabState, RotateState rotateState, PivotState pivotState) {
        grab = hardwareMap.get(Servo.class, "iG");
        leftRotate = hardwareMap.get(Servo.class, "iLR");
        rightRotate = hardwareMap.get(Servo.class, "iRR");
        leftPivot = hardwareMap.get(Servo.class, "iLP");
        rightPivot = hardwareMap.get(Servo.class, "iRP");
        this.telemetry = telemetry;
        this.grabState = grabState;
        this.rotateState = rotateState;
        this.pivotState = pivotState;
    }

    public void setRotateState(RotateState state) {
        if (state == RotateState.TRANSFER) {
            leftRotate.setPosition(intakeRotateTransfer);
            rightRotate.setPosition(intakeRotateTransfer);
            this.rotateState = RotateState.TRANSFER;
        } else if (state == RotateState.GROUND) {
            leftRotate.setPosition(intakeRotateLeftGround);
            rightRotate.setPosition(intakeRotateRightGround);
            this.rotateState = RotateState.GROUND;
        } else if (state == RotateState.INIT) {
            leftRotate.setPosition(intakeRotateInit);
            rightRotate.setPosition(intakeRotateInit);
            this.rotateState = RotateState.INIT;
        }
    }

    public void setGrabState(GrabState grabState) {
        if (grabState == GrabState.CLOSED) {
            grab.setPosition(intakeGrabClose);
            this.grabState = GrabState.CLOSED;
        } else if (grabState == GrabState.OPEN) {
            grab.setPosition(intakeGrabOpen);
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
            leftPivot.setPosition(intakePivotTransfer);
            rightPivot.setPosition(intakePivotTransfer);
            this.pivotState = PivotState.TRANSFER;
        } else if (pivotState == PivotState.GROUND) {
            leftPivot.setPosition(intakePivotGround);
            rightPivot.setPosition(intakePivotGround);
            this.pivotState = PivotState.GROUND;
        } else if (pivotState == PivotState.INIT) {
            leftPivot.setPosition(intakePivotInit);
            rightPivot.setPosition(intakePivotInit);
            this.pivotState = PivotState.INIT;
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

    public void ground() {
        setRotateState(RotateState.GROUND);
        setPivotState(PivotState.GROUND);
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
}
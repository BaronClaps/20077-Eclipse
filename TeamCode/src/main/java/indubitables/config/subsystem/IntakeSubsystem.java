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
        TRANSFER, GROUND, HOVER, SPECIMEN
    }

    public enum PivotState {
        TRANSFER, GROUND, HOVER, SPECIMEN
    }

    public Servo grab, leftRotate, rightRotate, leftPivot, rightPivot;
    public GrabState grabState;
    public RotateState rotateState;
    public PivotState pivotState;
    private Telemetry telemetry;
    private double rotateDegrees = 0;
    private static final double perDegree = 0.00122222222;

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
            leftRotate.setPosition(intakeRotateTransfer-0.03);
            rightRotate.setPosition(intakeRotateTransfer);
            this.rotateState = RotateState.TRANSFER;
        } else if (state == RotateState.GROUND) {
            leftRotate.setPosition(intakeRotateGroundVertical - 0.03 + (rotateDegrees * perDegree));
            rightRotate.setPosition(intakeRotateGroundVertical - (rotateDegrees * perDegree));
            this.rotateState = RotateState.GROUND;
        } else if (state == RotateState.HOVER) {
            leftRotate.setPosition(intakeRotateHoverVertical - 0.03 + (rotateDegrees * perDegree));
            rightRotate.setPosition(intakeRotateHoverVertical - (rotateDegrees * perDegree));
            this.rotateState = RotateState.HOVER;
        } else if (state == RotateState.SPECIMEN) {
            leftRotate.setPosition(intakeRotateSpecimen - 0.03);
            rightRotate.setPosition(intakeRotateSpecimen);

        }
    }

    public void rotateDegrees(double degrees) {

        degrees = ((degrees % 360) + 360) % 360;
        if (degrees > 180) {
            degrees -= 360;
        }

        if (degrees > 90) {
            degrees = 180 - degrees;
        } else if (degrees < -90) {
            degrees = -180 - degrees;
        }

        this.rotateDegrees = degrees;

        setPivotState(PivotState.HOVER);
        setRotateState(RotateState.HOVER);
        setGrabState(GrabState.OPEN);
    }


    public void rotateCycle(boolean right) {
        if (right) {
            if (rotateDegrees < 90)
                rotateDegrees += 45;
        } else {
            if (rotateDegrees > -90)
                rotateDegrees -= 45;
        }

        setPivotState(PivotState.HOVER);
        setRotateState(RotateState.HOVER);
        setGrabState(GrabState.OPEN);
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
        } else if (pivotState == PivotState.HOVER) {
            leftPivot.setPosition(intakePivotHover);
            rightPivot.setPosition(intakePivotHover);
            this.pivotState = PivotState.HOVER;
        } else if (pivotState == PivotState.SPECIMEN) {
            leftPivot.setPosition(intakePivotSpecimen);
            rightPivot.setPosition(intakePivotSpecimen);
            this.pivotState = PivotState.SPECIMEN;
        }
    }

    public void open() {
        setGrabState(GrabState.OPEN);
    }

    public void close() {
        setGrabState(GrabState.CLOSED);
    }

    public void transfer() {
        rotateDegrees = 0;
        setRotateState(RotateState.TRANSFER);
        setPivotState(PivotState.TRANSFER);
        setGrabState(GrabState.CLOSED);
    }

    public void ground() {
        setGrabState(GrabState.OPEN);
        setRotateState(RotateState.GROUND);
        setPivotState(PivotState.GROUND);
    }

    public void hover() {
        rotateDegrees = 0;
        setPivotState(PivotState.HOVER);
        setRotateState(RotateState.HOVER);
    }

    public void specimen() {
        rotateDegrees = 0;
        setPivotState(PivotState.SPECIMEN);
        setRotateState(RotateState.SPECIMEN);
        setGrabState(GrabState.OPEN);
    }

    public void init() {
        rotateDegrees = 0;
        specimen();
    }

    public void start() {
        rotateDegrees = 0;
        setPivotState(PivotState.HOVER);
        setRotateState(RotateState.HOVER);
        setGrabState(GrabState.OPEN);
    }

    public void telemetry() {
        telemetry.addData("Intake Grab State: ", grabState);
        telemetry.addData("Intake Rotate State: ", rotateState);
        telemetry.addData("Intake Pivot State: ", pivotState);
        telemetry.addData("Rotate Degrees: ", rotateDegrees);
    }
}
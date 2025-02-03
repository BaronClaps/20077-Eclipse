package indubitables.config.subsystem;

import static indubitables.config.util.RobotConstants.*;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import indubitables.pedroPathing.util.Timer;


/** @author Baron Henderson
 * @version 1.0 | 12/3/24
 */
// jay wuz hear
public class OuttakeSubsystem {

    public enum GrabState {
        CLOSED, OPEN
    }

    public enum RotateState {
        SCORE, SPECIMENGRAB, SPECIMENSCORE, TRANSFER_UNDETECTED, TRANSFER_DETECTED
    }
    
    public enum PivotState {
        SCORE, SPECIMENGRAB, SPECIMENSCORE, TRANSFER_UNDETECTED, TRANSFER_DETECTED
    }

    public Servo grab, leftRotate, rightRotate, leftPivot, rightPivot;
    public GrabState grabState;
    public RotateState rotateState;
    public PivotState pivotState;
    private Telemetry telemetry;
    private Timer specScoreTimer = new Timer();
    private int specGrabState = -1;

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
        if (state == RotateState.TRANSFER_DETECTED) {
            leftRotate.setPosition(outtakeRotateTransferDetected-0.02);
            rightRotate.setPosition(outtakeRotateTransferDetected);
            this.rotateState = RotateState.TRANSFER_DETECTED;
        } else if (state == RotateState.TRANSFER_UNDETECTED) {
            leftRotate.setPosition(outtakeRotateTransferUndetected-0.02);
            rightRotate.setPosition(outtakeRotateTransferUndetected);
            this.rotateState = RotateState.TRANSFER_UNDETECTED;
        } else if (state == RotateState.SCORE) {
            leftRotate.setPosition(outtakeRotateLeftScore);
            rightRotate.setPosition(outtakeRotateRightScore);
            this.rotateState = RotateState.SCORE;
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
        if (pivotState == PivotState.TRANSFER_DETECTED) {
            leftPivot.setPosition(outtakePivotTransferDetected);
            rightPivot.setPosition(outtakePivotTransferDetected);
            this.pivotState = PivotState.TRANSFER_DETECTED;
        } else if (pivotState == PivotState.TRANSFER_UNDETECTED) {
            leftPivot.setPosition(outtakePivotTransferUndetected);
            rightPivot.setPosition(outtakePivotTransferUndetected);
            this.pivotState = PivotState.TRANSFER_UNDETECTED;
        } else if (pivotState == PivotState.SCORE) {
            leftPivot.setPosition(outtakePivotScore);
            rightPivot.setPosition(outtakePivotScore);
            this.pivotState = PivotState.SCORE;
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

    public void transferDetected() {
        setRotateState(RotateState.TRANSFER_DETECTED);
        setPivotState(PivotState.TRANSFER_DETECTED);
        setGrabState(GrabState.OPEN);
    }

    public void transferUndetected() {
        setRotateState(RotateState.TRANSFER_UNDETECTED);
        setPivotState(PivotState.TRANSFER_UNDETECTED);
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

    public void specGrab() {
        switch(specGrabState) {
            case 0:
                if(pivotState == PivotState.SPECIMENSCORE) {
                    hang();
                    setGrabState(GrabState.OPEN);
                    setSpecGrabState(1);
                } else {
                    setRotateState(RotateState.SPECIMENGRAB);
                    setPivotState(PivotState.SPECIMENGRAB);
                    setGrabState(GrabState.OPEN);
                    setSpecGrabState(-1);
                }
                break;
            case 1:
                if(specScoreTimer.getElapsedTimeSeconds() > 0.65) {
                    setRotateState(RotateState.SPECIMENGRAB);
                    setPivotState(PivotState.SPECIMENGRAB);
                    setGrabState(GrabState.OPEN);
                    setSpecGrabState(-1);
                }
        }
    }

    public void setSpecGrabState(int i) {
        specGrabState = i;
        specScoreTimer.resetTimer();
    }

    public void startSpecGrab() {
        setSpecGrabState(0);
    }

    public void specimenScore() {
        setRotateState(RotateState.SPECIMENSCORE);
        setPivotState(PivotState.SPECIMENSCORE);
        setGrabState(GrabState.CLOSED);
    } 

    public void init() {
        leftPivot.setPosition(0.175);
        rightPivot.setPosition(0.175);
        leftRotate.setPosition(1);
        rightRotate.setPosition(0.63);
        close();
    }

    public void start() {
        setPivotState(PivotState.TRANSFER_DETECTED);
        setRotateState(RotateState.TRANSFER_DETECTED);
        setGrabState(GrabState.CLOSED);
    }

    public void loop() {
        specGrab();
    }

    public void hang() {
        setRotateState(RotateState.SPECIMENSCORE);
        leftPivot.setPosition(0.7); //.7
        rightPivot.setPosition(0.7); //.7
        setGrabState(GrabState.CLOSED);
    }

    public void telemetry() {
        telemetry.addData("Outtake Grab State: ", grabState);
        telemetry.addData("Outtake Rotate State: ", rotateState);
        telemetry.addData("Outtake Pivot State: ", pivotState);
    }
}
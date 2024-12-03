package indubitables.config.subsystem;

import static indubitables.config.util.RobotConstants.*;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import indubitables.config.util.action.RunAction;

/** @author Baron Henderson
 * @version 1.0 | 7/1/24
 */

public class IntakeSubsystem {

    public enum grabState {
        CLOSED, OPEN
    }

    public enum rotateState {
        TRANSFER, GROUND, INIT, SUBMERSIBLE
    }

    public Servo grab, leftPivot, rightPivot;
    public ClawGrabState grabState;
    public rotateState pivotState;
    public RunAction open, close, transfer, score, specimen;

    public IntakeSubsystem(HardwareMap hardwareMap, ClawGrabState clawGrabState, rotateState rotateState) {
        grab = hardwareMap.get(Servo.class, "iGrab");
        leftPivot = hardwareMap.get(Servo.class, "ilRotate");
        rightPivot = hardwareMap.get(Servo.class, "irRotate");
        this.grabState = clawGrabState;
        this.pivotState = rotateState;

        open = new RunAction(this::open);
        close = new RunAction(this::close);
        transfer = new RunAction(this::transfer);
        score = new RunAction(this::score);
    }

    public void setPivotState(rotateState state) {
        if (state == rotateState.TRANSFER) {
            leftPivot.setPosition(clawTransfer);
            rightPivot.setPosition(clawTransfer);
            this.pivotState = rotateState.TRANSFER;
        } else if (state == rotateState.SCORE) {
            leftPivot.setPosition(clawLeftScore);
            rightPivot.setPosition(clawRightScore);
            this.pivotState = rotateState.SCORE;
        } else if (state == rotateState.INIT) {
            leftPivot.setPosition(clawInit);
            rightPivot.setPosition(clawInit);
            this.pivotState = rotateState.INIT;
        } else if (state == rotateState.SPECIMENGRAB) {
            leftPivot.setPosition(clawSpecimenGrab);
            rightPivot.setPosition(clawSpecimenGrab);
            this.pivotState = rotateState.SPECIMENGRAB;
        }
    }

    public void switchPivotState() {
        if (pivotState == rotateState.TRANSFER) {
            setPivotState(rotateState.SCORE);
        } else if (pivotState == rotateState.SCORE) {
            setPivotState(rotateState.TRANSFER);
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
        setPivotState(rotateState.TRANSFER);
    }

    public void score() {
        setPivotState(rotateState.SCORE);
    }

    public void specimenGrab() {
        setPivotState(rotateState.SPECIMENGRAB);
    }

    public void specimenScore() {
        setPivotState(rotateState.SPECIMENSCORE);
    }

    public void initClaw() {
        setPivotState(rotateState.INIT);
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
    /*
    public enum IntakeSpinState {
        IN, OUT, STOP
    }

    public enum IntakePivotState {
        TRANSFER, GROUND, SUBMERSIBLE
    }

    public CRServo leftSpin, rightSpin, backSpin;
    private IntakeSpinState spinState;

    private Servo leftPivot, rightPivot;
    private IntakePivotState pivotState;

    public RunAction spinIn, spinOut, spinStop, pivotTransfer, pivotGround;

    public IntakeSubsystem(HardwareMap hardwareMap, IntakeSpinState spinState, IntakePivotState pivotState) {
        leftSpin = hardwareMap.get(CRServo.class, "intakeLeftSpin");
        rightSpin = hardwareMap.get(CRServo.class, "intakeRightSpin");
        backSpin = hardwareMap.get(CRServo.class, "intakeBackSpin");
        leftPivot = hardwareMap.get(Servo.class, "intakeLeftPivot");
        rightPivot = hardwareMap.get(Servo.class, "intakeRightPivot");

        this.spinState = spinState;
        this.pivotState = pivotState;

        spinIn = new RunAction(this::spinIn);
        spinOut = new RunAction(this::spinOut);
        spinStop = new RunAction(this::spinStop);
        pivotTransfer = new RunAction(this::pivotTransfer);
        pivotGround = new RunAction(this::pivotGround);

    }

    // ----------------- Intake Spin -----------------//

    public void setSpinState(IntakeSpinState spinState, boolean changeStateOnly) {
        if (changeStateOnly) {
            this.spinState = spinState;
        } else {
            if (spinState == IntakeSpinState.IN) {
                spinIn();
            } else if (spinState == IntakeSpinState.OUT) {
                spinOut();
            } else if (spinState == IntakeSpinState.STOP) {
                spinStop();
            }
        }
    }

    public void spinIn() {
        leftSpin.setPower(intakeSpinInPwr);
        rightSpin.setPower(-intakeSpinInPwr);
        if(pivotState == IntakePivotState.TRANSFER) {
            backSpin.setPower(intakeSpinInPwr);
        }
        this.spinState = IntakeSpinState.IN;
    }

    public void spinInBackAlways() {
        leftSpin.setPower(intakeSpinInPwr);
        rightSpin.setPower(-intakeSpinInPwr);
        backSpin.setPower(intakeSpinInPwr);
        this.spinState = IntakeSpinState.IN;
    }

    public void spinOut() {
        leftSpin.setPower(intakeSpinOutPwr);
        rightSpin.setPower(-intakeSpinOutPwr);
        backSpin.setPower(intakeSpinOutPwr);
        this.spinState = IntakeSpinState.OUT;
    }

    public void spinStop() {
        leftSpin.setPower(intakeSpinStopPwr);
        rightSpin.setPower(intakeSpinStopPwr);
        backSpin.setPower(intakeSpinStopPwr);
        this.spinState = IntakeSpinState.STOP;
    }

    // ----------------- Intake Pivot -----------------//

    public void setPivotState(IntakePivotState pivotState) {
        if (pivotState == IntakePivotState.TRANSFER) {
            leftPivot.setPosition(intakePivotTransfer);
            rightPivot.setPosition(intakePivotTransfer);
            this.pivotState = IntakePivotState.TRANSFER;
        } else if (pivotState == IntakePivotState.GROUND) {
            leftPivot.setPosition(intakePivotGround);
            rightPivot.setPosition(intakePivotGround);
            this.pivotState = IntakePivotState.GROUND;
        } else if (pivotState == IntakePivotState.SUBMERSIBLE) {
            leftPivot.setPosition(intakePivotSubmersible);
            rightPivot.setPosition(intakePivotSubmersible);
            this.pivotState = IntakePivotState.SUBMERSIBLE;
        }
    }

    public void switchPivotState() {
        if (pivotState == IntakePivotState.TRANSFER) {
            pivotGround();
        } else if (pivotState == IntakePivotState.GROUND) {
            pivotTransfer();
        }
    }

    public void pivotTransfer() {
        leftPivot.setPosition(intakePivotTransfer);
        rightPivot.setPosition(intakePivotTransfer);
        this.pivotState = IntakePivotState.TRANSFER;
    }

    public void pivotGround() {
        leftPivot.setPosition(intakePivotGround);
        rightPivot.setPosition(intakePivotGround);
        this.pivotState = IntakePivotState.GROUND;
    }


    public void init() {
        pivotTransfer();
        spinStop();
    }

    public void start() {
        pivotTransfer();
        spinStop();
    }
}    
*/
     
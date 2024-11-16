package indubitables.config.subsystem;

import static indubitables.config.util.RobotConstants.intakePivotGround;
import static indubitables.config.util.RobotConstants.intakePivotSubmersible;
import static indubitables.config.util.RobotConstants.intakePivotTransfer;
import static indubitables.config.util.RobotConstants.intakeSpinInPwr;
import static indubitables.config.util.RobotConstants.intakeSpinOutPwr;
import static indubitables.config.util.RobotConstants.intakeSpinStopPwr;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import indubitables.config.util.action.Actions;
import indubitables.config.util.action.ParallelAction;
import indubitables.config.util.action.RunAction;

public class IntakeSubsystem {

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
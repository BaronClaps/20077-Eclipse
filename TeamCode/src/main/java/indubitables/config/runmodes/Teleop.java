package indubitables.config.runmodes;

import indubitables.config.subsystem.ArmSubsystem;
import indubitables.config.subsystem.ClawSubsystem;
import indubitables.config.subsystem.ExtendSubsystem;
import indubitables.config.subsystem.IntakeSubsystem;
import indubitables.config.subsystem.LiftSubsystem;
import indubitables.config.util.RobotConstants;
import indubitables.pedroPathing.follower.Follower;
import indubitables.pedroPathing.localization.Pose;

import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import indubitables.pedroPathing.pathGeneration.BezierCurve;
import indubitables.pedroPathing.pathGeneration.PathChain;
import indubitables.pedroPathing.pathGeneration.Point;
import indubitables.pedroPathing.util.Timer;

public class Teleop {

    private ClawSubsystem claw;
    private ClawSubsystem.ClawGrabState clawGrabState;
    private ClawSubsystem.ClawPivotState clawPivotState;
    private LiftSubsystem lift;
    private ExtendSubsystem extend;
    private IntakeSubsystem intake;
    private IntakeSubsystem.IntakeSpinState intakeSpinState;
    private IntakeSubsystem.IntakePivotState intakePivotState;
    private ArmSubsystem arm;
    private ArmSubsystem.ArmState armState;

    private Follower follower;
    private Pose startPose;

    private Telemetry telemetry;

    private Gamepad gamepad1, gamepad2;
    private Gamepad currentGamepad1 = new Gamepad();
    private Gamepad currentGamepad2 = new Gamepad();
    private Gamepad previousGamepad1 = new Gamepad();
    private Gamepad previousGamepad2 = new Gamepad();

    private Timer autoBucketTimer = new Timer();

    private int flip = 1, autoBucketState = -1;

    public double speed = 0.75;

    private boolean fieldCentric, actionBusy;

    private PathChain autoBucketTo, autoBucketBack;
    private Pose autoBucketToEndPose, autoBucketBackEndPose;


    public Teleop(HardwareMap hardwareMap, Telemetry telemetry, Follower follower, Pose startPose,  boolean fieldCentric, Gamepad gamepad1, Gamepad gamepad2) {
        claw = new ClawSubsystem(hardwareMap, clawGrabState, clawPivotState);
        lift = new LiftSubsystem(hardwareMap, telemetry);
        extend = new ExtendSubsystem(hardwareMap, telemetry);
        intake = new IntakeSubsystem(hardwareMap, intakeSpinState, intakePivotState);
        arm = new ArmSubsystem(hardwareMap, armState);

        this.follower = follower;
        this.startPose = startPose;

        this.startPose = new Pose(56,102.25,Math.toRadians(270));

        this.fieldCentric = fieldCentric;
        this.telemetry = telemetry;
        this.gamepad1 = gamepad1;
        this.gamepad2 = gamepad2;
    }

    public void init() {}

    public void start() {
        extend.setLimitToSample();
        claw.init();
        arm.init();
        //lift.start();
        extend.start();
        intake.start();
        follower.setPose(startPose);
        follower.startTeleopDrive();
    }

    public void update() {

        if (actionNotBusy()) {
            previousGamepad1.copy(currentGamepad1);
            previousGamepad2.copy(currentGamepad2);
            currentGamepad1.copy(gamepad1);
            currentGamepad2.copy(gamepad2);

            if (gamepad1.right_bumper)
                speed = 1;
            else if (gamepad1.left_bumper)
                speed = 0.25;
            else
                speed = 0.75;

            lift.manual(gamepad2.right_trigger - gamepad2.left_trigger);

            if (gamepad2.b)
                intake.setSpinState(IntakeSubsystem.IntakeSpinState.IN, false);
            else if (gamepad2.dpad_down)
                intake.setSpinState(IntakeSubsystem.IntakeSpinState.OUT, false);
            else
                intake.setSpinState(IntakeSubsystem.IntakeSpinState.STOP, false);

            if (currentGamepad1.a && !previousGamepad1.a)
                intake.switchPivotState();

            if(gamepad1.dpad_left)
                startAutoBucket();

            if (gamepad1.x) {
                flip = -1;
            }

            if (gamepad1.b) {
                flip = 1;
            }

            if (gamepad2.right_bumper)
                extend.manual(1);
            else if (gamepad2.left_bumper)
                extend.manual(-1);
            else
                extend.manual(0);

            if (currentGamepad2.a && !previousGamepad2.a)
                claw.switchGrabState();

            if (currentGamepad2.y && !previousGamepad2.y)
                transferPos();

            if (currentGamepad2.x && !previousGamepad2.x)
                scoringPos();

            if (currentGamepad2.dpad_left && !previousGamepad2.dpad_left)
                specimenGrabPos();

            if (currentGamepad2.dpad_right && !previousGamepad2.dpad_right)
                specimenScorePos();

            if (currentGamepad1.b && !previousGamepad1.b)
                intake.setPivotState(IntakeSubsystem.IntakePivotState.TRANSFER);

            if (gamepad2.left_stick_button) {
                lift.hang = true;
            }

            if (gamepad2.right_stick_button) {
                lift.hang = false;
            }

            follower.setTeleOpMovementVectors(flip * -gamepad1.left_stick_y * speed, flip * -gamepad1.left_stick_x * speed, -gamepad1.right_stick_x * speed * 0.5, !fieldCentric);
        } else {
            if(gamepad1.dpad_right) {
                stopActions();
            }
        }

        lift.updatePIDF();

        autoBucket();

        follower.update();

        telemetry.addData("X", follower.getPose().getX());
        telemetry.addData("Y", follower.getPose().getY());
        telemetry.addData("Heading", Math.toDegrees(follower.getPose().getHeading()));

        telemetry.addData("Lift Pos", lift.getPos());
        telemetry.addData("Extend Pos", extend.leftExtend.getPosition());
        telemetry.addData("Extend Limit", extend.extendLimit);
        telemetry.addData("Claw Grab State", claw.grabState);
        telemetry.addData("Claw Pivot State", claw.pivotState);
        telemetry.addData("Intake Spin State", intakeSpinState);
        telemetry.addData("Intake Pivot State", intakePivotState);
        telemetry.addData("Arm State", arm.state);
        telemetry.addData("Action Busy", actionBusy);
        telemetry.addData("Auto Bucket State", autoBucketState);
        telemetry.update();
    }

    private void scoringPos() {
        extend.setLimitToSample();
        claw.score();
        claw.close();
        arm.score();
    }

    private void transferPos() {
        extend.setLimitToSample();
        claw.transfer();
        claw.open();
        arm.transfer();
    }

    private void specimenGrabPos() {
        extend.setLimitToSpecimen();
        claw.specimenGrab();
        claw.open();
        arm.specimenGrab();
    }

    private void specimenScorePos() {
        extend.setLimitToSpecimen();
        claw.specimenScore();
        claw.close();
        arm.specimenScore();
    }

    private void autoBucket() {
        switch (autoBucketState) {
            case 1:
                actionBusy = true;
                intake.pivotTransfer();
                intake.spinInBackAlways();
                claw.open();
                claw.transfer();
                extend.toZero();
                arm.transfer();

                follower.breakFollowing();
                follower.setMaxPower(0.85);

                autoBucketToEndPose = new Pose(17.750, 125.500, Math.toRadians(-45));

                autoBucketTo = follower.pathBuilder()
                        .addPath(new BezierCurve(new Point(follower.getPose()), new Point(58.000, 119.000, Point.CARTESIAN), new Point(autoBucketToEndPose)))
                        .setLinearHeadingInterpolation(follower.getPose().getHeading(), autoBucketToEndPose.getHeading())
                        .build();

                follower.followPath(autoBucketTo, true);

                setAutoBucketState(2);
                break;
            case 2:
                if (autoBucketTimer.getElapsedTimeSeconds() > 2) {
                    claw.close();
                    setAutoBucketState(3);
                }
                break;
            case 3:
                if (autoBucketTimer.getElapsedTimeSeconds() > 0.5) {
                    lift.toHighBucket();
                    setAutoBucketState(4);
                }
                break;
            case 4:
                if (autoBucketTimer.getElapsedTimeSeconds() > 0.5) {
                    arm.score();
                    claw.score();
                    intake.spinStop();
                    setAutoBucketState(5);
                }
                break;
            case 5:
                if (((follower.getPose().getX() <  autoBucketToEndPose.getX() + 0.5) && (follower.getPose().getY() > autoBucketToEndPose.getY() - 0.5)) && (lift.getPos() > RobotConstants.liftToHighBucket - 50) && autoBucketTimer.getElapsedTimeSeconds() > 1) {
                    claw.open();
                    setAutoBucketState(9);
                    //setAutoBucketState(6);
                }
                break;
            case 6:
                if(autoBucketTimer.getElapsedTimeSeconds() > 0.5) {
                    autoBucketBackEndPose = new Pose(60, 104, Math.toRadians(270));

                    autoBucketBack = follower.pathBuilder()
                            .addPath(new BezierCurve(new Point(follower.getPose()), new Point(58.000, 119.000, Point.CARTESIAN), new Point(autoBucketBackEndPose)))
                            .setLinearHeadingInterpolation(follower.getPose().getHeading(), autoBucketToEndPose.getHeading())
                            .build();

                    follower.followPath(autoBucketBack, true);

                    claw.open();
                    claw.transfer();
                    arm.transfer();
                    setAutoBucketState(7);
                }
                break;
            case 7:
                if(autoBucketTimer.getElapsedTimeSeconds() > 0.5) {
                    lift.toZero();
                    extend.toFull();
                    setAutoBucketState(8);
                }
                break;
            case 8:
                if((follower.getPose().getX() >  autoBucketBackEndPose.getX() - 0.5) && (follower.getPose().getY() < autoBucketBackEndPose.getY() + 0.5)) {
                    intake.pivotGround();
                    setAutoBucketState(9);
                }
                break;
            case 9:
                follower.breakFollowing();
                follower.setMaxPower(1);
                follower.startTeleopDrive();
                actionBusy = false;
                setAutoBucketState(-1);
                break;
        }
    }

    public void setAutoBucketState(int x) {
        autoBucketState = x;
        autoBucketTimer.resetTimer();
    }

    public void startAutoBucket() {
        setAutoBucketState(1);
    }

    private boolean actionNotBusy() {
        return !actionBusy;
    }

    private void stopActions() {
        follower.breakFollowing();
        follower.setMaxPower(1);
        follower.startTeleopDrive();
        actionBusy = false;
        setAutoBucketState(-1);
    }

}
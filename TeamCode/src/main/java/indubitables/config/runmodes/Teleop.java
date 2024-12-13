package indubitables.config.runmodes;

import indubitables.config.subsystem.OuttakeSubsystem;
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


    private ExtendSubsystem extend;
    private LiftSubsystem lift;

    private IntakeSubsystem intake;
    private IntakeSubsystem.GrabState intakeGrabState;
    private IntakeSubsystem.PivotState intakePivotState;
    private IntakeSubsystem.RotateState intakeRotateState;

    private OuttakeSubsystem outtake;
    private OuttakeSubsystem.GrabState outtakeGrabState;
    private OuttakeSubsystem.PivotState outtakePivotState;
    private OuttakeSubsystem.RotateState outtakeRotateState;

    private Follower follower;
    private Pose startPose;

    private Telemetry telemetry;

    private Gamepad gamepad1, gamepad2;
    private Gamepad currentGamepad1 = new Gamepad(), currentGamepad2 = new Gamepad(), previousGamepad1 = new Gamepad(), previousGamepad2 = new Gamepad();

    private Timer autoBucketTimer = new Timer(), transferTimer = new Timer(), submersibleTimer = new Timer();

    private int flip = 1, autoBucketState = -1, transferState = -1, submersibleState = -1;

    public double speed = 0.75;

    private boolean fieldCentric, actionBusy;

    private PathChain autoBucketTo, autoBucketBack;
    private Pose autoBucketToEndPose, autoBucketBackEndPose;


    public Teleop(HardwareMap hardwareMap, Telemetry telemetry, Follower follower, Pose startPose, boolean fieldCentric, Gamepad gamepad1, Gamepad gamepad2) {
        outtake = new OuttakeSubsystem(hardwareMap, telemetry, outtakeGrabState, outtakeRotateState, outtakePivotState);
        lift = new LiftSubsystem(hardwareMap, telemetry);
        extend = new ExtendSubsystem(hardwareMap, telemetry);
        intake = new IntakeSubsystem(hardwareMap, telemetry, intakeGrabState, intakeRotateState, intakePivotState);

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
        outtake.start();
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

            if (gamepad1.x) {
                flip = -1;
            }

            if (gamepad1.b) {
                flip = 1;
            }

            if (gamepad1.right_trigger > 0.1)
                extend.toFull();

            if (gamepad1.left_trigger > 0.1)
                extend.toZero();

            if (currentGamepad2.a && !previousGamepad2.a)
                outtake.switchGrabState();

            if (currentGamepad2.y && !previousGamepad2.y) {
                extend.setLimitToSample();
                outtake.transfer();
                intake.hover();
            }

            if (currentGamepad2.x && !previousGamepad2.x) {
                extend.setLimitToSample();
                outtake.score();
                intake.hover();
            }

            if (currentGamepad2.dpad_left && !previousGamepad2.dpad_left)
                specimenGrabPos();

            if (currentGamepad2.dpad_right && !previousGamepad2.dpad_right)
                specimenScorePos();

            if (currentGamepad2.b && !previousGamepad2.b)
                startTransfer();

            if (currentGamepad2.dpad_up && !previousGamepad2.dpad_up) {
                intake.switchGrabState();
            }

            if (currentGamepad2.dpad_down && !previousGamepad2.dpad_down) {
                startSubmersible();
                outtake.score();
            }

            if (currentGamepad2.left_bumper && !previousGamepad2.left_bumper) {
                intake.rotateCycle(false);
            }

            if (currentGamepad2.right_bumper && !previousGamepad2.right_bumper) {
                intake.rotateCycle(true);
            }

            if (gamepad2.left_stick_button) {
                outtake.hang();
                intake.transfer();
                extend.toZero();

            }

            if (gamepad2.right_stick_button) {
                intake.transfer();
            }

            follower.setTeleOpMovementVectors(flip * -gamepad1.left_stick_y * speed, flip * -gamepad1.left_stick_x * speed, -gamepad1.right_stick_x * speed * 0.5, !fieldCentric);

            if(gamepad1.dpad_right) {
                stopActions();
            }

        } else {
            if(gamepad1.dpad_right) {
                stopActions();
            }
        }

        lift.updatePIDF();
        outtake.loop();

        autoBucket();
        transfer();
        submersible();

        follower.update();
        telemetry.addData("X: ", follower.getPose().getX());
        telemetry.addData("Y: ", follower.getPose().getY());
        telemetry.addData("Heading: ", follower.getPose().getHeading());
        telemetry.addData("Action Busy?: ", actionBusy);
        telemetry.addData("Auto Bucket State", autoBucketState);
        telemetry.addData("Transfer State", transferState);
        telemetry.addData("Submersible State", submersibleState);
        extend.telemetry();
        lift.telemetry();
        outtake.telemetry();
        intake.telemetry();
        telemetry.update();
    }

    private void specimenGrabPos() {
        extend.setLimitToSpecimen();
        outtake.startSpecGrab();
        intake.specimen();
    }

    private void specimenScorePos() {
        extend.setLimitToSpecimen();
        outtake.specimenScore();
        intake.specimen();
    }

    private void transfer() {
        switch (transferState) {
            case 1:
                intake.close();
                outtake.score();
                intake.transfer();
                setTransferState(2);
                break;
            case 2:
                if (transferTimer.getElapsedTimeSeconds() > 0.1) {
                    outtake.setRotateState(OuttakeSubsystem.RotateState.TRANSFER);
                    extend.toZero();
                    setTransferState(3);
                }
                break;
            case 3:
                if (transferTimer.getElapsedTimeSeconds() > 0.2) {
                    outtake.transfer();
                    setTransferState(4);
                }
                break;
            case 4:
                if (transferTimer.getElapsedTimeSeconds() > 0.25) {
                    outtake.close();
                    setTransferState(5);
                }
                break;
            case 5:
                if (transferTimer.getElapsedTimeSeconds() > 0.5) {
                    outtake.score();
                    setTransferState(6);
                }
                break;
            case 6:
                if (transferTimer.getElapsedTimeSeconds() > 0) {
                    intake.open();
                    setTransferState(7);
                }
                break;
            case 7:
                if (transferTimer.getElapsedTimeSeconds() > 0.25) {
                    intake.hover();
                    actionBusy = false;
                    setTransferState(-1);
                }
                break;
        }
    }

    public void setTransferState(int x) {
        transferState = x;
        transferTimer.resetTimer();
    }

    public void startTransfer() {
        setTransferState(1);
    }

    private void submersible() {
        switch (submersibleState) {
            case 0:
                intake.ground();
                intake.open();
                outtake.transfer();
                setSubmersibleState(1);
                break;
            case 1:
                if(submersibleTimer.getElapsedTimeSeconds() > 0.3) {
                    intake.close();
                    setSubmersibleState(2);
                }
                break;
            case 2:
                if (submersibleTimer.getElapsedTimeSeconds() > 0.25) {
                    intake.hover();
                    setSubmersibleState(-1);
                }
                break;
        }
    }

    public void setSubmersibleState(int x) {
        submersibleState = x;
        submersibleTimer.resetTimer();
    }

    public void startSubmersible() {
        setSubmersibleState(0);
    }

    private void autoBucket() {
        switch (autoBucketState) {
            case 1:
                actionBusy = true;
                outtake.open();
                outtake.transfer();
                extend.toZero();

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
                    outtake.close();
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
                    outtake.score();
                    setAutoBucketState(5);
                }
                break;
            case 5:
                if (((follower.getPose().getX() <  autoBucketToEndPose.getX() + 0.5) && (follower.getPose().getY() > autoBucketToEndPose.getY() - 0.5)) && (lift.getPos() > RobotConstants.liftToHighBucket - 50) && autoBucketTimer.getElapsedTimeSeconds() > 1) {
                    outtake.open();
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

                    outtake.open();
                    outtake.transfer();
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
                    intake.ground();
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
        setTransferState(-1);
        setAutoBucketState(-1);
    }

}
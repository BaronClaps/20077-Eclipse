package indubitables.config.runmodes;

import static indubitables.config.util.FieldConstants.*;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import indubitables.pedroPathing.pathGeneration.BezierCurve;
import indubitables.config.subsystem.ArmSubsystem;
import indubitables.config.subsystem.ClawSubsystem;
import indubitables.config.subsystem.ExtendSubsystem;
import indubitables.config.subsystem.IntakeSubsystem;
import indubitables.config.subsystem.LiftSubsystem;
import indubitables.pedroPathing.follower.Follower;
import indubitables.pedroPathing.localization.Pose;
import indubitables.pedroPathing.pathGeneration.BezierLine;
import indubitables.pedroPathing.pathGeneration.Path;
import indubitables.pedroPathing.pathGeneration.PathBuilder;
import indubitables.pedroPathing.pathGeneration.PathChain;
import indubitables.pedroPathing.pathGeneration.Point;
import indubitables.pedroPathing.util.Timer;

public class Auto {

    private RobotStart startLocation;

    public ClawSubsystem claw;
    public ClawSubsystem.ClawGrabState clawGrabState;
    public ClawSubsystem.ClawPivotState clawPivotState;
    public LiftSubsystem lift;
    public ExtendSubsystem extend;
    public IntakeSubsystem intake;
    public IntakeSubsystem.IntakeSpinState intakeSpinState;
    public IntakeSubsystem.IntakePivotState intakePivotState;
    public ArmSubsystem arm;
    public ArmSubsystem.ArmState armState;


    public Follower follower;
    public Telemetry telemetry;

    public boolean actionBusy;

    public Timer transferTimer = new Timer(), bucketTimer = new Timer(), chamberTimer = new Timer(), intakeTimer = new Timer(), parkTimer = new Timer();
    public int transferState = -1, bucketState = -1, chamberState = -1, intakeState = -1, parkState = -1;

    public Path preload, element1, score1, element2, score2, element3, score3, park;
    public PathBuilder pushSamples;
    private Pose startPose, preloadPose, element1Pose, element1ControlPose, element2Pose, element2ControlPose, element3Pose, element3ControlPose, elementScorePose, parkControlPose, parkPose, humanPlayerPose;

    public Auto(HardwareMap hardwareMap, Telemetry telemetry, Follower follower, boolean isBlue, boolean isBucket) {
        claw = new ClawSubsystem(hardwareMap, clawGrabState, clawPivotState);
        lift = new LiftSubsystem(hardwareMap, telemetry);
        extend = new ExtendSubsystem(hardwareMap, telemetry);
        intake = new IntakeSubsystem(hardwareMap, intakeSpinState, intakePivotState);
        arm = new ArmSubsystem(hardwareMap, armState);

        this.follower = follower;
        this.telemetry = telemetry;

        startLocation = isBlue ? (isBucket ? RobotStart.BLUE_BUCKET : RobotStart.BLUE_OBSERVATION) : (isBucket ? RobotStart.RED_BUCKET : RobotStart.RED_OBSERVATION);

        createPoses();
        buildPaths();

        follower.setStartingPose(startPose);

        init();
    }

    public void init() {
        claw.init();
        lift.init();
        extend.toZero();
        intake.init();
        arm.init();

    }

    public void start() {
        lift.start();
        extend.start();
        extend.toZero();
        intake.start();
        claw.close();
    }

    public void update() {
        follower.update();
        lift.updatePIDF();
        transfer();
        bucket();
        chamber();
        intake();
        park();
    }

    public void createPoses() {
        switch (startLocation) {
            case BLUE_BUCKET:
                startPose = blueBucketStartPose;
                preloadPose = blueBucketPreloadPose;
                element1ControlPose = blueBucketLeftSampleControlPose;
                element1Pose = blueBucketLeftSamplePose;
                element2ControlPose = blueBucketMidSampleControlPose;
                element2Pose = blueBucketMidSamplePose;
                element3ControlPose = blueBucketRightSampleControlPose;
                element3Pose = blueBucketRightSamplePose;
                elementScorePose = blueBucketScorePose;
                parkControlPose = blueBucketParkControlPose;
                parkPose = blueBucketParkPose;
                break;

            case BLUE_OBSERVATION:
                startPose = blueObservationStartPose;
                preloadPose = blueObservationPreloadPose;
                humanPlayerPose = blueObservationHumanPlayerPose;
                element1ControlPose = blueObservationElement1ControlPose;
                element1Pose = blueObservationElement1Pose;
                element2ControlPose = blueObservationElement2ControlPose;
                element2Pose = blueObservationElement2Pose;
                element3ControlPose = blueObservationElement3ControlPose;
                element3Pose = blueObservationElement3Pose;
                break;

            case RED_BUCKET:
                startPose = redBucketStartPose;
                //parkPose = redBucketPark;
                break;

            case RED_OBSERVATION:
                startPose = redObservationStartPose;
                //parkPose = redObservationPark;
                break;
        }

        follower.setStartingPose(startPose);
    }

    public void buildPaths() {


        if((startLocation == RobotStart.BLUE_BUCKET) || (startLocation == RobotStart.RED_BUCKET)) {
            preload = new Path(new BezierLine(new Point(startPose), new Point(preloadPose)));
            preload.setLinearHeadingInterpolation(startPose.getHeading(), preloadPose.getHeading());

            element1 = new Path(new BezierCurve(new Point(preloadPose), new Point(element1ControlPose), new Point(element1Pose)));
            element1.setLinearHeadingInterpolation(preloadPose.getHeading(), element1Pose.getHeading());

            score1 = new Path(new BezierLine(new Point(element1Pose), new Point(elementScorePose)));
            score1.setLinearHeadingInterpolation(element1Pose.getHeading(), elementScorePose.getHeading());

            element2 = new Path(new BezierCurve(new Point(element1Pose), new Point(element2ControlPose), new Point(element2Pose)));
            element2.setLinearHeadingInterpolation(element1Pose.getHeading(), element2Pose.getHeading());

            score2 = new Path(new BezierLine(new Point(element2Pose), new Point(elementScorePose)));
            score2.setLinearHeadingInterpolation(element2Pose.getHeading(), elementScorePose.getHeading());

            element3 = new Path(new BezierCurve(new Point(element2Pose), new Point(element3ControlPose), new Point(element3Pose)));
            element3.setLinearHeadingInterpolation(element2Pose.getHeading(), element3Pose.getHeading());

            score3 = new Path(new BezierLine(new Point(element3Pose), new Point(elementScorePose)));
            score3.setLinearHeadingInterpolation(element3Pose.getHeading(), elementScorePose.getHeading());

            park = new Path(new BezierCurve(new Point(elementScorePose), new Point(parkControlPose), new Point(parkPose)));
            park.setLinearHeadingInterpolation(elementScorePose.getHeading(), parkPose.getHeading());
        }

        if (startLocation == RobotStart.BLUE_OBSERVATION || startLocation == RobotStart.RED_OBSERVATION) {
            preload = new Path(new BezierLine(new Point(startPose), new Point(preloadPose)));
            preload.setLinearHeadingInterpolation(startPose.getHeading(), preloadPose.getHeading());

            pushSamples.addPath(
                            // Line 1
                            new BezierCurve(
                                    new Point(preloadPose),
                                    new Point(16.088, 22.000, Point.CARTESIAN),
                                    new Point(57.345, 50.496, Point.CARTESIAN),
                                    new Point(56.000, 24.000, Point.CARTESIAN)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(90))
                    .addPath(
                            // Line 2
                            new BezierLine(
                                    new Point(56.000, 24.000, Point.CARTESIAN),
                                    new Point(13.000, 24.000, Point.CARTESIAN)
                            )
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(90))
                    .addPath(
                            // Line 3
                            new BezierCurve(
                                    new Point(13.000, 24.000, Point.CARTESIAN),
                                    new Point(56.000, 50.000, Point.CARTESIAN),
                                    new Point(56.000, 14.000, Point.CARTESIAN)
                            )
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(90))
                    .addPath(
                            // Line 4
                            new BezierLine(
                                    new Point(56.000, 14.000, Point.CARTESIAN),
                                    new Point(13.000, 14.000, Point.CARTESIAN)
                            )
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(90))
                    .addPath(
                            // Line 5
                            new BezierCurve(
                                    new Point(13.000, 14.000, Point.CARTESIAN),
                                    new Point(56.000, 40.000, Point.CARTESIAN),
                                    new Point(56.000, 9.000, Point.CARTESIAN)
                            )
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(90))
                    .addPath(
                            // Line 6
                            new BezierLine(
                                    new Point(56.000, 9.000, Point.CARTESIAN),
                                    new Point(13.000, 9.000, Point.CARTESIAN)
                            )
                    )
                    .setConstantHeadingInterpolation(Math.toRadians(90))
                    .addPath(
                            // Line 7
                            new BezierLine(
                                    new Point(13.000, 9.000, Point.CARTESIAN),
                                    new Point(humanPlayerPose)
                            )
                    )
                    .setLinearHeadingInterpolation(Math.toRadians(90), humanPlayerPose.getHeading());

        }
    }

    public void transfer() {
        switch (transferState) {
            case 1:
                actionBusy = true;
                intake.pivotTransfer();
                intake.spinIn();
                lift.toTransfer();
                arm.transfer();
                claw.transfer();
                claw.open();
                extend.toZero();
                transferTimer.resetTimer();
                setTransferState(2);
                break;
            case 2:
                if (transferTimer.getElapsedTimeSeconds() > 1.5) {
                    intake.spinStop();
                    transferTimer.resetTimer();
                    setTransferState(3);
                }
                break;
            case 3:
                if (transferTimer.getElapsedTimeSeconds() > 1) {
                    lift.toZero();
                    transferTimer.resetTimer();
                    setTransferState(4);
                }
                break;
            case 4:
                if (transferTimer.getElapsedTimeSeconds() > 0.5) {
                    claw.close();
                    actionBusy = false;
                    setTransferState(-1);
                }
                break;
        }
    }

    public void setTransferState(int x) {
        transferState = x;
    }

    public void startTransfer() {
        if (actionNotBusy()) {
            setTransferState(1);
        }
    }

    public void bucket() {
        switch (bucketState) {
            case 1:
                actionBusy = true;
                intake.pivotTransfer();
                intake.spinStop();
                lift.toHighBucket();
                claw.close();
                extend.toZero();
                bucketTimer.resetTimer();
                setBucketState(2);
                break;
            case 2:
                if (bucketTimer.getElapsedTimeSeconds() > 0.5) {
                    arm.score();
                    claw.score();
                    bucketTimer.resetTimer();
                    setBucketState(3);
                }
                break;
            case 3:
                if (bucketTimer.getElapsedTimeSeconds() > 1) {
                    actionBusy = false;
                    setBucketState(-1);
                }

        }
    }

    public void setBucketState(int x) {
        bucketState = x;
    }

    public void startBucket() {
        if (actionNotBusy()) {
            setBucketState(1);
        }
    }

    public void chamber() {
        switch (chamberState) {
            case 1:
                actionBusy = true;
                intake.pivotTransfer();
                intake.spinStop();
                claw.close();
                lift.toHighChamber();
                extend.toZero();
                chamberTimer.resetTimer();
                setChamberState(2);
                break;
            case 2:
                if (chamberTimer.getElapsedTimeSeconds() > 2) {
                    arm.chamber();
                    claw.chamber();
                    chamberTimer.resetTimer();
                    setChamberState(3);
                }
                break;
            case 3:
                if (chamberTimer.getElapsedTimeSeconds() > 1) {
                    lift.toTransfer();
                    setChamberState(4);
                }
            case 4:
                if(chamberTimer.getElapsedTimeSeconds() > 1) {
                    claw.open();
                    claw.transfer();
                    arm.transfer();
                    actionBusy = false;
                    setChamberState(-1);
                }
        }
    }

    public void setChamberState(int x) {
        chamberState = x;
    }

    public void startChamber() {
        if(actionNotBusy()) {
            setChamberState(1);
        }
    }

    public void intake() {
        switch (intakeState) {
            case 1:
                actionBusy = true;
                claw.open();
                intakeTimer.resetTimer();
                setTransferState(2);
                break;
            case 2:
                if(intakeTimer.getElapsedTimeSeconds() > 0.5) {
                    arm.transfer();
                    claw.transfer();
                    intake.pivotTransfer();
                    intake.spinStop();
                    lift.toTransfer();
                    claw.open();
                    extend.toHalf();
                    intakeTimer.resetTimer();
                    setTransferState(3);
                }
                break;
            case 3:
                if (intakeTimer.getElapsedTimeSeconds() > 1) {
                    intake.pivotGround();
                    intake.spinIn();
                    intakeTimer.resetTimer();
                    setTransferState(4);
                }
                break;
            case 4:
                if (intakeTimer.getElapsedTimeSeconds() > 1.5) {
                    intake.spinStop();
                    intakeTimer.resetTimer();
                    actionBusy = false;
                    setTransferState(-1);
                }
                break;
        }
    }

    public void setIntakeState(int x) {
        intakeState = x;
    }

    public void startIntake() {
        if (actionNotBusy()) {
            setIntakeState(1);
        }
    }

    public void park() {
        switch (parkState) {
            case 1:
                actionBusy = true;
                claw.open();
                parkTimer.resetTimer();
                setParkState(2);
                break;
            case 2:
                if(parkTimer.getElapsedTimeSeconds() > 0.5) {
                    intake.pivotTransfer();
                    intake.spinStop();
                    lift.toPark();
                    arm.transfer();
                    claw.transfer();
                    claw.open();
                    extend.toZero();
                    parkTimer.resetTimer();
                    actionBusy = false;
                    setTransferState(-1);
                }
                break;
        }
    }

    public void setParkState(int x) {
        parkState = x;
    }

    public void startPark() {
        if (actionNotBusy()) {
            setParkState(1);
        }
    }

    public boolean actionNotBusy() {
        return !actionBusy;
    }

    public boolean notBusy() {
        return (!follower.isBusy() && actionNotBusy());
    }
}
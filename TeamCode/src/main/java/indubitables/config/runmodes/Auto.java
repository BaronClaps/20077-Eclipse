package indubitables.config.runmodes;

import static indubitables.config.util.FieldConstants.*;

import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;
import indubitables.pedroPathing.pathGeneration.BezierCurve;
import indubitables.config.subsystem.OuttakeSubsystem;
import indubitables.config.subsystem.ExtendSubsystem;
import indubitables.config.subsystem.IntakeSubsystem;
import indubitables.config.subsystem.LiftSubsystem;
import indubitables.pedroPathing.follower.Follower;
import indubitables.pedroPathing.localization.Pose;
import indubitables.pedroPathing.pathGeneration.BezierLine;
import indubitables.pedroPathing.pathGeneration.Path;
import indubitables.pedroPathing.pathGeneration.PathChain;
import indubitables.pedroPathing.pathGeneration.Point;
import indubitables.pedroPathing.util.Timer;

public class Auto {

    private RobotStart startLocation;

    public IntakeSubsystem intake;
    private IntakeSubsystem.GrabState intakeGrabState;
    private IntakeSubsystem.PivotState intakePivotState;
    private IntakeSubsystem.RotateState intakeRotateState;

    public OuttakeSubsystem outtake;
    private OuttakeSubsystem.GrabState outtakeGrabState;
    private OuttakeSubsystem.PivotState outtakePivotState;
    private OuttakeSubsystem.RotateState outtakeRotateState;
    
    public LiftSubsystem lift;
    public ExtendSubsystem extend;


    public Follower follower;
    public Telemetry telemetry;

    public boolean actionBusy, liftPIDF = true;
    public double liftManual = 0;

    public Timer transferTimer = new Timer(), bucketTimer = new Timer(), chamberTimer = new Timer(), intakeTimer = new Timer(), parkTimer = new Timer(), specimenTimer = new Timer(), chamberTimer2 = new Timer();
    public int transferState = -1, bucketState = -1, chamberState = -1, intakeState = -1, parkState = -1, specimenState = -1;

    public Path element1, score1, element2, score2, element3, score3;
    public PathChain pushSamples, preload,specimen1, specimen2, specimen3, specimen4, grab1, grab2, grab3, grab4, park;
    public Pose startPose, preloadPose, sample1Pose, sample1ControlPose, sample2Pose, sample2ControlPose, sample3Pose, sample3ControlPose, sampleScorePose, parkControlPose, parkPose, grab1Pose, specimen1Pose, grab2Pose, specimen2Pose, grab3Pose, specimen3Pose, grab4Pose, specimen4Pose, specimenSetPose;

    public Auto(HardwareMap hardwareMap, Telemetry telemetry, Follower follower, boolean isBlue, boolean isBucket) {
        lift = new LiftSubsystem(hardwareMap, telemetry);
        extend = new ExtendSubsystem(hardwareMap, telemetry);

        this.follower = follower;
        this.telemetry = telemetry;

        startLocation = isBucket ? RobotStart.BUCKET : RobotStart.OBSERVATION;

        createPoses();
        buildPaths();

        init();
    }

    public void init() {
        outtake.init();
        lift.init();
        extend.toZero();
        intake.init();
        telemetryUpdate();

        follower.setStartingPose(startPose);
    }

    public void start() {
        lift.start();
        extend.start();
        extend.toZero();
        outtake.close();

        follower.setStartingPose(startPose);
    }

    public void update() {
        follower.update();

        if(!liftPIDF)
            lift.manual(liftManual);
        else
            lift.updatePIDF();

        transfer();
        bucket();
        chamber();
        intake();
        park();
        specimen();
        telemetryUpdate();
    }

    public void createPoses() { //Able to be cut
        switch (startLocation) {
            case BUCKET:
                startPose = bucketStartPose;
                preloadPose = bucketPreloadPose;
                sample1ControlPose = bucketLeftSampleControlPose;
                sample1Pose = bucketLeftSamplePose;
                sample2ControlPose = bucketMidSampleControlPose;
                sample2Pose = bucketMidSamplePose;
                sample3ControlPose = bucketRightSampleControlPose;
                sample3Pose = bucketRightSamplePose;
                sampleScorePose = bucketScorePose;
                parkControlPose = bucketParkControlPose;
                parkPose = bucketParkPose;
                break;

            case OBSERVATION:
                startPose = observationStartPose;
                preloadPose = observationPreloadPose;
                specimenSetPose = observationSpecimenSetPose;
                grab1Pose = observationSpecimenPickupPose;
                grab2Pose = observationSpecimenPickup2Pose;
                grab3Pose = observationSpecimenPickup3Pose;
                grab4Pose = observationSpecimenPickup4Pose;
                specimen1Pose = observationSpecimen1Pose;
                specimen2Pose = observationSpecimen2Pose;
                specimen3Pose = observationSpecimen3Pose;
                specimen4Pose = observationSpecimen4Pose;
                parkPose = observationParkPose;
                break;
        }

        follower.setStartingPose(startPose);
    }

    public void buildPaths() {
        if(startLocation == RobotStart.BUCKET) {
            preload = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(startPose), new Point(preloadPose)))
                    .setLinearHeadingInterpolation(startPose.getHeading(), preloadPose.getHeading())
                    .build();

            element1 = new Path(new BezierCurve(new Point(preloadPose), new Point(sample1ControlPose), new Point(sample1Pose)));
            element1.setLinearHeadingInterpolation(preloadPose.getHeading(), sample1Pose.getHeading());

            score1 = new Path(new BezierLine(new Point(sample1Pose), new Point(sampleScorePose)));
            score1.setLinearHeadingInterpolation(sample1Pose.getHeading(), sampleScorePose.getHeading());

            element2 = new Path(new BezierCurve(new Point(sampleScorePose), new Point(sample2ControlPose), new Point(sample2Pose)));
            element2.setLinearHeadingInterpolation(sampleScorePose.getHeading(), sample2Pose.getHeading());

            score2 = new Path(new BezierLine(new Point(sample2Pose), new Point(sampleScorePose)));
            score2.setLinearHeadingInterpolation(sample2Pose.getHeading(), sampleScorePose.getHeading());

            element3 = new Path(new BezierCurve(new Point(sampleScorePose), new Point(sample3ControlPose), new Point(sample3Pose)));
            element3.setLinearHeadingInterpolation(sampleScorePose.getHeading(), sample3Pose.getHeading());

            score3 = new Path(new BezierLine(new Point(sample3Pose), new Point(sampleScorePose)));
            score3.setLinearHeadingInterpolation(sample3Pose.getHeading(), sampleScorePose.getHeading());

            park = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(sampleScorePose), new Point(parkControlPose), new Point(parkPose)))
                    .setLinearHeadingInterpolation(sampleScorePose.getHeading(), parkPose.getHeading())
                    .build();
        }

        if (startLocation == RobotStart.OBSERVATION) {
            preload = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(startPose), new Point(preloadPose)))
                    .setLinearHeadingInterpolation(startPose.getHeading(), preloadPose.getHeading())
                    .setZeroPowerAccelerationMultiplier(4)
                    .build();

            pushSamples = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(preloadPose), new Point(15, 36, Point.CARTESIAN), new Point(61, 36.25, Point.CARTESIAN), new Point(59, 26.000, Point.CARTESIAN)))
                    .setLinearHeadingInterpolation(preloadPose.getHeading(), Math.toRadians(180))
                    .addPath(new BezierLine(new Point(59.000, 26.000, Point.CARTESIAN), new Point(28, 26.000, Point.CARTESIAN)))
                    .setLinearHeadingInterpolation(Math.toRadians(180),Math.toRadians(180))
                    .addPath(new BezierCurve(new Point(28, 26.000, Point.CARTESIAN), new Point(52.000, 30.000, Point.CARTESIAN), new Point(58.000, 16.000, Point.CARTESIAN)))
                    .setLinearHeadingInterpolation(Math.toRadians(180),Math.toRadians(180))
                    .addPath(new BezierLine(new Point(58.000, 16.000, Point.CARTESIAN),new Point(28, 16.000, Point.CARTESIAN)))
                    .setLinearHeadingInterpolation(Math.toRadians(180),Math.toRadians(180))
                    .addPath(new BezierCurve(new Point(28, 16.000, Point.CARTESIAN), new Point(56.000, 16.000, Point.CARTESIAN), new Point(56.000, 10, Point.CARTESIAN)))
                    .setLinearHeadingInterpolation(Math.toRadians(180),Math.toRadians(180))
                    .addPath(new BezierLine(new Point(56.000, 10, Point.CARTESIAN), new Point(28, 10, Point.CARTESIAN)))
                    .setLinearHeadingInterpolation(Math.toRadians(180),Math.toRadians(180))
                    .addPath(new BezierLine(new Point(28, 10, Point.CARTESIAN), new Point(specimenSetPose)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), specimenSetPose.getHeading())
                    //.setZeroPowerAccelerationMultiplier(0.5)
                    .build();

            grab1 = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(specimenSetPose), new Point(grab1Pose)))
                    .setLinearHeadingInterpolation(specimenSetPose.getHeading(), grab1Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            specimen1 = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(grab1Pose), new Point(specimen1Pose.getX()-10, specimen1Pose.getY(), Point.CARTESIAN), new Point(specimen1Pose)))
                    .setLinearHeadingInterpolation(grab1Pose.getHeading(), specimen1Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            grab2 = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(specimen1Pose), new Point(grab2Pose)))
                    .setLinearHeadingInterpolation(specimen1Pose.getHeading(), grab2Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            specimen2 = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(grab2Pose), new Point(specimen2Pose.getX() - 10, specimen2Pose.getY(), Point.CARTESIAN),new Point(specimen2Pose)))
                    .setLinearHeadingInterpolation(grab2Pose.getHeading(), specimen2Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            grab3 = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(specimen2Pose), new Point(grab3Pose)))
                    .setLinearHeadingInterpolation(specimen2Pose.getHeading(), grab3Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            specimen3 = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(grab3Pose), new Point(specimen3Pose.getX() - 10, specimen3Pose.getY(), Point.CARTESIAN),new Point(specimen3Pose)))
                    .setLinearHeadingInterpolation(grab3Pose.getHeading(), specimen3Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            grab4 = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(specimen3Pose), new Point(grab4Pose)))
                    .setLinearHeadingInterpolation(specimen3Pose.getHeading(), grab4Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            specimen4 = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(grab4Pose), new Point(specimen4Pose.getX() - 10, specimen3Pose.getY(), Point.CARTESIAN),new Point(specimen3Pose)))
                    .setLinearHeadingInterpolation(grab4Pose.getHeading(), specimen4Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            park = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(specimen4Pose), new Point(parkPose)))
                    .setLinearHeadingInterpolation(specimen4Pose.getHeading(), parkPose.getHeading())
                    .setZeroPowerAccelerationMultiplier(3)
                    .build();      

        }
    }

    public void transfer() {
        switch (transferState) {
            case 1:
                actionBusy = true;
                intake.transfer();
                lift.toTransfer();
                outtake.transfer();
                extend.toZero();
                lift.toZero();
                transferTimer.resetTimer();
                setTransferState(2);
                break;
            case 2:
                if (transferTimer.getElapsedTimeSeconds() > 1.5) {
                    outtake.close();
                    transferTimer.resetTimer();
                    setTransferState(3);
                }
                break;
            case 3:
                if (transferTimer.getElapsedTimeSeconds() > 1) {
                    intake.hover();
                    transferTimer.resetTimer();
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
                intake.transfer();
                lift.toHighBucket();
                outtake.close();
                extend.toZero();
                bucketTimer.resetTimer();
                setBucketState(2);
                break;
            case 2:
                if (bucketTimer.getElapsedTimeSeconds() > 0.5) {
                    intake.hover();
                    outtake.score();
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
                outtake.specimenScore();
                extend.toZero();
                chamberTimer.resetTimer();
                setChamberState(2);
                break;
            case 2:
                if ((follower.getPose().getX() >= specimen1Pose.getX() - 0.5)) {
                    chamberTimer.resetTimer();
                    setChamberState(3);
                }
                break;
            case 3:
                if(chamberTimer.getElapsedTimeSeconds() > 0.375) {
                    outtake.open();
                    actionBusy = false;
                    setChamberState(-1);
                }
                break;
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

    public void specimen() {
        switch (specimenState) {
            case 1:
                actionBusy = true;
                outtake.specimenGrab();
                extend.toZero();
                specimenTimer.resetTimer();
                setSpecimenState(2);
            case 2:
                if(specimenTimer.getElapsedTimeSeconds() > 0) {
                    actionBusy = false;
                    setSpecimenState(-1);
                }
                break;
        }
    }

    public void setSpecimenState(int x) {
        specimenState = x;
    }

    public void startSpecimen() {
        if(actionNotBusy()) {
            setSpecimenState(1);
        }
    }

    public void intake() {
        switch (intakeState) {
            case 1:
                actionBusy = true;
                outtake.open();
                intakeTimer.resetTimer();
                setTransferState(2);
                break;
            case 2:
                if(intakeTimer.getElapsedTimeSeconds() > 0.5) {
                    outtake.transfer();
                    intake.hover();
                    lift.toTransfer();
                    outtake.open();
                    extend.toHalf();
                    intakeTimer.resetTimer();
                    setTransferState(3);
                }
                break;
            case 3:
                if (intakeTimer.getElapsedTimeSeconds() > 1) {
                    intake.ground();
                    intakeTimer.resetTimer();
                    setTransferState(4);
                }
                break;
            case 4:
                if (intakeTimer.getElapsedTimeSeconds() > 1.5) {
                    intake.close();
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
                outtake.open();
                parkTimer.resetTimer();
                setParkState(2);
                break;
            case 2:
                if(parkTimer.getElapsedTimeSeconds() > 0.5) {
                    intake.transfer();
                    lift.toPark();
                    outtake.transfer();
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

    public void telemetryUpdate() {
        follower.update();
        telemetry.addData("X: ", follower.getPose().getX());
        telemetry.addData("Y: ", follower.getPose().getY());
        telemetry.addData("Heading: ", follower.getPose().getHeading());
        telemetry.addData("Action Busy?: ", actionBusy);
        extend.telemetry();
        lift.telemetry();
        outtake.telemetry();
        intake.telemetry();
        telemetry.update();
    }
}
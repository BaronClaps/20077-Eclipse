package indubitables.config.runmodes;

import static indubitables.config.util.FieldConstants.*;

import com.qualcomm.robotcore.hardware.DcMotor;
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

    public boolean actionBusy, liftPIDF = true;
    public double liftManual = 0;

    public Timer transferTimer = new Timer(), bucketTimer = new Timer(), chamberTimer = new Timer(), intakeTimer = new Timer(), parkTimer = new Timer(), specimenTimer = new Timer(), chamberTimer2 = new Timer();
    public int transferState = -1, bucketState = -1, chamberState = -1, intakeState = -1, parkState = -1, specimenState = -1, chamberState2 = -1;

    public Path element1, score1, element2, score2, element3, score3;
    public PathChain pushSamples, preload,specimen1, specimen2, specimen3, grab1, grab2, grab3, park;
    public Pose startPose, preloadPose, sample1Pose, sample1ControlPose, sample2Pose, sample2ControlPose, sample3Pose, sample3ControlPose, sampleScorePose, parkControlPose, parkPose, grab1Pose, specimen1Pose, grab2Pose, specimen2Pose, grab3Pose, specimen3Pose, specimenSetPose;

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
        telemetryUpdate();
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
        chamber2();

        telemetryUpdate();
    }

    public void createPoses() {
        switch (startLocation) {
            case BLUE_BUCKET:
                startPose = blueBucketStartPose;
                preloadPose = blueBucketPreloadPose;
                sample1ControlPose = blueBucketLeftSampleControlPose;
                sample1Pose = blueBucketLeftSamplePose;
                sample2ControlPose = blueBucketMidSampleControlPose;
                sample2Pose = blueBucketMidSamplePose;
                sample3ControlPose = blueBucketRightSampleControlPose;
                sample3Pose = blueBucketRightSamplePose;
                sampleScorePose = blueBucketScorePose;
                parkControlPose = blueBucketParkControlPose;
                parkPose = blueBucketParkPose;
                break;

            case BLUE_OBSERVATION:
                startPose = blueObservationStartPose;
                preloadPose = blueObservationPreloadPose;
                specimenSetPose = blueObservationSpecimenSetPose;
                grab1Pose = blueObservationSpecimenPickupPose;
                grab2Pose = blueObservationSpecimenPickup2Pose;
                grab3Pose = blueObservationSpecimenPickup3Pose;
                specimen1Pose = blueObservationSpecimen1Pose;
                specimen2Pose = blueObservationSpecimen2Pose;
                specimen3Pose = blueObservationSpecimen3Pose;


                parkPose = blueObservationParkPose;
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

        if (startLocation == RobotStart.BLUE_OBSERVATION || startLocation == RobotStart.RED_OBSERVATION) {
            preload = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(startPose), new Point(preloadPose)))
                    .setLinearHeadingInterpolation(startPose.getHeading(), preloadPose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            pushSamples = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(preloadPose), new Point(12.088, 16.000, Point.CARTESIAN), new Point(57.345, 40.496, Point.CARTESIAN), new Point(59.000, 24.000, Point.CARTESIAN)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(180))
                    .addPath(new BezierLine(new Point(59.000, 24.000, Point.CARTESIAN), new Point(24, 24.000, Point.CARTESIAN)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .addPath(new BezierCurve(new Point(24, 24.000, Point.CARTESIAN), new Point(52.000, 30.000, Point.CARTESIAN), new Point(58.000, 16.000, Point.CARTESIAN)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
                    .addPath(new BezierLine(new Point(58.000, 16.000, Point.CARTESIAN),new Point(24, 16.000, Point.CARTESIAN)))
                    .setConstantHeadingInterpolation(Math.toRadians(180))
             //       .addPath(new BezierCurve(new Point(24, 16.000, Point.CARTESIAN), new Point(56.000, 16.000, Point.CARTESIAN), new Point(56.000, 9.50, Point.CARTESIAN)))
               //     .setConstantHeadingInterpolation(Math.toRadians(0))
              //      .addPath(new BezierLine(new Point(56.000, 9.50, Point.CARTESIAN), new Point(24, 9.5, Point.CARTESIAN)))
                //    .setConstantHeadingInterpolation(Math.toRadians(0))
                    .addPath(new BezierLine(new Point(24, 16, Point.CARTESIAN), new Point(specimenSetPose)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), specimenSetPose.getHeading())
                    .build();

            grab1 = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(specimenSetPose), new Point(grab1Pose)))
                    .setLinearHeadingInterpolation(specimenSetPose.getHeading(), grab1Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            specimen1 = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(grab1Pose), new Point(specimen1Pose.getX()-10, specimen1Pose.getY(), Point.CARTESIAN), new Point(specimen1Pose)))
                    .setLinearHeadingInterpolation(grab1Pose.getHeading(), specimen1Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(0.5)
                    .build();

            grab2 = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(specimen1Pose), new Point(grab2Pose)))
                    .setLinearHeadingInterpolation(specimen1Pose.getHeading(), grab2Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            specimen2 = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(grab2Pose), new Point(specimen2Pose.getX() - 10, specimen2Pose.getY(), Point.CARTESIAN),new Point(specimen2Pose)))
                    .setLinearHeadingInterpolation(grab2Pose.getHeading(), specimen2Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(0.25)
                    .build();

            grab3 = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(specimen2Pose), new Point(grab3Pose)))
                    .setLinearHeadingInterpolation(specimen2Pose.getHeading(), grab3Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(1)
                    .build();

            specimen3 = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(grab3Pose), new Point(specimen3Pose.getX() - 10, specimen3Pose.getY(), Point.CARTESIAN),new Point(specimen3Pose)))
                    .setLinearHeadingInterpolation(grab3Pose.getHeading(), specimen3Pose.getHeading())
                    .setZeroPowerAccelerationMultiplier(0.25)
                    .build();

            park = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(specimen3Pose), new Point(parkPose)))
                    .setLinearHeadingInterpolation(specimen3Pose.getHeading(), parkPose.getHeading())
                    .build();      

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
                lift.manual = false;
                intake.pivotTransfer();
                intake.spinStop();
                claw.close();
                lift.toHighChamber();
                extend.toZero();
                chamberTimer.resetTimer();
                setChamberState(2);
                break;
            case 2:
                if (chamberTimer.getElapsedTimeSeconds() > 1) {
                    arm.chamber();
                    claw.chamber();
                    chamberTimer.resetTimer();
                    setChamberState(4);
                }
                break;
            case 4:
                if(chamberTimer.getElapsedTimeSeconds() > 0.5) {
                    claw.initClaw();
                    arm.initArm();
                    claw.open();
                    lift.toZero();
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

    public void chamber2() {
        switch (chamberState2) {
            case 1:
                actionBusy = true;
                arm.specimenScore();
                claw.close();
                claw.score();
                extend.toZero();
                chamberTimer2.resetTimer();
                setChamberState2(2);
                break;
            case 2:
                if ((follower.getPose().getX() > (specimen1Pose.getX())) && (follower.getPose().getY() > (specimen1Pose.getY()))) {
                    chamberTimer2.resetTimer();
                    setChamberState2(3);
                }
                break;
            case 3:
                if(chamberTimer2.getElapsedTimeSeconds() > 0.5) {
                    claw.specimenScore();
                    arm.score();
                    chamberTimer2.resetTimer();
                    setChamberState2(4);
                }
                break;
            case 4:
                if(chamberTimer2.getElapsedTimeSeconds() > 0.5) {
                    claw.init();
                    arm.init();
                    claw.open();
                    actionBusy = false;
                    setChamberState2(-1);
                }
        }
    }

    public void setChamberState2(int x) {
        chamberState2 = x;
    }

    public void startChamber2() {
        if(actionNotBusy()) {
            setChamberState2(1);
        }
    }

    public void specimen() {
        switch (specimenState) {
            case 1:
                actionBusy = true;
                claw.open();
                extend.toZero();
                arm.specimenGrab();
                claw.specimenGrab();
                specimenTimer.resetTimer();
                setSpecimenState(2);
            case 2:
                if(specimenTimer.getElapsedTimeSeconds() > 0.5) {
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

    public void telemetryUpdate() {
        telemetry.addData("X: ", follower.getPose().getX());
        telemetry.addData("Y: ", follower.getPose().getY());
        telemetry.addData("Heading: ", follower.getPose().getHeading());
        telemetry.addData("Action Busy?: ", actionBusy);
        telemetry.addData("Lift Pos", lift.getPos());
        telemetry.addData("Extend Pos", extend.leftExtend.getPosition());
        telemetry.addData("Extend Limit", extend.extendLimit);
        telemetry.addData("Claw Grab State", claw.grabState);
        telemetry.addData("Claw Pivot State", claw.pivotState);
        telemetry.addData("Intake Spin State", intakeSpinState);
        telemetry.addData("Intake Pivot State", intakePivotState);
        telemetry.addData("arm State", arm.state);
        telemetry.update();
    }
}
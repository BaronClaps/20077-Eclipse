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

    public Path element1, score1, element2, score2, element3, score3, grab3, align2, align3;
    public PathChain pushSamples, preload,specimen1, specimen2, specimen3, grab1, align1, lineUp2, grab2, park;
    public Pose startPose, preloadPose, element1Pose, element1ControlPose, element2Pose, element2ControlPose, element3Pose, element3ControlPose, elementScorePose, parkControlPose, parkPose, grab1Pose, align1Pose, specimen1Pose, grab2Pose, specimen2Pose;

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
                element1ControlPose = blueObservationElement1ControlPose;
                element1Pose = blueObservationElement1Pose;
                element2ControlPose = blueObservationElement2ControlPose;
                element2Pose = blueObservationElement2Pose;
                element3ControlPose = blueObservationElement3ControlPose;
                element3Pose = blueObservationElement3Pose;
                parkControlPose = blueObservationParkControlPose;
                grab1Pose = blueObservationSpecimenPickupPose;
                align1Pose = blueObservationSpecimenSetPose;
                specimen1Pose = blueObservationSpecimen1Pose;
                specimen2Pose = blueObservationSpecimen2Pose;
                grab2Pose = blueObservationSpecimenPickup2Pose;
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

            element1 = new Path(new BezierCurve(new Point(preloadPose), new Point(element1ControlPose), new Point(element1Pose)));
            element1.setLinearHeadingInterpolation(preloadPose.getHeading(), element1Pose.getHeading());

            score1 = new Path(new BezierLine(new Point(element1Pose), new Point(elementScorePose)));
            score1.setLinearHeadingInterpolation(element1Pose.getHeading(), elementScorePose.getHeading());

            element2 = new Path(new BezierCurve(new Point(elementScorePose), new Point(element2ControlPose), new Point(element2Pose)));
            element2.setLinearHeadingInterpolation(element1Pose.getHeading(), element2Pose.getHeading());

            score2 = new Path(new BezierLine(new Point(element2Pose), new Point(elementScorePose)));
            score2.setLinearHeadingInterpolation(element2Pose.getHeading(), elementScorePose.getHeading());

            element3 = new Path(new BezierCurve(new Point(element2Pose), new Point(element3ControlPose), new Point(element3Pose)));
            element3.setLinearHeadingInterpolation(element2Pose.getHeading(), element3Pose.getHeading());

            score3 = new Path(new BezierLine(new Point(element3Pose), new Point(elementScorePose)));
            score3.setLinearHeadingInterpolation(element3Pose.getHeading(), elementScorePose.getHeading());

            park = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(elementScorePose), new Point(parkControlPose), new Point(parkPose)))
                    .setLinearHeadingInterpolation(elementScorePose.getHeading(), parkPose.getHeading())
                    .build();
        }

        if (startLocation == RobotStart.BLUE_OBSERVATION || startLocation == RobotStart.RED_OBSERVATION) {
            preload = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(startPose), new Point(preloadPose)))
                    .setLinearHeadingInterpolation(startPose.getHeading(), preloadPose.getHeading())
                    .build();

            pushSamples = follower.pathBuilder()
                    .addPath(new BezierCurve(new Point(preloadPose), new Point(16.088, 22.000, Point.CARTESIAN), new Point(57.345, 50.496, Point.CARTESIAN), new Point(56.000, 26.000, Point.CARTESIAN)))
                    .setLinearHeadingInterpolation(Math.toRadians(180), Math.toRadians(90))
                    .addPath(new BezierLine(new Point(56.000, 26.000, Point.CARTESIAN), new Point(24, 26.000, Point.CARTESIAN)))
                    .setConstantHeadingInterpolation(Math.toRadians(90))
                    .addPath(new BezierCurve(new Point(24, 26.000, Point.CARTESIAN), new Point(56.000, 30.000, Point.CARTESIAN), new Point(56.000, 16.000, Point.CARTESIAN)))
                    .setConstantHeadingInterpolation(Math.toRadians(90))
                    .addPath(new BezierLine(new Point(56.000, 16.000, Point.CARTESIAN),new Point(24, 16.000, Point.CARTESIAN)))
                    .setConstantHeadingInterpolation(Math.toRadians(90))
             //       .addPath(new BezierCurve(new Point(24, 16.000, Point.CARTESIAN), new Point(56.000, 16.000, Point.CARTESIAN), new Point(56.000, 9.50, Point.CARTESIAN)))
               //     .setConstantHeadingInterpolation(Math.toRadians(0))
              //      .addPath(new BezierLine(new Point(56.000, 9.50, Point.CARTESIAN), new Point(24, 9.5, Point.CARTESIAN)))
                //    .setConstantHeadingInterpolation(Math.toRadians(0))
                    .addPath(new BezierLine(new Point(24, 16, Point.CARTESIAN), new Point(blueObservationSpecimenSetPose)))
                    .setLinearHeadingInterpolation(Math.toRadians(90), blueObservationSpecimenSetPose.getHeading())
                    .build();

            grab1 = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(blueObservationSpecimenSetPose), new Point(blueObservationSpecimenPickupPose)))
                    .setLinearHeadingInterpolation(blueObservationSpecimenSetPose.getHeading(), blueObservationSpecimenPickupPose.getHeading())
                    .build();

            specimen1 = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(blueObservationSpecimenPickupPose), new Point(blueObservationSpecimen1Pose)))
                    .setLinearHeadingInterpolation(blueObservationSpecimenPickupPose.getHeading(), blueObservationSpecimen1Pose.getHeading())
                    .build();

            grab2 = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(blueObservationSpecimen1Pose), new Point(blueObservationSpecimenPickup2Pose)))
                    .setLinearHeadingInterpolation(blueObservationSpecimen1Pose.getHeading(), blueObservationSpecimenPickup2Pose.getHeading())
                    .build();

            specimen2 = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(blueObservationSpecimenPickup2Pose), new Point(blueObservationSpecimen2Pose)))
                    .setLinearHeadingInterpolation(blueObservationSpecimenPickup2Pose.getHeading(), blueObservationSpecimen2Pose.getHeading())
                    .build();

            park = follower.pathBuilder()
                    .addPath(new BezierLine(new Point(blueObservationSpecimen2Pose), new Point(blueObservationParkPose)))
                    .setLinearHeadingInterpolation(blueObservationSpecimen2Pose.getHeading(), blueObservationParkPose.getHeading())  
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
                lift.manual = false;
                arm.specimenScore();
                claw.close();
                claw.score();
                lift.toZero();
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
                lift.manual = false;
                claw.open();
                lift.toZero();
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
}
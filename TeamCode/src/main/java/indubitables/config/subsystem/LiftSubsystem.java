package indubitables.config.subsystem;

import static indubitables.config.util.RobotConstants.*;
import static java.lang.Math.abs;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import indubitables.config.util.action.RunAction;

public class LiftSubsystem {
    private Telemetry telemetry;

    public DcMotor rightLift, leftLift;
    private int pos, initalPos;
    public RunAction toZero, toLowBucket, toHighBucket, toLowChamber, releaseLowChamber, toHighChamber, releaseHighChamber, toHumanPlayer;
    public PIDController liftPID;
    public static int target;
    public static double p = 0.04, i = 0, d = 0.000001;
    public static double f = 0.01;
    private final double ticks_in_degrees = 537.7 / 360.0;


    public LiftSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());
        rightLift = hardwareMap.get(DcMotor.class, "rightLift");
        leftLift = hardwareMap.get(DcMotor.class, "leftLift");
        rightLift.setDirection(DcMotor.Direction.FORWARD);
        leftLift.setDirection(DcMotor.Direction.REVERSE);
        rightLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        rightLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        /*liftPID = new PIDController(p, i, d);

        toZero = new RunAction(this::toZero);
        toLowBucket = new RunAction(this::toLowBucket);
        toHighBucket = new RunAction(this::toHighBucket);
        toLowChamber = new RunAction(this::toLowChamber);
        releaseLowChamber = new RunAction(this::releaseLowChamber);
        toHighChamber = new RunAction(this::toHighChamber);
        releaseHighChamber = new RunAction(this::releaseHighChamber);
        toHumanPlayer = new RunAction(this::toHumanPlayer);*/
    }

    // Manual Control //
    public void manual(double n){ //(int liftPos, boolean negative) {
        rightLift.setPower(n);
        leftLift.setPower(n);
    }

    public void setTarget(int b) {
        target = b;
    }

    public void addToTarget(int b) {
        target += b;
    }
/*
    public void updatePIDF(){
        liftPID.setPID(p,i,d);
        updatePos();
        double pid = liftPID.calculate(pos, target);
        double ff = Math.cos(Math.toRadians(target/ticks_in_degrees)) * f;

        double power = pid + ff;

        lift.setPower(power);
        telemetry.addData("lift pos", pos);
        telemetry.addData("lift target", target);
    }

    public void toZero() {
        setTarget(liftZero);
    }

    public void toLowBucket() {
        setTarget(liftToLowBucket);
    }

    public void toHighBucket() {
        setTarget(liftToHighBucket);
    }

    public void toLowChamber() {
        setTarget(liftToLowChamber);
    }

    public void releaseLowChamber() {
        setTarget(liftReleaseLowChamber);
    }

    public void toHighChamber() {
        setTarget(liftToHighChamber);
    }

    public void releaseHighChamber() {
        setTarget(liftReleaseHighChamber);
    }

    public void toHumanPlayer() {
        setTarget(liftToHumanPlayer);
    }

    // Util //
    public double getPos() {
        updatePos();
        return pos;
    }

    public void updatePos() {
        pos = lift.getCurrentPosition() - initalPos;
    }

    public boolean isAtTarget() {
        return Math.abs(pos - target) < 10;
    }

    public void resetEncoder() {
        lift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    // Init + Start //

    public void init() {
        resetEncoder();
        initalPos = lift.getCurrentPosition();
        lift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        lift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void start() {
        initalPos = lift.getCurrentPosition();
    }*/

}
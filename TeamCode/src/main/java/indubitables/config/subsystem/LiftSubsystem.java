package indubitables.config.subsystem;

import static indubitables.config.util.RobotConstants.*;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import org.firstinspires.ftc.robotcore.external.Telemetry;

import indubitables.config.util.RobotConstants;
import indubitables.config.util.action.RunAction;

public class LiftSubsystem {
    private Telemetry telemetry;

    public DcMotor rightLift, leftLift;
    public boolean manual = false;
    public boolean hang = false;
    public int pos, bottom;
    public RunAction toZero, toHighBucket, toHighChamber, toHumanPlayer, toTransfer, toPark;
    public PIDController liftPID;
    public static int target;
    public static double p = 0.01, i = 0, d = 0;
    public static double f = 0.005;


    public LiftSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        rightLift = hardwareMap.get(DcMotor.class, "rightLift");
        leftLift = hardwareMap.get(DcMotor.class, "leftLift");

        rightLift.setDirection(DcMotor.Direction.FORWARD);
        leftLift.setDirection(DcMotor.Direction.REVERSE);
        rightLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);


        liftPID = new PIDController(p, i, d);
    }

    public void updatePIDF(){
        if (!manual) {
            liftPID.setPID(p,i,d);
            
            rightLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            leftLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);

            double pid = liftPID.calculate(getPos(), target);
            double ticks_in_degrees = 537.7 / 360.0;
            double ff = Math.cos(Math.toRadians(target / ticks_in_degrees)) * f;
            double power = pid + ff;

            rightLift.setPower(power);
            leftLift.setPower(power);
        }
    }

    public void manual(double n){
        manual = true;

        rightLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        leftLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);

        rightLift.setPower(n);
        leftLift.setPower(n);
    }

    //Util
    public void targetCurrent() {
        setTarget(getPos());
        manual = false;
    }

    public double getTarget() {
        return target;
    }

    public void setTarget(int b) {
        target = b;
    }

    public void addToTarget(int b) {
        target += b;
    }

    public int getPos() {
        pos = rightLift.getCurrentPosition() - bottom;
        return rightLift.getCurrentPosition() - bottom;
    }

    // OpMode
    public void init() {
        liftPID.setPID(p,i,d);
        bottom = getPos();
    }

    public void start() {
        target = 0;
    }

    //Presets

    public void toZero() {
        manual = false;
        setTarget(liftToZero);
    }

    public void toHighBucket() {
        manual = false;
        setTarget(liftToHighBucket);
    }

    public void toHighChamber() {
        setTarget(liftToHighChamber);
    }

    public void toHumanPlayer() {
        setTarget(liftToHumanPlayer);
    }

    public void toTransfer() {
        setTarget(liftToTransfer);
    }

    public void toPark() {
        setTarget(liftToPark);
    }

    public void telemetry() {
        telemetry.addData("Lift Pos: ", getPos());
        telemetry.addData("Lift Target: ", target);
    }

}
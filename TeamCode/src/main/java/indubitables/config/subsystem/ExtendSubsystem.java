package indubitables.config.subsystem;

import static indubitables.config.util.RobotConstants.extendFull;
import static indubitables.config.util.RobotConstants.extendHalf;
import static indubitables.config.util.RobotConstants.extendManualIncrements;
import static indubitables.config.util.RobotConstants.extendZero;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import indubitables.config.util.RobotConstants;
import indubitables.config.util.action.Actions;
import indubitables.config.util.action.RunAction;

public class ExtendSubsystem {
    private Telemetry telemetry;

    public Servo leftExtend, rightExtend;
    private double pos = 0;
    public RunAction toZero, toHalf, toFull;

    public ExtendSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        leftExtend = hardwareMap.get(Servo.class, "leftExtend");
        rightExtend = hardwareMap.get(Servo.class, "leftExtend");

        toZero = new RunAction(this::toZero);
        toHalf = new RunAction(this::toHalf);
        toFull = new RunAction(this::toFull);
    }

    public void manual(int direction) {
        pos = leftExtend.getPosition();
        pos += (extendManualIncrements * direction);
        leftExtend.setPosition(pos);
        rightExtend.setPosition(pos);
    }

    public void setTarget(double b) {
        leftExtend.setPosition(b);
        rightExtend.setPosition(b);
        pos = b;
    }

    public void toZero() {
        setTarget(extendZero);
    }

    public void toHalf() {
        setTarget(extendHalf);
    }

    public void toFull() {
        setTarget(extendFull);
    }

    // Util //
    public double getPos() {
        updatePos();
        return pos;
    }

    public void updatePos() {
        pos = leftExtend.getPosition();
    }


    // Init + Start //
    public void init() {
        updatePos();
        toZero();
    }

    public void start() {
        updatePos();
        toZero();
    }

}
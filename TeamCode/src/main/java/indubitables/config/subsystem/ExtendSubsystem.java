package indubitables.config.subsystem;

import static indubitables.config.util.RobotConstants.*;
import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.MultipleTelemetry;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class ExtendSubsystem {
    private Telemetry telemetry;

    public Servo left, right;
    private double pos = 0;
    public double extendLimit = extendFullSample;

    public ExtendSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {
        this.telemetry = telemetry;
        this.telemetry = new MultipleTelemetry(telemetry, FtcDashboard.getInstance().getTelemetry());

        left = hardwareMap.get(Servo.class, "leftExtend");
        right = hardwareMap.get(Servo.class, "rightExtend");
    }

    public void manual(int direction) {
        double rightPos = right.getPosition();

        if (rightPos <= extendLimit || direction < 0) {
            rightPos += (extendManualIncrements * direction);
        } else {
            rightPos = right.getPosition();
        }

        left.setPosition(rightPos);
        right.setPosition(rightPos);
    }

    public void setTarget(double b) {
        left.setPosition(b);
        right.setPosition(b);
        pos = b;
    }

    public void toZero() {
        setTarget(extendZero);
    }

    public void toQuarter() {
        setTarget(extendLimit/4);
    }

    public void toHalf() {
        setTarget(extendLimit/2);
    }

    public void toFull() {
        setTarget(extendLimit);
    }

    // Util
    public double getPos() {
        pos = right.getPosition();
        return pos;
    }

    public void setLimitToSpecimen() {
        extendLimit = extendFullSpecimen;
    }

    public void setLimitToSample() {
        extendLimit = extendFullSample;
    }

    // Init + Start //
    public void init() {
        toZero();
    }

    public void start() {
        toZero();
    }

    public void telemetry() {
        telemetry.addData("Extend Pos: ", getPos());
    }
}
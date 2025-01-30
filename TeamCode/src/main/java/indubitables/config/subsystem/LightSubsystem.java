package indubitables.config.subsystem;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import indubitables.config.util.Headlight;
import indubitables.config.util.RGBLight;

public class LightSubsystem {
    private Headlight hL, hR;
    private RGBLight rL, rR;

    public LightSubsystem(HardwareMap h, Telemetry t) {
        hL = new Headlight(h.get(Servo.class, "hL"));
        hR = new Headlight(h.get(Servo.class, "hR"));
        rL = new RGBLight(h.get(Servo.class, "rL"));
        rR = new RGBLight(h.get(Servo.class, "rR"));
    }

    public void red() {
        rL.red();
        rR.red();
    }

    public void orange() {
        rL.orange();
        rR.orange();
    }

    public void yellow() {
        rL.yellow();
        rR.yellow();
    }

    public void sage() {
        rL.sage();
        rR.sage();
    }

    public void green() {
        rL.green();
        rR.green();
    }

    public void azure() {
        rL.azure();
        rR.azure();
    }

    public void blue() {
        rL.blue();
        rR.blue();
    }

    public void indigo() {
        rL.indigo();
        rR.indigo();
    }

    public void violet() {
        rL.violet();
        rR.violet();
    }

    public void max() {
        hL.max();
        hR.max();
    }

    public void off() {
        hL.off();
        hR.off();
    }

    public void half() {
        hL.half();
        hR.half();
    }

    public void allOff() {
        rL.off();
        rR.off();
        hL.off();
        hR.off();
    }

    public void allMax() {
        rL.white();
        rR.white();
        hL.max();
        hR.max();
    }
}

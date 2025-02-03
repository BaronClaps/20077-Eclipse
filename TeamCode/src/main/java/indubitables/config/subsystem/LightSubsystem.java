package indubitables.config.subsystem;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import indubitables.config.util.Headlight;
import indubitables.config.util.IntakeColor;
import indubitables.config.util.RGBLight;

public class LightSubsystem {
    //private Headlight hL, hR;
    private RGBLight rL, rR;
    private IntakeColor color;

    public LightSubsystem(HardwareMap h, Telemetry t) {
       // hL = new Headlight(h.get(Servo.class, "hL"));
     //   hR = new Headlight(h.get(Servo.class, "hR"));
        rL = new RGBLight(h.get(Servo.class, "rL"));
        rR = new RGBLight(h.get(Servo.class, "rR"));
    }

    public void setColor(IntakeColor color) {
        this.color = color;
        switch (color) {
            case RED:
                rL.red();
                rR.red();
                break;
            case ORANGE:
                rL.orange();
                rR.orange();
                break;
            case YELLOW:
                rL.yellow();
                rR.yellow();
                break;
            case SAGE:
                rL.sage();
                rR.sage();
                break;
            case GREEN:
                rL.green();
                rR.green();
                break;
            case AZURE:
                rL.azure();
                rR.azure();
                break;
            case BLUE:
                rL.blue();
                rR.blue();
                break;
            case INDIGO:
                rL.indigo();
                rR.indigo();
                break;
            case VIOLET:
                rL.violet();
                rR.violet();
                break;
            case OFF:
                rL.off();
                rR.off();
        }
    }

    public IntakeColor getColor() {
        return color;
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

    public void off() {
        rL.off();
        rR.off();
    }

//    public void max() {
//        hL.max();
//        hR.max();
//    }
//
//    public void off() {
//        hL.off();
//        hR.off();
//    }
//
//    public void half() {
//        hL.half();
//        hR.half();
//    }

    public void allOff() {
        rL.off();
        rR.off();
//        hL.off();
//        hR.off();
    }

    public void allMax() {
        rL.white();
        rR.white();
//        hL.max();
//        hR.max();
    }

    public void setPercent(double percent) {
        rL.setColorFromRange(percent);
        rR.setColorFromRange(percent);
//        hL.setIntensity(percent);
//        hR.setIntensity(percent);
    }
}

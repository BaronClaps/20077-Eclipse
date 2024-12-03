package indubitables.config.subsystem;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.limelightvision.LLResult;
import com.qualcomm.hardware.limelightvision.LLResultTypes;
import com.qualcomm.hardware.limelightvision.Limelight3A;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.List;

import indubitables.pedroPathing.follower.Follower;

@Config
public class VisionSubsystem {

    private Telemetry telemetry;
    private ExtendSubsystem extend;


    public VisionSubsystem(HardwareMap hardwareMap, Telemetry telemetry) {

    }

}
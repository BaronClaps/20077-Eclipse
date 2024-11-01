package indubitables.config.util;

import com.acmerobotics.dashboard.config.Config;

@Config
public class RobotConstants {
    public static double clawClose = 0.1;
    public static double clawOpen = 0.34;
    public static double clawSpecimen = 0.19;
    public static double clawTransfer = 0.2;
    public static double clawScore = 0.4;
    public static double clawChamber = 0.35;
    public static double clawSpecimenScore = 0.4;
    public static double clawInit = 0.8;

    public static double intakeSpinInPwr = -1;
    public static double intakeSpinOutPwr = -0.25;
    public static double intakeSpinStopPwr = 0;

    public static double intakePivotTransfer= 0.0425;
    public static double intakePivotGround = 0;
    public static double intakePivotSubmersible = 0.05;

    public static double armTransfer= 0.05;
    public static double armScoring = 0.5;
    public static double armSpecimen = 0.825;
    public static double armChamber = 0.6;
    public static double armInit = 0.15;

    public static int liftToZero = 100;
    public static int liftToHumanPlayer = 100;
    public static int liftToHighChamber = 125;
    public static int liftToHighBucket = 0;
    public static int liftToTransfer = 200;
    public static int liftToPark = 0;


    public static double extendZero = 0;
    public static double extendFull = 0.175;
    public static double extendHalf = extendFull/2;
    public static double extendManualIncrements = 0.01; //0.05
}
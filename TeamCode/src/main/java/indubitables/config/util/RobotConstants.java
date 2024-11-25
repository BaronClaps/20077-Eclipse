package indubitables.config.util;

import com.acmerobotics.dashboard.config.Config;

@Config
public class RobotConstants {
    public static double clawClose = 0.06;
    public static double clawOpen = 0.25;
    public static double clawTransfer = 0.4; //.25
    public static double clawInit = 0.9;
    public static double clawSpecimenGrab = 0.62;

    public static double clawLeftScore = 0.9;
    public static double clawRightScore = 0.525;

    public static double clawLeftSpecimenScore = 0.65;
    public static double clawRightSpecimenScore = 0.28;

    public static double intakeSpinInPwr = -1;
    public static double intakeSpinOutPwr = 1;
    public static double intakeSpinStopPwr = 0;

    public static double intakePivotTransfer= 0.16
            ; //.12
    public static double intakePivotGround = 0;
    public static double intakePivotSubmersible = 0.05;

    public static double armTransfer= 0.066; //.05
    public static double armScoring = 0.475;
    public static double armInit = 0.15;
    public static double armSpecimenGrab = 0.125;
    public static double armSpecimenScore = 0.6;

    public static int liftToZero = 30;
    public static int liftToHumanPlayer = 200;
    public static int liftToHighChamber = 200;
    public static int liftToHighChamber2 = 225;
    public static int liftToHighChamber3 = 350;
    public static int liftToHighBucket = 1750;
    public static int liftToTransfer = 200;
    public static int liftToPark = 0;


    public static double extendManualIncrements = 0.01; //0.05
    public static double extendZero = 0;
    public static double extendFullSample = 0.175;
    public static double extendFullSpecimen = 0.1;

}
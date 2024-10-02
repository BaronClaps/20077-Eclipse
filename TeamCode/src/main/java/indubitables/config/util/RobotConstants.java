package indubitables.config.util;

import com.acmerobotics.dashboard.config.Config;

@Config
public class RobotConstants {
    public static double clawClose = 0;
    public static double clawOpen = 0.25;
    public static double clawSpecimen = 0.5;
    public static double clawTransfer = 0.15;
    public static double clawScore = 0.3;
    
    public static double intakeSpinInPwr = 1;
    public static double intakeSpinOutPwr = -0.25;
    public static double intakeSpinStopPwr = 0;
    
    public static double intakePivotTransfer= 0;
    public static double intakePivotGround = 0.1;
    
    public static double armTransfer= 0.46;
    public static double armScoring = 0.95;
    public static double armSpecimen = 1;
    
    /*public static int liftZero = 0;
    public static int liftToHumanPlayer = 0;
    public static int liftToHighChamber = 0;
    public static int liftReleaseHighChamber = 0;
    public static int liftToLowChamber = 0;
    public static int liftReleaseLowChamber = 0;
    public static int liftToLowBucket = 0;
    public static int liftToHighBucket = 0;*/

    public static double extendZero = 0;
    public static double extendHalf = 0.05;
    public static double extendFull = 0.01;
    public static double extendManualIncrements = 0.005; //0.05
}
package indubitables.config.util;

import com.acmerobotics.dashboard.config.Config;

@Config
public class RobotConstants {

    // Outtake
    public static double outtakeGrabClose = 0.06;
    public static double outtakeGrabOpen = 0.25;
    public static double outtakeRotateTransfer = 0.4; //.25
    public static double outtakeRotateSpecimenGrab = 0.62;
    public static double outtakeRotateLeftScore = 0.93;
    public static double outtakeRotateRightScore = 0.5375;
    public static double outtakeRotateLeftSpecimenScore = 0.65;
    public static double outtakeRotateRightSpecimenScore = 0.28;
    public static double outtakePivotTransfer= 0.066; //.05
    public static double outtakePivotScore = 0.475;
    public static double outtakePivotSpecimenGrab = 0.125;
    public static double outtakePivotSpecimenScore = 0.6;

    // Intake
    public static double intakeGrabClose = 0.06;
    public static double intakeGrabOpen = 0.25;
    public static double intakeRotateTransfer = 0.9;
    public static double intakeRotateHover = 0.3;
    public static double intakeRotateGround = 0.25;
    public static double intakePivotTransfer= 0.15;
    public static double intakePivotGround = 0.7;
    public static double intakePivotHover = 0.6;

    // Lift Positions
    public static int liftToZero = 30;
    public static int liftToHumanPlayer = 200;
    public static int liftToHighChamber = 200;
    public static int liftToHighBucket = 1750;
    public static int liftToTransfer = 200;
    public static int liftToPark = 0;

    // Extend Positions
    public static double extendManualIncrements = 0.05;
    public static double extendZero = 0;
    public static double extendFullSample = 0.225;
    public static double extendFullSpecimen = 0.2;

}
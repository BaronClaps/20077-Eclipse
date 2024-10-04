//package indubitables.config.pedroPathing.localization.localizers;
//
//
//import com.qualcomm.robotcore.hardware.HardwareMap;
//
//import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
//import org.firstinspires.ftc.robotcore.external.navigation.Pose2D;
//
//import indubitables.config.pedroPathing.localization.GoBildaPinpointDriver;
//import indubitables.config.pedroPathing.localization.Localizer;
//import indubitables.config.pedroPathing.localization.Pose;
//import indubitables.config.pedroPathing.pathGeneration.MathFunctions;
//import indubitables.config.pedroPathing.pathGeneration.Vector;
//
///**
// * @Author Logan Nash
// * @version 1.0, 10/2/24
// */
//
//public class PinpointLocalizer extends Localizer {
//    private HardwareMap hardwareMap;
//    private Pose startPose;
//    private GoBildaPinpointDriver odo;
//    private double previousHeading;
//    private double totalHeading;
//
//    public PinpointLocalizer(HardwareMap map){ this(map, new Pose());}
//
//    public PinpointLocalizer(HardwareMap map, Pose setStartPose){
//        hardwareMap = map;
//
//        odo = hardwareMap.get(GoBildaPinpointDriver.class,"odo");
//
//        odo.setOffsets(-84.0, -168.0); //these are tuned for 3110-0002-0001 Product Insight #1
//        //TODO: Tune urself if needed
//        odo.setYawScalar(1.0);
//
//        odo.setEncoderResolution(GoBildaPinpointDriver.GoBildaOdometryPods.goBILDA_4_BAR_POD);
//        //odo.setEncoderResolution(13.26291192);
//
//        odo.setEncoderDirections(GoBildaPinpointDriver.EncoderDirection.FORWARD, GoBildaPinpointDriver.EncoderDirection.FORWARD);
//
//        odo.resetPosAndIMU();
//
//        setStartPose(setStartPose);
//        totalHeading = 0;
//        previousHeading = startPose.getHeading();
//
//        resetPinpoint();
//    }
//    @Override
//    public Pose getPose() {
//        Pose2D pose = odo.getPosition();
//        return new Pose(pose.getX(DistanceUnit.INCH), pose.getY(DistanceUnit.INCH), pose.getHeading(AngleUnit.DEGREES));
//    }
//
//    @Override
//    public Pose getVelocity() {
//        Pose2D pose = odo.getVelocity();
//        return new Pose(pose.getX(DistanceUnit.INCH), pose.getY(DistanceUnit.INCH), pose.getHeading(AngleUnit.DEGREES));
//    }
//
//    @Override
//    public Vector getVelocityVector() {
//        Pose2D pose = odo.getVelocity();
//        Vector returnVector = new Vector();
//        returnVector.setOrthogonalComponents(pose.getX(DistanceUnit.INCH), pose.getY(DistanceUnit.INCH));
//        return returnVector;
//    }
//
//    @Override
//    public void setStartPose(Pose setStart) {startPose = setStart;}
//
//    @Override
//    public void setPose(Pose setPose) {
//    resetPinpoint();
//    Pose setPinpointPose = MathFunctions.subtractPoses(setPose, startPose);
//    odo.setPosition(new Pose2D(DistanceUnit.INCH, setPinpointPose.getX(), setPinpointPose.getY(), AngleUnit.DEGREES, setPinpointPose.getHeading()));
//    }
//
//    @Override
//    public void update() {
//        odo.update();
//    totalHeading += MathFunctions.getSmallestAngleDifference(odo.getHeading(),previousHeading);
//    previousHeading = odo.getHeading();
//    }
//
//    @Override
//    public double getTotalHeading() {
//        return totalHeading;
//    }
//
//    @Override
//    public double getForwardMultiplier() {
//        return odo.getEncoderX();
//    }
//
//    @Override
//    public double getLateralMultiplier() {
//        return odo.getEncoderY();
//    }
//
//    @Override
//    public double getTurningMultiplier() {
//        return odo.getYawScalar();
//    }
//
//    @Override
//    public void resetIMU() {
//    odo.recalibrateIMU();
//    }
//
//    public void resetPinpoint(){
//        odo.resetPosAndIMU();
//    }
//}
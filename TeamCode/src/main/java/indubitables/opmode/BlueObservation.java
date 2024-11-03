package indubitables.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

import indubitables.pedroPathing.follower.Follower;
import indubitables.config.runmodes.Auto;
import indubitables.config.util.action.Actions;
import indubitables.config.util.action.SequentialAction;
import indubitables.pedroPathing.util.Timer;

@Autonomous(name="BlueObservation", group="A")
public class BlueObservation extends OpMode {
    public int pathState;
    public Auto auto;

    public Timer pathTimer = new Timer();

    @Override
    public void init() {
        auto = new Auto(hardwareMap, telemetry, new Follower(hardwareMap), true, false);

        telemetry.addData("state", pathState);
        telemetry.addData("x", auto.follower.getPose().getX());
        telemetry.addData("y", auto.follower.getPose().getY());
        telemetry.addData("h", auto.follower.getPose().getHeading());
        telemetry.addData("actionBusy", auto.actionBusy);
        telemetry.update();
    }

    @Override
    public void start() {
        auto.start();
        setPathState(0);
    }

    @Override
    public void loop() {
        auto.update();
        pathUpdate();

        telemetry.addData("state", pathState);
        telemetry.addData("x", auto.follower.getPose().getX());
        telemetry.addData("y", auto.follower.getPose().getY());
        telemetry.addData("h", auto.follower.getPose().getHeading());
        telemetry.addData("actionBusy", auto.actionBusy);
        telemetry.update();
    }

    public void pathUpdate() {
        switch (pathState) {
            case 0:
                auto.follower.setMaxPower(0.5);
                auto.follower.followPath(auto.preload, true);
                setPathState(1);
                break;
            case 1:
                if(auto.follower.getPose().getX() > auto.preloadPose.getX()) {
                    auto.startChamber();
                    setPathState(2);
                }
                break;
            case 2:
                if(auto.actionNotBusy()) {
                    auto.follower.setMaxPower(0.8);
                    auto.follower.followPath(auto.pushSamples, true);
                    setPathState(3);
                }
                break;
            case 3:
                if(!auto.follower.isBusy()) {
                    auto.follower.setMaxPower(0.5);
                    auto.startSpecimen();
                    setPathState(4);
                }
                break;
            case 4:
                if(auto.actionNotBusy() && !auto.follower.isBusy()) {
                    setPathState(5);
                }
                break;
            case 5:
                    auto.follower.followPath(auto.grab1, true);
                    setPathState(6);
                break;
            case 6:
                if(auto.follower.getPose().getX() < auto.grab1Pose.getX()) {
                    auto.claw.close();
                    setPathState(7);
                }
                break;
            case 7:
                if(!auto.follower.isBusy()) {
                    auto.init();
                    auto.lift.toZero();
                    setPathState(8);
                }
                break;
            case 8:
                if(auto.actionNotBusy()) {
                    auto.lift.rightLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    auto.lift.rightLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    auto.follower.setMaxPower(1);
                    auto.follower.followPath(auto.specimen1, true);
                    setPathState(9);
                }
                break;
            case 9:
                if((auto.follower.getPose().getX() > auto.specimen1Pose.getX()) && (auto.follower.getPose().getY() >= auto.specimen1Pose.getY())) {
                    auto.startChamber();
                    setPathState(11);
                }
                break;
            case 11:
                if(auto.actionNotBusy() && !auto.follower.isBusy()) {
                    auto.lift.toZero();
                    auto.follower.setMaxPower(0.7);
                    auto.follower.followPath(auto.lineUp2);
                    auto.startSpecimen();
                    setPathState(12);
                }
                break;
            case 12:
                if(!auto.follower.isBusy()) {
                    auto.lift.rightLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                    auto.lift.rightLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                    auto.follower.followPath(auto.grab2, true);
                    setPathState(13);
                }
                break;
            case 13:
                if(auto.actionNotBusy() && auto.follower.getPose().getX() < auto.grab2Pose.getX()) {
                    auto.claw.close();
                    setPathState(14);
                }
                break;
            case 14:
                if(pathTimer.getElapsedTimeSeconds() > 0.5)
                    auto.init();
                    setPathState(15);
                break;
            case 15:
                if(auto.actionNotBusy()) {
                    auto.follower.setMaxPower(1);
                    auto.follower.followPath(auto.specimen2, true);
                    setPathState(16);
                }
                break;
            case 16:
                if(auto.follower.getPose().getX() > auto.preloadPose.getX()) {
                    auto.startChamber();
                    setPathState(17);
                }
                break;
            case 17:
                if(auto.actionNotBusy()) {
                    setPathState(-1);
                }
                break;
        }
    }

    public void setPathState(int x) {
        pathState = x;
        pathTimer.resetTimer();
    }
}

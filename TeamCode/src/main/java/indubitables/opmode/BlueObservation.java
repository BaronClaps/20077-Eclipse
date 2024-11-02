package indubitables.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

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
                    auto.follower.followPath(auto.pushSamples);
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
                    auto.follower.followPath(auto.align1, true);
                    setPathState(5);
                }
                break;
            case 5:
                if(auto.follower.getPose().getX() > auto.align1Pose.getX()) {
                    auto.follower.followPath(auto.grab1, true);
                    setPathState(6);
                }
                break;
            case 6:
                if(auto.follower.getPose().getX() < auto.grab1Pose.getX()) {
                    auto.claw.close();
                    setPathState(7);
                }
                break;
            case 7:
                if(auto.actionNotBusy() && !auto.follower.isBusy()) {
                    auto.init();
                    setPathState(8);
                }
                break;
            case 8:
                if(auto.actionNotBusy()) {
                    auto.follower.setMaxPower(1);
                    auto.follower.followPath(auto.specimen1, true);
                    setPathState(9);
                }
            case 9:
                if(auto.follower.getPose().getX() > auto.preloadPose.getX()) {
                    auto.startChamber();
                    setPathState(-1);
                }
                break;
            case 1000000:
                if(!auto.follower.isBusy() && auto.actionNotBusy()) {
                    setPathState(-1);
                }
                break;
            case 100000000:
                if(auto.actionNotBusy() && !auto.follower.isBusy()) {
                    //auto.startTransfer();
                    setPathState(10);
                }
                break;
            case 10:
                if(auto.actionNotBusy() && !auto.follower.isBusy()) {
                    //auto.startBucket();
                    auto.follower.setMaxPower(0.5);
                    auto.follower.followPath(auto.score3);
                    setPathState(11);
                }
                break;
            case 11:
                if(auto.actionNotBusy() && !auto.follower.isBusy()) {
                    //auto.startPark();
                    auto.follower.setMaxPower(0.9);
                    auto.follower.followPath(auto.park);
                    setPathState(-1);
                }
                break;
        }
    }

    public void setPathState(int x) {
        pathState = x;
    }
}

package indubitables.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import indubitables.pedroPathing.follower.Follower;
import indubitables.config.runmodes.Auto;
import indubitables.config.util.action.Actions;
import indubitables.config.util.action.SequentialAction;

@Autonomous(name="BlueObservation", group="A")
public class BlueObservation extends OpMode {
    public int pathState;
    public Auto auto;

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
                auto.follower.setMaxPower(0.7);
                auto.startChamber();
                auto.follower.followPath(auto.preload);
                setPathState(1);
                break;
            case 1: 
                if(!auto.follower.isBusy() && auto.actionNotBusy()) {
                    auto.follower.followPath(auto.pushSamples);
                    setPathState(-1);
                }
                break;
            case 2:
                if(!auto.follower.isBusy()) {
                    //auto.startIntake();
                    setPathState(3);
                }
                break;
            case 3:
                if(/*auto.actionNotBusy() && */!auto.follower.isBusy()) {
                    //auto.startTransfer();
                    setPathState(4);
                }
                break;
            case 4:
                if(auto.actionNotBusy() && !auto.follower.isBusy()) {
                    ///auto.startBucket();
                    auto.follower.setMaxPower(0.5);
                    auto.follower.followPath(auto.score1);
                    setPathState(5);
                }
                break;
            case 5:
                if(!auto.follower.isBusy() && auto.actionNotBusy()) {
                    //auto.startIntake();
                    auto.follower.setMaxPower(0.5);
                    auto.follower.followPath(auto.element2);
                    setPathState(6);
                }
                break;
            case 6:
                if(auto.actionNotBusy() && !auto.follower.isBusy()) {
                    //auto.startTransfer();
                    setPathState(7);
                }
                break;
            case 7:
                if(auto.actionNotBusy() && !auto.follower.isBusy()) {
                    //auto.startBucket();
                    auto.follower.setMaxPower(0.5);
                    auto.follower.followPath(auto.score2);
                    setPathState(8);
                }
                break;
            case 8:
                if(!auto.follower.isBusy() && auto.actionNotBusy()) {
                    //auto.startIntake();
                    auto.follower.setMaxPower(0.5);
                    auto.follower.followPath(auto.element3);
                    setPathState(9);
                }
                break;
            case 9:
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

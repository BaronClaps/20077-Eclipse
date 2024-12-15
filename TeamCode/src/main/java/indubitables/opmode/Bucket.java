package indubitables.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import indubitables.pedroPathing.follower.Follower;
import indubitables.config.runmodes.Auto;
import indubitables.pedroPathing.util.Timer;

@Disabled
@Autonomous(name="Bucket", group="b")
public class Bucket extends OpMode {
    public int pathState;
    public Auto auto;

    public Timer pathTimer = new Timer();

    @Override
    public void init() {
        auto = new Auto(hardwareMap, telemetry, new Follower(hardwareMap), true, false);
    }

    @Override
    public void start() {
        auto.start();
        setPathState(0);
    }

    @Override
    public void loop() {
        telemetry.addData("State: ", pathState);
        telemetry.addData("Path Timer: ", pathTimer.getElapsedTimeSeconds());
        auto.update();
        pathUpdate();
    }

    public void pathUpdate() {
        switch (pathState) {
            case 0: //Runs to the position of the preload and holds it's point at 0.5 power
                auto.liftPIDF = false;
                auto.extend.toZero();
                auto.startChamber();
                setPathState(999);
                break;
            case 999:
                if(pathTimer.getElapsedTimeSeconds() > 0.5) {
                    auto.follower.setMaxPower(1);
                    auto.follower.followPath(auto.preload, false);
                    setPathState(1);
                }
                break;
            case 1:
                if(auto.actionNotBusy()) {
                    auto.outtake.open();
                    auto.follower.followPath(auto.element1);
                    auto.intake.hover();
                    setPathState(2);
                }
                break;
            case 2:
                if(pathTimer.getElapsedTimeSeconds() > 0.25) {
                    auto.outtake.transfer();
                    setPathState(3);
                }
                break;
            case 3:
                if(!auto.follower.isBusy()) {
                    auto.intake.ground();
                    setPathState(4);
                }
                break;
            case 4:
                if(pathTimer.getElapsedTimeSeconds() > 0.25) {
                    auto.startTransfer();
                    setPathState(5);
                }
                break;
            case 5:
                if(pathTimer.getElapsedTimeSeconds() > 0.5) {
                    auto.follower.followPath(auto.score1);
                    setPathState(6);
                }
                break;
            case 6:
                if(auto.actionNotBusy()) {
                    auto.liftPIDF = true;
                    auto.lift.toHighBucket();
                    auto.outtake.score();
                    setPathState(7);
                }
                break;
            case 7:
                if(auto.lift.getPos() >= 1740) {
                    auto.outtake.open();
                    setPathState(-1);
                }
                break;
            case 8:
                if(!auto.follower.isBusy()) {
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

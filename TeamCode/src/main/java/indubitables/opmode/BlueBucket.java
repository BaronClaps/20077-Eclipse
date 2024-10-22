package indubitables.opmode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;

import indubitables.pedroPathing.follower.Follower;
import indubitables.config.runmodes.Auto;
import indubitables.config.util.action.Actions;
import indubitables.config.util.action.SequentialAction;

@Autonomous(name="BlueBucket", group="B")
public class BlueBucket extends OpMode {
    public int pathState;
    public Auto auto;

    @Override
    public void init() {
        auto = new Auto(hardwareMap, telemetry, new Follower(hardwareMap), true, true);
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
    }

    public void pathUpdate() {
        switch (pathState) {
            case 0:
             //   Actions.runBlocking(auto.claw.close);
               // Actions.runBlocking(auto.lift.toHighChamber);
                auto.follower.followPath(auto.preload);
                setPathState(1);
                break;
            case 1:
                if(!auto.follower.isBusy()) {

                    //if (auto.lift.isAtTarget()) {
                   //     Actions.runBlocking(auto.lift.releaseHighChamber);
                   // }

                  //  if (auto.lift.isAtTarget()) {
                   //     Actions.runBlocking(auto.claw.open);
                 //   }

         //           Actions.runBlocking(auto.lift.toZero);
                    auto.follower.followPath(auto.element1);
        //            Actions.runBlocking(auto.intake.pivotGround);
        //            Actions.runBlocking(auto.intake.spinIn);
                    setPathState(2);
                }
                break;
            case 2:
                if(!auto.follower.isBusy()) {
                    auto.follower.followPath(auto.score1);
                 //   Actions.runBlocking(auto.intake.spinStop);
                 //   Actions.runBlocking(auto.intake.pivotTransfer);
                    setPathState(3);
                }
                break;
            case 3:
                if(!auto.follower.isBusy()) {
                //    Actions.runBlocking(
                  //          new SequentialAction(
                    //                auto.transfer,
                      //              auto.lift.toLowBucket
                        //    )
                    //);

                   // if (auto.lift.isAtTarget()) {
                 //       Actions.runBlocking(auto.arm.toScoring);
                //    }

                    auto.follower.followPath(auto.element2);
                    setPathState(4);
                }
                break;
            case 4:
                if(!auto.follower.isBusy()) {
                    auto.follower.followPath(auto.score2);
                    setPathState(5);
                }
                break;
            case 5:
                if(!auto.follower.isBusy()) {
                    auto.follower.followPath(auto.element3);
                    setPathState(6);
                }
                break;
            case 6:
                if(!auto.follower.isBusy()) {
                    auto.follower.followPath(auto.score3);
                    setPathState(7);
                }
                break;
            case 7:
                if(!auto.follower.isBusy()) {
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

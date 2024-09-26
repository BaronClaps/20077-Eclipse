package indubitables.config.subsystem;
import static indubitables.config.util.RobotConstants.*;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;

import indubitables.config.util.RobotConstants;
import indubitables.config.util.action.Actions;
import indubitables.config.util.action.RunAction;


public class ClawSubsystem {

    public enum ClawState {
        CLOSED, OPEN
    }

    private Servo grab;
    private ClawState state;
    public RunAction open, close;

    public ClawSubsystem(HardwareMap hardwareMap, ClawState clawState) {
        grab = hardwareMap.get(Servo.class, "claw");
        this.state = clawState;

        open = new RunAction(this::open);
        close = new RunAction(this::close);
    }

    public void setPos(double clawPos) {
        grab.setPosition(clawPos);
    }

    public void setState(ClawState clawState) {
        if (clawState == ClawState.CLOSED) {
            grab.setPosition(clawClose);
            this.state = ClawState.CLOSED;
        } else if (clawState == ClawState.OPEN) {
            grab.setPosition(clawOpen);
            this.state = ClawState.OPEN;
        }
    }

    public void switchState() {
        if (state == ClawState.CLOSED) {
            setState(ClawState.OPEN);
        } else if (state == ClawState.OPEN) {
            setState(ClawState.CLOSED);
        }
    }

    public void open() {
        setState(ClawState.OPEN);
    }

    public void close() {
        setState(ClawState.CLOSED);
    }

    public void init() {
        Actions.runBlocking(close);
    }

    public void start() {
        Actions.runBlocking(close);
    }



}
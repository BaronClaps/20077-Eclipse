/* Copyright (c) 2021 FIRST. All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification,
 * are permitted (subject to the limitations in the disclaimer below) provided that
 * the following conditions are met:
 *
 * Redistributions of source code must retain the above copyright notice, this list
 * of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice, this
 * list of conditions and the following disclaimer in the documentation and/or
 * other materials provided with the distribution.
 *
 * Neither the name of FIRST nor the names of its contributors may be used to endorse or
 * promote products derived from this software without specific prior written permission.
 *
 * NO EXPRESS OR IMPLIED LICENSES TO ANY PARTY'S PATENT RIGHTS ARE GRANTED BY THIS
 * LICENSE. THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package indubitables.opmode;

import com.acmerobotics.dashboard.config.Config;
import com.arcrobotics.ftclib.controller.PIDController;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;

import indubitables.config.util.action.RunAction;

@Config
@TeleOp(name="PID_Test", group="z")
public class pidTest extends OpMode {
    public DcMotor rightLift, leftLift;
    public static boolean manual = false, baron = true;
    public static int pos, bottom;
    public RunAction toZero, toHighBucket, toHighChamber, toHumanPlayer, toTransfer, toPark;
    public PIDController liftPID;
    public static int target;
    public static double p = 0.03, i = 0, d = 0;
    public static double f = 0.01;
    private Gamepad currentGamepad2 = new Gamepad(), previousGamepad2 = new Gamepad();

    @Override
    public void init() {
        rightLift = hardwareMap.get(DcMotor.class, "rightLift");
        leftLift = hardwareMap.get(DcMotor.class, "leftLift");

        rightLift.setDirection(DcMotor.Direction.FORWARD);
        leftLift.setDirection(DcMotor.Direction.REVERSE);
        rightLift.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        rightLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        leftLift.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);

        bottom = getPos();

        liftPID = new PIDController(p, i, d);
    }

    @Override
    public void loop() {

        if (!baron) {
            if ((gamepad2.right_trigger >= 0.05) || (gamepad2.left_trigger >= 0.05)) {
                manual(gamepad2.right_trigger - (gamepad2.left_trigger * .75));
            } else {
                targetCurrent();
            }
        } else {
            setTarget(target);
        }

        updatePIDF();

        telemetry.addData("manual", manual);
        telemetry.addData("pos", getPos());
        telemetry.addData("baron", baron);
        telemetry.addData("target", target);
        telemetry.addData("P:", p);
        telemetry.addData("I:", i);
        telemetry.addData("D:", d);
        telemetry.addData("F:", f);
        telemetry.update();
    }

    public void updatePIDF(){
        if (!manual) {
            lift.setPID(p,i,d);
            rightLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            leftLift.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
            double pid = liftPID.calculate(getPos(), target);
            double ticks_in_degrees = 537.7 / 360.0;
            double ff = Math.cos(Math.toRadians(target / ticks_in_degrees)) * f;
            double power = pid + ff;

            rightLift.setPower(power);
            leftLift.setPower(power);

            telemetry.addData("lift pos", getPos());
            telemetry.addData("lift target", target);
        }
    }

    public void manual(double n){
        manual = true;

        rightLift.setPower(n);
        leftLift.setPower(n);
    }

    //Util
    public void targetCurrent() {
        setTarget(getPos());
        manual = false;
    }

    public void setTarget(int b) {
        target = b;
    }

    public void addToTarget(int b) {
        target += b;
    }

    public int getPos() {
        pos = rightLift.getCurrentPosition() - bottom;
        return rightLift.getCurrentPosition() - bottom;
    }
}

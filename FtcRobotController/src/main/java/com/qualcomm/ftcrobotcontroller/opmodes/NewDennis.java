package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class NewDennis extends OpMode {

    //drive mode indicator
    boolean tankDrive = false;
    String driveMode;

    //define hardware
    DcMotor left;
    DcMotor right;
    DcMotor ruler;
    DcMotor dispense;
    DcMotor tilt;
    Servo push1;
    Servo push2;
    Servo climbers;

    //value capture
    float leftPower; //left wheel motor 1 power
    float rightPower; //right wheel motor 1 power
    float xValue; //arcade drive turn
    float yValue; //arcade drive power
    float tiltPower;
    boolean push1m = false;
    boolean push2m = false;

    //constants
    final float STOPMOTOR = 0;

    //timer
    long startTime;
    long endTime;
    float intervalTime;

    //constructor (no idea what it does)
    public NewDennis() {

    }

    @Override
    public void init() {

        //make associations with file.conf
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");
        ruler = hardwareMap.dcMotor.get("ruler");
        dispense = hardwareMap.dcMotor.get("dispense");
        tilt = hardwareMap.dcMotor.get("tilt");
        push1 = hardwareMap.servo.get("push1");
        push2 = hardwareMap.servo.get("push2");
        climbers = hardwareMap.servo.get("climbers");

        //reverse one if on opposite sides
        right.setDirection(DcMotor.Direction.REVERSE);

        //servo initial position
        push1.setPosition(0); //default
        push2.setPosition(0.7); //default
        climbers.setPosition(0.8); //default

        //timer
        startTime = System.nanoTime();
    }

    @Override
    public void loop() {

        //timer
        startTime = System.nanoTime();
        endTime = System.nanoTime();
        intervalTime = ((endTime-startTime)/(float)1000000000);

        //change between tank mode and arcade mode (operator 1)
        if(gamepad1.start) {
            if((endTime-startTime)>250000000) {
                leftPower = STOPMOTOR; //prevent value from carrying on from other mode
                rightPower = STOPMOTOR; //prevent value from carrying on from other mode
                if(tankDrive) {
                    tankDrive = false;
                } else if(!tankDrive) {
                    tankDrive = true;
                }
                startTime = System.nanoTime();
            }
        }

        //output mode to telemetry
        if(tankDrive) {
            driveMode = "Tank Mode";
        } else {
            driveMode = "Arcade Mode";
        }

        if(tankDrive) { //code for tank mode
            driveMode = "Tank Mode";
            leftPower = 0;
            rightPower = 0;
            leftPower = -gamepad1.left_stick_y; //get gp1.leftY value
            rightPower = -gamepad1.right_stick_y; //get gp1.rightY value
            leftPower = Range.clip(leftPower, -1, 1); //limit leftY value
            rightPower = Range.clip(rightPower, -1, 1); //limit rightY value

        }else if(!tankDrive) { //code for arcade mode
            driveMode = "Arcade Mode";
            xValue = 0;
            if(gamepad1.dpad_left) {
                xValue = -1;
            }
            if(gamepad1.dpad_right) {
                xValue = 1;
            }
            yValue = 0;
            if(gamepad1.dpad_up) {
                yValue = 1;
            }
            if(gamepad1.dpad_down) {
                yValue = -1;
            }
            leftPower = yValue + xValue; //leftPower = speed + direction (which is negative)
            rightPower = yValue - xValue; //rightPower = speed - direction (which is positive)
            leftPower = Range.clip(leftPower, -1, 1); //limit leftPower value
            rightPower = Range.clip(rightPower, -1, 1); //limit rightPower value
            leftPower = (float)scaleInput(leftPower); //smooth leftPower values (copypasta)
            rightPower = (float)scaleInput(rightPower); //smooth rightPower values (copypasta)
        }

        //output power to motors
        left.setPower(leftPower); //output to leftMotor1
        right.setPower(rightPower); //output to rightMotor1

        //change values from here

        //push (levers)
        if(gamepad1.left_bumper) {
            push1.setPosition(0.5);
        }
        if(gamepad1.left_trigger > 0) {
            push1.setPosition(0);
        }
        if(gamepad1.right_bumper) {
            push2.setPosition(0.0);
        }
        if(gamepad1.right_trigger > 0) {
            push2.setPosition(0.7);
        }

        //climbers
        if(gamepad1.y) {
            climbers.setPosition(0);
        }
        if(gamepad1.a) {
            climbers.setPosition(0.8);
        }

        //dispense
        dispense.setPower(0); //default
        ruler.setPower(0);
        if(-gamepad1.right_stick_y < 0) {
            dispense.setPower(-0.3);
            ruler.setPower(-0.5);
        } else if(-gamepad1.right_stick_y > 0) {
            dispense.setPower(1);
            ruler.setPower(0.3);
        }

        //tilt
        tilt.setPower(0);
        if(-gamepad1.left_stick_y < 0) {
            tiltPower = gamepad1.left_stick_y; //get tiltPower value
            tiltPower = Range.clip(tiltPower, -1, 1); //limit tiltPower value
            tiltPower = (float)scaleInput(tiltPower); //smoothen tiltPower
            tilt.setPower(tiltPower/8);
        }
        if(-gamepad1.left_stick_y > 0) {
            tiltPower = gamepad1.left_stick_y; //get tiltPower value
            tiltPower = Range.clip(tiltPower, -1, 1); //limit tiltPower value
            tiltPower = (float)scaleInput(tiltPower); //smoothen tiltPower
            tilt.setPower(tiltPower/8);
        }

        //telemetry
        telemetry.addData("Left Motor Power", String.format("%.2f", leftPower));
        telemetry.addData("Right Motor Power", String.format("%.2f", rightPower));
        telemetry.addData("Time Elapsed", String.format("%.2f", this.time));
        telemetry.addData("Drive Mode", driveMode);
        telemetry.addData("Timer", String.format("%.3f", intervalTime));
    }

    @Override
    public void stop() {

    }

    //copypasta scaleInput from K9TeleOp.java
    double scaleInput(double dVal)  {
        double[] scaleArray = { 0.0, 0.05, 0.09, 0.10, 0.12, 0.15, 0.18, 0.24,
                0.30, 0.36, 0.43, 0.50, 0.60, 0.72, 0.85, 1.00, 1.00 };

        // get the corresponding index for the scaleInput array.
        int index = (int) (dVal * 16.0);

        // index should be positive.
        if (index < 0) {
            index = -index;
        }

        // index cannot exceed size of array minus 1.
        if (index > 16) {
            index = 16;
        }

        // get value from the array.
        double dScale = 0.0;
        if (dVal < 0) {
            dScale = -scaleArray[index];
        } else {
            dScale = scaleArray[index];
        }

        // return scaled value.
        return dScale;
    }

}
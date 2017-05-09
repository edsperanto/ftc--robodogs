package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class Dennis extends OpMode {

    //drive mode indicator
    boolean tankDrive = false;
    String driveMode;

    //define hardware
    DcMotor left1;
    DcMotor left2;
    DcMotor right1;
    DcMotor right2;
    DcMotor ruler;
    Servo push1;
    Servo push2;
    Servo tilt;
    Servo guard1;
    Servo guard2;

    //value capture
    float leftPower1; //left wheel motor 1 power
    float rightPower1; //right wheel motor 1 power
    float leftPower2; //left wheel motor 1 power
    float rightPower2; //right wheel motor 1 power
    float xValue; //arcade drive turn
    float yValue; //arcade drive power

    //constants
    final float STOPMOTOR = 0;

    //timer
    long startTime;
    long endTime;
    float intervalTime;

    //constructor (no idea what it does)
    public Dennis() {

    }

    @Override
    public void init() {

        //make associations with file.conf
        left1 = hardwareMap.dcMotor.get("left1");
        left2 = hardwareMap.dcMotor.get("left2");
        right1 = hardwareMap.dcMotor.get("right1");
        right2 = hardwareMap.dcMotor.get("right2");
        ruler = hardwareMap.dcMotor.get("ruler");
        push1 = hardwareMap.servo.get("push1");
        push2 = hardwareMap.servo.get("push2");
        tilt = hardwareMap.servo.get("tilt");
        guard1 = hardwareMap.servo.get("guard1");
        guard2 = hardwareMap.servo.get("guard2");

        //reverse one if on opposite sides
        right1.setDirection(DcMotor.Direction.REVERSE);
        right2.setDirection(DcMotor.Direction.REVERSE);

        //servo initial position
        push1.setPosition(0.5);
        push2.setPosition(0.5);
        tilt.setPosition(0);
        guard1.setPosition(0.5);
        guard2.setPosition(0.5);

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
                leftPower1 = STOPMOTOR; //prevent value from carrying on from other mode
                rightPower1 = STOPMOTOR; //prevent value from carrying on from other mode
                if(tankDrive) {
                    tankDrive = false;
                } else if(!tankDrive) {
                    tankDrive = true;
                }
                startTime = System.nanoTime();
            }
        }

        //change front direction (operator 1)
        if(gamepad1.y) {
            if((endTime-startTime)>250000000) {
                leftPower1 = STOPMOTOR; //prevent value from carrying on from other mode
                rightPower1 = STOPMOTOR; //prevent value from carrying on from other mode
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
            leftPower1 = 0;
            rightPower1 = 0;
            leftPower1 = -gamepad1.left_stick_y; //get gp1.leftY value
            rightPower1 = -gamepad1.right_stick_y; //get gp1.rightY value
            leftPower1 = Range.clip(leftPower1, -1, 1); //limit leftY value
            rightPower1 = Range.clip(rightPower1, -1, 1); //limit rightY value

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
            leftPower1 = yValue + xValue; //leftPower = speed + direction (which is negative)
            rightPower1 = yValue - xValue; //rightPower = speed - direction (which is positive)
            leftPower1 = Range.clip(leftPower1, -1, 1); //limit leftPower value
            rightPower1 = Range.clip(rightPower1, -1, 1); //limit rightPower value
            leftPower1 = (float)scaleInput(leftPower1); //smooth leftPower values (copypasta)
            rightPower1 = (float)scaleInput(rightPower1); //smooth rightPower values (copypasta)
        }

        //output power to motors
        leftPower2 = -(float)left1.getPower(); //set leftMotor2 power to opposite
        rightPower2 = -(float)right1.getPower(); //set rightMotor2 power to opposite
        left1.setPower(leftPower1); //output to leftMotor1
        right1.setPower(rightPower1); //output to rightMotor1
        left2.setPower(leftPower2); //output to leftMotor2
        right2.setPower(rightPower2); //output to rightMotor2

        //change values from here

        //push (levers)
        push1.setPosition(0); //default
        push2.setPosition(0.7); //default
        if(gamepad1.left_bumper) {
            push1.setPosition(0.5);
        }
        if(gamepad1.right_bumper) {
            push2.setPosition(0.0);
        }

        //control tilt (for ruler)
        tilt.setPosition(0.5); //default
        if(gamepad1.y) {
            tilt.setPosition(0.25);
        } else if(gamepad1.a) {
            tilt.setPosition(0.75);
        }

        //guards (1=front, 2=back)
        guard1.setPosition(0.5); //default
        guard2.setPosition(0.5); //default
        if(gamepad1.x) {
            guard1.setPosition(1);
            guard2.setPosition(1);
        }

        //ruler (motor)
        if(-gamepad1.left_stick_y > 0) {
            ruler.setPower(-0.5);
        } else if(-gamepad1.left_stick_y < 0) {
            ruler.setPower(0.5);
        }

        //telemetry
        telemetry.addData("Left Motor Power", String.format("%.2f", leftPower1));
        telemetry.addData("Right Motor Power", String.format("%.2f", rightPower1));
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
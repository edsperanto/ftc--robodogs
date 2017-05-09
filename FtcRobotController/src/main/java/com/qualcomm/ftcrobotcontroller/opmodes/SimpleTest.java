package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class SimpleTest extends OpMode {

    //drive mode indicator
    boolean tankDrive = false;

    //define hardware
    DcMotor leftMotor;
    DcMotor rightMotor;
    Servo tiltServo;
    DcMotor brush;
    DcMotor arm;

    //value capture
    float leftPower; //left wheel power
    float rightPower; //right wheel power
    float xValue; //arcade drive turn
    float yValue; //arcade drive power

    //constants
    final float STOPMOTOR = 0;

    //timer
    long startTime;
    long endTime;
    float intervalTime;

    //constructor (no idea what it does)
    public SimpleTest() {

    }

    @Override
    public void init() {

        //make associations with file.conf
        leftMotor = hardwareMap.dcMotor.get("left_drive");
        rightMotor = hardwareMap.dcMotor.get("right_drive");
        tiltServo = hardwareMap.servo.get("tilt_servo");
        brush = hardwareMap.dcMotor.get("brush");
        arm = hardwareMap.dcMotor.get("arm");

        //reverse one if on opposite sides
        rightMotor.setDirection(DcMotor.Direction.REVERSE);

        //timer
        startTime = System.nanoTime();
    }

    @Override
    public void loop() {

        //timer
        endTime = System.nanoTime();
        intervalTime = ((endTime-startTime)/(float)1000000000);

        //change between tank mode and arcade mode (operator 1)
        if(gamepad1.start) {
            if((endTime-startTime)>125000000) {
                if (tankDrive) {
                    leftPower = STOPMOTOR; //prevent value from carrying on from other mode
                    rightPower = STOPMOTOR; //prevent value from carrying on from other mode
                    tankDrive = false;
                } else if (!tankDrive) {
                    leftPower = STOPMOTOR; //prevent value from carrying on from other mode
                    rightPower = STOPMOTOR; //prevent value from carrying on from other mode
                    tankDrive = true;
                }
                startTime = System.nanoTime();
            }
        }

        //basic drive & brake (operator 1)
        if(tankDrive) { //code for tank mode
            leftPower = -gamepad1.left_stick_y; //get gp1.leftY value
            rightPower = -gamepad1.right_stick_y; //get gp1.rightY value
            leftPower = Range.clip(leftPower, -1, 1); //limit leftY value
            rightPower = Range.clip(rightPower, -1, 1); //limit rightY value
            leftMotor.setPower(leftPower); //output to leftMotor
            rightMotor.setPower(rightPower); //output to rightMotor
        }else if(!tankDrive) { //code for arcade mode
            xValue = gamepad1.left_stick_x; //get gp1.xValue
            yValue = -gamepad1.right_stick_y; //get gp1.yValue
            leftPower = yValue + xValue; //leftPower = speed + direction (which is negative)
            rightPower = yValue - xValue; //rightPower = speed - direction (which is positive)
            leftPower = Range.clip(leftPower, -1, 1); //limit leftPower value
            rightPower = Range.clip(rightPower, -1, 1); //limit rightPower value
            leftPower = (float)scaleInput(leftPower); //smooth leftPower values (copypasta)
            rightPower = (float)scaleInput(rightPower); //smooth rightPower values (copypasta)
            leftMotor.setPower(leftPower); //output to leftMotor
            rightMotor.setPower(rightPower); //output to rightMotor
        }

        //control tilt (operator 1)
        tiltServo.setPosition(0.5);
        if(gamepad1.dpad_right) {
            tiltServo.setPosition(0.0);
        }
        if(gamepad1.dpad_left) {
            tiltServo.setPosition(1.0);
        }

        //control brush (operator 2)
        brush.setPower(0); //default
        if(gamepad1.a) {
            brush.setPower(1); //if press "a"
        }
        if(gamepad1.b) {
            brush.setPower(-1); //if press "b"
        }

        //control arm (operator 2)
        arm.setPower(0.0); //default
        if(gamepad1.x) {
            arm.setPower(0.2); //if press "x"
        }
        if(gamepad1.y) {
            arm.setPower(-0.2); //if press "y"
        }

        //telemetry
        telemetry.addData("Left Motor Power", String.format("%.2f", leftPower));
        telemetry.addData("Right Motor Power", String.format("%.2f", rightPower));
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
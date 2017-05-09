package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class NewDennisAuto extends OpMode {

    //define hardware
    DcMotor left;
    DcMotor right;
    Servo climbers;

    //value capture
    float leftPower; //left wheel motor 1 power
    float rightPower; //right wheel motor 1 power
    float xValue; //arcade drive turn
    float yValue; //arcade drive power

    //constructor (no idea what it does)
    public NewDennisAuto() {

    }

    @Override
    public void init() {

        //make associations with file.conf
        left = hardwareMap.dcMotor.get("left");
        right = hardwareMap.dcMotor.get("right");
        climbers = hardwareMap.servo.get("climbers");

        //reverse one if on opposite sides
        right.setDirection(DcMotor.Direction.REVERSE);

        //servo initial position
        climbers.setPosition(0.8); //default
    }

    @Override
    public void loop() {

        leftPower = yValue + xValue; //leftPower = speed + direction (which is negative)
        rightPower = yValue - xValue; //rightPower = speed - direction (which is positive)
        leftPower = Range.clip(leftPower, -1, 1); //limit leftPower value
        rightPower = Range.clip(rightPower, -1, 1); //limit rightPower value

        //output power to motors
        left.setPower(leftPower); //output to leftMotor1
        right.setPower(rightPower); //output to rightMotor1

        //autonomous
        if(this.time <= 2.5) {
            xValue = 0;
            yValue = 0;
        } else if(this.time > 2.5 && this.time <= 6.5) {
            xValue = 0;
            yValue = (float) -0.4;
        } else if(this.time > 6.5 && this.time <= 7) {
            yValue = (float) 0.4;
        } else if(this.time > 7 && this.time <= 7.5) {
            xValue = (float) 0.5;
            yValue = (float) -0.4;
        } else if(this.time > 7.5 && this.time <= 9) {
            xValue = 0;
            yValue = 0;
        } else if(this.time > 9 && this.time <= 11) {
            xValue = 0;
            yValue = (float)-0.4;
        } else if(this.time > 11 && this.time <= 13) {
            yValue = 0;
            climbers.setPosition(0);
        } else if(this.time > 13 && this.time <= 15) {
            climbers.setPosition(0.8);
        } else if(this.time > 15 && this.time <= 15.5) {
            yValue = (float)0.4;
        } else if(this.time > 15.5 && this.time <= 17.5) {
            yValue = 0;
            climbers.setPosition(0);
        } else if(this.time > 17.5 && this.time <= 20) {
            yValue = (float)-0.3;
        } else {
            yValue = 0;
        }

        //telemetry
        telemetry.addData("Left Motor Power", String.format("%.2f", leftPower));
        telemetry.addData("Right Motor Power", String.format("%.2f", rightPower));
        telemetry.addData("Time Elapsed", String.format("%.2f", this.time));
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
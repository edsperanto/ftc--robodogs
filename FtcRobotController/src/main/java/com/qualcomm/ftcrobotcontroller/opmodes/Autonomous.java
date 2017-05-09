package com.qualcomm.ftcrobotcontroller.opmodes;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

public class Autonomous extends OpMode {

    //define hardware
    DcMotor leftMotor1;
    DcMotor leftMotor2;
    DcMotor rightMotor1;
    DcMotor rightMotor2;
    DcMotor brush;

    //value capture
    float leftPower1; //left wheel motor 1 power
    float rightPower1; //right wheel motor 1 power
    float leftPower2; //left wheel motor 1 power
    float rightPower2; //right wheel motor 1 power
    float xValue; //arcade drive turn
    float yValue; //arcade drive power

    //constants
    final float STOPMOTOR = 0;

    //constructor (no idea what it does)
    public Autonomous() {

    }

    @Override
    public void init() {

        //make associations with file.conf
        leftMotor1 = hardwareMap.dcMotor.get("leftMotor1");
        leftMotor2 = hardwareMap.dcMotor.get("leftMotor2");
        rightMotor1 = hardwareMap.dcMotor.get("rightMotor1");
        rightMotor2 = hardwareMap.dcMotor.get("rightMotor2");
        brush = hardwareMap.dcMotor.get("brush");

        //reverse one if on opposite sides
        rightMotor1.setDirection(DcMotor.Direction.REVERSE);
        rightMotor2.setDirection(DcMotor.Direction.REVERSE);
    }

    @Override
    public void loop() {

        //Arcade mode
        //xValue = 0;
        //yValue = 0;
        leftPower1 = yValue + xValue; //leftPower = speed + direction (which is negative)
        rightPower1 = yValue - xValue; //rightPower = speed - direction (which is positive)
        leftPower1 = Range.clip(leftPower1, -1, 1); //limit leftPower value
        rightPower1 = Range.clip(rightPower1, -1, 1); //limit rightPower value
        leftPower1 = (float)scaleInput(leftPower1); //smooth leftPower values (copypasta)
        rightPower1 = (float)scaleInput(rightPower1); //smooth rightPower values (copypasta)
        rightPower1 = rightPower1 * (float)0.57; //fix turn

        //output power to motors
        leftPower2 = -(float)leftMotor1.getPower(); //set leftMotor2 power to opposite
        rightPower2 = -(float)rightMotor1.getPower(); //set rightMotor2 power to opposite
        leftMotor1.setPower(leftPower1); //output to leftMotor1
        rightMotor1.setPower(rightPower1); //output to rightMotor1
        leftMotor2.setPower(leftPower2); //output to leftMotor2
        rightMotor2.setPower(rightPower2); //output to rightMotor2

        //control brush
        brush.setPower(1); //default

        //time
        if(this.time <= 2) {
            xValue = 0;
            yValue = 0;
        } else if(this.time > 2 && this.time <= 3) {
            xValue = 0;
            yValue = (float)1;
        } else if(this.time > 3 && this.time <= 5) {
            xValue = 0;
            yValue = 0;
        } else if(this.time > 5 && this.time <= 7) {
            xValue = 0;
            yValue = (float)1;
        }else if(this.time > 7 && this.time <= 9) {
            xValue = 0;
            yValue = 0;
        }else if(this.time > 9 && this.time <= 12) {
            xValue = 0;
            yValue = (float)1;
        }else if(this.time > 12 && this.time <= 14) {
            xValue = 0;
            yValue = 0;
        }else if(this.time > 14 && this.time <= 15) {
            xValue = -(float)1; //turn left 1 sec
            yValue = 0;
        }else if(this.time > 15 && this.time <= 17) {
            xValue = 0;
            yValue = 0;
        }else if(this.time > 17 && this.time <= 19) {
            xValue = -(float)1; //turn left 2 sec
            yValue = 0;
        }else if(this.time > 19 && this.time <= 21) {
            xValue = 0;
            yValue = 0;
        }else if(this.time > 21 && this.time <= 22) {
            xValue = (float)1; //turn right 1 sec
            yValue = 0;
        }else if(this.time > 22 && this.time <= 24) {
            xValue = 0;
            yValue = 0;
        }else if(this.time > 24 && this.time <= 26) {
            xValue = (float)1; //turn right 2 sec
            yValue = 0;
        }else {
            xValue = 0;
            yValue = 0;
        }

        //telemetry
        telemetry.addData("Left Motor Power", String.format("%.2f", leftPower1));
        telemetry.addData("Right Motor Power", String.format("%.2f", rightPower1));
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
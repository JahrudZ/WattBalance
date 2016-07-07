package com.jahrud;

/*
 * Copyright 2011 Phidgets Inc.  All rights reserved.
 */

import com.phidgets.AnalogPhidget;
import com.phidgets.PhidgetException;
import com.phidgets.event.ErrorListener;
import jssc.SerialPortException;

public class Main {

    public int buffer = 0;
    public int balancePoint = 85;
    public int finishCounter = 0;
    public boolean lastWasSame = false;
    String numberOnly;

    public static void main(String[] args) throws InterruptedException {

        Main main = new Main();

        ErrorListener ErrorListener = null;

        try {
            AnalogPhidget ap = new AnalogPhidget();

            ap.openAny();
            System.out.println("Waiting for the Phidget Analog to be attached...");
            ap.waitForAttachment();
            System.out.println("Found Phidget Analog 1002");

            for (int i = 0; i < ap.getOutputCount(); i++) {
                ap.setEnabled(i, true);
            }

            Thread.sleep(100);

            SerialCom comPort = new SerialCom();
            comPort.selectPort();

            //this is where it went
            ap.setVoltage(3, 3);
            //ap.setVoltage(0, 0);
            ap.setVoltage(0, 10);

            Thread.sleep(3000);

            ap.setVoltage(0, 5);

            Thread.sleep(100);

            boolean running = true;
            while(running){
                main.numberOnly = comPort.extractedString.replaceAll("[^0-9]", "");

                int analogValue = Integer.parseInt(main.numberOnly);
                System.out.println(analogValue + " " + ap.getVoltage(0));
                if(analogValue > 1023){
                    analogValue = 1023;
                }

                double newSet = ap.getVoltage(0);
                double deltaVoltage = 0.003;
                if(analogValue > main.balancePoint + main.buffer){
                    newSet =  ap.getVoltage(0) - deltaVoltage;
                    main.lastWasSame = false;
                } else if(analogValue < main.balancePoint - main.buffer){
                    newSet = ap.getVoltage(0) + deltaVoltage;
                    main.lastWasSame = false;
                } else {
                    if(!main.lastWasSame){
                        main.finishCounter = 0;
                    }
                    main.finishCounter++;
                    main.lastWasSame = true;
                    if(main.finishCounter > 20){
                        running = false;
                    }
                }
                if(newSet > 10){
                    newSet = 10;
                } else if(newSet < -10){
                    newSet = -10;
                }
                ap.setVoltage(0, newSet);

                int waitTime = 200;

                Thread.sleep(waitTime);
            }

            //closing
            ap.setVoltage(0, 0);
            for (int i = 0; i < ap.getOutputCount() - 1; i++) {
                ap.setEnabled(i, false);
            }

            Thread.sleep(100);

            ap.removeErrorListener(ErrorListener);
            ap.close();

            try {
                comPort.serialPort.closePort();
            } catch (SerialPortException e) {
                e.printStackTrace();
            }

        } catch (PhidgetException ex) {
            System.out.println("Exception: " + "Phidget Error: " + ex.getDescription());
        }
    }

    public static void calibratePoint(double mass){

    }

    public static double measure(){
        double mass = 0;
        return mass;
    }
}

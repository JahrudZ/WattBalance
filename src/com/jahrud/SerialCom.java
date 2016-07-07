package com.jahrud; /**
 * Created by JahrudZ on 6/20/16.
 */
import jssc.*;

public class SerialCom {

    SerialPort serialPort;
    String readString = "";
    String extractedString = "";

    public SerialCom(){

    }

    public String selectPort(){
        String[] portNames = SerialPortList.getPortNames();
        System.out.println(portNames.length + " serial port(s) found:");
        for(int i = 0; i > -1; i++){
            int j = i % portNames.length;
            sleep(40);
            System.out.println(portNames[j]);
            openPort(portNames[j]);
            System.out.println("Opened Port");
            sleep(3000);
            String ID = extractedString;
            if(ID.contains(">")){
                System.out.println("Success! Connected to " + portNames[j]);
                return portNames[j];
            } else {
                try {
                    System.out.println("Closed Port");
                    serialPort.closePort();
                } catch (SerialPortException e) {
                    e.printStackTrace();
                }
            }
        }
        return "NONE";
    }

    public void openPort(String port){
        serialPort = new SerialPort(port);
        try {
            serialPort.openPort();
            serialPort.setParams(9600, 8, 1, 0, false, true);
            serialPort.setFlowControlMode(SerialPort.FLOWCONTROL_RTSCTS_IN |
                    SerialPort.FLOWCONTROL_RTSCTS_OUT);
            serialPort.addEventListener(new PortReader(), SerialPort.MASK_RXCHAR);
        } catch (SerialPortException e) {
            e.printStackTrace();
        }
    }

    private void sleep(int millis){
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private class PortReader implements SerialPortEventListener {
        @Override
        public void serialEvent(SerialPortEvent event) {
            //System.out.println("Recieved Event");
            if(event.isRXCHAR() && event.getEventValue() > 0) {
                try {
                    String receivedData = serialPort.readString(event.getEventValue());
                    //System.out.println("RecievedString: " + receivedData);
                    readString = readString.concat(receivedData);
                    //System.out.println("ReadString: " + readString);
                    if(readString.contains(">") && readString.contains("<")){
                        extractedString = readString.substring(readString.indexOf(">"), readString.indexOf("<"));
                        //System.out.print(extractedString + " ");
                        //readString.replace(extractedString, "");
                        readString = "";
                    }
                }
                catch (SerialPortException ex) {
                    System.out.println("Error in receiving string from COM-port: " + ex);
                }
            }
        }
    }
}

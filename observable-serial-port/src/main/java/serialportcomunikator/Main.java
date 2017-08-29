package serialportcomunikator;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

import java.io.*;
import java.util.Enumeration;

/**
 * Created by root on 9.7.2017.
 */
public class Main implements SerialPortEventListener {

    SerialPort serialPort;
    private ByteArrayOutputStream bout;
    private OutputStreamWriter writer;
    ObjectMapper mapper = new ObjectMapper();

    /**
     * The port we're normally going to use.
     */
    private static final String PORT_NAMES[] = {
            "/dev/tty.usbserial-A9007UX1", // Mac OS X
            "/dev/ttyACM0", // Raspberry Pi
            "/dev/ttyUSB0", // Linux
            "COM3", // Windows
    };
    /**
     * A BufferedReader which will be fed by a InputStreamReader
     * converting the bytes into characters
     * making the displayed results codepage independent
     */
    private BufferedReader input;
    /**
     * The output stream to the port
     */
    private OutputStream output;
    /**
     * Milliseconds to block while waiting for port open
     */
    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 9600;

    public void initialize() {

        // the next line is for Raspberry Pi and
        // gets us into the while loop and was suggested here was suggested http://www.raspberrypi.org/phpBB3/viewtopic.php?f=81&t=32186
        // System.setProperty("gnu.io.rxtx.SerialPorts", "/dev/ttyUSB0");

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : PORT_NAMES) {
                if (currPortId.getName().equals(portName)) {
                    portId = currPortId;
                    break;
                }
            }
        }
        if (portId == null) {
            System.out.println("Could not find COM port.");
            return;
        }

        try {
            // open serial port, and use class name for the appName.
            serialPort = (SerialPort) portId.open(this.getClass().getName(),
                    TIME_OUT);

            // set port parameters
            serialPort.setSerialPortParams(DATA_RATE,
                    SerialPort.DATABITS_8,
                    SerialPort.STOPBITS_1,
                    SerialPort.PARITY_NONE);

            // open the streams
            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
            output = serialPort.getOutputStream();
            bout = new ByteArrayOutputStream();
            writer = new OutputStreamWriter(output);


            // add event listeners
            serialPort.addEventListener(this);
            serialPort.notifyOnDataAvailable(true);


            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    while (true) {
                        try {
                            Thread.sleep(5000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        ArduinoRequestMessage arduinoRequestMessage = new ArduinoRequestMessage();
                        arduinoRequestMessage.setPower(100);

                        try {
                            writeToArduino(mapper.writeValueAsString(arduinoRequestMessage) + "\n");
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }
                }
            };
            runnable.run();

        } catch (Exception e) {
            System.err.println(e.toString());
        }
    }

    /**
     * This should be called when you stop using the port.
     * This will prevent port locking on platforms like Linux.
     */
    public synchronized void close() {
        if (serialPort != null) {
            serialPort.removeEventListener();
            serialPort.close();
        }
    }

    private void writeToArduino(final String message) {
        try {
            String command = message;
            writer.write(command, 0, command.length());
            writer.flush();
            bout.writeTo(output);
            bout.reset();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handle an event on the serial port. Read the data and print it.
     */
    @Override
    public synchronized void serialEvent(SerialPortEvent oEvent) {
        if (oEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                ArduinoResponseMessage arduinoResponseMessage = mapper.readValue(inputLine, ArduinoResponseMessage.class);
                System.out.println(arduinoResponseMessage);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        Main main = new Main();
        main.initialize();

        Thread t = new Thread() {
            public void run() {
                //the following line will keep this app alive for 1000 seconds,
                //waiting for events to occur and responding to them (printing incoming messages to console).
                try {
                    Thread.sleep(1000000);
                } catch (InterruptedException ie) {
                }
            }
        };
        t.start();
        System.out.println("Started");
    }

}


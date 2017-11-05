package arduinoclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;

/**
 * Created by root on 23.7.2017.
 */
public class ArduinoObservableSerialPortClient extends Observable implements ArduinoClient<ArduinoRequestMessage, ArduinoResponseMessage>, SerialPortEventListener {
    private static final String PROPERTY_FILE_NAME = "arduinoclient.properties";
    private static Logger log = LoggerFactory.getLogger(ArduinoSerialPortClient.class);
    private static List<String> portNames;
    private static final List<String> DEFAULT_PORT_NAMES = Arrays.asList(
            "/dev/tty.usbserial-A9007UX1",
            "/dev/ttyACM0",
            "/dev/ttyUSB0",
            "COM3");

    static {
        portNames = SerialPortProperties
                .createPropertyLoader("arduinoclient.properties")
                .getPortNameList();
    }


    SerialPort serialPort;
    private ByteArrayOutputStream bout;
    private OutputStreamWriter writer;
    ObjectMapper mapper = new ObjectMapper();
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

    private ArduinoResponseMessage arduinoResponseMessage;

    public ArduinoObservableSerialPortClient(SerialPort serialPort) {
        initialize();
    }

    public ArduinoObservableSerialPortClient() {
        initialize();
    }

    public void initialize() {

        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in portNames.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : portNames) {
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

        } catch (Exception e) {
            System.err.println(e.toString());
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

    @Override
    public ArduinoResponseMessage getData() {
        return arduinoResponseMessage;
    }

    public void setData(ArduinoResponseMessage arduinoResponseMessage) {
        this.arduinoResponseMessage = arduinoResponseMessage;
        setChanged();

    }


    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                String inputLine = input.readLine();
                arduinoResponseMessage = mapper.readValue(inputLine, ArduinoResponseMessage.class);
                System.out.println(arduinoResponseMessage);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
        setChanged();
        notifyObservers();
    }


    @Override
    public void postData(ArduinoRequestMessage arduinoRequestMessage) {

    }
}

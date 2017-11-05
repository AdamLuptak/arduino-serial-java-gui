package arduinoclient;

import com.fasterxml.jackson.databind.ObjectMapper;
import gnu.io.CommPortIdentifier;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.Enumeration;


/**
 * Created by root on 14.10.2017.
 */
public class ArduinoSerialPortClient implements ArduinoClient<ArduinoRequestMessage, ArduinoResponseMessage>, SerialPortEventListener {
    private static Logger log = LoggerFactory.getLogger(ArduinoSerialPortClient.class);

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

    private OutputStream output;

    private static final int TIME_OUT = 2000;
    /**
     * Default bits per second for COM port.
     */
    private static final int DATA_RATE = 9600;

    private ArduinoResponseMessage arduinoResponseMessage;

    public ArduinoSerialPortClient(SerialPort serialPort) {
        initialize();
    }

    public ArduinoSerialPortClient() {
        initialize();
    }

    public void initialize() {
        SerialPortProperties propertyLoader = SerialPortProperties.createPropertyLoader("");


        CommPortIdentifier portId = null;
        Enumeration portEnum = CommPortIdentifier.getPortIdentifiers();

        //First, Find an instance of serial port as set in PORT_NAMES.
        while (portEnum.hasMoreElements()) {
            CommPortIdentifier currPortId = (CommPortIdentifier) portEnum.nextElement();
            for (String portName : propertyLoader.getPortNameList()) {
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
    }

    @Override
    public void serialEvent(SerialPortEvent serialPortEvent) {
        if (serialPortEvent.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try{
                String inputLine = input.readLine();
                arduinoResponseMessage = mapper.readValue(inputLine, ArduinoResponseMessage.class);
                System.out.println(arduinoResponseMessage);
            } catch (Exception e) {
                System.err.println(e.toString());
            }
        }
    }

    @Override
    public void postData(ArduinoRequestMessage arduinoRequestMessage) {

    }
}

package arduinoclient;

import lombok.Data;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.*;

/**
 * Created by root on 15.10.2017.
 */
public
@Data
class SerialPortProperties {
    public static final int DEFAULT_BAUD_RATE = 9600;
    public static final int DEFAULT_PORT_TIMEOUT = 2000;
    private static Logger log = LoggerFactory.getLogger(ArduinoSerialPortClient.class);
    private List<String> portNameList;
    private Integer portBaudRate;
    private Integer portTimeout;
    private static final List<String> DEFAULT_PORT_NAME_LIST = Arrays.asList(
            "/dev/tty.usbserial-A9007UX1",
            "/dev/ttyACM0",
            "/dev/ttyUSB0",
            "COM3");

    private SerialPortProperties(String propertyFileName) {
        init(propertyFileName);
    }

    public static SerialPortProperties createPropertyLoader(String propertyFileName) {
        return new SerialPortProperties(propertyFileName);
    }

    private void init(String propertyFileName) {
        if (propertyFileName.isEmpty()) {
            portNameList = DEFAULT_PORT_NAME_LIST;
            portBaudRate = DEFAULT_BAUD_RATE;
            portTimeout = DEFAULT_PORT_TIMEOUT;
        } else {
            Properties prop = loadPropertiesFromFile(propertyFileName);
            portNameList = PropertyValueToPortNameList(prop.getProperty("port.names"));
            portTimeout = ifNullReturnDefaultInt(prop.getProperty("port.timout"), DEFAULT_PORT_TIMEOUT);
            portBaudRate = ifNullReturnDefaultInt(prop.getProperty("port.baudrate"), DEFAULT_BAUD_RATE);
            log.info(prop.toString());
        }

    }

    private Integer ifNullReturnDefaultInt(String property, int defaultValue) {
        return null;
    }

    private Properties loadPropertiesFromFile(String propertyFileName) {
        Properties prop = new Properties();
        try (InputStream stream = this.getClass().getResourceAsStream(propertyFileName)) {
            prop.load(stream);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            log.error("Unable to load properties from file {}", propertyFileName);
        }
        return prop;
    }

    private List<String> PropertyValueToPortNameList(String portNames) {
        portNameList = Arrays.asList(portNames.split(","));
        if (portNameList.isEmpty()) {
            log.info("Cannot load properties from file using default properties for serial port");
            return DEFAULT_PORT_NAME_LIST;
        }
        return portNameList;
    }

}

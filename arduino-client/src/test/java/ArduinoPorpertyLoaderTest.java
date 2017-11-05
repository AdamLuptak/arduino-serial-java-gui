import arduinoclient.SerialPortProperties;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by root on 15.10.2017.
 */
public class ArduinoPorpertyLoaderTest {
    private static final String PROPERTY_FILE_NAME = "arduinoclient.properties";
    public static final List<String> EXPECTED_SERIAL_PORT_NAME_LIST = Arrays.asList(
            "/dev/tty.usbserial-A9007UX1",
            "/dev/ttyACM0",
            "/dev/ttyUSB0",
            "COM3");
    public static final String NOT_EXISTING_PROPERTY_FILE = "not_existing_property_file";

    @Test
    public void loadPortNameListFromProperties() throws Exception {
        SerialPortProperties serialPortProperties = SerialPortProperties.createPropertyLoader(PROPERTY_FILE_NAME);
        List<String> actualSerialPortNameList = serialPortProperties.getPortNameList();
        assertEquals(EXPECTED_SERIAL_PORT_NAME_LIST, actualSerialPortNameList);
    }


    @Test
    public void noPropertyFile() throws Exception {
        SerialPortProperties serialPortProperties = SerialPortProperties.createPropertyLoader(NOT_EXISTING_PROPERTY_FILE);
        Integer actualPortBaudRate = serialPortProperties.getPortBaudRate();
        Integer actualPortTimeout = serialPortProperties.getPortTimeout();
        List<String> actualPortNameList = serialPortProperties.getPortNameList();
        assertEquals(EXPECTED_SERIAL_PORT_NAME_LIST, actualPortNameList);
        assertTrue(actualPortTimeout == 2000);
        assertTrue(actualPortBaudRate == 9600);

    }

    @Test
    public void name() throws Exception {
        String json = "{\n" +
                "    \"CisloFaktury\": \"70/2016\",\n" +
                "    \"FakturaInternal\": \"70/2016\",\n" +
                "    \"KuZmluve\": \"\",\n" +
                "    \"SumaCelkom\": 225,\n" +
                "    \"DatumVystavenia\": \"2016-07-01\",\n" +
                "    \"DatumSplatnosti\": \"2016-07-15\",\n" +
                "    \"DatumUhrady\": \"2016-07-06\",\n" +
                "    \"Predmet\": \"Syst?mov? podpora URBIS 01.07.2016-30.09.2016\",\n" +
                "    \"DodavatelNazov\": \"MADE spol. s.r.o.\",\n" +
                "    \"AdresaDodavatel\":{ \"Obec\": \"Bansk? Bystrica\", \"Psc\": \"974 01\", \"Ulica\": \"Lazorov?\", \"PopisneCislo\": \"69\"},\n" +
                "    \"DodavatelIco\": \"36041688\",\n" +
                "    \"OdoberatelNazov\": \"Mestsk? ?as? Ko?ice - Pere?\",\n" +
                "    \"AdresaObjednavatel\": {\n" +
                "      \"Ulica\": \"Krompa?sk? 54\",\n" +
                "      \"PopisneCislo\": \"54\",\n" +
                "      \"Psc\": \"040 11\",\n" +
                "      \"Obec\": \"Ko?ice\"\n" +
                "    },\n" +
                "    \"OdoberatelIco\": \"00690953\",\n" +
                "    \"Poznamka\": \"\",\n" +
                "    \"SposobUhrady\": \"prevodom\"\n" +
                "  }";

    }
}

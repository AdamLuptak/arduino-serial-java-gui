import javafx.fxml.FXML;
import serialportcomunikator.ArduinoProxy;
import serialportcomunikator.ArduinoResponseMessage;
import serialportcomunikator.ArduinoSerialPortProxy;

import javax.swing.*;
import java.util.Observable;
import java.util.Observer;


public class Controller implements Observer {

    ArduinoResponseMessage arduinoResponseMessage;
    Timer timer;

    @FXML
    public void initialize() {
        System.out.println("Initialize controller");
        arduinoProxy = new ArduinoSerialPortProxy();
        arduinoProxy.initialize();
        arduinoProxy.addObserver(this);
        timer = new Timer(1000, t -> {
            System.out.println("Updating values on front end");
            System.out.println(arduinoResponseMessage);
        });
        timer.start();
    }

    private ArduinoSerialPortProxy arduinoProxy;


    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Running background updating value");
        ArduinoSerialPortProxy arduinoSerialPortProxy = (ArduinoSerialPortProxy) o;
        arduinoResponseMessage = arduinoSerialPortProxy.getData();
    }
}

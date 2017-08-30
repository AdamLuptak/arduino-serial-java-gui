import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import serialportcomunikator.ArduinoProxy;
import serialportcomunikator.ArduinoResponseMessage;
import serialportcomunikator.ArduinoSerialPortProxy;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;
import java.util.Observer;


public class Controller implements Observer {

    @FXML
    public ToggleButton startButton;
    public TableColumn temp1Column;
    public TableView temperatureTableView;
    ArduinoResponseMessage arduinoResponseMessage;
    Timer timer;
    ObservableList<ArduinoResponseMessage> allData = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        System.out.println("Initialize controller");
        arduinoProxy = new ArduinoSerialPortProxy();
        arduinoProxy.initialize();
        arduinoProxy.addObserver(this);
        temperatureTableView.setItems(allData);
        timer = new Timer(1000, timerCallBack);
        temp1Column.setCellValueFactory(new PropertyValueFactory<ArduinoResponseMessage,String>("temp1"));

    }

    private ActionListener timerCallBack = t -> {
        System.out.println("Updating values on front end");
        System.out.println(arduinoResponseMessage);
        allData.add(arduinoResponseMessage);
    };

    private ArduinoSerialPortProxy arduinoProxy;

    @FXML
    public void startButtonToggle(ActionEvent event) {
        if (startButton.isSelected()) {
            timer.start();
        } else {
            timer.stop();
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        System.out.println("Running background updating value");
        ArduinoSerialPortProxy arduinoSerialPortProxy = (ArduinoSerialPortProxy) o;
        arduinoResponseMessage = arduinoSerialPortProxy.getData();
    }

}

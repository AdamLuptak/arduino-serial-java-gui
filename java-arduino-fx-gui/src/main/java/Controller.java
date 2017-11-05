import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.cell.PropertyValueFactory;
import arduinoclient.ArduinoClient;
import arduinoclient.ArduinoRequestMessage;
import arduinoclient.ArduinoResponseMessage;
import arduinoclient.ArduinoObservableSerialPortClient;

import javax.swing.*;
import java.awt.event.ActionListener;


public class Controller {

    @FXML
    public ToggleButton startButton;
    public TableColumn temp1Column;
    public TableView temperatureTableView;
    ArduinoResponseMessage arduinoResponseMessage;
    Timer timer;
    ObservableList<ArduinoResponseMessage> allData = FXCollections.observableArrayList();

    private ArduinoClient<ArduinoRequestMessage,ArduinoResponseMessage> arduinoClient;


    @FXML
    public void initialize() {
        System.out.println("Initialize controller");
        arduinoClient = new ArduinoObservableSerialPortClient();
        temperatureTableView.setItems(allData);
        allData.addListener(new ListChangeListener<ArduinoResponseMessage>() {
                                @Override
                                public void onChanged(Change<? extends ArduinoResponseMessage> c) {

                                        temperatureTableView.scrollTo(allData.size());
                                }
                            }

        );
        timer = new Timer(1000, updateMeasuredData);
        temp1Column.setCellValueFactory(new PropertyValueFactory<ArduinoResponseMessage, String>(temp1Column.getText()));
    }

    private ActionListener updateMeasuredData = t -> {
        System.out.println("Updating values on front end");
        System.out.println(arduinoResponseMessage);
        arduinoResponseMessage = arduinoClient.getData();
        allData.add(arduinoResponseMessage);
    };

    @FXML
    public void startButtonToggle(ActionEvent event) {
        if (startButton.isSelected()) {
            timer.start();
        } else {
            timer.stop();
        }
    }

}

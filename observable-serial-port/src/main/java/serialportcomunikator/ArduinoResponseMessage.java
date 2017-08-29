package serialportcomunikator;

import java.util.HashMap;

/**
 * Created by root on 23.7.2017.
 */
public class ArduinoResponseMessage {

    double temp1;
    double temp2;
    double temp3;
    double powerInterval;
    double power;

    public ArduinoResponseMessage() {
    }

    @Override
    public String toString() {
        return "ArduinoResponseMessage{" +
                "temp1=" + temp1 +
                ", temp2=" + temp2 +
                ", temp3=" + temp3 +
                ", powerInterval=" + powerInterval +
                ", power=" + power +
                '}';
    }

    public double getTemp1() {
        return temp1;
    }

    public void setTemp1(double temp1) {
        this.temp1 = temp1;
    }

    public double getTemp2() {
        return temp2;
    }

    public void setTemp2(double temp2) {
        this.temp2 = temp2;
    }

    public double getTemp3() {
        return temp3;
    }

    public void setTemp3(double temp3) {
        this.temp3 = temp3;
    }

    public double getPowerInterval() {
        return powerInterval;
    }

    public void setPowerInterval(double powerInterval) {
        this.powerInterval = powerInterval;
    }

    public double getPower() {
        return power;
    }

    public void setPower(double power) {
        this.power = power;
    }
}

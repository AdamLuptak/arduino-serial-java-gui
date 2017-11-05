package arduinoclient;

/**
 * Created by root on 23.7.2017.
 */
public interface ArduinoClient<Request, Response> {
    Response getData();
    void postData(Request request);
}

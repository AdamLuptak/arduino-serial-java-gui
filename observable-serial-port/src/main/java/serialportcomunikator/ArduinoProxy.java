package serialportcomunikator;

/**
 * Created by root on 23.7.2017.
 */
public interface ArduinoProxy<Request, Response> {
    Response getData(Request request);
    void postData(Request request);
}

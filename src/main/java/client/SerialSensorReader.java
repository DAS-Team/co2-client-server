package client;

// Roughly based on https://github.com/emersonmoretto/ArduinoTurbo-Java/blob/master/Mapper/src/br/eng/moretto/arduinoturbo/SerialTest.java
// but using jrxtx since it's still being maintained. API is slightly different.
// Hopefully an ADC will replace this class before deployment!

import org.openmuc.jrxtx.*;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.DoubleSummaryStatistics;
import java.util.Optional;

/**
 * Reads PPM sensor values coming from a serial device.
 */
public class SerialSensorReader implements AutoCloseable {
    public SerialSensorReader(){
        init();
    }

    private SerialPort serialPort;
    /** The port we're normally going to use. */
    private static final String PORT_NAMES[] = {
            "/dev/ttyACM1", // Raspberry Pi
            "/dev/ttyUSB0", // Some Linux distros
            "COM3"          // Windows apparently
    };
    /**
     * The {@link InputStream} for the chars into the port.
     */
    private InputStream input;
    /** Default baud rate for the port. */
    private static final int BAUD_RATE = 9600;

    private void init(){
        try {
            serialPort = SerialPortBuilder.newBuilder(PORT_NAMES[0])
                    .setBaudRate(BAUD_RATE)
                    .setDataBits(DataBits.DATABITS_8)
                    .setStopBits(StopBits.STOPBITS_1)
                    .setParity(Parity.NONE)
                    .build();

            input = serialPort.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Might be a better way of doing this, but the examples with BufferedReader give really stale results.
    private String readLine() throws IOException {
        input.skip(input.available());

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int ch;

        for(ch = input.read(); ch != '\n' && ch != -1; ch = input.read()){
            // Make sure we're at a newline
        }
        for(ch = input.read(); ch != '\n' && ch != -1; ch = input.read()){
            outputStream.write(ch);
        }

        return outputStream.toString(StandardCharsets.UTF_8.name());
    }

    public Optional<Double> pollForPPM() throws IOException {
        // TODO: Convert from sensor vals to PPM
        // At the moment the returned values are just whatever the uncalibrated sensor churns out.
        // Also this blocks, maybe it should be event based?

        String line = readLine();

        if(line == null){
            return Optional.empty();
        }

        System.out.println("Raw output: " + line);

        return Optional.of(Double.parseDouble(line));
    }

    public void close() throws IOException {
        serialPort.close();
    }
}

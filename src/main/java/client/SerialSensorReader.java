package client;

// Hopefully an ADC will replace this class before deployment!

import org.openmuc.jrxtx.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.DoubleSummaryStatistics;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Reads PPM sensor values coming from a serial device.
 */
public class SerialSensorReader implements SensorReader {
    public SerialSensorReader(){
        init();
    }

    private SerialPort serialPort;
    /** The port we're normally going to use. */
    private static final List<String> PORT_NAMES = Arrays.asList(
            "/dev/ttyACM0", // Raspberry Pi
            "/dev/ttyACM1", // Some Linux distros
            "/dev/ttyUSB0", // Some Linux distros
            "COM3"          // Windows apparently
    );
    /**
     * The {@link InputStream} for the chars into the port.
     */
    private InputStream input;
    /** Default baud rate for the port. */
    private static final int BAUD_RATE = 9600;

    private void init(){
        try {
            List<String> ports = Arrays.stream(SerialPortBuilder.getSerialPortNames())
                    .filter(PORT_NAMES::contains)
                    .collect(Collectors.toList());

            if(ports.isEmpty()){
                throw new IllegalStateException("No ports available");
            }
            else if(ports.size() > 1){
                throw new IllegalStateException("Ambiguous port");
            }
            else {
                System.out.println("Found valid port: " + ports.get(0));

                serialPort = SerialPortBuilder.newBuilder(ports.get(0))
                        .setBaudRate(BAUD_RATE)
                        .setDataBits(DataBits.DATABITS_8)
                        .setStopBits(StopBits.STOPBITS_1)
                        .setParity(Parity.NONE)
                        .build();

                input = serialPort.getInputStream();
            }

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

    @Override
    public Optional<Double> pollForPPM() throws IOException {
        // TODO: Convert from sensor vals to PPM
        // At the moment the returned values are just whatever the uncalibrated sensor churns out.
        // Also this blocks, maybe it should be event based?

        String line = readLine();

        if(line == null){
            return Optional.empty();
        }

        System.out.println("Raw output: " + line);

        Double ppmVal = null;
        try {
            ppmVal = Double.parseDouble(line);
        }
        catch(NullPointerException e){
            // Do nothing
        }

        return Optional.ofNullable(ppmVal);
    }

    @Override
    public void close() throws IOException {
        serialPort.close();
    }
}

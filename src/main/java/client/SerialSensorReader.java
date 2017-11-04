package client;

// Roughly based on https://github.com/emersonmoretto/ArduinoTurbo-Java/blob/master/Mapper/src/br/eng/moretto/arduinoturbo/SerialTest.java
// but using jrxtx since it's still being maintained. API is slightly different.
// Hopefully an ADC will replace this class before deployment!

import gnu.io.CommPortIdentifier;
import gnu.io.PortInUseException;
import org.openmuc.jrxtx.DataBits;
import org.openmuc.jrxtx.Parity;
import org.openmuc.jrxtx.SerialPort;
import org.openmuc.jrxtx.StopBits;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Enumeration;

/**
 * Reads PPM sensor values coming from a serial device.
 */
public class SerialSensorReader implements AutoCloseable {
    public SerialSensorReader(){
        init();
        // Idk other init goes here I guess.
    }

    private SerialPort serialPort;
    /** The port we're normally going to use. */
    private static final String PORT_NAMES[] = {
            "/dev/ttyACM0", // Raspberry Pi
            "/dev/ttyUSB0", // Some Linux distros
            "COM3"          // Windows apparently
    };
    /**
     * The {@link BufferedReader} for the chars into the port.
     */
    private BufferedReader input;
    /** Millis to block while waiting for port to open */
    private static final int TIMEOUT = 2000;
    /** Default baud rate for the port. */
    private static final int BAUD_RATE = 9600;

    // This is some icky Java 4? code, avoid if possible. Lol no generics.
    // Sets up the input stream for the sensor.
    private void init(){
        CommPortIdentifier portId = null;
        Enumeration<CommPortIdentifier> identifierEnumeration = CommPortIdentifier.getPortIdentifiers();

        while(identifierEnumeration.hasMoreElements()){
            CommPortIdentifier currentPort = identifierEnumeration.nextElement();

            for(String name: PORT_NAMES){
                if(name.equals(currentPort.getName())){
                    portId = currentPort;
                    break;
                }
            }
        }

        if(portId == null){
            throw new IllegalStateException("No port available for sensor device");
        }

        try {
            serialPort = (SerialPort) portId.open(this.getClass().getSimpleName(), TIMEOUT);

            serialPort.setBaudRate(BAUD_RATE);
            serialPort.setDataBits(DataBits.DATABITS_8);
            serialPort.setStopBits(StopBits.STOPBITS_1);
            serialPort.setParity(Parity.NONE);

            input = new BufferedReader(new InputStreamReader(serialPort.getInputStream()));
        } catch (PortInUseException | IOException e) {
            e.printStackTrace();
        }
    }

    public double pollForPPM() throws IOException {
        // TODO: Convert from sensor vals to PPM
        // At the moment the returned values are just whatever the uncalibrated sensor churns out.
        // Also this blocks, maybe it should be event based?
        return Double.parseDouble(input.readLine());
    }

    public void close() throws IOException {
        serialPort.close();
    }
}

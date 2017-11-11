package client;

import com.pi4j.gpio.extension.base.AdcGpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008GpioProvider;
import com.pi4j.gpio.extension.mcp.MCP3008Pin;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinAnalogInput;
import com.pi4j.io.spi.SpiChannel;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by paul on 08/11/17.
 *
 * Reads PPM values from a MCP3008 ADC.
 */
public class MCP3008SensorReader implements SensorReader {
    private final GpioController gpio = GpioFactory.getInstance();
    private final MQ135PPMConverter converter;
    private final AdcGpioProvider provider = new MCP3008GpioProvider(SpiChannel.CS0);
    private final GpioPinAnalogInput analogueInput = gpio.provisionAnalogInputPin(provider, MCP3008Pin.CH0, "CO2 Input");

    // TODO: Might be more efficient to act as a listener rather than polling?

    //private Double ppmValue = null;

    public MCP3008SensorReader(double rZeroValue) throws IOException {
        //provider.setEventThreshold(10, analogueInput);
        //provider.setMonitorInterval(100);
        //GpioPinListenerAnalog listener = event -> ppmValue = converter.toPPM(event.getValue());
        //gpio.addListener(listener, analogueInput);
        converter = new MQ135PPMConverter.Builder()
                .setRZero(rZeroValue)
                .build();
    }

    @Override
    public Optional<Double> pollForPPM() throws IOException {
        double outputVal = provider.getImmediateValue(analogueInput);
        double asPpm = converter.toPPM(outputVal);
        double asRZero = converter.toRZero(outputVal);

        System.out.println("Read state, analogue value:" + outputVal + ", as R0: " + asRZero + ", as PPM: " + asPpm);

        return Optional.of(converter.toPPM(provider.getImmediateValue(analogueInput)));
    }

    @Override
    public void close() throws IOException {
        gpio.shutdown();
    }
}

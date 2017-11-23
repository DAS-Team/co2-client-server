package client;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


public class MQ135PPMConverterTest {
    MQ135PPMConverter converter;

    @Before
    public void setUp() throws Exception {
        converter = new MQ135PPMConverter
                .Builder()
                .setRZero(100.0)
                .build();
    }

    @Test
    public void fromPPM() throws Exception {
        Assert.assertEquals(converter.fromPPM(converter.toPPM(120)), 120);
    }

}

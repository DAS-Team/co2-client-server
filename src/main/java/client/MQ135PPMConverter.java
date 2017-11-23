package client;

/**
 * Created by paul on 08/11/17.
 *
 * Responsible for converting raw values put out by the MQ135 to CO2 PPM values.
 */
class MQ135PPMConverter {
    // Vals from http://davidegironi.blogspot.co.uk/2014/01/cheap-co2-meter-using-mq135-sensor-with.html
    private static final double SCALING_FACTOR = 56.0820;
    private static final double EXPONENT = -5.9603;

    // From https://www.co2.earth/
    private static final double ATMOSPHERIC_CO2 = 403.38;

    private final double rLoad;
    private final double rZero;

    MQ135PPMConverter(double rLoad, double rZero){
        this.rLoad = rLoad;
        this.rZero = rZero;
    }

    /**
     * @param sensorVal
     * @return {@code sensorValue} converted to a resistance, according to the load resistance.
     */
    private double toResistance(double sensorVal){
        return ((1023.0 / sensorVal) - 1) * rLoad;
    }

    /**
     * @param ppmVal
     * @return {@code ppmVal} converted to the sensor reading which would have produced this value.
     */
    int fromPPM(double ppmVal){
        return (int) Math.round(1023.0 / (((Math.pow(ppmVal / SCALING_FACTOR, 1.0 / EXPONENT) * rZero) /rLoad) + 1));
    }

    /**
     * @param sensorVal
     * @return CO2 PPM reading based on the current {@code sensorVal}
     */
    double toPPM(double sensorVal){
        return SCALING_FACTOR * Math.pow(toResistance(sensorVal) / rZero, EXPONENT);
    }

    /**
     * @param sensorVal
     * @return rZero value used for calibration, based on the assumption that the sensor is in atmospheric conditions.
     * @see <a href="https://www.olimex.com/Products/Components/Sensors/SNS-MQ135/resources/SNS-MQ135.pdf">Datasheet</a>
     */
    double toRZero(double sensorVal){
        return toResistance(sensorVal) * Math.pow(ATMOSPHERIC_CO2 / SCALING_FACTOR, -1.0 / EXPONENT);
    }

    // Might replace this since there aren't as many params as I thought...
    static class Builder {
        private double rLoad = 10.0;
        private double rZero = 288.0;
        //private double voltage = 5.0;

        Builder(){

        }

        Builder setRLoad(double rLoad){
            this.rLoad = rLoad;
            return this;
        }

        Builder setRZero(double rZero){
            this.rZero = rZero;
            return this;
        }

        MQ135PPMConverter build(){
            return new MQ135PPMConverter(rLoad, rZero);
        }

    }
}

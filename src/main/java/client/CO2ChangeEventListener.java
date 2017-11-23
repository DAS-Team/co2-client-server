package client;

@FunctionalInterface
public interface CO2ChangeEventListener {
    /**
     * Called when the CO2 level changes by a certain amount.
     * @param newCO2Level the new CO2 level which caused the change.
     */
    void onCO2LevelChange(double newCO2Level);
}

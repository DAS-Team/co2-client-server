package client;

@FunctionalInterface
public interface CO2ChangeEventListener {
    void onCO2LevelChange(double newCO2Level);
}

package utils;

import java.util.Arrays;
import java.util.List;

public class MathUtils {
    public static double variance(double average, List<Double> values){
        return values.stream()
                .map(val -> val - average)
                .map(val -> val * val)
                .mapToDouble(Double::doubleValue)
                .sum() / values.size();
    }
}

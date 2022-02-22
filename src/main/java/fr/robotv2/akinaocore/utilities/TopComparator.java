package fr.robotv2.akinaocore.utilities;

import java.util.Comparator;
import java.util.Map;
import java.util.UUID;

public class TopComparator implements Comparator<Map.Entry<UUID, Double>> {
    @Override
    public int compare(Map.Entry<UUID, Double> o1, Map.Entry<UUID, Double> o2) {
        return o2.getValue() > o1.getValue() ? 1 : 0;
    }
}

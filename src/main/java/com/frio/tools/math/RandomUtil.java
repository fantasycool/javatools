package com.frio.tools.math;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Random;
import java.util.stream.Stream;

/**
 * Created by frio on 17/5/31.
 */
public class RandomUtil {
    public static <E> E getWeightedRandomJava8(Stream<Map.Entry<E, Double>> weights, Random random) {
        return weights
                .map(e -> new AbstractMap.SimpleEntry<E,Double>(e.getKey(),-Math.log(random.nextDouble()) / e.getValue()))
                .min((e0,e1)-> e0.getValue().compareTo(e1.getValue()))
                .orElseThrow(IllegalArgumentException::new).getKey();
    }
}

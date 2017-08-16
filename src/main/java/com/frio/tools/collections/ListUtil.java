package com.frio.tools.collections;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Created by frio on 2017/8/16.
 */
public class ListUtil {
    /**
     * chopped a list, divided by bucket length
     * @param arg
     * @param bucketLength
     * @param <T>
     * @return
     */
    public static <T> Optional<List<List<T>>> choppedList(Optional<List<T>> arg, int bucketLength) {
        if (bucketLength <= 0) {
            throw new IllegalArgumentException("bucketLength have to bigger than 0");
        }
        return arg.map(list -> {
            List<List<T>> resultList = new ArrayList<List<T>>();
            for (int i = 0; i < list.size(); i += bucketLength) {
                resultList.add(new ArrayList<T>(list.subList(i,
                        Math.min(list.size(), i + bucketLength))));
            }
            return resultList;
        });
    }
}

package com.amirrazmjou;

import java.util.Set;

/**
 * knn
 * Created by Amir Razmjou 2/15/15.
 */
public class ReductionAvg implements Reduction {
    @Override
    public double reduce(Set<Double> s) {
        double sum = 0;
        for (double e: s) {
            sum += e;
        }
        return sum / s.size();
    }
}

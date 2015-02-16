package com.amirrazmjou;

import java.util.Collections;
import java.util.Set;

/**
 * knn
 * Created by Amir Razmjou 2/15/15.
 */
public class ReductionMin implements Reduction {
    @Override
    public double reduce(Set<Double> s) {
        return Collections.min(s);
    }
}

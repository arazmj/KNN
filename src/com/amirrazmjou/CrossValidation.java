package com.amirrazmjou;

import java.util.LinkedList;
import java.util.List;

/**
 * knn
 * Created by Amir Razmjou 2/15/15.
 */

public class CrossValidation {
    private final List<Instance> instances;
    private final int x;
    private final int xi;
    private List<Instance> trainingInstances;
    private List<Instance> testInstances;

    public CrossValidation(List<Instance> instances, int x, int xi) {
        this.instances = instances;
        this.x = x;
        this.xi = xi;
    }

    public List<Instance> getTrainingInstances() {
        return trainingInstances;
    }

    public List<Instance> getTestInstances() {
        return testInstances;
    }

    public CrossValidation invoke() {
        int  s = instances.size();
        testInstances = instances.subList((s/x) * xi, (s/x) * (xi+1));
        trainingInstances = new LinkedList<Instance>(instances);
        trainingInstances.removeAll(testInstances);
        return this;
    }
}

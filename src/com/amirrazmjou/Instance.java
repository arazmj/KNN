package com.amirrazmjou;

/**
 * knn
 * Created by Amir Razmjou 2/1/15.
 */
class Instance implements Comparable {
    private double x;
    private double y;
    private double predict;
    private double distance;

    public Instance(double x, double y, double predict) {
        this.x = x;
        this.y = y;
        this.predict = predict;
    }

    public double getDistance() {
        return distance;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }

    
    @Override
    public int compareTo(Object o) {
        Instance i = (Instance)o;
        return ((Double)this.distance).compareTo(i.getDistance());
    }

    public double getPredict() {
        return predict;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }
}

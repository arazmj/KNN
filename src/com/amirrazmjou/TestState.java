package com.amirrazmjou;

/**
 * knn
 * Created by Amir Razmjou 2/16/15.
 */
public class TestState {
    private final int k;
    private final double t;
    private final int tp;
    private final int tn;
    private final int fp;
    private final int fn;

    public TestState(int k, double t, int tp, int tn, int fp, int fn) {
        this.k = k;
        this.t = t;
        this.tp = tp;
        this.tn = tn;
        this.fp = fp;
        this.fn = fn;
    }


    @Override
    public String toString() {
        return String.format("%d\t%.3f\t%d\t%d\t%d\t%d",
                k, t, tp, tn, fp, fn);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TestState testState = (TestState) o;

        if (k != testState.k) return false;
        if (Double.compare(testState.t, t) != 0) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = k;
        temp = Double.doubleToLongBits(t);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }
}

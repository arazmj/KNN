package com.amirrazmjou;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

class Main {

    public static void main(String[] args) {
        String argFile;
        int argK;
        double argT;
        int argX;
        try {
            argFile = args[0];
            argK = Integer.parseInt(args[1]);
            argT = Double.parseDouble(args[2]);
            argX = Integer.parseInt(args[3]);
        }
        catch (Exception ae) {
            System.out.println("Help...");
            return ;

        }

        final String headers = "\nk\tt\ttp\ttn\tfp\tfn\tacc";


        // read .csv file from first argument
        List<Instance> instances = readFile(argFile);
        // shuffle all instances
        Collections.shuffle(instances);

        // task1 using training set as test set
        List<Instance> trainingInstances = instances;
        List<Instance> testInstances = instances;
        int k = argK;
        double t = argT;

        res = new LinkedHashMap<TestState, Set<Double>>();
        System.out.println("\nTask 1.a - Calculating Accuracy, Varying K - Training Set = Test Set");
        for (int ki = 10; ki <= testInstances.size() - 1; ki += 10) {
            testDataSet(trainingInstances, testInstances, t, ki, false);
        }
        System.out.println(headers);
        printResults(res, new ReductionAvg());


        res = new LinkedHashMap<TestState, Set<Double>>();
        System.out.println("\nTask 1.b - Calculating Accuracy Varying T - Training Set = Test Set");
        for (double ti = 0; ti <= 1; ti += 0.01) {
            testDataSet(trainingInstances, testInstances, ti, k, false);
        }
        System.out.println(headers);
        printResults(res, new ReductionAvg());


        System.out.println("\nTask 1.c - ROC - Training Set = Test Set");
        roc = new TreeMap<Double, Set<Double>>();
        for (double ti = 0; ti <= 1; ti += 0.01) {
            testDataSet(trainingInstances, testInstances, ti, k, true);
        }
        System.out.println("\nfpr\ttpr");
        printResults(roc, new ReductionAvg());


        // task 2 - cross-validation
        int x = argX;
        System.out.println("\nTask 2.a - Calculating Accuracy, Varying K - Cross Validation");
        res = new LinkedHashMap<TestState, Set<Double>>();
        for (int xi = 0; xi < x; xi++) {
            CrossValidation crossValidation = new CrossValidation(instances, x, xi).invoke();
            trainingInstances = crossValidation.getTrainingInstances();
            testInstances = crossValidation.getTestInstances();

            for (int ki = 10; ki <= testInstances.size() - 1; ki += 10) {
                testDataSet(trainingInstances, testInstances, t, ki, false);
            }
        }
        System.out.println(headers);
        printResults(res, new ReductionMin());

        System.out.println("\nTask 2.b - Calculating Accuracy, Varying T - Cross Validation");
        res = new LinkedHashMap<TestState, Set<Double>>();
        for (int xi = 0; xi < x; xi++) {
            CrossValidation crossValidation = new CrossValidation(instances, x, xi).invoke();
            trainingInstances = crossValidation.getTrainingInstances();
            testInstances = crossValidation.getTestInstances();

            for (double ti = 0; ti <= 1; ti += 0.01) {
                testDataSet(trainingInstances, testInstances, ti, k, false);
            }
        }
        System.out.println(headers);
        printResults(res, new ReductionAvg());

        System.out.println("\nTask 2.c - ROC - Cross Validation");
        roc = new TreeMap<Double, Set<Double>>();
        for (int xi = 0; xi < x; xi++) {
            CrossValidation crossValidation = new CrossValidation(instances, x, xi).invoke();
            trainingInstances = crossValidation.getTrainingInstances();
            testInstances = crossValidation.getTestInstances();

            for (double ti = 0; ti <= 1; ti += 0.01) {
                testDataSet(trainingInstances, testInstances, ti, k, true);
            }
        }
        System.out.println("\nfpr\ttpr");
        printResults(roc, new ReductionAvg());

        setDistance(instances, 0, 0);
        Collections.sort(instances);
        trainingInstances = instances.subList(200, 1000);
        testInstances = instances.subList(0, 200);
        res = new LinkedHashMap<TestState, Set<Double>>();
        System.out.println("Task 2.a - Calculating Accuracy Varying K - Worst Case");
        for (int ki = 10; ki <= testInstances.size() - 1; ki += 10) {
            testDataSet(trainingInstances, testInstances, t, ki, false);
        }
        System.out.println(headers);
        printResults(res, new ReductionMin());


        res = new LinkedHashMap<TestState, Set<Double>>();
        System.out.println("\nTask 2.b - Calculating Accuracy Varying T - Worst Case ");
        for (double ti = 0; ti <= 1; ti += 0.01) {
            testDataSet(trainingInstances, testInstances, ti, k, false);
        }
        System.out.println(headers);
        printResults(res, new ReductionMin());


        roc = new TreeMap<Double, Set<Double>>();
        System.out.println("\nTask 2.c - ROC - Worst Case");
        roc = new TreeMap<Double, Set<Double>>();
        for (double ti = 0; ti <= 1; ti += 0.01) {
            testDataSet(trainingInstances, testInstances, ti, k, true);
        }
        System.out.println("\nfpr\ttpr");
        printResults(roc, new ReductionMin());
    }

    private static <T> void printResults(Map<T, Set<Double>> r, Reduction reduction) {
        for (Map.Entry<T, Set<Double>> entry : r.entrySet()) {
            double result = reduction.reduce(entry.getValue());
            
            // in case of ROC two both columns are doubles
            String key = entry.getKey() instanceof Double ? 
                    String.format("%.3f", entry.getKey()) : entry.getKey().toString();
            
            String value = String.format("%.3f", result);
            System.out.print(key + "\t" + value + "\n");
        }
    }

    private static Map<Double, Set<Double>> roc;
    private static Map<TestState, Set<Double>> res;

    private static void testDataSet(List<Instance> instances, List<Instance> testInstances,
                                    double t, int k, boolean outputRoc) {
        // TreeMap maintains keys (FPR)
        // sorted that what we need to draw ROC
        int tp = 0, tn = 0, fp = 0, fn = 0;
        Double fpr, tpr;

        System.out.print(".");


        for (Instance test : testInstances) {
            double result = testPoint(instances, test.getX(), test.getY(), k);
            int predict = (result > t ? 1 : 0);

            if (predict != test.getPredict()) {
                if (predict == 1)
                    fp++;
                else fn++;
            } else {
                if (predict == 1)
                    tp++;
                else tn++;
            }
        }


        fpr = (double) fp / (fp + tn);
        tpr = (double) tp / (tp + fn);


        if (!fpr.isInfinite() && !fpr.isNaN() &&
                !tpr.isInfinite() && !tpr.isNaN() && outputRoc) {
            
            if (roc.containsKey(fpr))
                roc.get(fpr).add(tpr);
            else {
                HashSet<Double> newSet = new HashSet<Double>();
                newSet.add(tpr);
                roc.put(fpr, newSet);
            }
        }

        double accuracy = (double) (tp + tn) / testInstances.size();
        
        if (!outputRoc) {
            String key = String.format("%d\t%.3f\t%d\t%d\t%d\t%d",
                    k, t, tp, tn, fp, fn);
            TestState ts = new TestState(k, t, tp, tn, fp, fn);
            if (res.containsKey(key))
                res.get(ts).add(accuracy);
            else {
                HashSet<Double> newSet = new HashSet<Double>();
                newSet.add(accuracy);
                res.put(ts, newSet);
            }
        }
    }

    private static double testPoint(List<Instance> instances, double qx, double qy, int k) {
        setDistance(instances, qx, qy);
        
        // as Instance implements comparable
        // all instances are sorted Euclidean
        // distance centered around qx and qy
        Collections.sort(instances);

        double minDistance = instances
                .get(Math.min(k, instances.size()) - 1)
                .getDistance();
        double sumPredicts = 0;
        double distance = 0;

        int n = 0;
        while (distance <= minDistance && n < instances.size()) {
            distance = instances.get(n).getDistance();
            sumPredicts += instances.get(n).getPredict();
            n++;
        }

        return sumPredicts / n;
    }

    private static void setDistance(List<Instance> instances, double qx, double qy) {
        for (Instance i: instances) {
            double x = i.getX();
            double y = i.getY();
            double distance = Math.sqrt(Math.pow((x - qx), 2) + Math.pow((y - qy), 2));
            i.setDistance(distance);
        }
    }
    
    private static LinkedList<Instance> readFile(String file) {
        LinkedList<Instance> instances = new LinkedList<Instance>();
        BufferedReader br = null;
        try {
            String sCurrentLine;
            br = new BufferedReader(new FileReader(file));
            while ((sCurrentLine = br.readLine()) != null) {
                String values[] = sCurrentLine.split(",");
                double x, y, c;
                x = Double.parseDouble(values[0]);
                y = Double.parseDouble(values[1]);
                c = Double.parseDouble(values[2]);
                instances.add(new Instance(x, y, c));
                
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
        return instances;
    }
}

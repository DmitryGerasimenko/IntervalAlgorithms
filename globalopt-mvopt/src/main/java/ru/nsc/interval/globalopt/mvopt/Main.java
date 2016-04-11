package ru.nsc.interval.globalopt.mvopt;

import java.io.IOException;
import java.nio.file.Paths;
import net.java.jinterval.expression.OptimizationProblem;
import net.java.jinterval.expression.example.FireRisk;
import net.java.jinterval.interval.set.*;
import net.java.jinterval.rational.ExtendedRational;

public class Main {

    public static void test(String msg, OptimizationProblem problem, SetIntervalContext ic, double... tolerances) {
        System.out.println(msg);
        for (double tolerance : tolerances) {
            SetInterval[] box = new SetInterval[problem.getNumInps()];
            for (int i = 0; i < box.length; i++) {
                box[i] = ic.textToInterval(problem.getInpRange(i));
            }
            IAOGO algorithm = new IAOGO(problem.getObjective());

            long startTime = System.currentTimeMillis();
            algorithm.start(box, ExtendedRational.valueOf(tolerance), ic);
            long stopTime = System.currentTimeMillis();
            System.out.println("tol=" + tolerance + " time=" + (stopTime - startTime) / 1E3 + "s");
        }
    }

    public static void main(String[] args) {
        OptimizationProblem lit = FireRisk.createOptimizationProblemTempMaxFv();
        OptimizationProblem num = Functions.createOptimizationProblem();
        SetIntervalContext plain = SetIntervalContexts.getPlain();
        SetIntervalContext accur = SetIntervalContexts.getAccur64();
        switch (4) {
            case 1:
                test("num plain", num, plain, 1E-9);
                break;
            case 2:
                test("num accur", num, accur, 1E-9);
                break;
            case 3:
                test("lit plain", lit, plain, 1E-9);
                break;
            case 4:
                test("lit accur", lit, accur, 1E-9);
                break;
        }
    }

    private static void Help() {
        System.out.println("Usage: [-p] problemfile tolerance");
        System.out.println("       -p plain mode (faster but without guarantee");
        System.exit(1);
    }

    public static void mainWithArgs(String[] args) {
        SetIntervalContext ic = SetIntervalContexts.getDefault();
        String problemFile = null;
        Double tolerance = null;
        for (String arg : args) {
            if (arg.startsWith("-")) {
                if (arg.equals("-p")) {
                    ic = SetIntervalContexts.getPlain();
                } else {
                    Help();
                }
            } else if (problemFile == null) {
                problemFile = arg;
            } else if (tolerance == null) {
                tolerance = new Double(arg);
            } else {
                Help();
            }
        }
        if (tolerance == null) {
            Help();
        }
        OptimizationProblem problem = null;
        try {
            problem = OptimizationProblem.load(Paths.get(problemFile));
        } catch (IOException e) {
            System.out.println("File " + problemFile + " not found");
            Help();
        }
        SetInterval[] box = new SetInterval[problem.getNumInps()];
        for (int i = 0; i < box.length; i++) {
            box[i] = ic.textToInterval(problem.getInpRange(i));
        }
        IAOGO algorithm = new IAOGO(problem.getObjective());

        long startTime = System.currentTimeMillis();
        algorithm.start(box, ExtendedRational.valueOf(tolerance), ic);
        long stopTime = System.currentTimeMillis();
        System.out.println("tol=" + tolerance + " time=" + (stopTime - startTime) / 1E3 + "s");
    }
}
/*
num plain
62553
-0.080214431476509080	[170.000000000000000000, 170.000000000000000000]	[299.000000000000000000, 299.000000000000000000]	[9.000000000000000000, 9.000000000000000000]	[34.800000000000000000, 34.800000000000000000]
-0.080214431476509080	[170.000000000000000000, 170.000000000000000000]	[299.000000000000000000, 299.000000000000000000]	[9.000000000000000000, 9.000000000000000000]	[34.800000000000000000, 34.800000000000000000]
tol=1.0E-9 time=198.342s

num accur
62553
-0.080214431476514280	[170.000000000000000000, 170.000000000000000000]	[299.000000000000000000, 299.000000000000000000]	[9.000000000000000000, 9.000000000000000000]	[34.800000000000004000, 34.800000000000004000]
-0.080214431476514280	[170.000000000000000000, 170.000000000000000000]	[299.000000000000000000, 299.000000000000000000]	[9.000000000000000000, 9.000000000000000000]	[34.800000000000004000, 34.800000000000004000]
tol=1.0E-9 time=323.784s

lit plain
62553
-0.080214431476510380	[170.000000000000000000, 170.000000000000000000]	[299.000000000000000000, 299.000000000000000000]	[9.000000000000000000, 9.000000000000000000]	[34.800000000000000000, 34.800000000000000000]
-0.080214431476510380	[170.000000000000000000, 170.000000000000000000]	[299.000000000000000000, 299.000000000000000000]	[9.000000000000000000, 9.000000000000000000]	[34.800000000000000000, 34.800000000000000000]
tol=1.0E-9 time=206.655s

lit accur
62553
-0.080214431476518500	[170.000000000000000000, 170.000000000000000000]	[299.000000000000000000, 299.000000000000000000]	[9.000000000000000000, 9.000000000000000000]	[34.800000000000004000, 34.800000000000004000]
-0.080214431476518500	[170.000000000000000000, 170.000000000000000000]	[299.000000000000000000, 299.000000000000000000]	[9.000000000000000000, 9.000000000000000000]	[34.800000000000004000, 34.800000000000004000]
tol=1.0E-9 time=300.758s
 */

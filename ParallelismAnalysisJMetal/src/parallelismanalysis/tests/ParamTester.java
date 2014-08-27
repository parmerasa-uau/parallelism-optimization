/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.tests;

import parallelismanalysis.optimization.Parameter;

/**
 *
 * @author jahrralf
 */
public class ParamTester {

    public static void main(String[] args) {
        System.out.println("10 - 20");
        Parameter ps[] = {
            new Parameter(10, 20),
            new Parameter(1, 2),
            new Parameter(-1, 1),
            new Parameter(-100, 100),
            new Parameter(99, 99),};


        for (Parameter p : ps) {
            int min = Integer.MAX_VALUE;
            int max = Integer.MIN_VALUE;
            double sum = 0, count = 0;

            for (int i = 0; i < 1000000; i++) {
                int value = p.getRandom();
                min = Math.min(min, value);
                max = Math.max(max, value);
                sum += value;
                count++;
            }

            System.out.println("Parameter " + p);
            System.out.println("min: " + min);
            System.out.println("max: " + max);
            System.out.println("avg: " + (sum / count));
        }
    }
}

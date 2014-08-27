/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import parallelismanalysis.entities.Activity;
import parallelismanalysis.optimization.Objective;
import parallelismanalysis.optimization.ObjectiveException;

/**
 *
 * @author jahrralf
 */
public class NumberPacker {
    
    public static Activity[][] getPacks(Activity[] items, int packs, Objective obj, Comparator<Activity> comparator) throws ObjectiveException {
        Activity[][] data = new Activity[packs][items.length];
        double[] sums = new double[packs];
        int[] top = new int[packs];

        for (int i = 0; i < sums.length; i++) {
            sums[i] = 0;
        }
        for (int i = 0; i < sums.length; i++) {
            top[i] = 0;
        }

        Arrays.sort(items,comparator);

        for (int i = 0; i < items.length / 2; ++i) {
            Activity temp = items[i];
            items[i] = items[items.length - i - 1];
            items[items.length - i - 1] = temp;
        }

        for (Activity item : items) {
            int pack_with_min_sum = 0;

            // Find best pack
            for (int i = 0; i < sums.length; i++) {
                if (sums[i] < sums[pack_with_min_sum]) {
                    pack_with_min_sum = i;
                }
            }

            // System.out.println("Will be put in " + pack_with_min_sum);

            // Put in pack
            data[pack_with_min_sum][top[pack_with_min_sum]] = item;
            sums[pack_with_min_sum] += item.getObjectiveValue(obj);
            top[pack_with_min_sum]++;

            /* for (int i = 0; i < data.length; i++) {
             System.out.println("Pack " + i + ": " + ArrayHelper.dats(data[i]));
             } */
            // System.out.println("Sums: " + ArrayHelper.dats(sums));
        }

        // Reduce packs
        int max_top = 0;
        for (int i : top) {
            max_top = Math.max(max_top, i);
        }

        Activity[][] result = new Activity[packs][max_top];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = data[i][j];
            }
        }

        // System.out.println("Sums: " + ArrayHelper.dats(sums));

        return result;
    }

    public static double[][] getPacks(double[] items, int packs) {
        double[][] data = new double[packs][items.length];
        double[] sums = new double[packs];
        int[] top = new int[packs];

        for (int i = 0; i < sums.length; i++) {
            sums[i] = 0;
        }
        for (int i = 0; i < sums.length; i++) {
            top[i] = 0;
        }

        Arrays.sort(items);

        for (int i = 0; i < items.length / 2; ++i) {
            double temp = items[i];
            items[i] = items[items.length - i - 1];
            items[items.length - i - 1] = temp;
        }

        for (double item : items) {
            int pack_with_min_sum = 0;

            // Find best pack
            for (int i = 0; i < sums.length; i++) {
                if (sums[i] < sums[pack_with_min_sum]) {
                    pack_with_min_sum = i;
                }
            }

            // System.out.println("Will be put in " + pack_with_min_sum);

            // Put in pack
            data[pack_with_min_sum][top[pack_with_min_sum]] = item;
            sums[pack_with_min_sum] += item;
            top[pack_with_min_sum]++;

            /* for (int i = 0; i < data.length; i++) {
             System.out.println("Pack " + i + ": " + ArrayHelper.dats(data[i]));
             } */
            // System.out.println("Sums: " + ArrayHelper.dats(sums));
        }

        // Reduce packs
        int max_top = 0;
        for (int i : top) {
            max_top = Math.max(max_top, i);
        }

        double[][] result = new double[packs][max_top];
        for (int i = 0; i < result.length; i++) {
            for (int j = 0; j < result[i].length; j++) {
                result[i][j] = data[i][j];
            }
        }

        // System.out.println("Sums: " + ArrayHelper.dats(sums));

        return result;
    }

    public static void main(String[] args) {
        double[] numbers;
        double[][] packs;

        for (int numbers_size = 1; numbers_size < 50; numbers_size += 2) {

            numbers = new double[numbers_size];
            for (int i = 0; i < numbers.length; i++) {
                numbers[i] = Math.round(Math.random() * 1000);
            }

            for (int packs_size = 1; packs_size < 9; packs_size++) {
                System.out.println("######################################################");

                packs = getPacks(numbers, packs_size);

                System.out.println("------------------------------------------------------");

                System.out.println("Input: " + ArrayHelper.dats(numbers));

                System.out.println("------------------------------------------------------");

                for (int i = 0; i < packs.length; i++) {
                    System.out.println("Pack " + i + ": " + ArrayHelper.dats(packs[i]));
                }
            }
        }
    }
}

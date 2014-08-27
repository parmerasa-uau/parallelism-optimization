/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.optimization;

import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ralf
 */
abstract public class AbstractIntegerConfiguration implements Comparable<AbstractIntegerConfiguration> {

    protected int[] parameter_values;
    protected double[] objective_values;
    protected boolean is_evaluated = false;
    private static Parameter[] parameters;
    private int domination_count = -1;

    @Override
    public boolean equals(Object them_o) {
        if (!(them_o instanceof AbstractIntegerConfiguration)) {
            return them_o.equals(this);
        } else {
            AbstractIntegerConfiguration them = (AbstractIntegerConfiguration) them_o;

            int[] para_me = this.getParameterValues();
            int[] para_them = them.getParameterValues();

            // them.equals(them);

            for (int i = 0; i < para_me.length; i++) {
                if (para_me[i] != para_them[i]) {
                    return false;
                }
            }

            return true;
        }
    }

    public double[] getObjectiveValues() {
        if (!is_evaluated) {
            evaluate();
        }
        return objective_values;
    }

    public abstract void evaluate();

    public boolean isFeasible() {
        if (!is_evaluated) {
            evaluate();
        }

        for (double obj : objective_values) {
            if (obj == Double.MAX_VALUE) {
                return false;
            }
        }

        return true;
    }

    public void setParameterValues(int[] parameter_values) {
        this.parameter_values = parameter_values;
        this.is_evaluated = false;
        this.domination_count = -1;
    }

    public int[] getParameterValues() {
        return this.parameter_values;
    }

    public static void setParameters(Parameter[] parameters) {
        AbstractIntegerConfiguration.parameters = parameters;
    }

    public static Parameter[] getParameters() {
        return AbstractIntegerConfiguration.parameters;
    }

    /** compares two configuations and uses comination count for this */
    @Override
    public int compareTo(AbstractIntegerConfiguration t) {
        int mine = 0, theirs = 0;

        try {
            mine = this.getDominationCount();
            theirs = t.getDominationCount();
        } catch (Exception ex) {
            Logger.getLogger(AbstractIntegerConfiguration.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(1);
        }

        return new Integer(mine).compareTo(theirs);
    }

    @Override
    public String toString() {
        String result = "";
        result += Arrays.toString(this.parameter_values);
        result += " => ";
        if (is_evaluated) {
            result += Arrays.toString(this.objective_values);
        } else {
            result += "not evaluated";
        }
        result += " -- " + domination_count;
        return result;
    }

    public String toCsv() {
        String result = "";

        for (int i : this.parameter_values) {
            result += i + ", ";
        }
        // result += Arrays.toString(this.parameter_values);

        // result += " => ";
        if (is_evaluated) {
            // result += Arrays.toString(this.objective_values);
            for (double i : this.objective_values) {
                result += i + ", ";
            }
            result += "D=" + domination_count;
        } else {
            result += "not evaluated";
        }

        return result.replace(",", "\t").replace(".", ",");
    }

    /** Creates new random configuration */
    public AbstractIntegerConfiguration() {
        this.parameter_values = new int[AbstractIntegerConfiguration.parameters.length];

        for (int i = 0; i < parameters.length; i++) {
            this.parameter_values[i] = parameters[i].getRandom();
        }
    }

    /** Creates new configuation with mutation and crossover */
    public AbstractIntegerConfiguration(int[] param_a, int[] param_b) {
        // int[] param_a = a.getParameterValues();
        // int[] param_b = b.getParameterValues();

        this.parameter_values = new int[parameters.length];

        for (int i = 0; i < parameter_values.length; i++) {
            if (Math.random() <= 0.6) { // Crossover
                if (Math.random() <= 0.5) {
                    this.parameter_values[i] = param_a[i];
                } else {
                    this.parameter_values[i] = param_b[i];
                }
            } else if (Math.random() <= 0.6) { // Mutation
                this.parameter_values[i] = parameters[i].getRandom();
                // this.a += 5 * (Math.random() - 0.5);
            } else {
                this.parameter_values[i] = param_a[i];
            }
        }
    }

    /** Returns the PREVIOUSLY CALCULATED number of configurations in the generation which dominate this configuration */
    public int getDominationCount() throws Exception {
        if (this.domination_count < 0) {
            throw new Exception("First calculate domination count!");
        }

        return this.domination_count;
    }

    /** Calculates the number of configurations in the generation which dominate this configuration */
    public int calcDominationCount(List<PatternConfiguration> generation) {
        this.domination_count = 0;
        for (int i = 0; i < generation.size(); i++) {
            if (!this.equals(generation.get(i)) && this.isDominatedBy(generation.get(i))) {
                // System.out.println("  - dominated by " + generation.get(i) );
                this.domination_count++;
            } else {
                // System.out.println("  - NOT dominated by " + generation.get(i) );
            }
        }

        return domination_count;
    }

    /** Returns true of this configuation is dominated by the other configuration */
    public boolean isDominatedBy(AbstractIntegerConfiguration other) {
        double[] me = this.getObjectiveValues();
        double[] them = other.getObjectiveValues();

        if (me.length != them.length) {
            System.out.println("Obejctive arrays should have same length!");
            System.exit(1);
        }

        int them_eq_me = 0;
        int them_l_me = 0;

        /* int me_better = 0;
         int them_better = 0; */
        // int none_better = 0;

        for (int i = 0; i < me.length; i++) {
            if (them[i] < me[i]) {
                them_l_me++;
            } else if (them[i] == me[i]) {
                them_eq_me++;
            } /* else {
             none_better++;
             } */
        }

        // System.out.println("Comparing " + this + " and " + other + " => " + them_l_me + " --- " + them_eq_me);

        if (them_l_me > 0 && them_eq_me == me.length - them_l_me) {
            return true;
        } else {
            return false;
        }
    }
}

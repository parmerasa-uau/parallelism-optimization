/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.optimization;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import parallelismanalysis.Platform;
import parallelismanalysis.entities.ActivityPatternDiagram;
import parallelismanalysis.entities.patterns.ParallelDesignPattern;

/**
 *
 * @author ralf
 */
public class PatternConfiguration extends AbstractIntegerConfiguration {

    private static ActivityPatternDiagram apd;
    private static ParallelDesignPattern[] patterns;
    private static Objective[] objectives;

    public static void setAPD(ActivityPatternDiagram apd) {
        PatternConfiguration.apd = apd;
    }

    public static void setObjectives(Objective[] objectives) {
        PatternConfiguration.objectives = objectives;
    }

    public static void setPatterns(ParallelDesignPattern[] patterns) {
        PatternConfiguration.patterns = patterns;
    }

    public PatternConfiguration(PatternConfiguration last_conf, PatternConfiguration b) {
        super(last_conf.getParameterValues(), b.getParameterValues());
        // new AbstractIntegerConfiguration(last_conf.getParameterValues(), b.getParameterValues());
    }

    public PatternConfiguration() {
        super();
    }

    @Override
    public void evaluate() {
        // System.out.println("EVALUATING " + this.toCsv());

        int[] values = this.getParameterValues();

        // System.out.println("Evaluating " + this + "...");

        for (int i = 0; i < values.length; i++) {
            patterns[i].getParameter().setValue(values[i]);
        }

        // System.out.println(a.getCurrent() + "  " + ArrayHelper.iats(values) + " => " + current_values);

        if (objectives == null || objectives.length == 0) {
            System.out.println("No Objectives configured. EXIT!");
            System.exit(1);
        }

        double[] objective_values = new double[objectives.length];
        for (int i = 0; i < objectives.length; i++) {
            try {
                objective_values[i] = apd.getObjectiveValue(objectives[i].toString());

                if (objectives[i].equals(Objective.OBJ_CORES)) {
                    if (objective_values[i] > Platform.CORES) {
                        objective_values[i] = Double.MAX_VALUE;
                    }
                }
            } catch (ObjectiveException ex) {
                objective_values[i] = Double.MAX_VALUE;
                System.out.println("EXCEPTION " + ex.getMessage());
            }
        }

        this.objective_values = objective_values;
        this.is_evaluated = true;

        // System.out.println("Evaluated " + this + ".");
    }

    public void printDetails() {
        System.out.println("# DETAILS FOR " + this.toCsv());
        System.out.println("");
        
        int[] values = this.getParameterValues();
        for (int i = 0; i < values.length; i++) {
            patterns[i].getParameter().setValue(values[i]);
        }

        // System.out.println("Hier bin ich...");

        for (int i = 0; i < patterns.length; i++) {
            ParallelDesignPattern pdp = patterns[i];
            System.out.println("[" + i + "] " + pdp);
        }
        
        System.out.println("");

        try {
            for (int i = 0; i < patterns.length; i++) {
                ParallelDesignPattern pdp = patterns[i];

                System.out.println("  * VARS for " + pdp);

                HashSet<String> set = pdp.getSyncGlobals();

                if (!set.isEmpty() && set != null) {
                    ArrayList<String> vars = new ArrayList<>();
                    
                    Iterator<String> iterator = set.iterator();
                    while(iterator.hasNext()) {
                        // System.out.println("    - " + iterator.next());
                        vars.add(iterator.next());
                    }
                    
                    Collections.sort(vars);
                    
                    for(String var : vars) {
                        System.out.println("    - " + var);
                    }
                    
                    /* String[] vars = null;
                    vars = set.toArray(vars);

                    if (vars != null && vars.length > 0) {
                        for (String var : vars) {
                            System.out.println("    - " + var);
                        }
                    }  */
                } else {
                    System.out.println("    - no vars");
                }
            }
        } catch (ObjectiveException ex) {
            Logger.getLogger(PatternConfiguration.class.getName()).log(Level.SEVERE, null, ex);
        }

        System.out.println("");
        System.out.println("");
                
    }
}

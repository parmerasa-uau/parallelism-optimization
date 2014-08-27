/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.contrib.MyResult;
import jmetal.contrib.MyStandardStudy;
import jmetal.problems.PatternParameterOptimizationProblem;
import parallelismanalysis.entities.ActivityPatternDiagram;
import parallelismanalysis.entities.ConsistencyException;
import parallelismanalysis.entities.patterns.ParallelDesignPattern;
import parallelismanalysis.optimization.Objective;
import parallelismanalysis.optimization.Parameter;
import parallelismanalysis.optimization.PatternConfiguration;
import parallelismanalysis.util.ArrayHelper;

/**
 *
 * @author jahrralf
 */
public class ParallelismAnalysis {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            ActivityPatternDiagram apd = new ActivityPatternDiagram(
                    new File(
                            "MC128_MOET_ACET.xml" // "D:/svn_ginkgo/parmerasa_privat/Tools/ParallelismAnalysis/UAV_PMAM.xml" // "D:/svn_ginkgo/parmerasa_privat/Tools/ParallelismAnalysis/UAV_64s.xml"
                    )
            );

            // ######################################################
            // CONSISTENCY
            // ######################################################
            try {
                apd.checkConsistency();
                System.out.println(apd + " is consistent");
            } catch (ConsistencyException ex) {
                Logger.getLogger(ParallelismAnalysis.class.getName()).log(Level.SEVERE, null, ex);
            }

            // ######################################################
            // DUMP
            // ######################################################
            apd.dump();

            // ######################################################
            // COLLECT PATTERNS
            // ######################################################
            ArrayList<ParallelDesignPattern> patterns = new ArrayList<ParallelDesignPattern>();
            apd.collectParallelDesignPatterns(patterns);

            ArrayList<Parameter> paras = new ArrayList<Parameter>();

            for (ParallelDesignPattern p : patterns) {
                paras.add(p.getParameter());
                System.out.println("[" + p.getParameter().getDomainSize() + "] " + p.getParameter() + " <-- " + p);
            }

            // ######################################################
            // HEURISTIC ALGORITHM
            // ######################################################
            // evaluateFixedConfiguration(apd, patterns, paras);
            
            evaluateWithJMetal(apd, patterns, paras);
            
            System.exit(0);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the evaluation with jMetal
     */
    private static void evaluateWithJMetal(ActivityPatternDiagram apd, ArrayList<ParallelDesignPattern> patterns, ArrayList<Parameter> paras) {
        // Echo all the platform parameters
        System.out.println("OVERHEAD_PER_SKELETON " + ArrayHelper.iats(Platform.OVERHEAD_PER_SKELETON));
        // System.out.println("OVERHEAD_PER_THREAD " + Platform.OVERHEAD_PER_THREAD);
        // System.out.println("OVERHEAD_PER_TASK " + Platform.OVERHEAD_PER_TASK);
        // System.out.println("OVERHEAD_PER_ROUND " + Platform.OVERHEAD_PER_ROUND_DP);
        System.out.println("OVERHEAD_PER_GETSET " + Platform.OVERHEAD_PER_GETSET);

        // Specify the objectives of the optimization
        // Objective[] objs = {Objective.OBJ_DURATION, Objective.OBJ_CORES, Objective.OBJ_GLOBALS, Objective.OBJ_GLOBALS_ACCESSES}; // , Objective.OBJ_PATTERNS, Objective.OBJ_GLOBALS, Objective.OBJ_PATTERNS, Objective.OBJ_CORES 
        Objective[] objs = {Objective.OBJ_DURATION, Objective.OBJ_CORES}; // , Objective.OBJ_PATTERNS, Objective.OBJ_GLOBALS, Objective.OBJ_PATTERNS, Objective.OBJ_CORES 

        // Configure optimization
        PatternParameterOptimizationProblem.setAPD(apd);
        PatternParameterOptimizationProblem.setParameters(paras.toArray(new Parameter[paras.size()]));
        PatternParameterOptimizationProblem.setPatterns(patterns.toArray(new ParallelDesignPattern[patterns.size()]));
        PatternParameterOptimizationProblem.setObjectives(objs);

        try {
            ArrayList<MyResult> results = MyStandardStudy.runJMetal("SMPSO", "PatternParameterOptimizationProblem");
            System.out.println("RESULTS");

            // Echo the raw results
            for (MyResult foo : results) {
                System.out.println(foo);
            }

            // Echo the results with all details
            System.out.println("");
            for (MyResult foo : results) {
                System.out.println(foo);

                PatternConfiguration.setAPD(apd);
                PatternConfiguration.setObjectives(objs);
                PatternConfiguration.setParameters(paras.toArray(new Parameter[paras.size()]));
                PatternConfiguration.setPatterns(patterns.toArray(new ParallelDesignPattern[patterns.size()]));

                PatternConfiguration bar = new PatternConfiguration();

                int[] vars = new int[foo.variables.length];
                for (int i = 0; i < foo.variables.length; i++) {
                    vars[i] = (int) Math.round(foo.variables[i]);
                }
                
                bar.evaluate();

                bar.setParameterValues(vars);

                bar.printDetails();
            }

            System.out.println("");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private static void evaluateFixedConfiguration(ActivityPatternDiagram apd, ArrayList<ParallelDesignPattern> patterns, ArrayList<Parameter> paras) {
        // Echo all the platform parameters
        System.out.println("OVERHEAD_PER_SKELETON " + ArrayHelper.iats(Platform.OVERHEAD_PER_SKELETON));
        // System.out.println("OVERHEAD_PER_THREAD " + Platform.OVERHEAD_PER_THREAD);
        // System.out.println("OVERHEAD_PER_TASK " + Platform.OVERHEAD_PER_TASK);
        // System.out.println("OVERHEAD_PER_ROUND " + Platform.OVERHEAD_PER_ROUND_DP);
        System.out.println("OVERHEAD_PER_GETSET " + Platform.OVERHEAD_PER_GETSET);

        // Specify the objectives of the optimization
        Objective[] objs = {Objective.OBJ_DURATION, Objective.OBJ_CORES, Objective.OBJ_GLOBALS, Objective.OBJ_GLOBALS_ACCESSES}; // , Objective.OBJ_PATTERNS, Objective.OBJ_GLOBALS, Objective.OBJ_PATTERNS, Objective.OBJ_CORES 
        // Objective[] objs = {Objective.OBJ_DURATION, Objective.OBJ_CORES}; // , Objective.OBJ_PATTERNS, Objective.OBJ_GLOBALS, Objective.OBJ_PATTERNS, Objective.OBJ_CORES 

        try {
                PatternConfiguration.setAPD(apd);
                PatternConfiguration.setObjectives(objs);
                PatternConfiguration.setParameters(paras.toArray(new Parameter[paras.size()]));
                PatternConfiguration.setPatterns(patterns.toArray(new ParallelDesignPattern[patterns.size()]));

                PatternConfiguration bar = new PatternConfiguration();
                
                int [] parameter_values = {2, 3, 2, 2, 1, 2, 2, 1, 2, 1, 1};
                bar.setParameterValues(parameter_values);

                bar.evaluate();

                bar.printDetails();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

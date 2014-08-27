//  StandardStudy.java
//
//  Authors:
//       Antonio J. Nebro <antonio@lcc.uma.es>
//       Juan J. Durillo <durillo@lcc.uma.es>
//
//  Copyright (c) 2011 Antonio J. Nebro, Juan J. Durillo
//
//  This program is free software: you can redistribute it and/or modify
//  it under the terms of the GNU Lesser General Public License as published by
//  the Free Software Foundation, either version 3 of the License, or
//  (at your option) any later version.
//
//  This program is distributed in the hope that it will be useful,
//  but WITHOUT ANY WARRANTY; without even the implied warranty of
//  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//  GNU Lesser General Public License for more details.
// 
//  You should have received a copy of the GNU Lesser General Public License
//  along with this program.  If not, see <http://www.gnu.org/licenses/>.
package jmetal.contrib;

import jmetal.experiments.studies.*;
import jmetal.core.Algorithm;
import jmetal.experiments.Settings;
import jmetal.experiments.settings.*;
import jmetal.util.JMException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.SolutionSet;
import jmetal.core.Variable;
import jmetal.experiments.ExperimentWithSolution;

/**
 * Class implementing a typical experimental study. Five algorithms are compared
 * when solving the ZDT, DTLZ, and WFG benchmarks, and the hypervolume, spread
 * and additive epsilon indicators are used for performance assessment.
 */
public class MyStandardStudy extends ExperimentWithSolution {

    /**
     * Configures the algorithms in each independent run
     *
     * @param problemName The problem to solve
     * @param problemIndex
     * @throws ClassNotFoundException
     */
    public void algorithmSettings(String problemName,
            int problemIndex,
            Algorithm[] algorithm) throws ClassNotFoundException {
        try {
            int numberOfAlgorithms = algorithmNameList_.length;

            HashMap[] parameters = new HashMap[numberOfAlgorithms];

            for (int i = 0; i < numberOfAlgorithms; i++) {
                parameters[i] = new HashMap();
            } // for

            if (!paretoFrontFile_[problemIndex].equals("")) {
                for (int i = 0; i < numberOfAlgorithms; i++) {
                    parameters[i].put("paretoFrontFile_", paretoFrontFile_[problemIndex]);
                    parameters[i].put("maxIterations", 300); // was: 250
                    parameters[i].put("swarmSize", 300); // was: 100
                    parameters[i].put("archiveSize", 300); // was: 100
                }
            } // if

            // algorithm[0] = new NSGAII_Settings(problemName).configure(parameters[0]);
            algorithm[0] = new SMPSO_Settings(problemName).configure(parameters[0]);
        } catch (IllegalArgumentException ex) {
            Logger.getLogger(StandardStudy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(StandardStudy.class.getName()).log(Level.SEVERE, null, ex);
        } catch (JMException ex) {
            Logger.getLogger(StandardStudy.class.getName()).log(Level.SEVERE, null, ex);
        }
    } // algorithmSettings

    /**
     * Main method
     *
     * @param args
     * @throws JMException
     * @throws IOException
     */
    public static ArrayList<MyResult> runJMetal(String algorithm, String problem) throws JMException, IOException {
        MyStandardStudy exp = new MyStandardStudy();

        exp.experimentName_ = "MyStandardStudy_" + algorithm + "_" + problem;
        exp.algorithmNameList_ = new String[]{algorithm}; // "NSGAII"
        exp.problemList_ = new String[]{problem};
        exp.paretoFrontFile_ = new String[]{problem + ".pf"};

        exp.indicatorList_ = new String[]{}; // "HV", "SPREAD", "EPSILON"};

        int numberOfAlgorithms = exp.algorithmNameList_.length;

        exp.experimentBaseDirectory_ = "./result_data/" + exp.experimentName_;
        exp.paretoFrontDirectory_ = "./result_data/pareto/" + exp.experimentName_;

        exp.algorithmSettings_ = new Settings[numberOfAlgorithms];

        exp.independentRuns_ = 1;

        exp.initExperiment();

        // Run the experiments
        int numberOfThreads = 1;
        SolutionSet[] solutions = exp.runExperimentWithSolution(numberOfThreads);

        ArrayList<MyResult> results = new ArrayList();

        // double best_objective = Double.MAX_VALUE;
        for (int i = 0; i < solutions.length; i++) {
            for (int j = 0; j < solutions[i].size(); j++) {
                // double objectives = solutions[i].get(j).getObjective(0);
                
                MyResult result = new MyResult();
                result.objectives = new double[solutions[i].get(j).getNumberOfObjectives()];

                String objectives = "";
                for (int k = 0; k < solutions[i].get(j).getNumberOfObjectives(); k++) {
                    objectives += solutions[i].get(j).getObjective(k) + " ";
                    result.objectives[k] = solutions[i].get(j).getObjective(k);
                }
                objectives = objectives.trim();

                String vars_string = "";
                Variable[] vars = solutions[i].get(j).getDecisionVariables();
                result.variables = new double[vars.length];
                int bar = 0;
                for (Variable foo : vars) {
                    vars_string += Math.round(foo.getValue()) + " ";
                    result.variables[bar] = Math.round(foo.getValue());
                    bar++;
                }
                vars_string = vars_string.trim();
                
                results.add(result);

                // if (objective < best_objective) {
                // results += objectives + " for " + vars_string + "\n";
                // best_objective = objective;
                // }
            }
        }

        // exp.generateQualityIndicators();

        // Generate latex tables
        // exp.generateLatexTables();
        // Configure the R scripts to be generated
        /* int rows;
         int columns;
         String prefix;
         String[] problems;
         boolean notch; */
        // Configuring scripts for ZDT
        /* rows = 3;
         columns = 2;
         prefix = new String("CcrEstimation");
         problems = new String[]{"CcrEstimation"};

         exp.generateRBoxplotScripts(rows, columns, problems, prefix, notch = false, exp);
         exp.generateRWilcoxonScripts(problems, prefix, exp); */
        // Applying Friedman test
        /* Friedman test = new Friedman(exp);
         test.executeTest("EPSILON");
         test.executeTest("HV");
         test.executeTest("SPREAD"); */
        // return results.trim();
        return results;
    } // main
} // StandardStudy


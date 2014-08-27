//  OKA1.java
//
//  Author:
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
package jmetal.problems;

import jmetal.core.Problem;
import jmetal.core.Solution;
import jmetal.core.Variable;
import jmetal.encodings.solutionType.BinaryRealSolutionType;
import jmetal.encodings.solutionType.IntSolutionType;
import jmetal.encodings.solutionType.RealSolutionType;
import jmetal.util.JMException;
import parallelismanalysis.Platform;
import parallelismanalysis.entities.ActivityPatternDiagram;
import parallelismanalysis.entities.patterns.ParallelDesignPattern;
import parallelismanalysis.optimization.Objective;
import parallelismanalysis.optimization.ObjectiveException;
import parallelismanalysis.optimization.Parameter;
import parallelismanalysis.util.ArrayHelper;

/**
 * Class representing problem OKA1
 */
public class PatternParameterOptimizationProblem extends Problem {

    private static ActivityPatternDiagram apd;
    private static ParallelDesignPattern[] patterns;
    private static Objective[] objectives;
    private static Parameter[] parameters;

    public static void setAPD(ActivityPatternDiagram apd) {
        PatternParameterOptimizationProblem.apd = apd;
    }

    public static void setObjectives(Objective[] objectives) {
        PatternParameterOptimizationProblem.objectives = objectives;
    }

    public static void setPatterns(ParallelDesignPattern[] patterns) {
        PatternParameterOptimizationProblem.patterns = patterns;
    }

    public static void setParameters(Parameter[] parameters) {
        PatternParameterOptimizationProblem.parameters = parameters;
    }

    public static Parameter[] getParameters() {
        return PatternParameterOptimizationProblem.parameters;
    }

    /**
     * Constructor. Creates a new instance of the OKA2 problem.
     *
     * @param solutionType The solution type must "Real" or "BinaryReal".
     */
    public PatternParameterOptimizationProblem(String solutionType) {
        numberOfVariables_ = parameters.length;
        numberOfObjectives_ = objectives.length;
        numberOfConstraints_ = 0;
        problemName_ = "PatternParameterOptimizationProblem";

        upperLimit_ = new double[numberOfVariables_];
        lowerLimit_ = new double[numberOfVariables_];

        // this.solutionType_ = new IntSolutionType(this);

        /*
         lowerLimit_[0] = Double.MIN_NORMAL; // 6 * Math.sin(Math.PI/12.0) ;
         upperLimit_[0] = Double.MAX_VALUE; // 6 * Math.sin(Math.PI/12.0) + 2 * Math.PI * Math.cos(Math.PI/12.0) ;    
         lowerLimit_[1] = Double.MIN_NORMAL; // 6 * Math.sin(Math.PI/12.0) ;
         upperLimit_[1] = Double.MAX_VALUE; // 6 * Math.sin(Math.PI/12.0) + 2 * Math.PI * Math.cos(Math.PI/12.0) ;    
         lowerLimit_[2] = Double.MIN_NORMAL; // 6 * Math.sin(Math.PI/12.0) ;
         upperLimit_[2] = Double.MAX_VALUE; // 6 * Math.sin(Math.PI/12.0) + 2 * Math.PI * Math.cos(Math.PI/12.0) ;    
         lowerLimit_[3] = Double.MIN_NORMAL; // 6 * Math.sin(Math.PI/12.0) ;
         upperLimit_[3] = Double.MAX_VALUE; // 6 * Math.sin(Math.PI/12.0) + 2 * Math.PI * Math.cos(Math.PI/12.0) ;    
         */
        for (int i = 0; i < numberOfVariables_; i++) {
            lowerLimit_[i] = parameters[i].getMin();
            upperLimit_[i] = parameters[i].getMax();
        }

        /* lowerLimit_[0] = 0;
         upperLimit_[0] = 1000;

         lowerLimit_[1] = 0;
         upperLimit_[1] = 1000;

         lowerLimit_[2] = 0;
         upperLimit_[2] = 1000;

         lowerLimit_[3] = 0;
         upperLimit_[3] = 10000000; */
        if (solutionType.compareTo("BinaryReal") == 0) {
            solutionType_ = new BinaryRealSolutionType(this);
        } else if (solutionType.compareTo("Real") == 0) {
            solutionType_ = new RealSolutionType(this);
        } else if (solutionType.compareTo("Integer") == 0) {
            solutionType_ = new IntSolutionType(this);
        } else {
            System.out.println("Error: solution type " + solutionType + " invalid");
            System.exit(-1);
        }

        // best_error = Double.MAX_VALUE;
        System.out.println("############# init done!");
    } // OKA1

    // private static double best_error = Double.MAX_VALUE;
    /**
     * Evaluates a solution
     *
     * @param solution The solution to evaluate
     * @throws JMException
     */
    @Override
    public void evaluate(Solution solution) throws JMException {
        Variable[] decisionVariables = solution.getDecisionVariables();

        // Get array for objective values
        double[] objective_values = new double[objectives.length];

        // Get variable values
        double[] values = new double[numberOfVariables_]; // 2 variables        
        for (int i = 0; i < numberOfVariables_; i++) {
            values[i] = Math.round(decisionVariables[i].getValue());
        }

        for (int i = 0; i < values.length; i++) {
            patterns[i].getParameter().setValue((int) Math.round(values[i]));
        }

        // System.out.println("Evaluating " + ArrayHelper.dats(values));
        if (objectives == null || objectives.length == 0) {
            System.out.println("No Objectives configured. EXIT!");
            System.exit(1);
        }

        // Calculate objective values
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

        /* for(DataPoint foo : data)
         fx[0] += foo.getSquaredError(x[0], x[1], x[2], x[3]);
        
         fx[0] = fx[0] / data.size(); */
        /* if(best_error > fx[0]) {
         System.out.println("BEST ERROR " + fx[0] + " for " + data.size() + " points");
         best_error = fx[0];
         } */
        // fx[0] = Math.abs(4 - x[0]) + Math.abs(3 - x[1]) + Math.abs(2 - x[2]) + Math.abs(1 - x[3]);
        // System.out.println("# Evaluation: " + fx[0] + " for " + x[0] + " " + x[1] + " " + x[2] + " " + x[3] + " ");
        
        // if(objective_values[1] == 6)
            // System.out.println("Evaluating " + ArrayHelper.dats(values) + " to " + ArrayHelper.dats(objective_values));

        for (int i = 0; i < objectives.length; i++) {
            solution.setObjective(i, objective_values[i]);
        }
    } // evaluate
} // OKA1

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.experiments.util;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.Algorithm;
import jmetal.core.Problem;
import jmetal.core.SolutionSet;
import jmetal.experiments.Experiment;
import static jmetal.experiments.util.RunExperiment.finished;
import jmetal.util.JMException;

/**
 *
 * @author jahrralf
 */
public class RunExperimentWithSolution extends RunExperiment {

    public RunExperimentWithSolution(Experiment experiment, HashMap<String, Object> map, int id, int numberOfThreads, int numberOfProblems) {
        super(experiment, map, id, numberOfThreads, numberOfProblems);
    }

    public SolutionSet resultFront = null;

    public void run() {
        Algorithm[] algorithm; // jMetal algorithms to be executed

        String experimentName = (String) map_.get("experimentName");
        experimentBaseDirectory_ = (String) map_.get("experimentDirectory");
        algorithmNameList_ = (String[]) map_.get("algorithmNameList");
        problemList_ = (String[]) map_.get("problemList");
        indicatorList_ = (String[]) map_.get("indicatorList");
        paretoFrontDirectory_ = (String) map_.get("paretoFrontDirectory");
        paretoFrontFile_ = (String[]) map_.get("paretoFrontFile");
        independentRuns_ = (Integer) map_.get("independentRuns");
        outputParetoFrontFile_ = (String) map_.get("outputParetoFrontFile");
        outputParetoSetFile_ = (String) map_.get("outputParetoSetFile");

        int numberOfAlgorithms = algorithmNameList_.length;

        algorithm = new Algorithm[numberOfAlgorithms];

        // Inicio modificación planificación Threads
        int[] problemData; // Contains current problemId, algorithmId and iRun

        while (!finished) {

            problemData = null;
            problemData = experiment_.getNextProblem();

            if (!finished && problemData != null) {
                int problemId = problemData[0];
                int alg = problemData[1];
                int runs = problemData[2];

                // The problem to solve
                Problem problem;
                String problemName;

                // STEP 2: get the problem from the list
                problemName = problemList_[problemId];

                // STEP 3: check the file containing the Pareto front of the problem
                // STEP 4: configure the algorithms
                try {
                    experiment_.algorithmSettings(problemName, problemId, algorithm);
                } catch (ClassNotFoundException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
                }

                problem = algorithm[0].getProblem();

                // STEP 5: run the algorithms
                // STEP 6: create output directories
                File experimentDirectory;
                String directory;

                directory = experimentBaseDirectory_ + "/data/" + algorithmNameList_[alg] + "/"
                        + problemList_[problemId];

                experimentDirectory = new File(directory);
                if (!experimentDirectory.exists()) {
                    boolean result = new File(directory).mkdirs();
                    System.out.println("Creating " + directory);
                }

                // STEP 7: run the algorithm
                System.out.println(Thread.currentThread().getName() + " Running algorithm: "
                        + algorithmNameList_[alg]
                        + ", problem: " + problemList_[problemId]
                        + ", run: " + runs);
                try {
                    try {
                        resultFront = algorithm[alg].execute();
                    } catch (ClassNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } catch (JMException ex) {
                    Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
                }

                // STEP 8: put the results in the output directory
                resultFront.printObjectivesToFile(directory + "/" + outputParetoFrontFile_ + "." + runs);
                resultFront.printVariablesToFile(directory + "/" + outputParetoSetFile_ + "." + runs);
                if (!finished) {
                    if (experiment_.finished_) {
                        finished = true;
                    }
                }
            } // if
        } //while
        // Fin modificación planificación Threads
    }
}

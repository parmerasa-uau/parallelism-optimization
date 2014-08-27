/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmetal.experiments;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import jmetal.core.SolutionSet;
import jmetal.experiments.Experiment;
import jmetal.experiments.util.RunExperiment;
import jmetal.experiments.util.RunExperimentWithSolution;
import jmetal.util.JMException;

/**
 *
 * @author jahrralf
 */
public abstract class ExperimentWithSolution extends Experiment {

    /**
     * Runs the experiment
     */
    public SolutionSet[] runExperimentWithSolution(int numberOfThreads) throws JMException, IOException {

        //initExperiment();
        //SolutionSet[] resultFront = new SolutionSet[algorithmNameList_.length];
        // Inicio modificación planificación Threads
        System.out.println("Experiment: Name: " + experimentName_);
        System.out.println("Experiment: creating " + numberOfThreads + " threads");
        System.out.println("Experiment: Number of algorithms: " + algorithmNameList_.length);
        System.out.println("Experiment: Number of problems: " + problemList_.length);
        System.out.println("Experiment: runs: " + independentRuns_);
        System.out.println("Experiment: Experiment directory: " + experimentBaseDirectory_);

        // Fin modificación planificación Threads
        RunExperimentWithSolution[] p = new RunExperimentWithSolution[numberOfThreads];
        SolutionSet[] solutions = new SolutionSet[numberOfThreads];

        for (int i = 0; i < numberOfThreads; i++) {
            //p[i] = new Experiment(map_, i, numberOfThreads, problemList_.length);
            p[i] = new RunExperimentWithSolution(this, map_, i, numberOfThreads, problemList_.length);
            p[i].start();
        }

        try {
            for (int i = 0; i < numberOfThreads; i++) {
                p[i].join();
                solutions[i] = p[i].resultFront;
            }
        } catch (InterruptedException ex) {
            Logger.getLogger(Experiment.class.getName()).log(Level.SEVERE, null, ex);
        }

        // Inicio modificación ReferenceFronts
        //generateQualityIndicators();
        // Fin modificación ReferenceFronts
        return solutions;
    }
}

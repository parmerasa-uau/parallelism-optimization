package parallelismanalysis.entities.patterns;

import java.util.HashSet;
import parallelismanalysis.optimization.Parameter;
import parallelismanalysis.optimization.ObjectiveException;
import parallelismanalysis.optimization.Objective;
import org.w3c.dom.Node;
import parallelismanalysis.Platform;
import parallelismanalysis.entities.Activity;
import parallelismanalysis.entities.ConsistencyException;

public class ParallelPipeline extends ParallelDesignPattern {
    
    public final static String XML_NODE_NAME = "parallel_pipeline";

    /**
     * Delay between pipeline stages
     */
    public int offset = 1;

    public ParallelPipeline(String title, int times) {
        super(title, times);
    }
    
    public ParallelPipeline(Node node, Activity parent) {
        super(node, parent);
        
        if(node.getAttributes().getNamedItem("iterations") != null) {
            System.out.println("Iterations: " + node.getAttributes().getNamedItem("iterations").getNodeValue());
            this.iterations =  Integer.parseInt(node.getAttributes().getNamedItem("iterations").getNodeValue());
        }
    }

    @Override
    public void checkConsistency() throws ConsistencyException {
        if (this.iterations == 0) {
            throw new ConsistencyException("Iterations for " + this + " cannot be 0");
        }
    }

    /**
     * PeriodicTaskParallelism can use any number of cores between 1 and MAX
     */
    @Override
    public void initParameter() {
        int cores = Platform.CORES;
        this.setParameter(new Parameter(1, Math.min(cores, this.stages.size())));
    }

    @Override
    protected double getObjectiveValueCores() throws ObjectiveException {
        if (getParameter().getValue() == 1 || iterations == 1) {
            double max_cores = 0;

            for (Activity a : stages) {
                max_cores = Math.max(max_cores, a.getObjectiveValue(Objective.OBJ_CORES));
            }

            return max_cores;
        } else if (stages.size() <= getParameter().getValue()) {
            int kernel_cores = 0;

            for (Activity a : stages) {
                kernel_cores += a.getObjectiveValue(Objective.OBJ_CORES);
            }

            return kernel_cores;
        } else {
            throw new ObjectiveException("Cores cannot be calculated for " + stages.size() + " stages and parameter " + getParameter().getValue());
        }
    }

    @Override
    protected double getObjectiveValueDuration() throws ObjectiveException {
        if(4 != 0) {
            throw new UnsupportedOperationException("Not supported yet.");
        }
        
        if (getParameter().getValue() == 1 || iterations == 1) {
            double total = 0;
            for (Activity a : stages) {
                total += a.getObjectiveValue(Objective.OBJ_DURATION);
            }
            return iterations * total;
        } else if (stages.size() <= getParameter().getValue()) {
            if (offset == 1) {
                double max_duration = Integer.MIN_VALUE;
                for (Activity a : stages) {
                    max_duration = Math.max(max_duration, a.getObjectiveValue(Objective.OBJ_DURATION));
                }

                double pro = (stages.size() - 1) * max_duration;
                double epi = (stages.size() - 1) * max_duration;
                double ker = (iterations - (stages.size() - 1)) * max_duration;

                return pro + ker + epi;
            } else {
                throw new UnsupportedOperationException("Not supported yet for offset != 1.");
            }
        } else {
            throw new ObjectiveException("Duration cannot be calculated for " + stages.size() + " stages and parameter " + getParameter().getValue());
        }
    }

    @Override
    public HashSet<String> getSyncGlobals() throws ObjectiveException {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    /* @Override
    public HashSet<String> getSyncGlobalsAccesses() throws ObjectiveException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    } */
}
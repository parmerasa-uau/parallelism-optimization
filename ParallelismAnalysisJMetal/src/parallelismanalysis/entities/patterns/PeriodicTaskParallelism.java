package parallelismanalysis.entities.patterns;

import parallelismanalysis.optimization.Parameter;
import parallelismanalysis.optimization.ObjectiveException;
import parallelismanalysis.optimization.Objective;
import java.util.HashMap;
import java.util.HashSet;
import org.w3c.dom.Node;
import parallelismanalysis.Platform;
import parallelismanalysis.entities.Activity;

public class PeriodicTaskParallelism extends ParallelDesignPattern {
    
    public final static String XML_NODE_NAME = "periodic_task_parallelism";

    // int iterations = 0;
    public HashMap<Activity, Integer> periods = new HashMap<Activity, Integer>();

    public PeriodicTaskParallelism(String title) {
        super(title);
    }

    public PeriodicTaskParallelism(String title, int times) {
        super(title, times);
    }
    
    public PeriodicTaskParallelism(Node node, Activity parent) {
        super(node, parent);
        
        if(node.getAttributes().getNamedItem("iterations") != null) {
            this.iterations = Integer.parseInt(node.getAttributes().getNamedItem("iterations").getNodeValue());
        }
    }

    public void addStage(Activity stage, int period) {
        stages.add(stage);
        periods.put(stage, period);
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
        if (getParameter().getValue() == 1) {
            double result = 0;
            for (Activity a : stages) {
                result = Math.max(result, a.getObjectiveValue(Objective.OBJ_CORES));
            }
            return result;
        } else if (stages.size() <= getParameter().getValue()) {
            double result = 0;
            for (Activity a : stages) {
                result += a.getObjectiveValue(Objective.OBJ_CORES);
            }
            return result;
        } else {
            throw new ObjectiveException("Cores cannot be calculated for " + stages.size() + " stages and parameter " + getParameter().getValue());
        }
    }

    @Override
    protected double getObjectiveValueDuration() throws ObjectiveException {
        if (getParameter().getValue() == 1) {
            // Duration is the times * max of the (a) max period and (b) sum of the overall weights
            long interval = 0;

            // Find max period -> only one is supported.
            for (Integer i : periods.values()) {
                interval = Math.max(i, interval);
            }

            // Find cost of executing all work sequentially
            int work = 0;
            for (Activity a : periods.keySet()) {
                work += a.getObjectiveValue(Objective.OBJ_DURATION);
            }
            
            if(work > interval) {
                return work * super.iterations;
            } else {
                return (super.iterations - 1) * interval + work;
            }
        } else if (stages.size() <= getParameter().getValue()) {
            // Duration is the times * max of the (a) max period and (b) the max overall weight
            double interval = 0;

            // Find max period -> only one is supported.
            for (Integer i : periods.values()) {
                interval = Math.max(i, interval);
            }

            // Find cost of executing all work in parallel
            double work = 0;
            for (Activity a : periods.keySet()) {
                work = Math.max(work, a.getObjectiveValue(Objective.OBJ_DURATION));
            }

            if(work > interval) {
                return work * super.iterations;
            } else {
                return (super.iterations - 1) * interval + work;
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
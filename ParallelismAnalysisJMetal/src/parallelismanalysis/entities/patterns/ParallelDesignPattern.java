package parallelismanalysis.entities.patterns;

import parallelismanalysis.optimization.Parameter;
import parallelismanalysis.optimization.ObjectiveException;
import parallelismanalysis.optimization.Objective;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import org.w3c.dom.Node;
import parallelismanalysis.entities.Activity;
import parallelismanalysis.entities.ConsistencyException;

abstract public class ParallelDesignPattern extends Activity {

    public ArrayList<Activity> stages = new ArrayList<Activity>();
    public int iterations = Integer.MIN_VALUE;
    private Parameter parameter = null;

    public ParallelDesignPattern(String title) {
        super(title);
    }

    public ParallelDesignPattern(String title, int times) {
        super(title, 0);
        this.iterations = times;
    }

    public ParallelDesignPattern(Node node, Activity parent) {
        super(node, parent);
    }

    @Override
    public void dump(int indent, String prefix, boolean with_objectives) {
        super.dump(indent, prefix, with_objectives);

        for (Activity a : stages) {
            a.dump(indent + 1, "PAR", with_objectives);
        }
    }

    @Override
    public void checkConsistency() throws ConsistencyException {
        if (stages.size() == 0) {
            throw new ConsistencyException("no stages defined");
        }
    }

    // @Override
    // abstract public int getOverallWeight(int mode);
    // @Override
    /* public int getOverallWeight() {
     return this.getOverallWeight(mode);
     } */
    @Override
    public String toString() {
        if (iterations == Integer.MIN_VALUE) {
            return "[" + this.getClass().getSimpleName() + "@" + this.getParameter() + "] " + "" + title + " (" + weight + ")";
        } else {
            return "[" + this.getClass().getSimpleName() + "@" + this.getParameter() + "] " + "" + title + " (" + weight + " x " + iterations + " iterations)";
        }
    }

    @Override
    public void collectParallelDesignPatterns(ArrayList<ParallelDesignPattern> list) {
        list.add(this);

        for (Activity a : stages) {
            a.collectParallelDesignPatterns(list);
        }
    }

    abstract public void initParameter();

    public Parameter getParameter() {
        if (this.parameter == null) {
            initParameter();
        }
        return this.parameter;
    }

    public void setParameter(Parameter p) {
        this.parameter = p;
    }

    @Override
    public double getObjectiveValue(Objective obj) throws ObjectiveException {
        try {
            if (Objective.OBJ_CORES.equals(obj)) {
                return getObjectiveValueCores();
            } else if (Objective.OBJ_DURATION.equals(obj)) {
                return getObjectiveValueDuration();
            } else if (Objective.OBJ_PATTERNS.equals(obj)) {
                return getObjectiveValuePatterns();
            } else if (Objective.OBJ_GLOBALS.equals(obj)) {
                return getObjectiveValueGlobals();
            /* } else if (Objective.OBJ_GLOBALS_ACCESSES.equals(obj)) {
                return getObjectiveValueGlobalsAccesses(); */
            } else {
                throw new ObjectiveException("Unknown objective " + obj);
            }
        } catch (ObjectiveException o) {
            throw o;
        }
    }

    /**
     * Calculates the number of max. occupied cores
     */
    protected abstract double getObjectiveValueCores() throws ObjectiveException;

    /**
     * Calculated the execution duration
     */
    protected abstract double getObjectiveValueDuration() throws ObjectiveException;

    /**
     * Calculate the number of globals to sync
     * @return 
     * @throws parallelismanalysis.optimization.ObjectiveException
     */
    protected double getObjectiveValueGlobals() throws ObjectiveException {
        return this.getSyncGlobals().size();
    }
    
    @Override
    public int countSyncGlobalAccesses(HashSet<String> all_globals) throws ObjectiveException {
        int result = 0;
        
        // System.out.println("START in " + this);
        
        for(Activity foo : stages) {
            result += foo.countSyncGlobalAccesses(all_globals);
        }

        // System.out.println("PDP: Acccessed globals for " + this + " is " + result);
        
        // System.out.println("STOP in " + this);
        
        return result;
    }
    
    /**
     * Calculate the number of accesses to globals to sync
     */
    /* protected double getObjectiveValueGlobalsAccesses() throws ObjectiveException {
        return this.getSyncGlobalsAccesses().size();
    } */

    /**
     * Calculates the number of patterns to be executed in a parallel way
     */
    protected double getObjectiveValuePatterns() throws ObjectiveException {
        int count = 1;
        if (getParameter().getValue() == 1) {
            count = 0;
        }

        for (int i = 0; i < stages.size(); i++) {
            count += stages.get(i).getObjectiveValue(Objective.OBJ_PATTERNS);
        }

        return count;
    }

    /**
     * Returns set union of all global sets of all stages assuming the stages
     * are executed in parallel
     */
    @Override
    public HashSet<String> getGlobals() {
        HashSet<String> result = new HashSet<>();

        for (Activity item : stages) {
            result.addAll(item.getGlobals());
        }

        return result;
    }

    /**
     * Returns the set of all globals which have to be synched
     */
    @Override
    public abstract HashSet<String> getSyncGlobals() throws ObjectiveException;
    
    /**
     * Returns the set of accesses to all globals which have to be synched
     */
    /* @Override
    public abstract HashSet<String> getSyncGlobalsAccesses() throws ObjectiveException; */
}
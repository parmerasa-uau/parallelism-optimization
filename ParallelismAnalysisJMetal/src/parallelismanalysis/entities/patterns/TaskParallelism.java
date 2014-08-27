package parallelismanalysis.entities.patterns;

import java.util.ArrayList;
import java.util.HashSet;
import parallelismanalysis.optimization.Parameter;
import parallelismanalysis.optimization.ObjectiveException;
import parallelismanalysis.optimization.Objective;
import org.w3c.dom.Node;
import parallelismanalysis.Platform;
import parallelismanalysis.entities.Activity;
import parallelismanalysis.util.NumberPacker;

public class TaskParallelism extends ParallelDesignPattern {

    public final static String XML_NODE_NAME = "task_parallelism";

    public TaskParallelism(String title) {
        super(title);
    }

    public TaskParallelism(Node node, Activity parent) {
        super(node, parent);
    }

    /**
     * TaskParallelism can use any number of cores between 1 and MAX
     */
    @Override
    public void initParameter() {
        int cores = Platform.CORES;
        this.setParameter(new Parameter(1, Math.min(cores, this.stages.size())));
    }

    @Override
    protected double getObjectiveValueCores() throws ObjectiveException {
        Activity[] items = new Activity[stages.size()];

        for (int i = 0; i < stages.size(); i++) {
            items[i] = stages.get(i);
        }

        Activity[][] packs = NumberPacker.getPacks(
                items,
                getParameter().getValue(),
                Objective.OBJ_DURATION,
                new ActivityComparator(Objective.OBJ_DURATION));

        double max_sum = 0;
        for (int j = 0; j < packs[0].length; j++) {
            double sum = 0;
            for (int i = 0; i < packs.length; i++) {
                if (packs[i][j] != null) {
                    sum += packs[i][j].getObjectiveValue(Objective.OBJ_CORES);
                }
            }
            max_sum = Math.max(max_sum, sum);
        }

        return max_sum;
    }

    @Override
    protected double getObjectiveValueDuration() throws ObjectiveException {
        Activity[] items = new Activity[stages.size()];

        for (int i = 0; i < stages.size(); i++) {
            items[i] = stages.get(i);
        }

        // Calculate rounds
        Activity[][] packs = NumberPacker.getPacks(
                items,
                getParameter().getValue(),
                Objective.OBJ_DURATION,
                new ActivityComparator(Objective.OBJ_DURATION));

        // Calculate max duration of a round
        double max_sum = 0;
        for (int i = 0; i < packs.length; i++) {
            double sum = 0;
            for (int j = 0; j < packs[i].length; j++) {
                if (packs[i][j] != null) {
                    sum += packs[i][j].getObjectiveValue(Objective.OBJ_DURATION);
                }
            }
            max_sum = Math.max(max_sum, sum);
        }
        
        // Add overheads
        double result = max_sum;
        
        /* if(getParameter().getValue() > 1) {
            result += Platform.OVERHEAD_PER_SKELETON;
            result += packs.length * Platform.OVERHEAD_PER_THREAD;
            result += packs[0].length * Platform.OVERHEAD_PER_TASK_TP;
            result += stages.size() * Platform.OVERHEAD_PER_TASK;
        } */
        
        result += Platform.OVERHEAD_PER_SKELETON[getParameter().getValue() - 1];

        return result;
    }

    /**
     * Returns set of all globals which have to be synched
     */
    @Override
    public HashSet<String> getSyncGlobals() throws ObjectiveException {
        HashSet<String> result = new HashSet<>();

        if (this.getParameter().getValue() == 1) {
            // Sequential -> No additional sync!
        } else {
            // Create packs
            Activity[] items = new Activity[stages.size()];

            for (int i = 0; i < stages.size(); i++) {
                items[i] = stages.get(i);
            }

            Activity[][] packs = NumberPacker.getPacks(
                    items,
                    getParameter().getValue(),
                    Objective.OBJ_DURATION,
                    new ActivityComparator(Objective.OBJ_DURATION));


            // Calculate globals in all the packs
            ArrayList<HashSet<String>> vars_in_packs = new ArrayList<>();

            for (int pack = 0; pack < packs.length; pack++) {
                HashSet<String> vars = new HashSet<>();

                for (Activity item : packs[pack]) {
                    if (item != null) {
                        // System.out.println("Globals for " + item);
                        vars.addAll(item.getGlobals());
                    }
                }

                vars_in_packs.add(vars);
            }

            // Add all variables which are used in more than one pack
            HashSet<String> seen_once = new HashSet<>();
            for (HashSet<String> vars_in_pack : vars_in_packs) {
                for (String foo : vars_in_pack) {
                    if (seen_once.contains(foo)) {
                        result.add(foo);
                    } else {
                        seen_once.add(foo);
                    }
                }
            }
        }

        for (Activity item : stages) {
            result.addAll(item.getSyncGlobals());
        }

        return result;
    }

    /* @Override
    public HashSet<String> getSyncGlobalsAccesses() throws ObjectiveException {
        HashSet<String> result = new HashSet<>();
        HashSet<String> globals = getSyncGlobals();
        
        for(Activity act : stages) {
            HashSet<String> my_mars = act.getGlobals();
            my_mars.retainAll(globals);
            
            for(String var : my_mars) {
                result.add(act.toString() + " >> " + var);
            }
        }
        
        for (Activity item : stages) {
            result.addAll(item.getSyncGlobalsAccesses());
        }
        
        return result;
    } */
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.optimization;

/**
 *
 * @author jahrralf
 */
public class Objective implements Comparable<Objective> {
    /** Execution duration */
    public final static Objective OBJ_DURATION = new Objective("Duration");
    
    /** Globals */
    public final static Objective OBJ_GLOBALS = new Objective("Globals");
    
    /** Approximated Accesses to Globals */
    public final static Objective OBJ_GLOBALS_ACCESSES = new Objective("Accesses to Globals");
    
    /** In parallel used cores for the execution */
    public final static Objective OBJ_CORES = new Objective("Cores");
    
    /** Patterns executed in parallel */
    public final static Objective OBJ_PATTERNS = new Objective("ParallelPatterns");
    
    /** Array with all Objective keys */
    public final static Objective[] OBJECTIVES = {OBJ_PATTERNS, OBJ_CORES, OBJ_DURATION};// OBJ_CORES
    
    private String title;
    
    private Objective(String title) {
        this.title = title;
    }
    
    @Override
    public String toString() {
        return title;
    }

    @Override
    public int compareTo(Objective t) {
        return this.title.compareTo(t.title);
    }

    @Override
    public boolean equals(Object o) {
        return this.title.equals(o.toString());
    }
}

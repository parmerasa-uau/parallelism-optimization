/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.entities.patterns;

import parallelismanalysis.optimization.ObjectiveException;
import java.util.Comparator;
import java.util.logging.Level;
import java.util.logging.Logger;
import parallelismanalysis.entities.Activity;
import parallelismanalysis.optimization.Objective;

/**
 *
 * @author jahrralf
 */
class ActivityComparator implements Comparator<Activity> {
    private Objective objective;

    public ActivityComparator(Objective objective) {
        this.objective = objective;
    }

    @Override
    public int compare(Activity a, Activity b) {
        Double x, y;
        try {
            x = new Double(a.getObjectiveValue(objective));
        } catch (ObjectiveException ex) {
            Logger.getLogger(ActivityComparator.class.getName()).log(Level.SEVERE, null, ex);
            x = Double.MAX_VALUE;
        }
        
        try {
            y = new Double(b.getObjectiveValue(objective));
        } catch (ObjectiveException ex) {
            Logger.getLogger(ActivityComparator.class.getName()).log(Level.SEVERE, null, ex);
            y = Double.MAX_VALUE;
        }
        
        return x.compareTo(y);
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.optimization;

/**
 *
 * @author jahrralf
 */
public class Parameter {
    private int value = 0;
    private int min, max;
    
    /** Created a new Parameter
     * @param min Minimum value (value is valid!)
     * @param max Maximum value (value is valid!)
     */
    public Parameter(int min, int max) {
        this.min = min;
        this.max = max;
        value = this.min;
    }

    /** Returns the current value of the parameter */
    public int getValue() {
        return value;
    }
    
    /** Sets the value of the parameter, which should be between min and max */
    public void setValue(int v) {
        this.value = v;
    }
    
    /** Returns the number of possible values for this parameter */
    public int getDomainSize() {
        return max - min + 1;
    }
    
    public int getMin() {
        return min;
    }
    
    public int getMax() {
        return max;
    }
    
    public String toString() {
        return "(" + min +  "|" + value + "|" + max + ")";
    }

    public int getRandom() {
        return min + (int)Math.round(Math.random() * (max - min));
    }
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.util;

import java.util.ArrayList;
import parallelismanalysis.optimization.Parameter;

/**
 *
 * @author jahrralf
 */
public class NumberGenerator {
    int[] elements;
    int[] displacement;
    int current = 0;
    
    public NumberGenerator(int[] elements) {
        this.elements = elements;
        this.displacement = new int[elements.length];
        for(int i = 0; i < displacement.length; i++) displacement[i] = 0;
    }
    
    public NumberGenerator(ArrayList<Parameter> paras) {
        this.elements = new int[paras.size()];
        this.displacement = new int[paras.size()];
        
        for(int i = 0; i < paras.size(); i++) {
            elements[i] = paras.get(i).getDomainSize();
            displacement[i] = paras.get(i).getMin();
        }
    }
    
    public void reset() {
        current = 0;
    }
    
    public int getCurrent() {
        return current;
    }
    
    public int size() {
        int result = 1;
        for(int i : elements) result *= (i);
        return result - 1;
    }
    
    public int[] next() {
        int[] number = new int[elements.length];
        
        int my_current = current;
        
        for(int i = 0; i < elements.length; i++) {
            number[i] = my_current % elements[i] + displacement[i];
            my_current = my_current / elements[i];
        }
        
        current++;
        
        return number;
    }
    
    public boolean hasNext() {
        return current <= size();
    }
    
    public static void main(String[] args) {
        System.out.println("START mit [4,2,5,4,3,3]");
        
        int[] ae = {4,2,5,4,3,3};
        NumberGenerator a = new NumberGenerator(ae);
        
        System.out.println("Size: " + a.size());
        
        while(a.hasNext()) {
            System.out.println(a.getCurrent() + "  " + ArrayHelper.iats(a.next()));
        } 
        
    }

    public void skip(int i) {
        this.current += i;
        
    }
    
}

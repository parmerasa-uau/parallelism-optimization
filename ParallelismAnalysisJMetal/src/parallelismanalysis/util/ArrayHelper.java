/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.util;

/**
 *
 * @author jahrralf
 */
public class ArrayHelper {
    /** Integer array to String */
    public static String iats(int[] elements) {
        String[] data = new String[elements.length];
        for(int i = 0; i < elements.length; i++) {
            data[i] = elements[i] + "";
        }
        return sats(data);
    }    
    
    /** Double array to String */
    public static String dats(double[] elements) {
        String[] data = new String[elements.length];
        for(int i = 0; i < elements.length; i++) {
            data[i] = elements[i] + "";
        }
        return sats(data);
    }    
    
    /** String array to String */
    public static String sats(String[] elements) {
        String result = "";
        for(int i = 0; i < elements.length; i++) {
            result += elements[i];
            if(i + 1 < elements.length) result += " ";
        }
        return result;
    }    
}

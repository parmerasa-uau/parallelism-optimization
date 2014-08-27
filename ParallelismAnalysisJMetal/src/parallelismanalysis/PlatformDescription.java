/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis;

import java.util.HashMap;

/**
 *
 * @author jahrralf
 */
class PlatformDescription {
    public final static String KEY_CORES = "cores";
    
    private static PlatformDescription instance = null;
    
    private PlatformDescription() {
        this.parameters.put(KEY_CORES, 4);
    }
    
    private HashMap<String, Integer> parameters = new HashMap<>();
    
    public static PlatformDescription getInstance() {
        if(instance == null) instance = new PlatformDescription();
        return instance;
    }
    
    public int getParameter(String key) {
        Integer result = this.parameters.get(key);
        if(result == null) System.out.println("Platform parameter " + key + " is not defined!");
        return result.intValue();
    }
}

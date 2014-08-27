/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis;

/**
 * @author jahrralf
 */
public class Platform {
    public final static int NONE = 0, LOW = 10000, MID = 50000, HIGH = 100000;
    // public final static int[] OVERHEAD_PER_SKELETON = {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
    public final static int[] OVERHEAD_PER_SKELETON = {25000, 104766, 144649, 184532, 224415, 264298, 304181, 344064, 383947, 423830, 463713, 503596, 543478, 583361, 623244, 663127, 703010, 742893, 782776, 822659, 862542, 902425, 942308, 982191, 1022074, 1061957, 1101840, 1141723, 1181606, 1221489, 1261372, 1301255, 1341138, 1381021, 1420904, 1460787, 1500670, 1540552, 1580435, 1620318, 1660201, 1700084, 1739967, 1779850, 1819733, 1859616, 1899499, 1939382, 1979265, 2019148, 2059031, 2098914};
    // public final static int OVERHEAD_PER_THREAD = LOW;
    // public final static int OVERHEAD_PER_TASK = NONE;
    // public final static int OVERHEAD_PER_TASK_TP = NONE;
    // public final static int OVERHEAD_PER_ROUND_DP = NONE;
    
    public final static int OVERHEAD_PER_GETSET = 1000;
    
    public final static int CORES = 64;
}

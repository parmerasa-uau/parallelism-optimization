/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package jmetal.contrib;

/**
 *
 * @author jahrralf
 */
public class MyResult {
    public double variables[];
    public double objectives[];
    
    public String toString() {
        return ArrayHelper.dats(objectives) + " for " + ArrayHelper.dats(variables);
    }
}

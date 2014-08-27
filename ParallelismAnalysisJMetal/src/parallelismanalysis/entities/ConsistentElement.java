/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.entities;

/**
 *
 * @author jahrralf
 */
public interface ConsistentElement {

    public void checkConsistency() throws ConsistencyException;
}

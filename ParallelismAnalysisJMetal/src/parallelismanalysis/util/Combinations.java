/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.util;

/*************************************************************************
 *  Compilation:  javac Combinations.java
 *  Execution:    java Combinations N k 
 *  
 *  Enumerates all subsets of size k on N elements.
 *  Uses some String library functions.
 *
 *  % java Combinations 5 3
 *  abc
 *  abd
 *  abe
 *  acd
 *  ace
 *  ade
 *  bcd
 *  bce
 *  bde
 *  cde
 *
 * http://www.comscigate.com/cs/IntroSedgewick/20elements/27recursion/Combinations.java
 *************************************************************************/

public class Combinations {

    // print all subsets that take k of the remaining elements, with given prefix 
    public static void generate(String prefix, String elements, int k) {
        if (k == 0) {
            System.out.println(prefix);
            return;
        }
        for (int i = 0; i < elements.length(); i++) {
            generate(prefix + elements.charAt(i), elements.substring(i + 1), k - 1);
        }
    }

    // read in N and k from command line, and print all subsets of size k from N elements
    public static void main(String[] args) {
        int N = 5; // Integer.parseInt(args[0]);
        int k = 5; // Integer.parseInt(args[1]);
        String elements = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
        generate("", elements.substring(0, N), k);
    }
}



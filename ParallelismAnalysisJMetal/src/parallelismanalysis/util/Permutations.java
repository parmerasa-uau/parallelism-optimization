/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package parallelismanalysis.util;

/*************************************************************************
 *  Compilation:  javac Permutations.java
 *  Execution:    java Permutations N
 *  
 *  Enumerates all permutations on N elements.
 *
 *  % java Permutations 3
 *  bca
 *  cba
 *  cab
 *  acb
 *  bac
 *  abc
 * 
 *  % java Permutations 3 | sort
 *  abc
 *  acb
 *  bac 
 *  bca
 *  cab
 *  cba
 *
 * http://www.comscigate.com/cs/IntroSedgewick/20elements/27recursion/Permutations.java
 *************************************************************************/

public class Permutations {

    // swap the characters at indices i and j
    public static void swap(char[] a, int i, int j) {
        char c;
        c = a[i];
        a[i] = a[j];
        a[j] = c;
    }

    // print n! permutation of the characters of a
    public static void enumerate(char[] a, int n) {
        if (n == 1) {
            System.out.println(a);
            return;
        }
        for (int i = 0; i < n; i++) {
            swap(a, i, n - 1);
            enumerate(a, n - 1);
            swap(a, i, n - 1);
        }
    }

    public static void main(String[] args) {
        int N = 6; // Integer.parseInt(args[0]);
        String elements = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // initialize a[i] to ith letter, starting at 'a'
        char[] a = new char[N];
        for (int i = 0; i < N; i++) {
            a[i] = elements.charAt(i);
        }

        enumerate(a, N);
    }
}

package Heuristics;

import DataStructures.Node;

/**
 * Created by Administrator on 3/30/2015.
 */
public class WeightedAStar extends Heuristic {

    private int W;

    public WeightedAStar(Node initialState) {
        super(initialState);
        W = 5;
    }

    public int f(Node n) {
        return n.g() + W * h(n);
    }

    public String toString() {
        return String.format("WA*(%d) evaluation", W);
    }
}
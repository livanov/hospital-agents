package Heuristics;

import DataStructures.Node;

/**
 * Created by Administrator on 3/30/2015.
 */
public class AStar extends Heuristic {

    public AStar(Node initialState) {
        super(initialState);
    }

    public int f(Node n) {
        return n.g() + h(n);
    }

    public String toString() {
        return "A* evaluation";
    }
}
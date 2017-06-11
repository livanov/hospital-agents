package Heuristics;

import DataStructures.Node;

public class Greedy extends Heuristic {

    public Greedy(Node initialState) {
        super(initialState);
    }

    public int f(Node n) {
        return h(n);
    }

    public String toString() {
        return "Greedy evaluation";
    }
}
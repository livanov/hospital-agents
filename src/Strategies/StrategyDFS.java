package Strategies;

import DataStructures.Node;

import java.util.ArrayDeque;

public class StrategyDFS extends Strategy {
    private ArrayDeque< Node > frontier;

    public StrategyDFS() {
        super();
        frontier = new ArrayDeque<Node>();
    }

    public Node getAndRemoveLeaf() {
        return frontier.pop();
    }

    public void addToFrontier( Node n ) {
        frontier.push(n);
    }

    public int countFrontier() {
        return frontier.size();
    }

    public boolean frontierIsEmpty() {
        return frontier.isEmpty();
    }

    public boolean inFrontier( Node n ) {
        return frontier.contains(n);
    }

    public String toString() {
        return "Depth-first Search";
    }
}
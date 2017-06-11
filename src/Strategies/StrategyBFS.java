package Strategies;

import DataStructures.Node;
import java.util.ArrayDeque;

public class StrategyBFS extends Strategy {

    private ArrayDeque<Node> frontier;

    public StrategyBFS() {
        super();
        frontier = new ArrayDeque<Node>();
    }

    public Node getAndRemoveLeaf() {
        return frontier.pollFirst();
    }

    public void addToFrontier( Node n ) {
        frontier.addLast( n );
    }

    public int countFrontier() {
        return frontier.size();
    }

    public boolean frontierIsEmpty() {
        return frontier.isEmpty();
    }

    public boolean inFrontier( Node n ) {
        return frontier.contains( n );
    }

    public String toString() {
        return "Breadth-first Search";
    }
}
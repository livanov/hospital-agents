package Strategies;

import DataStructures.Memory;
import DataStructures.Node;

import java.util.HashSet;

public abstract class Strategy {

//    public HashSet<Node> explored;
        public HashSet<String> explored;
    public long startTime = System.currentTimeMillis();

    public Strategy() {
//        explored = new HashSet<Node>();
        explored = new HashSet<String>();
    }

    public void addToExplored(Node n) {
//        explored.add(n);
        explored.add(n.encode());
    }

    public boolean isExplored(Node n) {
//        return explored.contains( n );
        return explored.contains(n.encode());
    }

    public int countExplored() {
        return explored.size();
    }

    public String searchStatus() {
        return String.format("#Explored: %4d, #Frontier: %3d, Time: %3.2f s \t%s", countExplored(), countFrontier(), timeSpent(), Memory.stringRep());
    }

    public float timeSpent() {
        return (System.currentTimeMillis() - startTime) / 1000f;
    }

    public abstract Node getAndRemoveLeaf();

    public abstract void addToFrontier(Node n);

    public abstract boolean inFrontier(Node n);

    public abstract int countFrontier();

    public abstract boolean frontierIsEmpty();

    public abstract String toString();
}

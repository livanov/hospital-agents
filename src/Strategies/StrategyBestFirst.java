package Strategies;

import DataStructures.Node;
import Heuristics.Heuristic;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Created by Administrator on 3/30/2015.
 */
public class StrategyBestFirst extends Strategy {

    private Heuristic heuristic;

    private HashSet<String> front;
    private PriorityQueue<Node> frontier;
    //TODO: only for debugging, delete later
//    public List<Node> fronttt;

    public StrategyBestFirst( Heuristic h ) {
        super();
        heuristic = h;
        frontier = new PriorityQueue<Node>(100, h);
        front = new HashSet<String>(100);
//        fronttt = new LinkedList<Node>();
    }

    public Node getAndRemoveLeaf() {
        Node headNode = frontier.poll();
        front.remove(headNode.encode());
//        fronttt.remove(headNode);
        return headNode;
    }

    public void addToFrontier( Node n ) {
        frontier.add(n);
        front.add(n.encode());
//        fronttt.add(n);
    }

    public int countFrontier() {
        return frontier.size();
    }

    public boolean frontierIsEmpty() {
        return frontier.isEmpty();
    }

    public boolean inFrontier( Node n ) {
        return front.contains(n.encode());
    }

    public String toString() {
        return "Best-first Search (PriorityQueue) using " + heuristic.toString();
    }
}
import DataStructures.Memory;
import DataStructures.Node;
import Strategies.Strategy;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

public class SearchClient {

    public static void error(String msg) throws Exception {
        throw new Exception("GSCError: " + msg);
    }

    private Node initialState = null;

    public SearchClient(Node initialState){
        this.initialState = initialState;
    }

    public LinkedList<Node> Search(Strategy strategy) throws IOException {
//        System.err.format("Search starting with strategy %s\n", strategy);
        strategy.addToFrontier(this.initialState);

        int iterations = 0;
        while (true) {
            if (iterations % 200 == 0) {
                Main.maxMemory = Math.max(Main.maxMemory, Memory.used());
                System.err.println(strategy.searchStatus());
            }
//            if (Memory.shouldEnd()) {
//                System.err.format("Memory limit almost reached, terminating search %s\n", Memory.stringRep());
//                return null;
//            }
//            if (strategy.timeSpent() > 300) { // Minutes timeout
//                System.err.format("Time limit reached, terminating search %s\n", Memory.stringRep());
//                return null;
//            }

            if (strategy.frontierIsEmpty()) {
                return null;
            }

            Node leafNode = strategy.getAndRemoveLeaf();

            if (leafNode.isGoalState()) {
                return leafNode.extractPlan();
            }

            strategy.addToExplored(leafNode);
            ArrayList<Node> expandedNodes = leafNode.getExpandedNodes();
            for (Node n : expandedNodes) {
                if (!strategy.isExplored(n) && !strategy.inFrontier(n)) {
                    strategy.addToFrontier(n);
                }
            }
            iterations++;
        }
    }
}

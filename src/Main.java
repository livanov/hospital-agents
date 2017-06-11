import DataStructures.*;
import Heuristics.AStar;
import Heuristics.Greedy;
import Heuristics.WeightedAStar;
import Strategies.Strategy;
import Strategies.StrategyBFS;
import Strategies.StrategyBestFirst;
import Strategies.StrategyDFS;

import java.io.*;
import java.util.*;

public class Main {

    private static boolean fromFile = false;

    private static int totalSolutionLength;
    private static long startTime;

    private static BufferedReader serverMessages;

    public static float maxMemory;

    public static void main(String[] args) throws Exception {
        serverMessages = GetInputSource(args);

        // Read level and create the initial state of the problem
        ReadInput(serverMessages);

        TreeMap<Agent, List<Node>> solutions = new TreeMap<Agent, List<Node>>();

        startTime = System.currentTimeMillis();

        while (!Level.AreGoalsSatisfied()) {

            for (Agent agent : Level.getAgents()) {
                if (agent.isFree()) {

                    agent.setBusy();

                    List<Node> agentPlan = CreatePlan(agent, args);

                    //if there is no solution or no goals
                    if (agentPlan == null) {
                        agent.setTakenGoalFree();
                        agent.setFree();
                    }

                    solutions.put(agent, agentPlan);
                }
            }

            ExecutePlans(solutions);
        }

        PrintTotals();
    }

    private static List<Node> CreatePlan(Agent agent, String[] args) throws IOException {
        Integer goal = Level.getGoalFor(agent);
        if (goal == -1) {
            return null;
        }
        Integer box = Level.getBoxFor(agent);

        Node initialState = new Node(null);
        initialState.agentHashCoordinates = agent.hashCoordinates;
        initialState.updateFrom(Level.state);
        initialState.setDedicatedGoal(box, goal);

        System.err.println("Starting search with goal " + goal + " and box " + box);

        SearchClient client = new SearchClient(initialState);
        Strategy strategy = getStrategy(initialState, args);
        List<Node> solution = client.Search(strategy);

        agent.countExplored += strategy.explored.size();
        agent.countFrontier += strategy.countFrontier();

        return solution;
    }

    private static void ExecutePlans(TreeMap<Agent, List<Node>> solutions) throws IOException {

        Boolean needReplan = false;
        Random random = new Random();

        while (!needReplan) {
            List<String> jointAction = new LinkedList<String>();
            TreeMap<Agent, Command> updateActions = new TreeMap<Agent, Command>();

            for (Agent agent : Level.getAgents()) {

                List<Node> list = solutions.get(agent);

                //If there are any moves planned
                if (list != null && !list.isEmpty()) {

                    //If this is the last planned move - replan on next step
                    if (list.size() == 1) {
                        needReplan = true;
                        agent.setFree();
                        agent.setTakenGoalFree();
                        Level.setBoxFree(agent.name);
                    }

                    Node n = list.get(0);
                    updateActions.put(agent, n.action);
                    jointAction.add(n.action.toString());
                } else {
                    jointAction.add("NoOp");
                    needReplan = true;
                    agent.setFree();
                    agent.setTakenGoalFree();
                    Level.setBoxFree(agent.name);
                    updateActions.put(agent, null);
                }
            }

            totalSolutionLength++;

            if (fromFile) {
                //if it is from file, just update regardless validity of actions
                int i = 0;
                for (Map.Entry<Agent, Command> entry : updateActions.entrySet()) {
                    char agentName = entry.getKey().name;
                    Command command = entry.getValue();

                    if (command != null) {
                        Level.update(agentName, command);
                    }
                }

                continue;
            } else {
                //if it is not from file, write on console
                System.out.format("[%s]\n", String.join(",", jointAction));
            }

            String response = serverMessages.readLine();

            // Response message is of type [ True, False .... ]
            // Substring removes [] brackets
            response = response.substring(1, response.length() - 1);

            String[] splitResponse = response.split(",");

            for (Agent agent : Level.getAgents()) {
                Command command = updateActions.get(agent);
                String result = splitResponse[agent.name - '0'];

                // If result is "True"
                if (result.trim().equalsIgnoreCase("true")) {
                    if (command != null) {
                        Level.update(agent.name, command);
                        agent.retryCounter = 0;
                        solutions.get(agent).remove(0);
                    }
                } else {
                    //Then it should be "False"
                    agent.retryCounter++;
                    if (agent.retryCounter > random.nextInt(4) + 2) {
                        needReplan = true;
                        agent.setFree();
                        agent.setTakenGoalFree();
                        Level.setBoxFree(agent.name);
                    }
                }
            }
        }
    }

    private static Strategy getStrategy(Node initialState, String[] commandLineArguments) {
        if (commandLineArguments.length >= 1) {
            String alg = "astar";
            for (String cmdParameter : commandLineArguments) {
                if (cmdParameter.startsWith("-alg=")) {
                    alg = cmdParameter.replace("-alg=", "");
                    break;
                }
            }

            if (alg.equalsIgnoreCase("dfs")) {
                return new StrategyDFS();
            } else if (alg.equalsIgnoreCase("bfs")) {
                return new StrategyBFS();
            } else if (alg.equalsIgnoreCase("astar")) {
                return new StrategyBestFirst(new AStar(initialState));
            } else if (alg.equalsIgnoreCase("wastar")) {
                return new StrategyBestFirst(new WeightedAStar(initialState));
            } else if (alg.equalsIgnoreCase("greedy")) {
                return new StrategyBestFirst(new Greedy(initialState));
            } else {
                System.err.println("Unrecognized strategy - " + alg + ". Try with - DFS, BFS, AStar, WAStar, Greedy");
                System.exit(0);
            }
        }

        return new StrategyBestFirst(new AStar(initialState));
    }

    private static BufferedReader GetInputSource(String[] args) throws FileNotFoundException {
        BufferedReader serverMessages = new BufferedReader(new InputStreamReader(System.in));
        if (args.length >= 1) {
            for (String arg : args) {
                if (arg.startsWith("-file=")) {
                    serverMessages = new BufferedReader(new FileReader(arg.replace("-file=", "")));

                    fromFile = true;

                    System.setErr(new PrintStream(new OutputStream() {
                        public void write(int b) {
                        }
                    }));

                    break;
                }
            }
        }
        return serverMessages;
    }

    private static void ReadInput(BufferedReader serverMessages) throws IOException {
        final String colorDefinitionPattern = "^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$";

        String currentLine = serverMessages.readLine();

        while (currentLine.matches(colorDefinitionPattern)) {
            currentLine = currentLine.replaceAll("\\s", "");
            String[] colonSplit = currentLine.split(":");
            Color color = Color.valueOf(colonSplit[0].trim().toUpperCase());

            for (String id : colonSplit[1].split(",")) {
                Level.addObjectColor(id.trim().charAt(0), color);
            }

            currentLine = serverMessages.readLine();
        }

        int row = 0;
        while (currentLine != null && !currentLine.equals("")) {
            int col;
            for (col = 0; col < currentLine.length(); col++) {
                char chr = currentLine.charAt(col);
                if ('+' == chr) { // Walls
                    Level.addWall(row, col);
                } else if ('0' <= chr && chr <= '9') { // Agents
                    Level.addAgent(row, col, chr);
                } else if ('A' <= chr && chr <= 'Z') { // Boxes
                    Level.addBox(row, col, chr);
                } else if ('a' <= chr && chr <= 'z') { // Goal cells
                    Level.addGoal(row, col, chr);
                }
            }

            Level.MAX_COLUMN = Math.max(col, Level.MAX_COLUMN);

            row++;
            currentLine = serverMessages.readLine();
        }

        serverMessages.readLine();

        Level.MAX_ROW = row;
    }

    private static void PrintTotals() {

        PrintStream printStream;

        if (fromFile) {
            printStream = System.out;
        } else {
            printStream = System.err;
        }

        printStream.format("Solution length: %14d\n", totalSolutionLength);
        printStream.format("Time taken:     %14.2fs\n", (System.currentTimeMillis() - startTime) / 1000f);
        printStream.format("Max memory used:%12.2f MB\n", maxMemory);
        printStream.format("Encode counter:  %14d\n", Node.encodeCounter);
        printStream.println();
        printStream.println("          Explored     Frontier");

        for (Agent agent : Level.getAgents()) {
            printStream.format("Agent %c: %9d    %9d\n", agent.name, agent.countExplored, agent.countFrontier);
        }

//        printStream.println("");
//        printStream.println("Hash count: "+ Node.hashCount);
//        printStream.println("Equals count: "+ Node.equalsCount);
//        printStream.println("Failed count: "+ Node.failedEqualsCount);
    }
}

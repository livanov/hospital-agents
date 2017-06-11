package DataStructures;

import java.lang.reflect.Array;
import java.util.*;

public class GoalPrioritize {
    private static int maxRows;
    private static int maxCols;

    private static boolean enablePrinting = false;

    private static Map<Integer, Goal> goals;
    private static Set<Integer> walls;
    private static List<Goal> goalDependencies;

    public GoalPrioritize(Set<Integer> walls, Map<Integer, Goal> goals, int maxRows, int maxCols, int agentHashCoordinates) {
        this.walls = walls;
        this.goals = goals;
        this.maxRows = maxRows;
        this.maxCols = maxCols;

        Coordinates agentCoordinates = new Coordinates(agentHashCoordinates);

        goalDependencies = GetGoalDependencies(agentCoordinates);

    }

    public List<Goal> prioritizeFor(int agentHashCoordinates) {

        Coordinates agentCoordinates = new Coordinates(agentHashCoordinates);

//        List<Goal> goalDependencies = GetGoalDependencies(agentCoordinates);

        //key is goal hash coordinate
        //value is distance to goal from agent
        Map<Integer, Integer> goalDistances = GetGoalDistances(agentCoordinates);

        return PrioritizeGoals(goalDependencies, goalDistances);
    }

    private List<Goal> PrioritizeGoals(List<Goal> goalDependencies, final Map<Integer, Integer> goalDistances) {
        List<Goal> sortedGoals = new ArrayList<Goal>(goalDependencies);

        Collections.sort(sortedGoals, new Comparator<Goal>() {
            @Override
            public int compare(Goal o1, Goal o2) {
                return goalDistances.get(o1.hashCoordinates).compareTo(goalDistances.get(o2.hashCoordinates));
            }
        });

        List<Goal> prioritizedGoals = new LinkedList<Goal>();

        while(!sortedGoals.isEmpty()){
            for (Goal goal : sortedGoals){
                if(goal.dependentGoals == null || prioritizedGoals.containsAll(goal.dependentGoals)){
                    prioritizedGoals.add(goal);
                    sortedGoals.remove(goal);
                    break;
                }
            }
        }

        return prioritizedGoals;
    }

    /////////////////// GOAL DISTANCE SECTION /////////////////////////////

    private Map<Integer, Integer> GetGoalDistances(Coordinates agentCoordinates) {
        Integer[][] pathMap = InitializePathMap(maxRows, maxCols, Integer.MAX_VALUE);
        PathFlood(pathMap, agentCoordinates.row, agentCoordinates.col, 0);

        Map<Integer, Integer> paths = new HashMap<Integer, Integer>();

        if (enablePrinting) {
            PrintArray(pathMap, agentCoordinates);
        }

        for (Goal goal : goals.values()) {
            Coordinates coordinates = new Coordinates(goal.hashCoordinates);

            paths.put(goal.hashCoordinates, pathMap[coordinates.row][coordinates.col]);
        }

        return paths;
    }

    private static void PathFlood(Integer[][] pathMap, int row, int col, int pathLength) {
        if (row < 0 || row >= pathMap.length) {
            return;
        }

        if (col < 0 || col >= pathMap[row].length) {
            return;
        }

        if (walls.contains(Coordinates.hashCode(row, col))) {
            return;
        }

        if (pathLength < pathMap[row][col]) {
            pathMap[row][col] = pathLength++;
        } else {
            return;
        }

        PathFlood(pathMap, row + 1, col, pathLength);
        PathFlood(pathMap, row - 1, col, pathLength);
        PathFlood(pathMap, row, col + 1, pathLength);
        PathFlood(pathMap, row, col - 1, pathLength);
    }

    private static Integer[][] InitializePathMap(int rows, int cols, int defaultValue) {
        Integer[][] map = new Integer[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                map[i][j] = defaultValue;
            }
        }

        return map;
    }

    /////////////////// GOAL DEPENDENCIES SECTION /////////////////////////

    private List<Goal> GetGoalDependencies(Coordinates agentCooridnates) {
        List<Integer>[][] goalDependencyMap = InitializeDependencyMap(maxRows, maxCols);

        DependencyFlood(goalDependencyMap, agentCooridnates.row, agentCooridnates.col, new ArrayList<Integer>());

        Map<Integer, Goal> goalsWithDependencies = new HashMap<Integer, Goal>(goals.size());

        for (Goal goal : goals.values()) {
            goalsWithDependencies.put(goal.hashCoordinates, goal);
        }

        if (enablePrinting) {
            PrintArray(goalDependencyMap, agentCooridnates);
        }

        for (Goal goal : goalsWithDependencies.values()) {

            Coordinates goalCoordinates = new Coordinates(goal.hashCoordinates);
            List<Integer> goalsOnTheWay = goalDependencyMap[goalCoordinates.row][goalCoordinates.col];

            for (int goalHashOnTheWay : goalsOnTheWay) {
                Goal goalOnTheWay = goalsWithDependencies.get(goalHashOnTheWay);

                if (goalOnTheWay.hashCoordinates != goal.hashCoordinates) {
                    goalOnTheWay.addDependentGoal(goal);
//                    goalsWithDependencies.add(goalOnTheWay);
                }
            }
        }

        return new LinkedList<Goal>(goalsWithDependencies.values());
    }

    private static List<Integer>[][] InitializeDependencyMap(int rows, int cols) {
        return (List<Integer>[][]) Array.newInstance((new LinkedList<Integer>()).getClass(), rows, cols);
    }

    private static void DependencyFlood(List<Integer>[][] goalDependencyMap, int row, int col, List<Integer> parent) {
        if (row < 0 || row >= goalDependencyMap.length) {
            return;
        }

        if (col < 0 || col >= goalDependencyMap[row].length) {
            return;
        }

        int hashCode = Coordinates.hashCode(row, col);

        if (walls.contains(hashCode)) {
            return;
        }

        if (goalDependencyMap[row][col] == null || parent.size() < goalDependencyMap[row][col].size()) {
            goalDependencyMap[row][col] = new LinkedList<Integer>(parent);
            if (goals.containsKey(hashCode)) {
                goalDependencyMap[row][col].add(hashCode);
            }
        } else {
            return;
        }

        DependencyFlood(goalDependencyMap, row + 1, col, goalDependencyMap[row][col]);
        DependencyFlood(goalDependencyMap, row - 1, col, goalDependencyMap[row][col]);
        DependencyFlood(goalDependencyMap, row, col + 1, goalDependencyMap[row][col]);
        DependencyFlood(goalDependencyMap, row, col - 1, goalDependencyMap[row][col]);

    }

    /////////////////// PRINT METHOD //////////////////////////////////////

    private <T> void PrintArray(T[][] array, Coordinates agentCoordinates) {
        for (int row = 0; row < array.length; row++) {
            for (int col = 0; col < array[row].length; col++) {

                int hashCode = Coordinates.hashCode(row, col);

                if (goals.containsKey(hashCode) || (row == agentCoordinates.row && col == agentCoordinates.col)) {

                    int value = array[row][col] instanceof LinkedList<?>
                            ? ((List<Integer>) array[row][col]).size()
                            : (Integer) array[row][col];

                    System.out.format("%3d", value);
                } else if (walls.contains(hashCode)) {
                    System.out.format("  +");
                } else {
                    System.out.format("   ");
                }
            }
            System.out.println();
        }
    }
}

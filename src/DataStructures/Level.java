package DataStructures;

import java.util.*;

public final class Level {

    public static int MAX_ROW = 100;
    public static int MAX_COLUMN = 100;

    private static Set<Integer> walls = new HashSet<Integer>();
    private static Map<Character, Color> colors = new HashMap<Character, Color>();

    private static Map<Integer, Character> takenBoxes = new HashMap<Integer, Character>();

//    private static Map<Integer, Box> boxes = new HashMap<Integer, Box>();

    private static Map<Integer, Goal> goalsByCoordinates2 = new HashMap<Integer, Goal>();
    private static Map<Character, LinkedList<Goal>> goalsByCharacter2 = new HashMap<Character, LinkedList<Goal>>();

//    private static Map<Character, Queue<Goal>> goals;

    private static Map<Character, Agent> agentsByName = new TreeMap<Character, Agent>();

    private static GoalPrioritize goalPrioritize;

    public static Node state = new Node(null);

    private Level() {
    }

    public static Collection<Agent> getAgents() {
        return agentsByName.values();
    }

    public static char getGoal(int row, int col) {
        return goalsByCoordinates2.get(Coordinates.hashCode(row, col)).letter;
    }

    public static void addWall(int row, int col) {
        walls.add(Coordinates.hashCode(row, col));
    }

    public static void addGoal(int row, int col, char goalLetter) {
        Integer coordinates = Coordinates.hashCode(row, col);

        Goal goal = new Goal(coordinates, goalLetter, Status.FREE);

        goalsByCoordinates2.put(coordinates, goal);

        if (goalsByCharacter2.containsKey(goalLetter)) {
            goalsByCharacter2.get(goalLetter).add(goal);
        } else {
            LinkedList<Goal> newGoals = new LinkedList<Goal>();
            newGoals.add(goal);
            goalsByCharacter2.put(goalLetter, newGoals);
        }
    }

    public static void addObjectColor(Character object, Color color) {
        colors.put(object, color);
    }

    public static void addAgent(int row, int col, char agentName) {

        if (!colors.containsKey(agentName)) {
            colors.put(agentName, Color.BLUE);
        }

        Integer coordinates = Coordinates.hashCode(row, col);
//        agents.put(coordinates, chr);

        Agent agent = new Agent();
        agent.color = colors.get(agentName);
        agent.hashCoordinates = coordinates;
        agent.name = agentName;
        agent.status = Status.FREE;

        agentsByName.put(agentName, agent);
        state.addAgent(coordinates, agentName);
    }

    public static void addBox(int row, int col, char boxLetter) {
        Integer hashCoordinates = Coordinates.hashCode(row, col);

        if (!colors.containsKey(boxLetter)) {
            colors.put(boxLetter, Color.BLUE);
        }

//        Box box = new Box();
//        box.hashCoordinates = hashCoordinates;
//        box.letter = boxLetter;
//        box.Status = Status.FREE;

//        boxes.put(box.hashCoordinates, box);

        state.addBox(hashCoordinates, boxLetter);
    }

    public static boolean hasGoal(int goalHashCoordinates) {
        return goalsByCoordinates2.containsKey(goalHashCoordinates);
    }

    public static boolean hasWallAt(int wallHashCoordinate) {
        return walls.contains(wallHashCoordinate);
    }

    public static void update(Character agentName, Command command) {

        int agentHashCoordinates = agentsByName.get(agentName).hashCoordinates;

        state.agentHashCoordinates = agentHashCoordinates;
        state = state.ChildNode(agentHashCoordinates, command);

        agentsByName.get(agentName).hashCoordinates = state.agentHashCoordinates;
//        moveAgent(agentHashCoordinates, command.dir1);
        if (command.actType == Command.type.Pull) {
            int boxHashCoordinates = Coordinates.move(agentHashCoordinates, command.dir2);
            moveBox(boxHashCoordinates, Command.GetOpposite(command.dir2));
        } else if (command.actType == Command.type.Push) {
            int boxHashCoordinates = Coordinates.move(agentHashCoordinates, command.dir1);
            moveBox(boxHashCoordinates, command.dir2);
        }
    }

    public static boolean sameColor(Character object1, Character object2) {
        return colors.get(object1).equals(colors.get(object2));
    }

    public static Integer getGoalFor(Agent agent) {

//        if (goals == null) {
//            InstantiateGoalQueues();
//        }

        Queue<Goal> agentQueue = new ArrayDeque<Goal>(prioritizeGoalsForAgent(agent));
//                goals.get(agent.name);
        Integer goalCount = 0;

        Goal goal = agentQueue.poll();
        goalCount++;

        // keep searching for a goal, until you find unsatisfied
        // or you have tried all of them
        while (!(goal.isFree() && goal.areDependentSatisfied() &&
                sameColor(Character.toUpperCase(goal.letter), agent.name))) {

            agentQueue.add(goal);

            if (goalCount > agentQueue.size()) {
                return -1;
            }

            goal = agentQueue.poll();
            goalCount++;
        }

        goal.Status = Status.TAKEN;
        agentQueue.add(goal);

        agent.goalTaken = goal;

        return goal.hashCoordinates;
    }

//    private static void InstantiateGoalQueues() {
//        goals = new HashMap<Character, Queue<Goal>>();
//
//        for (Agent agent : getAgents()) {
//            Queue<Goal> goalQueue = new ArrayDeque<Goal>(prioritizeGoalsForAgent(agent));
//
//            goals.put(agent.name, goalQueue);
//        }
//    }

    //TODO: do not dismiss goals of different color at this point
    private static List<Goal> prioritizeGoalsForAgent(Agent agent) {

        if (goalPrioritize == null) {
            //Initialize priorities only on first call for goal prioritization
            //TODO: change maxRow, maxCol
            goalPrioritize =
                    new GoalPrioritize(walls, goalsByCoordinates2, MAX_ROW, MAX_COLUMN, agent.hashCoordinates);
        }

        List<Goal> prioritizedGoals = goalPrioritize.prioritizeFor(agent.hashCoordinates);

        return prioritizedGoals;

//        Color agentColor = agent.color;
//        Set<Goal> goalsOfAgentColor = new HashSet<Goal>(GetGoalsOfColor(agentColor));
//
//        List<Goal> goalsToReturn = new LinkedList<Goal>();
//        for (Goal goal : prioritizedGoals) {
//            if (goalsOfAgentColor.contains(goal)) {
//                goalsToReturn.add(goal);
//            }
//        }
//
//        return goalsToReturn;
    }

    public static Integer getBoxFor(Agent agent) {
        char goalLetter = agent.goalTaken.letter;
        char boxLetter = Character.toUpperCase(goalLetter);

        int minDistance = Integer.MAX_VALUE;
        int targetBox = -1;

        for (int boxHashCoordinates : state.getBoxes(boxLetter)) {

            if (!goalsByCoordinates2.containsKey(boxHashCoordinates)) {
                if (!takenBoxes.containsKey(boxHashCoordinates)) {

                    int currentDistance =
                            Coordinates.manhattanDistance(boxHashCoordinates, agent.hashCoordinates);

                    //TODO: get closest box, which do not satisfy a goal with
                    //TODO: bigger or equal priority
                    if (minDistance > currentDistance) {
                        minDistance = currentDistance;
                        targetBox = boxHashCoordinates;
                    }
                }
            }
        }

        agent.boxTaken = targetBox;
        takenBoxes.put(targetBox, agent.name);
        return targetBox;
    }

    private static List<Goal> GetGoalsOfColor(Color targetColor) {
        List<Goal> goals = new LinkedList<Goal>();

        for (Map.Entry<Character, Color> entry : colors.entrySet()) {
            Color objectColor = entry.getValue();
            Character goalLetter = Character.toLowerCase(entry.getKey());

            if (Character.isDigit(goalLetter)) {
                continue;
            }

            if (objectColor == targetColor) {
                if (goalsByCharacter2.containsKey(goalLetter)) {
                    goals.addAll(goalsByCharacter2.get(goalLetter));
                }
            }
        }

        return goals;
    }

    private static void moveBox(int boxHashCoordinates, Command.dir dir) {
//        boxesByCharacter.get(boxLetter).removeFirstOccurrence(boxHashCoordinates);
//
        int newCoordinates = Coordinates.move(boxHashCoordinates, dir);
//        boxesByCharacter.get(boxLetter).add(newCoordinates);
//        boxes.put(newCoordinates, boxLetter);
        if (takenBoxes.containsKey(boxHashCoordinates)) {
            Character agentName = takenBoxes.remove(boxHashCoordinates);
            takenBoxes.put(newCoordinates, agentName);
        }

        Character boxLetter = state.getBoxLetterAt(newCoordinates);
        // set as satisfied if a box is moved to the goal
        if (goalsByCoordinates2.containsKey(newCoordinates) &&
                goalsByCoordinates2.get(newCoordinates).letter == Character.toLowerCase(boxLetter)) {
            goalsByCoordinates2.get(newCoordinates).Status = Status.SATISFIED;
        }

        if (goalsByCoordinates2.containsKey(boxHashCoordinates)) {
            goalsByCoordinates2.get(boxHashCoordinates).Status = Status.FREE;
            takenBoxes.remove(boxHashCoordinates);
        }

        // TODO: set as unsatisfied if a box is moved out of the goal
        // TODO: add it in the "goal queue"
    }

    public static boolean AreGoalsSatisfied() {

        for (Goal goal : goalsByCoordinates2.values()) {
            if (!goal.isSatisfied()) {
                return false;
            }
        }

        return true;
    }

    //TODO: THIS IS AWFUL
    // it is setting a box free, but getting agentName for parameter ?? wtf
    public static void setBoxFree(Character agentName) {
        agentsByName.get(agentName).boxTaken = -1;

        for (Map.Entry<Integer, Character> entry : takenBoxes.entrySet()) {
            if (entry.getValue().equals(agentName)) {
                takenBoxes.remove(entry.getKey());
                return;
            }
        }
    }

}

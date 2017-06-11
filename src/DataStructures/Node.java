package DataStructures;

import java.util.*;

public class Node {

    public int agentHashCoordinates;

    public Node parent;
    public Command action;

    public static int encodeCounter;

    //TODO: is it possible to save space ?
    private HashMap<Integer, Character> agents = new HashMap<Integer, Character>();
    private HashMap<Integer, Character> boxes = new HashMap<Integer, Character>();
    private HashMap<Character, HashSet<Integer>> boxesByCharacter = new HashMap<Character, HashSet<Integer>>();

    public int goal;
    public int box;

    private int g;
    public static int equalsCount;
    public static int failedEqualsCount;
    public static int hashCount;
    public int h = -1;

    private String encoding;


    public Node(Node parent) {
        this.parent = parent;
        if (parent == null) {
            g = 0;
        } else {
            g = parent.g() + 1;
        }
    }

    //////////////////////////// GETTERS & SETTERS ////////////////////////////

    public int g() {
        return g;
    }

    public HashMap<Integer, Character> getAgents() {
        return agents;
    }

    public HashMap<Integer, Character> getBoxes() {
        return this.boxes;
    }

    public HashSet<Integer> getBoxes(char boxLetter) {
        return this.boxesByCharacter.get(boxLetter);
    }

    public Set<Character> getAllBoxLetters() {
        return boxesByCharacter.keySet();
    }

    public void setDedicatedGoal(Integer boxHashCoordinates, Integer goalHashCoordinates) {
        box = boxHashCoordinates;
        goal = goalHashCoordinates;
    }

    @Override
    public int hashCode() {

        hashCount++;

        //TODO: make this faster
        final int prime = 31;
        int result = 1;
        result = prime * result + agentHashCoordinates;
        result = prime * result + box;
        result = prime * result + goal;
        for (Integer boxHashCoordinates : this.boxes.keySet()) {
            result = result ^ boxHashCoordinates.hashCode();
        }
//        result = prime * result + this.boxes.hashCode();
//        for (Map.Entry<Integer, Character> entry : this.boxes.entrySet()) {
//            result = prime * result + entry.getKey().hashCode();
//            result = prime * result + entry.getValue().hashCode();
//        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        //TODO: make this faster

        equalsCount++;

        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass()) {
            failedEqualsCount++;
            return false;
        }
        Node other = (Node) obj;
        if (agentHashCoordinates != other.agentHashCoordinates) {
            failedEqualsCount++;
            return false;
        }
        if (box != other.box) {
            failedEqualsCount++;
            return false;
        }
        if (goal != other.goal) {
            failedEqualsCount++;
            return false;
        }
        if (!boxes.equals(other.boxes)) {
            failedEqualsCount++;
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();

        for (int row = 0; row < Level.MAX_ROW; row++) {
            for (int col = 0; col < Level.MAX_COLUMN; col++) {
                int currentHashCoordinates = Coordinates.hashCode(row, col);
                if (this.boxes.containsKey(currentHashCoordinates)) {
                    s.append(this.getBoxLetter(row, col));
                } else if (Level.hasGoal(currentHashCoordinates)) {
                    s.append(Level.getGoal(row, col));
                } else if (Level.hasWallAt(currentHashCoordinates)) {
                    s.append("+");
                } else if (agentHashCoordinates == currentHashCoordinates) {
                    s.append("0");
                } else {
                    s.append(" ");
                }
            }
            s.append("\n");
        }

        return s.toString();
    }

    public String encode() {

        if(encoding != null){
            return encoding;
        }

        encodeCounter++;

        StringBuilder sb = new StringBuilder();

        for (Map.Entry<Integer, Character> entry : agents.entrySet()) {
            sb.append(entry.getValue());
            sb.append("#");
            sb.append(entry.getKey());
            sb.append("|");
        }

        sb.append(box);
        sb.append("b#");
        sb.append(goal);
        sb.append("g|");

        for (Map.Entry<Character, HashSet<Integer>> entry : boxesByCharacter.entrySet()) {
            sb.append(entry.getKey());
            sb.append("#");

            Set<Integer> values = entry.getValue();

            if (values.size() > 1) {
                values = new TreeSet<Integer>(values);
            }

            for (int hashCoordinates : values) {
                sb.append(hashCoordinates);
                sb.append("|");
            }
        }

        encoding = sb.toString();

        return encoding;
    }

    public boolean isInitialState() {
        return this.parent == null;
    }

    public boolean isGoalState() {
        return box == goal;

//        for (Character goalLetter : boxesByCharacter.keySet()) {
//            HashSet<Integer> boxesSet = this.getBoxes(goalLetter);
////            HashSet<Integer> goalsSet = Level.getGoalCoordinates(goalLetter);
//
//            for(Integer goalHash : Level.getGoalCoordinates(goalLetter)){
//                if(!boxesSet.contains(goalHash)){
//                    return false;
//                }
//            }
//        }
//
//        return true;
    }

    public ArrayList<Node> getExpandedNodes() {
        ArrayList<Node> expandedNodes = new ArrayList<Node>(Command.every.length);
        for (Command command : Command.every) {
            // Determine applicability of action
//            int newAgentRow = this.agentRow + dirToRowChange(command.dir1);
//            int newAgentCol = this.agentCol + dirToColChange(command.dir1);

            int newAgentHashCoordinates = Coordinates.move(agentHashCoordinates, command.dir1);

            if (command.actType == Command.type.Move) {
                if (this.hasFreeCellAt(newAgentHashCoordinates)) {
                    expandedNodes.add(this.ChildNode(agentHashCoordinates, command));
                }
            } else if (command.actType == Command.type.Pull) {

                if (this.hasFreeCellAt(newAgentHashCoordinates)) {
                    int boxHashCoordinates = Coordinates.move(agentHashCoordinates, command.dir2);

                    char agentName = agents.get(agentHashCoordinates);

                    if (this.hasBoxAt(boxHashCoordinates) && Level.sameColor(agentName, getBoxLetterAt(boxHashCoordinates))) {
                        expandedNodes.add(this.ChildNode(command));
                    }
                }
            } else if (command.actType == Command.type.Push) {
                int boxHashCoordinates = Coordinates.move(agentHashCoordinates, command.dir1);
                char agentName = agents.get(agentHashCoordinates);

                if (this.hasBoxAt(boxHashCoordinates) && Level.sameColor(agentName, getBoxLetterAt(boxHashCoordinates))) {

                    int newBoxHashCoordinates = Coordinates.move(boxHashCoordinates, command.dir2);
                    if (this.hasFreeCellAt(newBoxHashCoordinates)) {
                        expandedNodes.add(this.ChildNode(command));
                    }
                }
            }

//            if (command.actType == Command.type.Move) {
//                // Check if there's a wall or box on the cell to which the agent is moving
//                if (this.hasFreeCellAt(newAgentRow, newAgentCol)) {
//                    Node n = this.ChildNode();
//                    n.action = command;
//                    n.agentRow = newAgentRow;
//                    n.agentCol = newAgentCol;
//                    expandedNodes.add(n);
//                }
//            } else if (command.actType == Command.type.Push) {
//                // Make sure that there's actually a box to move
//                if (this.hasBoxAt(newAgentRow, newAgentCol)) {
//                    int newBoxRow = newAgentRow + dirToRowChange(command.dir2);
//                    int newBoxCol = newAgentCol + dirToColChange(command.dir2);
//                    // .. and that new cell of box is free
//                    if (this.hasFreeCellAt(newBoxRow, newBoxCol)) {
//
//                        // .. and it's of this agent's color
//
//                        Node n = this.ChildNode();
//                        n.action = command;
//                        n.moveAgent(newAgentRow, newAgentCol);
//                        n.moveBox(newAgentRow, newAgentCol, newBoxRow, newBoxCol);
//                        expandedNodes.add(n);
//                    }
//                }
//            } else if (command.actType == Command.type.Pull) {
//                // Cell is free where agent is going
//                if (hasFreeCellAt(newAgentRow, newAgentCol)) {
//                    int boxRow = this.agentRow + dirToRowChange(command.dir2);
//                    int boxCol = this.agentCol + dirToColChange(command.dir2);
//                    // .. and there's a box in "dir2" of the agent
//                    if (this.hasBoxAt(boxRow, boxCol)) {
//
//                        // .. and is in this agent's color
//
//                        Node n = this.ChildNode();
//                        n.action = command;
//                        n.moveAgent(newAgentRow, newAgentCol);
//                        n.moveBox(boxRow, boxCol, this.agentRow, this.agentCol);
//                        expandedNodes.add(n);
//                    }
//                }
//            }
        }
//        Collections.shuffle(expandedNodes, rnd);
        return expandedNodes;
    }

    public Node ChildNode(Command command) {
        return ChildNode(this.agentHashCoordinates, command);
    }

    public Node ChildNode(int agentHashCoordinates, Command command) {
        Node childNode = this.ChildNode();
        childNode.action = command;
        childNode.moveAgent(agentHashCoordinates, command.dir1);
        if (command.actType == Command.type.Pull) {
            int boxHashCoordinates = Coordinates.move(agentHashCoordinates, command.dir2);
            childNode.moveBox(boxHashCoordinates, Command.GetOpposite(command.dir2));
        } else if (command.actType == Command.type.Push) {
            int boxHashCoordinates = Coordinates.move(agentHashCoordinates, command.dir1);
            childNode.moveBox(boxHashCoordinates, command.dir2);
        }

        return childNode;
    }

    private Node ChildNode() {
        Node copy = new Node(this);
        copy.box = this.box;
        copy.goal = this.goal;
        copy.agentHashCoordinates = this.agentHashCoordinates;
        copy.boxes = new HashMap<Integer, Character>();
        copy.boxesByCharacter = new HashMap<Character, HashSet<Integer>>();
        copy.agents = new HashMap<Integer, Character>();

        for (Map.Entry<Integer, Character> box : this.boxes.entrySet()){
            copy.boxes.put(box.getKey(), box.getValue());
        }

        for (Map.Entry<Character, HashSet<Integer>> box : this.boxesByCharacter.entrySet()){
            copy.boxesByCharacter.put(box.getKey(), new HashSet<Integer>(box.getValue()));
        }

        for (Map.Entry<Integer, Character> agent : this.agents.entrySet()){
            copy.agents.put(agent.getKey(), agent.getValue());
        }

        return copy;
    }

    public LinkedList<Node> extractPlan() {
        LinkedList<Node> plan = new LinkedList<Node>();
        Node n = this;
        while (!n.isInitialState()) {
            plan.addFirst(n);
            n = n.parent;
        }
        return plan;
    }

    public Character getBoxLetterAt(int boxHashCoordinates) {
        return boxes.get(boxHashCoordinates);
    }

    public void addAgent(Integer agentHashCoordinates, Character agentName) {
        this.agents.put(agentHashCoordinates, agentName);
    }

    public void addBox(Integer boxHashCoordinates, Character boxLetter) {
        this.boxes.put(boxHashCoordinates, boxLetter);

        if (this.boxesByCharacter.containsKey(boxLetter)) {
            this.boxesByCharacter.get(boxLetter).add(boxHashCoordinates);
        } else {
            HashSet<Integer> newCoordinates = new HashSet<Integer>();
            newCoordinates.add(boxHashCoordinates);
            this.boxesByCharacter.put(boxLetter, newCoordinates);
        }
    }

    public void updateFrom(Node state) {

        for (Map.Entry<Integer, Character> entry2 : state.getBoxes().entrySet()) {
            this.addBox(entry2.getKey(), entry2.getValue());
        }

        for (Map.Entry<Integer, Character> entry2 : state.getAgents().entrySet()) {
            this.addAgent(entry2.getKey(), entry2.getValue());
        }
    }

    //////////////////////////// PRIVATE METHODS ////////////////////////////

    private int moveBox(int boxHashCoordinates, Command.dir direction) {
        Character boxLetter = this.boxes.remove(boxHashCoordinates);
        this.boxesByCharacter.get(boxLetter).remove(boxHashCoordinates);

        int newCoordinates = Coordinates.move(boxHashCoordinates, direction);
        this.boxesByCharacter.get(boxLetter).add(newCoordinates);
        this.boxes.put(newCoordinates, boxLetter);

        if (this.box == boxHashCoordinates) {
            this.box = newCoordinates;
        }

//        takenBoxes.remove(boxHashCoordinates);
//        takenBoxes.add(newCoordinates);

        // set as satisfied if a box is moved to the goal
//        if(goalsByCoordinates2.containsKey(newCoordinates) &&
//                goalsByCoordinates2.get(newCoordinates).letter.equals(Character.toLowerCase(boxLetter))){
//            goalsByCoordinates2.get(newCoordinates).Status = Status.SATISFIED;
//        }

        return newCoordinates;
    }

    private void moveAgent(int agentHashCoordinates, Command.dir dir) {
        Character agentName = agents.remove(agentHashCoordinates);
        int newCoordinates = Coordinates.move(agentHashCoordinates, dir);
        this.agentHashCoordinates = newCoordinates;
//        agentsByName.put(agentName, newCoordinates);
        agents.put(newCoordinates, agentName);
    }

    private boolean hasFreeCellAt(int cellHashCoordinates) {

        if (Level.hasWallAt(cellHashCoordinates)) {
            return false;
        }

        // has box at cellHashCoordinates
        if (this.boxes.containsKey(cellHashCoordinates)) {
            return false;
        }

        // has agent at cellHashCoordinates
        if (this.agents.containsKey(cellHashCoordinates)) {
            return false;
        }

        return true;
    }

    private boolean hasBoxAt(int boxHashCoordinate) {
        return boxes.containsKey(boxHashCoordinate);
    }

    private char getBoxLetter(int x, int y) {
        return boxes.get(Coordinates.hashCode(x, y));
    }

}
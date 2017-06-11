package DataStructures;

import java.util.HashSet;
import java.util.List;

public class Goal {
    public Status Status;
    public final int hashCoordinates;
    public final char letter;
    private final char capitalLetter;

    //TODO: add color here

    // Goals this goal rely on to get satisfied
    public List<Goal> relyingGoals;

    // Goals that depend on this goal
    public HashSet<Goal> dependentGoals;

    public Goal(Integer coordinates, char goalLetter, Status status) {
        hashCoordinates = coordinates;
        letter = goalLetter;
        Status = status;
        capitalLetter = Character.toUpperCase(goalLetter);
    }

    @Override
    public int hashCode() {
        return hashCoordinates;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Goal){
            Goal other = (Goal) obj;
            return this.letter == other.letter && this.hashCoordinates == other.hashCoordinates;
        }

        return false;
    }

    @Override
    public String toString(){
        return String.format("%c (%d)", this.letter, this.hashCoordinates);
    }

    public void addDependentGoal(Goal goal) {
        if(dependentGoals == null){
            dependentGoals = new HashSet<Goal>();
        }

        dependentGoals.add(goal);
    }

    public boolean areDependentSatisfied() {

        if(dependentGoals == null || dependentGoals.isEmpty()){
            return true;
        }

        for (Goal goal : dependentGoals){
            if(!goal.isSatisfied()){
                return false;
            }
        }

        return true;
    }

    public boolean isFree() {
        return this.Status == Status.FREE;
    }

    public boolean isSatisfied() {return Level.state.getBoxes(capitalLetter).contains(this.hashCoordinates);}

    public int getPriority(){
        return dependentGoals.size();
    }
}

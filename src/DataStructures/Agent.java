package DataStructures;

public class Agent implements Comparable<Agent> {

    public Goal goalTaken;
    public int hashCoordinates;
    public Color color;
    public char name;
    public int boxTaken;
    public Status status;
    public int retryCounter;

    public int countExplored;
    public int countFrontier;

    public boolean isFree() {
        return status == Status.FREE;
    }

    public void setBusy() {
        status = Status.TAKEN;
    }

    public void setFree() {
        status = Status.FREE;
    }

    public void setTakenGoalFree() {

        if(goalTaken != null && goalTaken.Status == Status.TAKEN){
            goalTaken.Status = Status.FREE;
        }
    }

    @Override
    public int hashCode() {
        return 31 * hashCoordinates + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Agent other = (Agent)obj;
        if(this.hashCoordinates != other.hashCoordinates){
            return false;
        }
        if(this.name != other.name){
            return false;
        }

        return true;
    }

    @Override
    public int compareTo(Agent other) {
        if(this.name < other.name){
            return -1;
        }

        if(this.name == other.name){
            return 0;
        }

        return 1;
    }

    public String toString(){

        if(goalTaken != null){
            return String.format("%s(%s) - goalTaken: %s(%s) ;   boxTaken: %s(%s)",
                    name, hashCoordinates, goalTaken.letter, goalTaken.hashCoordinates, "?", boxTaken);
        }

        return name + "(" + hashCoordinates + ")";
    }
}

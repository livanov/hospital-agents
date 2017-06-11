package DataStructures;

public class Coordinates {
    public final Integer row;
    public final Integer col;
    public final int hashCode;

    //log(MAX_COLUMN) should be less than offset
    private final static int offset = 7;
    private final static int hashOffsetPerRow = 1 << offset;

    public Coordinates(Integer hashCode) {
        this.hashCode = hashCode;

        row = hashCode >> offset;
        col = hashCode - (row << offset);
    }

    @Override
    public int hashCode() {
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        Coordinates other = (Coordinates) obj;
        return other == null ? false : this.row == other.row && this.col == other.col;
    }

    @Override
    public String toString() {
        return String.format("row: %d    col: %d    h: %d", row, col, hashCode);
    }

    public static int hashCode(int x, int y) {
        return (x << offset) + y;
    }

    public static int manhattanDistance(int sourceHash, int targetHash) {
        int sourceRow = sourceHash >> offset;
        int targetRow = targetHash >> offset;
        int sourceCol = sourceHash - (sourceRow << offset);
        int targetCol = targetHash - (targetRow << offset);

        return Math.abs(sourceRow - targetRow) + Math.abs(sourceCol - targetCol);
    }

    public static int move(int agentHashCoordinates, Command.dir direction) {
        if (direction == Command.dir.E) {
            return agentHashCoordinates + 1;
        }
        if (direction == Command.dir.W) {
            return agentHashCoordinates - 1;
        }
        if (direction == Command.dir.N) {
            return agentHashCoordinates - hashOffsetPerRow;
        }
        return agentHashCoordinates + hashOffsetPerRow;
    }

//    public Coordinates(int row, int col) {
//        this.row = Integer.valueOf(row);
//        this.col = Integer.valueOf(col);
//
//        this.hashCode = (row << offset) + col;
//    }
//
//    public Integer getRow() {
//        return this.row;
//    }
//
//    public Integer getCol() {
//        return this.col;
//    }
//    public int manhattanDistanceTo(Coordinates targetCoordinates) {
//        return Math.abs(this.row.intValue() - targetCoordinates.row.intValue()) +
//                Math.abs(this.col.intValue() - targetCoordinates.col.intValue());
//    }
}

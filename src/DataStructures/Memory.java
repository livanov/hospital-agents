package DataStructures;

public final class Memory {
    public static Runtime runtime = Runtime.getRuntime();
    public static final float mb = 1024 * 1024;
    public static final float limitRatio = .9f;
    public static final int timeLimit = 180;

    public static float used() {
        return ( runtime.totalMemory() - runtime.freeMemory() ) / mb;
    }

    public static float free() {
        return runtime.freeMemory() / mb;
    }

    public static float total() {
        return runtime.totalMemory() / mb;
    }

    public static float max() {
        return runtime.maxMemory() / mb;
    }

    public static boolean shouldEnd() {
        return ( used() / max() > limitRatio );
    }

    public static String stringRep() {
        return String.format( "[Used: %.2f MB, Free: %.2f MB, Alloc: %.2f MB, MaxAlloc: %.2f MB]", used(), free(), total(), max() );
    }

    private Memory(){

    }
}
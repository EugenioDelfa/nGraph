package weird.ngraph.Utils;

public class FunctionWrapper {
    public static long timeSnapshot() {
        return System.currentTimeMillis();
    }
    public static String timeDiff(long s, long e) {
        long diff = e - s;
        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000);
        String strTime = String.format("%1$02d:%2$02d:%3$02d", diffHours, diffMinutes, diffSeconds);
        return strTime;
    }
    public static void timmed(boolean quiet, String message, Runnable func ) throws Exception {
        try {
            if (!quiet) {
                System.out.println(message);
            }
            long start_time = FunctionWrapper.timeSnapshot();
            func.run();
            long end_time = FunctionWrapper.timeSnapshot();
            String strTime = FunctionWrapper.timeDiff(start_time, end_time);
            if (!quiet) {
                System.out.println("\tDuration: " + strTime);
            }
        } catch (Exception ex) {
            throw new Exception("Timed function error.");
        }
    }

}

package ro.andrei.drivingtestplatform.util;

public abstract class Logger {
    private static volatile Logger instance;
    protected Logger() {}

    public static Logger getInstance() {
        if (instance == null) {
            synchronized (Logger.class) {
                if (instance == null) {
                    instance = new ConsoleLogger();
                }
            }
        }
        return instance;
    }

    public abstract void info(String message);
    public abstract void warn(String message);
    public abstract void error(String message);

}

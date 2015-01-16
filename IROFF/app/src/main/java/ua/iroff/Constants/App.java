package ua.iroff.Constants;

/**
 * Created by daniil on 11/19/14.
 */
public class App {

    public interface CommandType {
        public static int POWER_ON_OFF = 0;
    }

    public interface FileName{
        public String CODES_POWER_ON_OFF = "codes_power_on_off";
    }

    public interface Pref{
        public static final String NAME = "ir_off_prefs";
        public static final String IS_FIRST = "is_first_start";
    }
}

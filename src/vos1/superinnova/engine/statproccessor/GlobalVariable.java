package vos1.superinnova.engine.statproccessor;

/**
 * Created by Wachirawat on 11/14/15 AD.
 */
public class GlobalVariable {

    public static String VERSION_ID = "1.0.2";

    public static String BASE_PATH = "/opt/elementManagement";
    public static String BASE_LOG_PATH = BASE_PATH + "/log/";
    public static String BASE_BIN_PATH = BASE_PATH + "/bin/";

    public static void setBasePath(String path) {
        BASE_PATH = path;
        BASE_LOG_PATH = BASE_PATH + "/log/";
        BASE_BIN_PATH = BASE_PATH + "/bin/";
    }
}

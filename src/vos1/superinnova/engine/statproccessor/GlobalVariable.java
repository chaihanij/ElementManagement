package vos1.superinnova.engine.statproccessor;

/**
 * Created by Wachirawat on 11/14/15 AD.
 */
public class GlobalVariable {

    public static String VERSION_ID = "1.1.0";

    public static String BASE_PATH = "/opt/elementManagement";
    public static String BASE_LOG_PATH = BASE_PATH + "/log/";
    public static String BASE_BIN_PATH = BASE_PATH + "/bin/";
    public static String BASE_CONF_PATH = BASE_PATH + "/conf";
    public static String BASE_TMP_PATH = BASE_PATH + "/tmp";
   
    public static String APPLICATION = "app";

    public static void setBasePath(String path) {
        BASE_PATH = path;
        BASE_LOG_PATH = BASE_PATH + "/log/";
        BASE_BIN_PATH = BASE_PATH + "/bin/";
        BASE_CONF_PATH = BASE_PATH + "/conf";
        BASE_TMP_PATH = BASE_PATH + "/tmp";
    }

    public static void setApplicationName(String name) {
        APPLICATION = name;
    }
}

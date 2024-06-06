    static {
        //initial Logger
        try {

            if (SystemType.isWindows()) {
                LongriLoggerConfiguration.setConfigurationFile(Main.class.getClassLoader().getResourceAsStream("logger/longriLoggerWin.properties"));
            } else if (SystemType.isLinux() || SystemType.getSystemType() == SystemType.UNKNOWN) {
                LongriLoggerConfiguration.setConfigurationFile(Main.class.getClassLoader().getResourceAsStream("logger/longriLoggerLinux.properties"));
            } else {
                LongriLoggerConfiguration.setConfigurationFile(Main.class.getClassLoader().getResourceAsStream("logger/longriLogger.properties"));
            }
            LongriLoggerFactory factory = ((LongriLoggerFactory) LoggerFactory.getILoggerFactory());
            factory.reset();
            LongriLoggerInit.init();


            //Exclude some Classes from debug Logging
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.session.Session", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:de.longri.fx.file_handle_tree_view.FileHandleTreeItem", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.longri.prtg.backup.crontab.Main", "info");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }



Maybe you must create a class like

package de.longri.logging;

public class LongriLoggerInit {

    public static void init() {
        LongriLogger.resetLazyInit();
        LongriLogger.lazyInit();
    }

}
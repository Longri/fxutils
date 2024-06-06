package de.longri.logging;


import de.longri.filetransfer.Local_FileTransferHandle;
import de.longri.rolling_file.RollingFileHandle;
import org.slf4j.helpers.MessageFormatter;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributeView;
import java.nio.file.attribute.FileTime;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Date;
import java.util.Properties;

/**
 * This class holds configuration values for {@link LongriLogger}. The
 * values are computed at runtime. See {@link LongriLogger} documentation for
 * more information.
 *
 * @author Ceki G&uuml;lc&uuml;
 * @author Scott Sanders
 * @author Rod Waldhoff
 * @author Robert Burrell Donkin
 * @author C&eacute;drik LIME
 * @since 1.7.25
 */
public class LongriLoggerConfiguration {

    public static void setConfigurationFile(InputStream configurationFile) {
        CONFIGURATION_FILE = configurationFile;
    }

    private static InputStream CONFIGURATION_FILE;

    static int DEFAULT_LOG_LEVEL_DEFAULT = LongriLogger.LOG_LEVEL_INFO;
    int defaultLogLevel = DEFAULT_LOG_LEVEL_DEFAULT;

    private static final boolean SHOW_DATE_TIME_DEFAULT = false;
    boolean showDateTime = SHOW_DATE_TIME_DEFAULT;

    private static final boolean LOG_COLORFUL = true;
    boolean logColorful = LOG_COLORFUL;

    private static final String DATE_TIME_FORMAT_STR_DEFAULT = null;
    private static String dateTimeFormatStr = DATE_TIME_FORMAT_STR_DEFAULT;

    DateFormat dateFormatter = null;

    private static final boolean SHOW_THREAD_NAME_DEFAULT = true;
    boolean showThreadName = SHOW_THREAD_NAME_DEFAULT;

    /**
     * See https://jira.qos.ch/browse/SLF4J-499
     *
     * @since 1.7.33 and 2.0.0-alpha6
     */
    private static final boolean SHOW_THREAD_ID_DEFAULT = false;
    boolean showThreadId = SHOW_THREAD_ID_DEFAULT;

    final static boolean SHOW_LOG_NAME_DEFAULT = true;
    boolean showLogName = SHOW_LOG_NAME_DEFAULT;

    private static final boolean SHOW_SHORT_LOG_NAME_DEFAULT = false;
    boolean showShortLogName = SHOW_SHORT_LOG_NAME_DEFAULT;

    private static final boolean LEVEL_IN_BRACKETS_DEFAULT = false;
    boolean levelInBrackets = LEVEL_IN_BRACKETS_DEFAULT;

    private static final String LOG_FILE_DEFAULT = "System.err";

    public void setOutputChoice(OutputChoice choice) {
        outputChoice = choice;
    }

    public void addOutputChoice(PrintStream stream) {
        outputChoice.add(stream);
    }

    OutputChoice outputChoice = null;

    private static final String WARN_LEVELS_STRING_DEFAULT = "WARN";
    String warnLevelString = WARN_LEVELS_STRING_DEFAULT;

    private final Properties properties = new Properties();

    public LongriLoggerConfiguration() {
    }

    void init() {
        this.init(CONFIGURATION_FILE);
    }

    void init(InputStream inputStream) {
        loadProperties(inputStream);

        String defaultLogLevelString = getStringProperty(LongriLogger.DEFAULT_LOG_LEVEL_KEY, null);
        if (defaultLogLevelString != null)
            defaultLogLevel = stringToLevel(defaultLogLevelString);

        showLogName = getBooleanProperty(LongriLogger.SHOW_LOG_NAME_KEY, LongriLoggerConfiguration.SHOW_LOG_NAME_DEFAULT);
        showShortLogName = getBooleanProperty(LongriLogger.SHOW_SHORT_LOG_NAME_KEY, SHOW_SHORT_LOG_NAME_DEFAULT);
        showDateTime = getBooleanProperty(LongriLogger.SHOW_DATE_TIME_KEY, SHOW_DATE_TIME_DEFAULT);
        logColorful = getBooleanProperty(LongriLogger.LOG_COLORFUL_KEY, LOG_COLORFUL);
        showThreadName = getBooleanProperty(LongriLogger.SHOW_THREAD_NAME_KEY, SHOW_THREAD_NAME_DEFAULT);
        showThreadId = getBooleanProperty(LongriLogger.SHOW_THREAD_ID_KEY, SHOW_THREAD_ID_DEFAULT);
        dateTimeFormatStr = getStringProperty(LongriLogger.DATE_TIME_FORMAT_KEY, DATE_TIME_FORMAT_STR_DEFAULT);
        levelInBrackets = getBooleanProperty(LongriLogger.LEVEL_IN_BRACKETS_KEY, LEVEL_IN_BRACKETS_DEFAULT);
        warnLevelString = getStringProperty(LongriLogger.WARN_LEVEL_STRING_KEY, WARN_LEVELS_STRING_DEFAULT);

        String logFile = getStringProperty(LongriLogger.LOG_FILE_KEY, null);
        String logBasePath = getStringProperty(LongriLogger.LOG_BASE_PATH_KEY, null);
        String logFilePattern = getStringProperty(LongriLogger.LOG_FILE_PATTERN_KEY, null);
        String sysLog = getStringProperty(LongriLogger.SYS_LOG_KEY, null);

        if (logFile == null && sysLog == null) {
            sysLog = LongriLogger.DEFAULT_LOG;
        }

        if (logFile != null && logBasePath != null) {
            logFile = logFile.replace("${logFileBasePath}", logBasePath);
        }


        //Set ENV example
        // mac: export LONGRI_LOG_PATH=/Users/Longri/temp/logger.log

        String envLogPath = System.getenv("LONGRI_LOG_PATH");
        if (envLogPath != null) {
            File f = new File(envLogPath);
            logBasePath = f.getParent();
            logFile = "${logFileBasePath}/" + f.getName();
        }

        outputChoice = computeOutputChoice(logBasePath, logFilePattern, false, logFile, sysLog);

        if (dateTimeFormatStr != null) {
            try {
                dateFormatter = new SimpleDateFormat(dateTimeFormatStr);
            } catch (IllegalArgumentException e) {
                LongriMessageFormatter.report("Bad date format in " + CONFIGURATION_FILE + "; will output relative time", e);
            }
        }
    }

    private void loadProperties(InputStream in) {
        // Add props from the resource simplelogger.properties
        if (null != in) {
            try {
                properties.load(in);
            } catch (IOException e) {
                // ignored
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    // ignored
                }
            }
        }
    }

    String getStringProperty(String name, String defaultValue) {
        String prop = getStringProperty(name);
        return (prop == null) ? defaultValue : prop;
    }

    int getIntegerProperty(String name, int defaultValue) {
        String prop = getStringProperty(name);
        return (prop == null) ? defaultValue : Integer.parseInt(prop);
    }

    boolean getBooleanProperty(String name, boolean defaultValue) {
        String prop = getStringProperty(name);
        return (prop == null) ? defaultValue : "true".equalsIgnoreCase(prop);
    }

    String getStringProperty(String name) {
        String prop = null;
        try {
            prop = System.getProperty(name);
        } catch (SecurityException e) {
            ; // Ignore
        }
        return (prop == null) ? properties.getProperty(name) : prop;
    }

    static int stringToLevel(String levelStr) {
        if ("trace".equalsIgnoreCase(levelStr)) {
            return LongriLogger.LOG_LEVEL_TRACE;
        } else if ("debug".equalsIgnoreCase(levelStr)) {
            return LongriLogger.LOG_LEVEL_DEBUG;
        } else if ("info".equalsIgnoreCase(levelStr)) {
            return LongriLogger.LOG_LEVEL_INFO;
        } else if ("warn".equalsIgnoreCase(levelStr)) {
            return LongriLogger.LOG_LEVEL_WARN;
        } else if ("error".equalsIgnoreCase(levelStr)) {
            return LongriLogger.LOG_LEVEL_ERROR;
        } else if ("off".equalsIgnoreCase(levelStr)) {
            return LongriLogger.LOG_LEVEL_OFF;
        }
        // assume INFO by default
        return LongriLogger.LOG_LEVEL_INFO;
    }

    private OutputChoice computeOutputChoice(String logBasePath, String logFilePattern, boolean tre, String... logFiles) {
        OutputChoice output = new OutputChoice();

        for (String logFile : logFiles) {
            if (logFile == null) continue;
            if ("System.err".equalsIgnoreCase(logFile))
                output.add(OutputChoiceType.SYS_ERR);
            else if ("System.out".equalsIgnoreCase(logFile)) {
                output.add(OutputChoiceType.SYS_OUT);
            } else {
                try {
                    output.add(initialFileInputStream(logBasePath, logFilePattern, logFile));
                } catch (IOException e) {
                    LongriMessageFormatter.report("Could not open [" + logFile + "]. Defaulting to System.err", e);
                }
            }
        }
        return output;
    }

    private PrintStream initialFileInputStream(String logBasePath, String logFilePattern, String logFile) throws IOException {

        if (logBasePath == null) logBasePath = "./";
        Local_FileTransferHandle baseDir = new Local_FileTransferHandle(logBasePath);
        baseDir.mkdirs();

        logFile = new File(logFile).getName();

        RollingFileHandle rollingFileHandle = new RollingFileHandle(new Local_FileTransferHandle(logBasePath), logFile);

        int age = getIntegerProperty(LongriLogger.LOG_FILE_AGE_KEY, 0);
        int keep = getIntegerProperty(LongriLogger.LOG_FILE_KEEP_KEY, 1);

        boolean roll = rollingFileHandle.rolling(logFilePattern.replace("${logFileBasePath}", ""), age, keep);

        Local_FileTransferHandle currentLogFile = (Local_FileTransferHandle) baseDir.child(logFile);
        if (!currentLogFile.exists()) {
            currentLogFile.createNewFile();

            // set file attributes
            LocalDateTime creationDate = LocalDateTime.now();
            BasicFileAttributeView attributes = Files.getFileAttributeView(currentLogFile.getFile().toPath(), BasicFileAttributeView.class);
            FileTime time = FileTime.fromMillis(creationDate.toInstant(ZoneOffset.UTC).toEpochMilli());
            attributes.setTimes(time, time, time);

        }
        return new LogFileStream(currentLogFile.getFile());

    }

    public static boolean isSameDay(Date date1, Date date2) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        LocalDate localDate2 = date2.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate1.isEqual(localDate2);
    }

    public static boolean isOlderThenDays(Date date1, int days) {
        LocalDate localDate1 = date1.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        return localDate1.isBefore(LocalDate.now().minusDays(days));
    }

    public void setProperty(String key, boolean value) {
        properties.setProperty(key, String.valueOf(value));
        System.setProperty(key, String.valueOf(value));
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
        System.setProperty(key, value);
    }

}

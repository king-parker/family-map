package server;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpServer;
import handlers.*;
import handlers.FileHandler;
import model.objData.NameData;
import model.objData.LocationData;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.net.InetSocketAddress;
import java.util.logging.*;

/**
 * Server that receives requests to access, change and get data from the database from a client. The
 * server utilizes HTTP protocol
 */
public class Server {
    // STATIC MEMBERS
    /**
     * This member stores location data in ram to avoid loading the file every time new event data is
     * created to reduce runtime
     */
    public static final LocationData LOCATION_DATA;
    /**
     * This member stores female name data in ram to avoid loading the file every time new event data
     * is created to reduce runtime
     */
    public static final NameData FEMALE_NAME_DATA;
    /**
     * This member stores male name data in ram to avoid loading the file every time new event data
     * is created to reduce runtime
     */
    public static final NameData MALE_NAME_DATA;
    /**
     * This member stores surname name data in ram to avoid loading the file every time new event data
     * is created to reduce runtime
     */
    public static final NameData SURNAME_DATA;
    private static final int MAX_WAITING_CONNECTIONS = 12;
    private static Logger logger;
    /**
     * Name of the Server logger. Classes that use the logger can reference this name to use the server's
     * logger.
     */
    public static String logName;

    static {
        try {
            initializeLog();
        } catch (IOException e) {
            System.out.println("Could not initialize log: " + e.getMessage());
            e.printStackTrace();
        }

        LOCATION_DATA = loadData("locations","Location",LocationData.class);
        FEMALE_NAME_DATA = loadData("fnames","Female name", NameData.class);
        MALE_NAME_DATA = loadData("mnames","Male name", NameData.class);
        SURNAME_DATA = loadData("snames","Surname", NameData.class);
    }

    private static void initializeLog() throws IOException {
        initializeLog("FamilyMapServer");
    }

    private static void initializeLog(String name) throws IOException {
        initializeLog(name,"INFO");
    }

    private static void initializeLog(String name,String level) throws IOException {
        initializeLog(name,level,"true");
    }

    private static void initializeLog(String name,String level,String isLogConsole) throws IOException {
        initializeLog(name,level,isLogConsole,"true");
    }

    private static void initializeLog(String name,String level,String isLogConsole,String isLogFile)
            throws IOException {
        System.setProperty("java.util.logging.SimpleFormatter.format",
                "%1$tc %2$s%n%4$9s: %5$s%6$s%n");

        logName = name;
        Level logLevel = Level.parse(level.toUpperCase());

        logger = Logger.getLogger(name);
        logger.setLevel(logLevel);
        logger.setUseParentHandlers(false);

        removeLogHandlers();
        if (Boolean.parseBoolean(isLogConsole)) addLogHandler(new ConsoleHandler(),logLevel);
        if (Boolean.parseBoolean(isLogFile)) {
            addLogHandler(new java.util.logging.FileHandler("Server_Log.txt",false),logLevel);
        }
    }

    private static void addLogHandler(Handler handle,Level logLevel) {
        handle.setLevel(logLevel);
        handle.setFormatter(new SimpleFormatter());
        logger.addHandler(handle);
    }

    private static void removeLogHandlers() {
        Handler[] handlers = logger.getHandlers();
        for (Handler handler : handlers) logger.removeHandler(handler);
    }

    private static <DataType> DataType loadData(String jsonFileName,String dataName,
                                               Class<DataType> dataTypeClass) {
        String fileName = "json/" + jsonFileName + ".json";
        logger.fine("Loading " + dataName + " data from " + fileName);
        File locationFile = new File(fileName);
        try (Reader reader = new FileReader(locationFile)) {
            DataType data = new Gson().fromJson(reader, dataTypeClass);
            logger.finer(dataName + " data was successfully loaded");
            return data;
        } catch (IOException e) {
            logger.log(Level.SEVERE,dataName + " data was not loaded: " + e.getMessage(),e);
            return null;
        }
    }

    // NON-STATIC MEMBERS
    private void run(String portNumber) {
        logger.info("Initializing HTTP Server");
        HttpServer server;
        try {
            server = HttpServer.create(new InetSocketAddress(Integer.parseInt(portNumber)),
                    MAX_WAITING_CONNECTIONS);
        }
        catch (IOException e) {
            logger.log(Level.SEVERE,"Could not start server: " + e.getMessage(),e);
            return;
        }
        server.setExecutor(null);

        createContexts(server);

        server.start();
        logger.info("Server started on port: " + portNumber);
    }

    private static void createContexts(HttpServer server) {
        logger.info("Creating contexts");
        server.createContext("/user/register", new RegisterHandler());
        server.createContext("/user/login", new LoginHandler());
        server.createContext("/clear", new ClearHandler());
        server.createContext("/fill", new FillHandler());
        server.createContext("/load", new LoadHandler());
        server.createContext("/person", new PersonHandler());
        server.createContext("/event", new EventHandler());
        server.createContext("/", new FileHandler());
    }

    /**
     * Main function used to host the server. The first command line argument is the port the server will
     * use. The following command line arguments are used to configure the logger. They are used in this order:
     * Logger Name, Message Level, boolean to activate console log and boolean to activate file log. Any
     * parameters not included be turned into default parameters which are: "FamilyMapServer", INFO, true and
     * true.
     * @param args Port Number, Logger Name, Message Level, boolean to activate console log and boolean to
     *            activate file log
     */
    public static void main(String[] args) {
        inputLoggerConfig(args);
        String portNumber = args[0];
        new Server().run(portNumber);
    }

    private static void inputLoggerConfig(String[] args) {
        boolean isSuccess = false;
        try {
            if (args.length > 4 && isBool(args[4]) && isBool(args[3]) && isLevel(args[2])) {
                initializeLog(args[1],args[2],args[3],args[4]);
                isSuccess = true;
            } else if (args.length > 3 && isBool(args[3]) && isLevel(args[2])) {
                initializeLog(args[1],args[2],args[3]);
                isSuccess = true;
            } else if (args.length > 2 && isLevel(args[2])) {
                initializeLog(args[1],args[2]);
                isSuccess = true;
            } else if (args.length > 1) {
                initializeLog(args[1]);
                isSuccess = true;
            }
        } catch (IOException e) {
            logger.log(Level.WARNING, "Could not initialize log using command line " +
                                                          "arguments: " + e.getMessage(),e);
            return;
        }
        logCmdLnCnfg(isSuccess);
    }

    private static boolean isLevel(String level) {
        level = level.toUpperCase();
        return level.equals("SEVERE") || level.equals("WARNING") || level.equals("INFO") ||
               level.equals("CONFIG") || level.equals("FINE") || level.equals("FINER") ||
               level.equals("FINEST");
    }

    private static boolean isBool(String bool) {
        return bool.toLowerCase().equals("true") || bool.toLowerCase().equals("false");
    }

    private static void logCmdLnCnfg(boolean isSuccess) {
        if (isSuccess) logger.info("Logger set up with command line arguments");
        else logger.info("Could not initialize log using command line arguments");
    }
}

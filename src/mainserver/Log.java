package mainserver;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
  public static void log(int status, String msg) {
    Logger logger = null;
    try {

      logger = Logger.getLogger("Log");
      FileHandler fh;

      // This block configure the logger with handler and formatter
      fh = new FileHandler("MyLogFile.log", true);
      logger.addHandler(fh);
      SimpleFormatter formatter = new SimpleFormatter();
      fh.setFormatter(formatter);

      // the following statement is used to log any messages
      if (status == 0) {
        logger.severe(msg);
      } else if (status == 2) {
        logger.warning(msg);
      } else {
        logger.info(msg);
      }
    } catch (SecurityException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }

  }
}

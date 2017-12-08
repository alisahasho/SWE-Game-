package mainserver;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class Property_File {
	
public static String get_property(String property) {
	Properties prop = new Properties();
	InputStream input = null;
	try {
	  input = new FileInputStream("config.properties");
	  prop.load(input);
	} catch (IOException e) {
	  Log.log(0, "Config file missing or unreadable! Abort");
	  System.exit(15);
	} finally {
	  if (input != null) {
		try {
		  input.close();
		} catch (IOException e) {
		}
	  }
	}
	return prop.getProperty(property);
  }
}
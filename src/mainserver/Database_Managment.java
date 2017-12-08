package mainserver;

import java.sql.*;
import java.util.ArrayList;

public class Database_Managment {
  private Connection database;
  private PreparedStatement insert;
  private PreparedStatement update;
  private PreparedStatement delete;
  private PreparedStatement deleteAll;
  private PreparedStatement send;
  private Statement read;
  private ResultSet resultRead;

  public int connect() {
    try {
      Class.forName("org.postgresql.Driver");
      database = DriverManager.getConnection("jdbc:postgresql://localhost:5432/GingerGame", "postgres", "maria");
      // substitute jdbc:postgresql://localhost:5432/mensaDev with custom
      // URI of your DB
      // DirectoryPanel.mb.setText(DirectoryPanel.mb.getText()+
      // "\n Connection to DirectoryDB");
      database.setAutoCommit(false);
      // DirectoryPanel.mb.setText(DirectoryPanel.mb.getText()+
      // "\n AutoCommit not Active");
      return 0;
    } catch (SQLException e) {
      // DB offline or wrong credentials, create exception
      return -1;
    } catch (ClassNotFoundException ex) {
      // driver for DB is not correct
      return -2;
    }
  }

  public Connection getConnection() {
    // getter
    return database;
  }

  public boolean insert(String tableName, String[] attributeNames, String[] attributeValues) {

    String namesSeparated = "(";
    String valuesSeparated = "(";
    for (int i = 0; i < attributeNames.length - 1; i++) {
      namesSeparated += (attributeNames[i] + ", ");
      valuesSeparated += ("'" + attributeValues[i] + "', ");
    }
    namesSeparated += (attributeNames[attributeNames.length - 1] + ")");
    valuesSeparated += ("'" + attributeValues[attributeValues.length - 1] + "')");
    String insertTableSQL = "INSERT INTO " + tableName + namesSeparated + " VALUES" + valuesSeparated;
    try {
      insert = database.prepareStatement(insertTableSQL);
      insert.executeUpdate(); // still not commit
      database.commit(); // now commit
      // insertion worked
      return true;
    } catch (SQLException e) {
      // insertion failed
      return false;
    }
  }

  public void update(String tableName, String keyName, String keyValue, String updateName, String updateValue) {
    String updateTableSQL = "UPDATE " + tableName + " SET " + updateName + " = '" + updateValue + "' WHERE " + keyName + " = '" + keyValue + "'";
    try {
      update = database.prepareStatement(updateTableSQL);
      update.executeUpdate(); // still not commit
      database.commit(); // now commit
      // update worked
    } catch (SQLException e) {
      // update failed
    }
  }

  public void delete(String tableName, String keyName, String keyValue) {
    String deleteTableSQL = "DELETE FROM " + tableName + " WHERE " + tableName + "." + keyName + " = " + "'" + keyValue + "'";
    try {
      delete = database.prepareStatement(deleteTableSQL);
      delete.executeUpdate(); // still not commit
      database.commit(); // now commit
      // delete worked
    } catch (SQLException e) {
      // delete worked
    }
  }

  public void deleteAll(String tableName) {
    String deleteTableSQL = "DELETE FROM " + tableName;
    try {
      deleteAll = database.prepareStatement(deleteTableSQL);
      deleteAll.executeUpdate(); // still not commit
      database.commit(); // now commit
      // deleteall worked
    } catch (SQLException e) {
      // deleteall worked
    }
  }

  //
  // public boolean customSendQuery(String query) {
  // try {
  // send = database.prepareStatement(query);
  // send.executeUpdate(); // still not commit
  // database.commit(); // now commit
  // BashPanel.mbAdd("Operation finished with success\n");
  // return true;
  // } catch (SQLException e) {
  // BashPanel.mbAdd("Operation finished with Failure\n");
  // return false;
  // }
  // }

  public String[][] read(String tableName, String[] attributes) {
    String thingsToSeeSeparatedByCommas = "";
    for (int i = 0; i < attributes.length - 1; i++) {
      thingsToSeeSeparatedByCommas += (attributes[i] + ", ");
    }
    thingsToSeeSeparatedByCommas += attributes[attributes.length - 1];
    String readTableSQL = "SELECT " + thingsToSeeSeparatedByCommas + " FROM " + tableName;
    int columns = attributes.length;
    String[][] readString = null;
    try {
      read = database.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
      resultRead = read.executeQuery(readTableSQL);
      resultRead.last();
      int rows = resultRead.getRow();
      resultRead.beforeFirst();
      readString = new String[rows + 1][columns];
      int b = 1;
      readString[0] = attributes;
      while (resultRead.next()) {
        for (int a = 0; a < columns; a++) {
          readString[b][a] = resultRead.getString(attributes[a]);
        }
        b++;
      }
    } catch (SQLException e) {
      // read failed
    }
    return readString;
  }

  public String[][] read(String query) {
    int columns = 0;
    String[] components = query.split("\u0020");
    ArrayList<String> titles = new ArrayList<String>();
    int lastcomponent = 0;
    for (int i = 0; i < components.length; i++) {
      if (components[i].toUpperCase().equals("SELECT")) {
        for (int j = i + 1; j < components.length; j++) {
          if (!components[j].toUpperCase().equals("DISTINCT")) {
            if (components[j].charAt(components[j].length() - 1) == (44)) {
              titles.add(components[j].substring(0, components[j].length() - 1));
              columns++;
            } else {
              lastcomponent = j;
              break;
            }
          }
        }
        titles.add(components[lastcomponent]);
        columns++;
      }
    }
    String[] columnNames = new String[columns];
    for (int i = 0; i < columns; i++) {
      if (titles.get(i).contains(".")) {
        String[] complex = titles.get(i).split("\\.");
        columnNames[i] = complex[complex.length - 1];
      } else {
        columnNames[i] = titles.get(i);
      }
    }
    String[][] readString = null;
    try {
      read = database.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
      resultRead = read.executeQuery(query);
      resultRead.last();
      int rows = resultRead.getRow();
      resultRead.beforeFirst();
      readString = new String[rows + 1][columns];
      int b = 1;
      readString[0] = columnNames;
      while (resultRead.next()) {
        for (int a = 0; a < columns; a++) {
          readString[b][a] = resultRead.getString(columnNames[a]);
        }
        b++;
      }
      // BashPanel.mb.setText(BashPanel.mb.getText()+
      // "Operation finished with success\n");
    } catch (SQLException e) {
      // read failed
      e.printStackTrace();
    }
    return readString;
  }
}

import java.sql.*;

//Run instructions on my machine:  java -cp .:mysql-connector-java-8.0.13/mysql-connector-java-8.0.13.jar homework3
//I pledge my honor that I have abided by the stevens honor system - Daniel Kramer
public class homework3 {
   // JDBC driver name and database URL
   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
   static final String DB_URL = "jdbc:mysql://localhost/test?serverTimezone=EST"; //Recognizable timezone

   //  Database credentials
   static final String USER = "root";
   static final String PASS = "root";
   
   public static void main(String[] args) {
   Connection conn = null;
   Statement stmt = null;
   try{
      //Register JDBC driver
      Class.forName("com.mysql.cj.jdbc.Driver");

      //Open a connection to database
      System.out.println("Connecting to database...");
      conn = DriverManager.getConnection(DB_URL, USER, PASS);

      System.out.println("Creating database...");
      stmt = conn.createStatement();

      //Use SQL to Create Database BoatRental 
      String sql = "DROP DATABASE if exists BoatRental";
      stmt.executeUpdate(sql);
       sql = "CREATE DATABASE BoatRental";
      stmt.executeUpdate(sql);
      System.out.println("Database created successfully...");
      
      //STEP 4: Use SQL to select the database
      sql = "use BoatRental";
      stmt.executeUpdate(sql);

      //Create Tables ---------

      //Table sailors
      sql = "create table sailors(sid integer not null PRIMARY KEY, " +
            "sname varchar(20) not null," +
            "rating real not null," +
            "age integer not null)";
      stmt.executeUpdate(sql);

      //Table boats
      sql = "create table boats(bid integer not null PRIMARY KEY, " +
            "bname varchar(40) not null," +
            "color varchar(40) not null)";
      stmt.executeUpdate(sql);

      //Table reserves
      sql = "create table reserves(sid integer not null, " +
            "bid integer not null," +
            "day date not null," +
            "PRIMARY KEY(sid, bid, day)," +
            "FOREIGN KEY(sid) REFERENCES sailors(sid)," +
            "FOREIGN KEY(bid) REFERENCES boats(bid))";
      stmt.executeUpdate(sql);

      //Insert Tuples into tables (one at a time) ------
      //sailors
      sql = "insert into sailors values(22, 'Dustin', 7, 45)";
      stmt.executeUpdate(sql);
      sql = "insert into sailors values(29, 'Brutus', 1, 33)";
      stmt.executeUpdate(sql);
      sql = "insert into sailors values(31, 'Lubber', 8, 55)";
      stmt.executeUpdate(sql);
      sql = "insert into sailors values(32, 'Andy', 8, 26)";
      stmt.executeUpdate(sql);
      sql = "insert into sailors values(58, 'Rusty', 10, 35)";
      stmt.executeUpdate(sql);
      sql = "insert into sailors values(64, 'Horatio', 7, 35)";
      stmt.executeUpdate(sql);
      sql = "insert into sailors values(71, 'Zorba', 20, 18)";
      stmt.executeUpdate(sql);
      sql = "insert into sailors values(74, 'Horatio', 9, 35)";
      stmt.executeUpdate(sql);

      //boats
      sql = "insert into boats values(101, 'Interlake', 'Blue')";
      stmt.executeUpdate(sql);
      sql = "insert into boats values(102, 'Interlake', 'Red')";
      stmt.executeUpdate(sql);
      sql = "insert into boats values(103, 'Clipper', 'Green')";
      stmt.executeUpdate(sql);
      sql = "insert into boats values(104, 'Marine', 'Red')";
      stmt.executeUpdate(sql);

      //reserves
      sql = "insert into reserves values(22, 101, '2018-10-10')";
      stmt.executeUpdate(sql);
      sql = "insert into reserves values(22, 102, '2018-10-10')";
      stmt.executeUpdate(sql);
      sql = "insert into reserves values(22, 103, '2017-10-08')";
      stmt.executeUpdate(sql);
      sql = "insert into reserves values(22, 104, '2017-10-09')";
      stmt.executeUpdate(sql);
      sql = "insert into reserves values(31, 102, '2018-11-10')";
      stmt.executeUpdate(sql);
      sql = "insert into reserves values(31, 103, '2018-11-06')";
      stmt.executeUpdate(sql);
      sql = "insert into reserves values(31, 104, '2018-11-12')";
      stmt.executeUpdate(sql);
      sql = "insert into reserves values(64, 101, '2018-04-05')";
      stmt.executeUpdate(sql);
      sql = "insert into reserves values(64, 102, '2018-09-08')";
      stmt.executeUpdate(sql);
      sql = "insert into reserves values(74, 103, '2018-09-08')";
      stmt.executeUpdate(sql);

      //Execute SQL Queries ----------------
      //Q1:  Find the name of all sailors who have reserved red boats but not green boats before June 1, 2018.
      Statement s = conn.createStatement ();
      s.executeQuery ("SELECT sname FROM sailors natural join reserves natural join boats"
        + " WHERE color = 'red' AND day < '2018-06-01'"
        + " AND sid NOT IN"
        + "(SELECT sid FROM sailors natural join reserves natural join boats"
        + " WHERE color = 'green' AND day < '2018-06-01')");
      ResultSet rs = s.getResultSet ();
      int count = 0;
      while (rs.next ())
      {
          String nameVal = rs.getString ("sname");
          System.out.println("Sailor name = " + nameVal);
          ++count;
      }
      rs.close ();
      s.close ();
      System.out.println (count + " rows were retrieved for Q1\n");

      //Q2:  Find the names of sailors who never reserved a red boat.
      s = conn.createStatement ();
      s.executeQuery ("SELECT sname FROM sailors"
        + " WHERE sid NOT IN (SELECT sid FROM reserves natural join boats"
        + " WHERE color = 'red')");
      rs = s.getResultSet ();
      count = 0;
      while (rs.next ())
      {
          String nameVal = rs.getString ("sname");
          System.out.println("Sailor name = " + nameVal);
          ++count;
      }
      rs.close ();
      s.close ();
      System.out.println (count + " rows were retrieved for Q2\n");
      
      //Q3:  Find sailors whose rating is better than all the sailors named Horatio.
      s = conn.createStatement ();
     s.executeQuery ("SELECT sname FROM sailors"
        + " WHERE rating > (SELECT MAX(rating) FROM sailors"
        + " WHERE sname = 'Horatio')");
      rs = s.getResultSet ();
      count = 0;
      while (rs.next ())
      {
          String nameVal = rs.getString ("sname");
          System.out.println("Sailor name = " + nameVal);
          ++count;
      }
      rs.close ();
      s.close ();
      System.out.println (count + " rows were retrieved for Q3\n");

      //Q4:  Find the names of sailors who have reserved all the boats.
      s = conn.createStatement ();
      s.executeQuery ("SELECT sname FROM reserves natural join sailors"
        + " GROUP BY sid"
        + " HAVING COUNT(*) =(SELECT COUNT(bid) FROM boats)");
      rs = s.getResultSet ();
      count = 0;
      while (rs.next ())
      {
          String nameVal = rs.getString ("sname");
          System.out.println("Sailor name = " + nameVal);
          ++count;
      }
      rs.close ();
      s.close ();
      System.out.println (count + " rows were retrieved for Q4\n");

      }catch(SQLException se){
      //Handle errors for JDBC
      se.printStackTrace();
   }catch(Exception e){
      //Handle errors for Class.forName
      e.printStackTrace();
   }finally{
      //finally block used to close resources
      try{
         if(stmt!=null)
            stmt.close();
      }catch(SQLException se2){
      }// nothing we can do
      try{
         if(conn!=null)
            conn.close();
      }catch(SQLException se){
         se.printStackTrace();
      }//end finally try
   }//end try
   System.out.println("Goodbye!");
}//end main
}//end homework3
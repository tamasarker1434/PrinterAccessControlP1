package org.example;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.sql.*;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.io.Serializable;

public class PrintServices extends UnicastRemoteObject implements IPrintServices {
    HashMap<String,Long> sessionHashMap = new HashMap<String,Long>();
    public PrintServices() throws RemoteException{
        super();
    }

    private boolean sessionCheck(String sessionId){
        if (sessionHashMap.containsKey(sessionId)){
            long timeNano = System.nanoTime() - sessionHashMap.get(sessionId);
            long timeSec = TimeUnit.SECONDS.convert( timeNano,TimeUnit.NANOSECONDS);
            System.out.println("Time Elapsed= " + timeSec +" seconds");
            if (timeSec <= 10) {
                System.out.println("SessionId generated by the server for this session = " + sessionId);
                sessionHashMap.put(sessionId,System.nanoTime());
                return true;
            }
            else {
                sessionHashMap.remove(sessionId);
            }
        }
        return false;
    }
    @Override
    public Dictionary<String, UserDetails> userLogin(String userId, String password) throws RemoteException {
        Dictionary<String,UserDetails> userInfo = new Hashtable<>();
        Dictionary<Integer, String> functionAccesed = new Hashtable<>();
        String session = null,query;
        UserDetails userDetails = new UserDetails();
        query = "SELECT * FROM userprofile \n" +
                "INNER JOIN functionaccess on functionaccess.userid = userprofile.userid\n" +
                "INNER JOIN functions ON functions.functionid = functionaccess.fuctionid\n" +
                "WHERE userprofile.userid ='"+userId+"' AND userprofile.password ='"+password+"'";
        String url = "jdbc:mysql://localhost:3306/jdbcprinterdbp1", username ="root", dbPassword="";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url,username,dbPassword);
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                Random random = new Random();
                int sessionId = random.nextInt();
                session =Integer.toString(sessionId);
                sessionHashMap.put(session,System.nanoTime());
                userDetails.userId = resultSet.getInt("id");
                functionAccesed.put(resultSet.getInt("functionid"), resultSet.getString("functiontitle"));
            }

            if ( userDetails.userId == 0)
                return userInfo;
            else {
                userDetails.function = functionAccesed;
                userInfo.put(session,userDetails);
                System.out.println("SessionId=" + session);
                connection.close();
                return userInfo;
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public String echo(String input) throws RemoteException {
        return "From Server: "+ input;
    }

    @Override
    public String print(String filename, String printer, String sessionId) throws RemoteException {
        if (sessionCheck(sessionId)) {
            return "Print the file= " + filename + " on printer = " + printer;
        }
        else
            return null;
    }

    @Override
    public String queue(String printer, String sessionId) throws RemoteException {
        if (sessionCheck(sessionId)) {
            return "Print the queue of the printer = " + printer ;
        }
        else
            return null;
    }

    @Override
    public String topQueue(String printer, int job, String sessionId) throws RemoteException {
        if (sessionCheck(sessionId)) {
            return "Move the Job no = "+ job+ "of printer no= "+printer ;
        }
        else
            return null;
    }

    @Override
    public String start(String sessionId) throws RemoteException {
        if (sessionCheck(sessionId)) {
            return "Start the printer." ;
        }
        else
            return null;
    }

    @Override
    public String stop(String sessionId) throws RemoteException {
        if (sessionCheck(sessionId)) {
            return "Stop the printer" ;
        }
        else
            return null;
    }

    @Override
    public String restart(String sessionId) throws RemoteException {
        if (sessionCheck(sessionId)) {
            return "Restart the Printer" ;
        }
        else
            return null;
    }

    @Override
    public String status(String printer, String sessionId) throws RemoteException {
        if (sessionCheck(sessionId)) {
            return "The status of the printer no= " + printer ;
        }
        else
            return null;
    }

    @Override
    public String readConfig(String parameter, String sessionId) throws RemoteException {
        if (sessionCheck(sessionId)) {
            return "The parameter is =" + parameter ;
        }
        else
            return null;
    }

    @Override
    public String setConfig(String parameter, String value, String sessionId) throws RemoteException {
        if (sessionCheck(sessionId)) {
            return "Set the parameter = " + parameter+" with the value  = " + value ;
        }
        else
            return null;
    }
    @Override
    public boolean singnUpUser(UserDetails userDetails, int ulUserArr[]){
        boolean result = false;
        String url = "jdbc:mysql://localhost:3306/jdbcprinterdbp1", username ="root", dbPassword="";
        String queryf, query = "INSERT INTO `userprofile` (`id`, `userid`, `password`, `activeuser`) \n" +
                "VALUES (NULL, '"+userDetails.userName+"', '"+userDetails.password+"', '1');";
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url,username,dbPassword);
            Statement statement = connection.createStatement();
            statement.addBatch(query);
            for(int i = 0 ; i< ulUserArr.length ; i++){
                if (ulUserArr[i]!=0) {
                    queryf = "INSERT INTO `functionaccess` (`id`, `userid`, `fuctionid`) VALUES (NULL, '" + userDetails.userName + "', '" + ulUserArr[i] + "')";
                    statement.addBatch(queryf);
                }
            }
            statement.executeBatch();
            result = true;
            connection.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return result;
    }
    @Override
    public boolean deleteUser(String userId){
        boolean result = false;
        String url = "jdbc:mysql://localhost:3306/jdbcprinterdbp1", username ="root", dbPassword="";
        String query = "UPDATE userprofile SET activeuser = 0 WHERE userprofile.userid ='" + userId + "'";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url,username,dbPassword);
            Statement statement = connection.createStatement();
            int resultSet = statement.executeUpdate(query);
            if (resultSet != 0)
                result = true;
            connection.close();
        } catch (Exception e) {
            result = false;
            throw new RuntimeException(e);
        }
        return result;
    }
}

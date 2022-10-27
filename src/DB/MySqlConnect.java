package DB;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import javax.swing.JOptionPane;


public class MySqlConnect
{
    Connection conn;

    public static Connection connectDB()
    {
        try
        {
            Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/chat-application", "Prem1912", "studylink");
            JOptionPane.showMessageDialog(null, "Connection Established Successfully");
            return conn;
        }
        catch(SQLException e)
        {
            JOptionPane.showMessageDialog(null, "Connection Failed!! " + e);
            return null;
        }
    }
}

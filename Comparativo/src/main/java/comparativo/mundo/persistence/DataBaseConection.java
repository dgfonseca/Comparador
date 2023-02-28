package comparativo.mundo.persistence;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;


public class DataBaseConection {

    private Connection connection;

    public DataBaseConection(String user, String password, String uri) throws SQLException{
        this.connection = conexionDb(user, password, uri);
    }

    public Connection conexionDb(String user, String password, String uri) throws SQLException{
		String url = "jdbc:postgresql://"+uri+"/comparativo";
		Properties props = new Properties();
		props.setProperty("user",user);
		props.setProperty("password",password);
		return DriverManager.getConnection(url,props);
	}

    public Connection getConnection(){
        return this.connection;
    }
}

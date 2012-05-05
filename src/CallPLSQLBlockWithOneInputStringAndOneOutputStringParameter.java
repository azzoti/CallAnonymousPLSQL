import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Types;

public class CallPLSQLBlockWithOneInputStringAndOneOutputStringParameter {

    // Warning: this is a simple example program : In a long running application,
    // exception handlers MUST clean up connections statements and result sets.
    public static void main(String[] args) throws SQLException {

    	DriverManager.registerDriver(new oracle.jdbc.OracleDriver());

        final Connection c = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "manager");
		String plsql = "" +
		" declare " +  
		"      p_id varchar2(20) := null; " +
		" begin " +
		"    p_id := ?; " +
		"    ? := 'input parameter was = ' || p_id;" +
		" end;";
        CallableStatement cs = c.prepareCall(plsql);
		cs.setString(1, "12345");
		cs.registerOutParameter(2, Types.VARCHAR);
		cs.execute();
		
		System.out.println("Output parameter was = '" + cs.getObject(2) + "'");
		
		cs.close();
        c.close();
    }
}
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Types;

import oracle.jdbc.OracleTypes;

public class CallPLSQLBlockWithOneInputStringAndOneOutputStringParameterAndOneOutputCursorParameter {

    public static void main(String[] args) throws Exception {

    	DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
    	
        final Connection c = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "manager");
		String plsql = "" +
		" declare " +  
		"    p_id varchar2(20) := null; " +
		"	 l_rc sys_refcursor;" +
		" begin " +
		"    p_id := ?; " +
		"    ? := 'input parameter was = ' || p_id;" +
		"    open l_rc for " +
		"        select 1 id, 'hello' name from dual " +
		"        union " +
		"        select 2, 'peter' from dual; " +
		"    ? := l_rc;" +
		" end;";

        CallableStatement cs = c.prepareCall(plsql);
		cs.setString(1, "12345");
		cs.registerOutParameter(2, Types.VARCHAR);
		cs.registerOutParameter(3, OracleTypes.CURSOR);
		
		cs.execute();
		
		System.out.println("Result = " + cs.getObject(2));
		
		ResultSet cursorResultSet = (ResultSet) cs.getObject(3);
		while (cursorResultSet.next ())
		{
			System.out.println (cursorResultSet.getInt(1) + " " + cursorResultSet.getString(2));
		} 
		cs.close();
        c.close();
    }
}
import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Types;

import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;

public class CallPLSQLBlockWithOneInputStringArrayAndOneOutputStringParameterAndOneOutputCursorParameter {

    public static void main(String[] args) throws Exception {

        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        
        // Warning: this is a simple example program : In a long running application,
        // error handlers MUST clean up connections statements and result sets.
        
        final Connection c = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "manager");
        String plsql = "" +
        " declare " +  
        "    p_id string_array := null; " +
        "     l_rc sys_refcursor;" +
        " begin " +
        "    p_id := ?; " +
        "    ? := 'input parameter first element was = ' || p_id(1);" +
        "    open l_rc for select * from table(p_id) ; " +
        "    ? := l_rc;" +
        " end;";

        String[] stringArray = new String[]{ "mathew", "mark"};
        
        // MUST CREATE THIS IN ORACLE BEFORE RUNNING
        System.out.println("(This should be done once in Oracle)");
        c.createStatement().execute("create or replace type string_array is table of varchar2(32)");
        
        ArrayDescriptor descriptor = ArrayDescriptor.createDescriptor( "STRING_ARRAY", c );
        
        Array array_to_pass = new ARRAY( descriptor, c, stringArray );
        
        CallableStatement cs = c.prepareCall(plsql);
        cs.setArray( 1, array_to_pass );
        cs.registerOutParameter(2, Types.VARCHAR);
        cs.registerOutParameter(3, OracleTypes.CURSOR);
        
        cs.execute();
        
        System.out.println("Result = " + cs.getObject(2));
        
        ResultSet cursorResultSet = (ResultSet) cs.getObject(3);
        while (cursorResultSet.next ())
        {
            System.out.println (cursorResultSet.getString(1));
        } 
        cs.close();
        c.close();
    }
}
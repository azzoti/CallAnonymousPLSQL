import java.sql.Array;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

public class CallPLSQLBlockWithOneInputStructureArrayAndOneOutputStringParameterAndOneOutputCursorParameter {

    public static void main(String[] args) throws Exception {

        DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
        
        // Warning: this is a simple example program : In a long running application,
        // error handlers MUST clean up connections statements and result sets.
        
        final Connection c = DriverManager.getConnection("jdbc:oracle:thin:@localhost:1521:XE", "system", "manager");
        String plsql = "" +
        " declare " +  
        "    p_id student_array := null; " +
        "     l_rc sys_refcursor;" +
        " begin " +
        "    p_id := ?; " +
        "    ? := 'input parameter first element was = (' || p_id(1).id_num || ', ' || p_id(1).name || ')'; " +
        "    open l_rc for select * from table(p_id) ; " +
        "    ? := l_rc;" +
        " end;";

        
        // MUST CREATE ORACLE TYPES BEFORE RUNNING
        setupOracleTypes(c);
        
        StructDescriptor structDescr = StructDescriptor.createDescriptor("STUDENT", c);
        STRUCT s1struct = new STRUCT(structDescr, c, new Object[]{1, "mathew"});
        STRUCT s2struct = new STRUCT(structDescr, c, new Object[]{2, "mark"});
        ArrayDescriptor arrayDescr = ArrayDescriptor.createDescriptor( "STUDENT_ARRAY", c );
        Array array_to_pass = new ARRAY( arrayDescr, c, new Object[]{s1struct, s2struct} );
        
        CallableStatement cs = c.prepareCall(plsql);
        cs.setArray( 1, array_to_pass );
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

    private static void setupOracleTypes(final Connection c)
            throws SQLException {
        System.out.println("(This should be done once in Oracle)");
        try {
            c.createStatement().execute("drop type student_array ");
        } catch (Exception e) {
            // ignore
        }
        c.createStatement().execute("create or replace type student as object (id_num integer(4), name varchar2(25))");
        c.createStatement().execute("create or replace type student_array is table of student");
    }


    

}
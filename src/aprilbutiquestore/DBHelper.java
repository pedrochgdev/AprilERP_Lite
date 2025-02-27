// DBHelper.java
package aprilbutiquestore;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import oracle.jdbc.OracleTypes;

public class DBHelper {
    private final Connection conexion;

    public DBHelper(Connection conexion) {
        this.conexion = conexion;
    }

    public void ejecutarProcedimiento(String procedimiento, Object... parametros) throws SQLException {
        try (CallableStatement stmt = conexion.prepareCall("{call " + procedimiento + "}")) {
            for (int i = 0; i < parametros.length; i++) {
                stmt.setObject(i + 1, parametros[i]);
            }
            stmt.execute();
        }
    }
    
    public void registrarDevolucion(int idVenta, String razon, Map<Integer, Integer> productosDevolucion) 
    throws SQLException {
    
        try {
            conexion.setAutoCommit(false);

            // Registrar cabecera
            try (CallableStatement cstmt = conexion.prepareCall("{call REGISTRAR_DEVOLUCION(?, ?)}")) {
                cstmt.setInt(1, idVenta);
                cstmt.setString(2, razon);
                cstmt.execute();
            }

            // Registrar detalle
            try (CallableStatement cstmt = conexion.prepareCall("{call REGISTRAR_DETALLE_DEVOLUCION(?, ?, ?)}")) {
                for (Map.Entry<Integer, Integer> entry : productosDevolucion.entrySet()) {
                    cstmt.setInt(1, idVenta);
                    cstmt.setInt(2, entry.getKey());
                    cstmt.setInt(3, entry.getValue());
                    cstmt.addBatch();
                }
                cstmt.executeBatch();
            }

            conexion.commit();
        } catch(SQLException e) {
            conexion.rollback();
            throw e;
        } finally {
            conexion.setAutoCommit(true);
        }
    }

    public ResultSet ejecutarConsulta(String sql, Object... parametros) throws SQLException {
        PreparedStatement stmt = conexion.prepareStatement(sql);
        for (int i = 0; i < parametros.length; i++) {
            stmt.setObject(i + 1, parametros[i]);
        }
        return stmt.executeQuery();
    }

    // Versión final con manejo seguro de recursos
    public ResultSet ejecutarFuncionCursor(String funcion) throws SQLException {
        CallableStatement stmt = conexion.prepareCall("{ ? = call " + funcion + " }");
        try {
            stmt.registerOutParameter(1, OracleTypes.CURSOR);
            stmt.execute();
            return stmt.getObject(1, ResultSet.class);
        } catch (SQLException e) {
            stmt.close(); // Cerrar statement si hay error
            throw e;
        }
    }
    public Map<Integer, Object> ejecutarProcedimientoConOut(
        String procedimiento, 
        Object[] parametrosIn,
        int[] posicionesOut,
        int[] tiposOut
    ) throws SQLException {
        Map<Integer, Object> outValues = new HashMap<>();
        String call = "{call " + procedimiento + "(";
        for (int i = 0; i < parametrosIn.length + posicionesOut.length; i++) {
            call += (i > 0 ? ",?" : "?"); 
        }
        call += ")}";

        CallableStatement stmt = conexion.prepareCall(call);

        // Parámetros IN
        for (int i = 0; i < parametrosIn.length; i++) {
            stmt.setObject(i + 1, parametrosIn[i]);
        }

        // Parámetros OUT
        for (int i = 0; i < posicionesOut.length; i++) {
            stmt.registerOutParameter(parametrosIn.length + i + 1, tiposOut[i]);
        }

        stmt.execute();

        // Recuperar OUT
        for (int i = 0; i < posicionesOut.length; i++) {
            outValues.put(posicionesOut[i], stmt.getObject(parametrosIn.length + i + 1));
        }

        return outValues;
    }
    
    public ResultSet ejecutarProcedimientoCursor(String procedimiento, Object... parametros) throws SQLException {
        Map<Integer, Object> outValues = ejecutarProcedimientoConOut(
            procedimiento,
            parametros,
            new int[]{parametros.length + 1}, // Asume que el OUT es último
            new int[]{OracleTypes.CURSOR}
        );
        return (ResultSet) outValues.get(parametros.length + 1);
    }
}
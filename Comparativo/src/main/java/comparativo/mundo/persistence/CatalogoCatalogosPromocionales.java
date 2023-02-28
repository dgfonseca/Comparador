package comparativo.mundo.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import comparativo.mundo.model.Stock;

public class CatalogoCatalogosPromocionales {

    private Connection connection;

    public CatalogoCatalogosPromocionales(Connection connection) {
        this.connection = connection;
    }

    public ResultSet getStock(String referencia) throws SQLException{
        PreparedStatement ps = this.connection.prepareStatement("SELECT color, bodega_local, bodega_zona_franca, total_disponible,"+
        "llegada_bodega_local, cantidad_transito, estado_orden FROM stock_propios where referencia=?");
        ps.setString(1,referencia);
        return ps.executeQuery();
    }

    public void deleteStockProducto(String referencia)throws SQLException{
        PreparedStatement ps = this.connection.prepareStatement("DELETE FROM stock_propios WHERE referencia=?");
        ps.setString(1, referencia);
        ps.executeUpdate();
    }

    public int insertStockProducto(Stock stock) throws SQLException{
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO stock_propios(referencia,color,bodega_local,bodega_zona_franca,total_disponible,llegada_bodega_local,cantidad_transito,estado_orden) "+
        "VALUES (?,?,?,?,?,?,?,?) ON CONFLICT(referencia,color,bodega_local,bodega_zona_franca,total_disponible,llegada_bodega_local,cantidad_transito,estado_orden) DO NOTHING");
        ps.setString(1, stock.getReferencia());
        ps.setString(2,stock.getColor());
        ps.setInt(3, stock.getBodegaLocal());
        ps.setInt(4, stock.getBodegaZonaFranca());
        ps.setInt(5, stock.getTotalDisponible());
        ps.setString(6,stock.getLlegadaBodegaLocal());
        ps.setInt(7, stock.getCantidadTransito());
        ps.setString(8,stock.getEstadoOrden());
        return ps.executeUpdate();
    }

    public int insertProducto(String referencia,String nombre,Double precio, Double precio2, Double precio3, Double precio4, Double precio5) throws SQLException{
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO productos_propios(referencia,nombre,precio,precio2,precio3,precio4,precio5) "+
        "VALUES(?,?,?,?,?,?,?) ON CONFLICT(referencia) DO UPDATE SET precio = ?, precio2= ?, precio3= ?, precio4 = ?, precio5 = ?");
        ps.setString(1, referencia);
        ps.setString(2, nombre);
        ps.setDouble(3, precio);
        ps.setDouble(4, precio2);
        ps.setDouble(5, precio3);
        ps.setDouble(6, precio4);
        ps.setDouble(7, precio5);
        ps.setDouble(8, precio);
        ps.setDouble(9, precio2);
        ps.setDouble(10, precio3);
        ps.setDouble(11, precio4);
        ps.setDouble(12, precio5);
        return ps.executeUpdate();
    }

    public int updateProductosPropios(Double precio1, Double precio2, Double precio3, Double precio4, Double precio5, String referencia)throws SQLException{
		PreparedStatement ps = connection.prepareStatement("UPDATE productos_propios SET precio = ?, precio2 = ?, precio3 = ?, precio4 = ?, precio5 = ? "+
		" WHERE referencia = ? AND (precio != ? OR precio2 != ? OR precio3 != ? OR precio4 != ? OR precio5 != ?)");
		ps.setDouble(1, precio1);
			ps.setDouble(2, precio2);
			ps.setDouble(3, precio3);
			ps.setDouble(4, precio4);
			ps.setDouble(5, precio5);
			ps.setString(6, referencia);
			ps.setDouble(7, precio1);
			ps.setDouble(8, precio2);
			ps.setDouble(9, precio3);
			ps.setDouble(10, precio5);
			ps.setDouble(11, precio5);
		return ps.executeUpdate();
	}
    
    
}

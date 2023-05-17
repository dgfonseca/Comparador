package comparativo.mundo.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ComparacionesPromopciones {

    private Connection connection;

    public ComparacionesPromopciones(Connection connection){
        this.connection=connection;
    }

    public ResultSet getHistoricoComparaciones() throws SQLException{

        PreparedStatement ps = this.connection.prepareStatement("SELECT pp.referencia, pc.codigo_hijo,"+
			"cmp.fecha_comparacion, cmp.precio_propio, cmp.descuento_propio, cmp.precio_competencia, cmp.descuento_competencia,"+
			"cmp.descuento_propio2, cmp.descuento_competencia2, pp.nombre, pc.codigo_padre, pc.nombre, cmp.numero_precio FROM historico_comparaciones as cmp INNER JOIN PRODUCTOS_PROPIOS as pp on cmp.referencia_propio = pp.referencia "+
			"INNER JOIN PRODUCTOS_COMPETENCIA as pc on pc.codigo_hijo = cmp.referencia_competencia");
		ResultSet rs = ps.executeQuery();
        return rs;
    }

    public int insertHistoricoComparacion(String referenciaPropio, String referenciaCompetencia, String fecha, Double precioPropio, Double descuentoPropio, Double precioCompetencia, Double descuentoCompetencia, Double descuentoPropio2, Double descuentoCompetencia2, int numeroPrecio) throws SQLException{
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO historico_comparaciones(referencia_propio,referencia_competencia,fecha_comparacion,precio_propio,descuento_propio,precio_competencia,descuento_competencia, descuento_propio2, descuento_competencia2, numero_precio) "+
        "VALUES(?,?,?,?,?,?,?,?,?,?) ON CONFLICT (referencia_propio, referencia_competencia, fecha_comparacion) DO UPDATE SET descuento_propio=?, descuento_competencia=?, descuento_propio2=?, descuento_competencia2=?");
        ps.setString(1, referenciaPropio);
		ps.setString(2, referenciaCompetencia);
		ps.setString(3, fecha);
		ps.setDouble(4, precioPropio);
		ps.setDouble(5, descuentoPropio);
		ps.setDouble(6, precioCompetencia);
		ps.setInt(7, descuentoCompetencia.intValue());
		ps.setDouble(8, descuentoPropio2);
		ps.setDouble(9, descuentoCompetencia2);
		ps.setInt(10, numeroPrecio);
		ps.setDouble(11, descuentoPropio);
		ps.setInt(12, descuentoCompetencia.intValue());
		ps.setDouble(13, descuentoPropio2);
		ps.setDouble(14, descuentoCompetencia2);

        return ps.executeUpdate();
    }

    public int insertComparacion(String referenciaPropio, String referenciaCompetencia, Double descuentoPropio, Double descuentoCompetencia, Double descuentoPropio2, Double descuentoCompetencia2, int numeroPrecio) throws SQLException{
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO comparaciones(referencia_propio,referencia_competencia,descuento_propio,descuento_competencia," +
        "descuento_propio2,descuento_competencia2, numero_precio)" +
        "SELECT * FROM (SELECT ? AS referencia_propio, ? as referencia_competencia, ? as descuento_propio, ? as descuento_competencia, ? as descuento_propio2, ? as descuento_competencia2, ? as numero_precio) AS temp "+
        "WHERE NOT EXISTS ( "+
        "SELECT referencia_propio, referencia_competencia FROM comparaciones WHERE referencia_propio = ? and referencia_competencia = ? and numero_precio = ?"+
        ") LIMIT 1");
        ps.setString(1, referenciaPropio);
		ps.setString(2, referenciaCompetencia);
		ps.setDouble(3, descuentoPropio);
		ps.setInt(4, descuentoCompetencia.intValue());
		ps.setDouble(5, descuentoPropio2);
		ps.setDouble(6, descuentoCompetencia2);
		ps.setDouble(7, numeroPrecio);
		ps.setString(8, referenciaPropio);
		ps.setString(9, referenciaCompetencia);
		ps.setDouble(10, numeroPrecio);

        return ps.executeUpdate();
    }

    public int updateDescuentoComparacion(Double descuentoPropio, Double descuentoCompetencia, Double descuentoPropio2, Double descuentoCompetencia2, String referenciaPropio, String referenciaCompetencia, int numeroPrecio) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE comparaciones SET descuento_propio = ? , "+
				"descuento_competencia = ?, descuento_propio2 = ?, descuento_competencia2 = ? WHERE referencia_propio = ? AND referencia_competencia = ?"+
				" AND numero_precio = ? AND (descuento_propio != ? OR descuento_competencia != ? OR descuento_propio2 != ? OR descuento_competencia2 != ?)");
		ps.setDouble(1, descuentoPropio);
		ps.setInt(2, descuentoCompetencia.intValue());
		ps.setDouble(3, descuentoPropio2);
		ps.setDouble(4, descuentoCompetencia2);
		ps.setString(5, referenciaPropio);
		ps.setString(6, referenciaCompetencia);
		ps.setInt(7, numeroPrecio);
		ps.setDouble(8, descuentoPropio);
		ps.setInt(9, descuentoCompetencia.intValue());
		ps.setDouble(10, descuentoPropio2);
		ps.setDouble(11, descuentoCompetencia2);
        return ps.executeUpdate();
    }

    public int updateDescuentoHistoricoComparacion(String referenciaPropio, String referenciaCompetencia, String fecha, Double precioPropio, Double descuentoPropio, Double precioCompetencia, Double descuentoCompetencia, Double descuentoPropio2, Double descuentoCompetencia2, int numeroPrecio) throws SQLException{
        PreparedStatement ps = connection.prepareStatement("INSERT INTO historico_comparaciones(referencia_propio,referencia_competencia,fecha_comparacion,precio_propio,descuento_propio,precio_competencia,descuento_competencia, descuento_propio2, descuento_competencia2, numero_precio) "+
        "VALUES(?,?,?,?,?,?,?,?,?,?) ON CONFLICT (referencia_propio, referencia_competencia, fecha_comparacion) DO UPDATE SET descuento_propio=?, descuento_competencia=?, descuento_propio2=?, descuento_competencia2=?");
        ps.setString(1, referenciaPropio);
		ps.setString(2, referenciaCompetencia);
		ps.setString(3, fecha);
		ps.setDouble(4, precioPropio);
		ps.setDouble(5, descuentoPropio);
		ps.setDouble(6, precioCompetencia);
		ps.setDouble(7, descuentoCompetencia);
		ps.setDouble(8, descuentoPropio2);
		ps.setDouble(9, descuentoCompetencia2);
		ps.setInt(10, numeroPrecio);
		ps.setDouble(11, descuentoPropio);
		ps.setDouble(12, descuentoCompetencia);
		ps.setDouble(13, descuentoPropio2);
		ps.setDouble(14, descuentoCompetencia2);
        
        return ps.executeUpdate();
    }
	public boolean deleteComparacion(String referenciaPropio, String referenciaCompetencia, int numeroPrecio)throws SQLException{
			connection.setAutoCommit(false);
			PreparedStatement ps = connection.prepareStatement("DELETE FROM comparaciones "+
					"WHERE referencia_propio = ? AND referencia_competencia = ? AND numero_precio = ?");
					
			ps.setString(1, referenciaPropio);
			ps.setString(2, referenciaCompetencia);
			ps.setInt(3, numeroPrecio);
			int response=0;
			response=ps.executeUpdate();
			if(response>0){
				connection.commit();
				connection.setAutoCommit(true);
				return true;
			}else{
				connection.rollback();
				connection.setAutoCommit(true);
				return false;
			}
	}

	public boolean deleteHistoricoComparacion(String referenciaPropio, String referenciaCompetencia, String fecha)throws SQLException{
			connection.setAutoCommit(false);
			PreparedStatement ps = connection.prepareStatement("DELETE FROM historico_comparaciones "+
			"WHERE referencia_propio = ? AND referencia_competencia = ? and fecha_comparacion=?");
					
			ps.setString(1, referenciaPropio);
			ps.setString(2, referenciaCompetencia);
			ps.setString(3, fecha);
			int response=0;
			response=ps.executeUpdate();
			if(response>0){
				connection.commit();
				connection.setAutoCommit(true);
				return true;
			}else{
				connection.rollback();
				connection.setAutoCommit(true);
				return false;
			}
	}

	public ResultSet getListaComparaciones()throws SQLException{
		PreparedStatement ps = connection.prepareStatement("SELECT pp.referencia, pc.codigo_hijo, pp.precio, cmp.descuento_propio,"+
			"pc.precio, cmp.descuento_competencia,"+
			"cmp.descuento_propio2, cmp.descuento_competencia2, pp.nombre, pc.codigo_padre, pc.nombre, cmp.numero_precio, "+
			"pp.precio2, pp.precio3, pp.precio4, pp.precio5, pc.stock_string "+
			"FROM COMPARACIONES as cmp INNER JOIN PRODUCTOS_PROPIOS as pp on cmp.referencia_propio = pp.referencia "+
			"INNER JOIN PRODUCTOS_COMPETENCIA as pc on pc.codigo_hijo = cmp.referencia_competencia");
		return ps.executeQuery();
	}


}

package comparativo.mundo.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import comparativo.mundo.model.ComparacionMarpico;
import comparativo.mundo.model.Producto;
import comparativo.mundo.response.InventarioTransitoMarpico;
import comparativo.mundo.response.MaterialesMarpico;

public class CatalogoMarpicoPersistence {

    private Connection connection;

    public CatalogoMarpicoPersistence(Connection pConnection) {
        this.connection = pConnection;
    }

    public int updateDescuentoComparacion(ComparacionMarpico comparacion, String date)throws SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE comparaciones_marpico SET descuento_propio = ? , "+
		"descuento_competencia = ?, descuento_propio2 = ?, descuento_competencia2 = ? WHERE referencia_propio = ? AND referencia_competencia = ? AND numero_precio= ? AND numero_precio_marpico = ?");
		ps.setDouble(1, comparacion.getProductoPropio().getDescuento());
		ps.setDouble(2, comparacion.getProductoCompetencia().getDescuento1());
		ps.setDouble(3, comparacion.getProductoPropio().getDescuento2());
		ps.setDouble(4, comparacion.getProductoCompetencia().getDescuento2());
		ps.setString(5, comparacion.getProductoPropio().getReferencia());
		ps.setString(6, comparacion.getProductoCompetencia().getFamilia());
		ps.setInt(7, comparacion.getNumeroPrecio());
		ps.setInt(8, comparacion.getNumeroPrecioMarpico());
		PreparedStatement ps3 = connection.prepareStatement("INSERT INTO historico_comparaciones_marpico(referencia_propio,referencia_competencia,fecha_comparacion,precio_propio,descuento_propio,precio_competencia,descuento_competencia,"+
		"descuento_propio2, descuento_competencia2, numero_precio,numero_precio_marpico) "+
		"VALUES(?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT (referencia_propio,referencia_competencia, fecha_comparacion) DO UPDATE SET descuento_propio=?,descuento_competencia=?, descuento_propio2=?, descuento_competencia2=?");
		ps3.setString(1, comparacion.getProductoPropio().getReferencia());
		ps3.setString(2, comparacion.getProductoCompetencia().getFamilia());
		ps3.setString(3, date);
		ps3.setDouble(4, comparacion.getProductoPropio().getPrecio1());
		ps3.setDouble(5, comparacion.getProductoPropio().getDescuento());
		ps3.setDouble(6, comparacion.getProductoCompetencia().getPrecio());
		ps3.setDouble(7, comparacion.getProductoCompetencia().getDescuento1());
		ps3.setDouble(8, comparacion.getProductoPropio().getDescuento2());
		ps3.setDouble(9, comparacion.getProductoCompetencia().getDescuento2());
		ps3.setInt(10, comparacion.getNumeroPrecio());
		ps3.setInt(11, comparacion.getNumeroPrecioMarpico());
		ps3.setDouble(12, comparacion.getProductoPropio().getDescuento());
		ps3.setDouble(13, comparacion.getProductoCompetencia().getDescuento1());
		ps3.setDouble(14, comparacion.getProductoPropio().getDescuento2());
		ps3.setDouble(15, comparacion.getProductoCompetencia().getDescuento2());
		ps3.executeUpdate();
        return ps.executeUpdate();

    }

    public int updateTodosDescuentosComparacion(Double descuentoPropio, Double descuentoCompetencia, Double descuentoPropio2, Double descuentoCompetencia2) throws SQLException {
    StringBuilder queryBuilder = new StringBuilder("UPDATE comparaciones_marpico SET ");
    List<Object> parameters = new ArrayList<>();

    if (descuentoPropio != -1) {
        queryBuilder.append("descuento_propio = ?, ");
        parameters.add(descuentoPropio);
    }
    if (descuentoCompetencia != -1) {
        queryBuilder.append("descuento_competencia = ?, ");
        parameters.add(descuentoCompetencia.intValue());
    }
    if (descuentoPropio2 != -1) {
        queryBuilder.append("descuento_propio2 = ?, ");
        parameters.add(descuentoPropio2);
    }
    if (descuentoCompetencia2 != -1) {
        queryBuilder.append("descuento_competencia2 = ?, ");
        parameters.add(descuentoCompetencia2.intValue());
    }

    queryBuilder.delete(queryBuilder.length() - 2, queryBuilder.length());

    queryBuilder.append(" WHERE ");
    if (descuentoPropio != -1) {
        queryBuilder.append("descuento_propio != ? OR ");
        parameters.add(descuentoPropio);
    }
    if (descuentoCompetencia != -1) {
        queryBuilder.append("descuento_competencia != ? OR ");
        parameters.add(descuentoCompetencia.intValue());
    }
    if (descuentoPropio2 != -1) {
        queryBuilder.append("descuento_propio2 != ? OR ");
        parameters.add(descuentoPropio2);
    }
    if (descuentoCompetencia2 != -1) {
        queryBuilder.append("descuento_competencia2 != ? OR ");
        parameters.add(descuentoCompetencia2.intValue());
    }

    queryBuilder.delete(queryBuilder.length() - 4, queryBuilder.length());

    PreparedStatement ps = connection.prepareStatement(queryBuilder.toString());

    for (int i = 0; i < parameters.size(); i++) {
        Object parameter = parameters.get(i);
        if (parameter instanceof Double) {
            ps.setDouble(i + 1, (Double) parameter);
        } else if (parameter instanceof Integer) {
            ps.setInt(i + 1, (Integer) parameter);
        }
    }

    System.out.println("Update:" + ps);
    return ps.executeUpdate();
}

    public int insertProductoMarpico(ProductosMarpico pProducto) throws SQLException {
        PreparedStatement ps2 = connection
                .prepareStatement("INSERT INTO productos_marpico(familia,descripcion_comercial, precio) " +
                        "VALUES(?,?,?) ON CONFLICT(familia) DO UPDATE SET precio = ?");
        ps2.setString(1, pProducto.getFamilia());
        ps2.setString(2, pProducto.getDescripcion_comercial());
        ps2.setDouble(3, pProducto.getPrecio());
        ps2.setDouble(4, pProducto.getPrecio());
        return ps2.executeUpdate();
    }

    public int insertMaterialesMarpico(MaterialesMarpico pMaterial, String pFamilia) throws SQLException {
        PreparedStatement ps3 = connection.prepareStatement(
                "INSERT INTO materiales_marpico(identificador, fk_familia, color_nombre,precio,descuento,inventario,variedad) " +
                        "VALUES(?,?,?,?,?,?,?) ON CONFLICT(identificador) DO UPDATE SET precio=? ,descuento=? ,inventario=?");

        ps3.setInt(1, pMaterial.getCodigo());
        ps3.setString(2, pFamilia);
        ps3.setString(3, pMaterial.getColor_nombre());
        ps3.setDouble(4, pMaterial.getPrecio());
        ps3.setDouble(5, pMaterial.getDescuento());
        ps3.setDouble(6, pMaterial.getInventario());
        ps3.setString(7, pMaterial.getVariedad());
        ps3.setDouble(8, pMaterial.getPrecio());
        ps3.setDouble(9, pMaterial.getDescuento());
        ps3.setDouble(10, pMaterial.getInventario());
        return ps3.executeUpdate();
    }

    public int insertInventarioTransito(InventarioTransitoMarpico pInventario, int pCodigo) throws SQLException {
        PreparedStatement ps4 = connection
                .prepareStatement("INSERT INTO inventario_transito(fk_materiales,fecha,cantidad,ultima_actualizacion)" +
                        "VALUES(?,?,?,?) ON CONFLICT(fk_materiales,fecha) DO UPDATE SET cantidad=?, ultima_actualizacion=?");
        ps4.setInt(1, pCodigo);
        ps4.setString(2, pInventario.getFecha());
        ps4.setInt(3, pInventario.getCantidad());
        ps4.setString(4, pInventario.getUltima_actualizacion());
        ps4.setInt(5, pInventario.getCantidad());
        ps4.setString(6, pInventario.getUltima_actualizacion());
        return ps4.executeUpdate();
    }

    public int insertComparacion(Producto productoPropio, ProductosMarpico productoCompetencia, int pPrecio,
            int pPrecioMarpico) throws SQLException {
        PreparedStatement ps5 = connection.prepareStatement(
                "INSERT INTO comparaciones_marpico(referencia_propio,referencia_competencia,descuento_propio,descuento_competencia,"
                        +
                        "descuento_propio2,descuento_competencia2, numero_precio, numero_precio_marpico)" +
                        "SELECT * FROM (SELECT ? AS referencia_propio, ? as referencia_competencia, ? as descuento_propio, ? as descuento_competencia, ? as descuento_propio2, ? as descuento_competencia2, ? as numero_precio, ? as numero_precio_marpico) AS temp "
                        +
                        "WHERE NOT EXISTS ( " +
                        "SELECT referencia_propio, referencia_competencia, numero_precio, numero_precio_marpico FROM comparaciones_marpico WHERE referencia_propio = ? and referencia_competencia = ? and numero_precio = ? and numero_precio_marpico = ?"
                        +
                        ") LIMIT 1");
        ps5.setString(1, productoPropio.getReferencia());
        ps5.setString(2, productoCompetencia.getFamilia());
        ps5.setDouble(3, productoPropio.getDescuento());
        ps5.setDouble(4, productoCompetencia.getDescuento1());
        ps5.setDouble(5, productoPropio.getDescuento2());
        ps5.setDouble(6, productoCompetencia.getDescuento2());
        ps5.setInt(7, pPrecio);
        ps5.setInt(8, pPrecioMarpico);
        ps5.setString(9, productoPropio.getReferencia());
        ps5.setString(10, productoCompetencia.getFamilia());
        ps5.setInt(11, pPrecio);
        ps5.setInt(12, pPrecioMarpico);
        return ps5.executeUpdate();
    }

    public int insertHistoricoComparacion(Producto productoPropio, ProductosMarpico productoCompetencia, int pPrecio,
    int pPrecioMarpico,double precioPropio) throws SQLException{

        PreparedStatement ps6 = connection.prepareStatement(
				"INSERT INTO historico_comparaciones_marpico(referencia_propio,referencia_competencia,fecha_comparacion,precio_propio,descuento_propio,precio_competencia,descuento_competencia, descuento_propio2, descuento_competencia2, numero_precio, numero_precio_marpico) "
						+
						"VALUES(?,?,?,?,?,?,?,?,?,?,?) ON CONFLICT (referencia_propio, referencia_competencia, fecha_comparacion) DO UPDATE SET descuento_propio=?, descuento_competencia=?, descuento_propio2=?, descuento_competencia2=?");
		ps6.setString(1, productoPropio.getReferencia());
		ps6.setString(2, productoCompetencia.getFamilia());
		ps6.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		ps6.setDouble(4, precioPropio);
		ps6.setDouble(5, productoPropio.getDescuento());
		ps6.setDouble(6, productoCompetencia.getPrecio());
		ps6.setDouble(7, productoCompetencia.getDescuento1());
		ps6.setDouble(8, productoPropio.getDescuento2());
		ps6.setDouble(9, productoCompetencia.getDescuento2());
		ps6.setInt(10, pPrecio);
		ps6.setInt(11, pPrecioMarpico);
		ps6.setDouble(12, productoPropio.getDescuento());
		ps6.setDouble(13, productoCompetencia.getDescuento1());
		ps6.setDouble(14, productoPropio.getDescuento2());
		ps6.setDouble(15, productoCompetencia.getDescuento2());
		return ps6.executeUpdate();
        
    }

    public int deleteComparacion(ComparacionMarpico comparacion, boolean esHistorico) throws SQLException{
        connection.setAutoCommit(false);
		int response=0;
		PreparedStatement ps = connection.prepareStatement("DELETE FROM comparaciones_marpico "+
		"WHERE referencia_propio = ? AND referencia_competencia = ? AND numero_precio= ? AND numero_precio_marpico = ?");
		ps.setString(1, comparacion.getProductoPropio().getReferencia());
		ps.setString(2, comparacion.getProductoCompetencia().getFamilia());
		ps.setInt(3, comparacion.getNumeroPrecio());
		ps.setInt(4, comparacion.getNumeroPrecioMarpico());
		if(esHistorico){
			ps = connection.prepareStatement("DELETE FROM historico_comparaciones_marpico "+
			"WHERE referencia_propio = ? AND referencia_competencia = ? and	fecha_comparacion=?");
			ps.setString(1,comparacion.getProductoPropio().getReferencia());
			ps.setString(2, comparacion.getProductoCompetencia().getFamilia());
			ps.setString(3, comparacion.getFechaComparacion());
			response = ps.executeUpdate();
		}else{
			response = ps.executeUpdate();
		}
        connection.commit();
		connection.setAutoCommit(true);
        return response;
    }

    public ResultSet getComparaciones()throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT pp.referencia,pc.familia, pp.precio, cmp.descuento_propio,"+
		"pc.precio, cmp.descuento_competencia, cmp.descuento_propio2,cmp.descuento_competencia2, pp.nombre, pc.descripcion_comercial,cmp.numero_precio,"+
		"pp.precio2,pp.precio3, pp.precio4, pp.precio5, cmp.numero_precio_marpico FROM comparaciones_marpico AS cmp INNER JOIN productos_propios "+
		"AS pp ON cmp.referencia_propio = pp.referencia INNER JOIN productos_marpico AS pc ON pc.familia = cmp.referencia_competencia");
		return ps.executeQuery();
    }

    public ResultSet getMateriales(String familia)throws SQLException{
        PreparedStatement ps=connection.prepareStatement("SELECT mt.identificador, mt.color_nombre,mt.precio, mt.descuento, mt.inventario, mt.variedad "+
			"FROM materiales_marpico as mt WHERE mt.fk_familia = ?");
		ps.setString(1, familia);
        return ps.executeQuery();
    }

    public ResultSet getInventarios(int materiales)throws SQLException{
        PreparedStatement ps=connection.prepareStatement("SELECT inv.fecha, inv.cantidad,	inv.ultima_actualizacion "+
				"FROM inventario_transito as inv WHERE inv.fk_materiales = ?");
		ps.setInt(1, materiales);
        return ps.executeQuery();
    }

    public ResultSet getHistoricoComparaciones()throws SQLException{
        PreparedStatement ps = connection.prepareStatement("SELECT pp.referencia,pc.familia, cmp.precio_propio, cmp.descuento_propio,"+
		"cmp.precio_competencia, cmp.descuento_competencia, cmp.descuento_propio2,cmp.descuento_competencia2, pp.nombre, pc.descripcion_comercial,cmp.numero_precio,"+
		"pp.precio2,pp.precio3, pp.precio4, pp.precio5, cmp.numero_precio_marpico,cmp.fecha_comparacion FROM historico_comparaciones_marpico AS cmp INNER JOIN productos_propios "+
		"AS pp ON cmp.referencia_propio = pp.referencia INNER JOIN productos_marpico AS pc ON pc.familia = cmp.referencia_competencia");
        return ps.executeQuery();
    }

    public int updateProducto(String familia, double precio)throws SQLException{
        PreparedStatement ps2 = connection.prepareStatement("UPDATE	productos_marpico SET precio = ? "+
		" WHERE familia = ? AND precio != ? ");
        ps2.setDouble(1,precio);
		ps2.setString(2, familia);
		ps2.setDouble(3, precio);
        return ps2.executeUpdate();
    }


}

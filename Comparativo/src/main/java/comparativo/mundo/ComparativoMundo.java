package comparativo.mundo;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import comparativo.mundo.model.Catalogo;
import comparativo.mundo.model.Categoria;
import comparativo.mundo.model.Comparacion;
import comparativo.mundo.model.ListaComparaciones;
import comparativo.mundo.model.Producto;
import comparativo.mundo.model.ProductoCompetencia;
import comparativo.mundo.response.CategoriaResponse;
import comparativo.mundo.response.ProductosResponse;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.google.gson.Gson;



public class ComparativoMundo {
	
	private Catalogo catalogoPropio;
	private ArrayList<ProductoCompetencia> catalogoCompetencia;
	private ArrayList<String> productosCambiaron;
	private ArrayList<String> productosCompetenciaCambiaron;
	private ListaComparaciones listaComparaciones;
	private ListaComparaciones historicoComparaciones;
	private ResteasyClient client;
	private Connection connection;
	private Categoria categoriaPorReferencia;

	enum Campos {
		CODIGOHIJO,
		CODIGOPADRE,
		NOMBRE,
		PRECIOBASE,
	  }

	
	public ComparativoMundo(String user, String password, String uri) throws SQLException, UnknownHostException {
		super();
		try{
		this.catalogoPropio=obtenerInformacionApiCategorias();
		}catch(Exception e){
			this.catalogoPropio=null;
		}
		this.catalogoCompetencia=null;
		this.listaComparaciones = new ListaComparaciones();
		this.historicoComparaciones=new ListaComparaciones();
		this.connection = conexionDb(user, password, uri);
		productosCompetenciaCambiaron = new ArrayList<>();
		productosCambiaron = new ArrayList<>();
		this.categoriaPorReferencia=null;
	}

	public ComparativoMundo() throws UnknownHostException{
		super();
		this.catalogoPropio=obtenerInformacionApiCategorias();
		this.catalogoCompetencia=null;
		this.listaComparaciones = new ListaComparaciones();
		this.historicoComparaciones=new ListaComparaciones();
		this.connection = null;
		this.categoriaPorReferencia=null;

	}




	public Connection darConexion(){
		return connection;
	}

	public ArrayList<String> getProductosActualizados(){
		return productosCambiaron;
	}
	
	public ArrayList<String> getProductosCompetenciaActualizaron(){
		return productosCompetenciaCambiaron;
	}
	
	public Comparacion obtenerComparacionPorReferencias(String referencia, String codigoHijo,String fecha ,boolean esHistorico){
		Comparacion rta = null;
		boolean termino=false;
		Comparacion actual;
		System.out.println(esHistorico);
		if(!esHistorico){
			for(int i = 0;i<listaComparaciones.getListaComparaciones().size() && !termino;i++){
				actual = listaComparaciones.getListaComparaciones().get(i);
				if((referencia.equalsIgnoreCase(actual.getProductoPropio().getReferencia())
				&& codigoHijo.equalsIgnoreCase(actual.getProductoCompetencia().getCodigoHijo()) || (referencia.equals(actual.getProductoPropio().getReferencia()) && codigoHijo.equals(actual.getProductoCompetencia().getCodigoHijo())) 
				|| (referencia.contains(actual.getProductoPropio().getReferencia()) && codigoHijo.contains(actual.getProductoCompetencia().getCodigoHijo()))
				)
				){
					rta=actual;
					termino=true;
				}
			}
		}else{
			for(int i = 0;i<historicoComparaciones.getListaComparaciones().size() && !termino;i++){
				actual = historicoComparaciones.getListaComparaciones().get(i);
				if((referencia.equalsIgnoreCase(actual.getProductoPropio().getReferencia())
				&& codigoHijo.equalsIgnoreCase(actual.getProductoCompetencia().getCodigoHijo()) && fecha.equalsIgnoreCase(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(actual.getFechaComparacion()))) || (referencia.equals(actual.getProductoPropio().getReferencia()) && fecha.equals(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(actual.getFechaComparacion())) &&  codigoHijo.equals(actual.getProductoCompetencia().getCodigoHijo())) 
				|| (referencia.contains(actual.getProductoPropio().getReferencia()) && fecha.contains(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(actual.getFechaComparacion())) && codigoHijo.contains(actual.getProductoCompetencia().getCodigoHijo()))
				){
					rta=actual;
					termino=true;
				}
			}
		}
		return rta;
	}

	public void exportarCsv(String path, boolean esHistorico) throws IOException{
		ArrayList<Comparacion> lista = null;
		if(esHistorico){
			lista = historicoComparaciones.getListaComparaciones();
		}else{
			lista=listaComparaciones.getListaComparaciones();
		}
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("listaComparaciones");
		sheet.setDefaultColumnWidth(25);
		int rowNum = 0;
		Row row = sheet.createRow(rowNum);
		row.createCell(0).setCellValue("Nombre");
		row.createCell(1).setCellValue("Codigo Promos");
		row.createCell(2).setCellValue("Descuento Promos");
		row.createCell(3).setCellValue("Descuento Promos 2");
		row.createCell(4).setCellValue("Codigo Competencia");
		row.createCell(5).setCellValue("Descuento Competencia");
		row.createCell(6).setCellValue("Descuento Competencia 2");
		row.createCell(7).setCellValue("Precio promos");
		row.createCell(8).setCellValue("Precio competencia");
		row.createCell(9).setCellValue("Precio promos descuento");
		row.createCell(10).setCellValue("Precio competencia descuento");
		row.createCell(11).setCellValue("Precio promos descuento 2");
		row.createCell(12).setCellValue("Precio competencia descuento 2");
		row.createCell(13).setCellValue("Fecha");
		CellStyle green = workbook.createCellStyle();
		green.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		green.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		CellStyle red = workbook.createCellStyle();
		red.setFillForegroundColor(IndexedColors.RED.getIndex());
		red.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		rowNum++;

		for(int i = 0 ; i<lista.size(); i++){
			Comparacion comparacion = lista.get(i);
			row=sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(comparacion.getProductoPropio().getNombre());
			row.createCell(1).setCellValue(comparacion.getProductoPropio().getReferencia());
			row.createCell(2).setCellValue(comparacion.getProductoPropio().getDescuento());
			row.createCell(3).setCellValue(comparacion.getProductoPropio().getDescuento2());
			row.createCell(4).setCellValue(comparacion.getProductoCompetencia().getCodigoHijo());
			row.createCell(5).setCellValue(comparacion.getProductoCompetencia().getDescuento());
			row.createCell(6).setCellValue(comparacion.getProductoCompetencia().getDescuento2());
			row.createCell(13).setCellValue(comparacion.getFechaComparacion()!=null ? new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(comparacion.getFechaComparacion()): new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			if(comparacion.getProductoPropio().getPrecio1()>comparacion.getProductoCompetencia().getPrecioBase()){
				Cell precioPropio=row.createCell(7);
				precioPropio.setCellValue(comparacion.getProductoPropio().getPrecio1());
				precioPropio.setCellStyle(red);
				Cell precioCompetencia=row.createCell(8);
				precioCompetencia.setCellValue(comparacion.getProductoCompetencia().getPrecioBase());
				precioCompetencia.setCellStyle(green);
			}else{
				Cell precioPropio=row.createCell(7);
				precioPropio.setCellValue(comparacion.getProductoPropio().getPrecio1());
				precioPropio.setCellStyle(green);
				Cell precioCompetencia=row.createCell(8);
				precioCompetencia.setCellValue(comparacion.getProductoCompetencia().getPrecioBase());
				precioCompetencia.setCellStyle(red);
			}
			if(comparacion.getProductoPropio().getPrecioDescuento()>comparacion.getProductoCompetencia().getPrecioDescuento()){
				Cell precioPropio=row.createCell(9);
				precioPropio.setCellValue(comparacion.getProductoPropio().getPrecioDescuento());
				precioPropio.setCellStyle(red);
				Cell precioCompetencia=row.createCell(10);
				precioCompetencia.setCellValue(comparacion.getProductoCompetencia().getPrecioDescuento());
				precioCompetencia.setCellStyle(green);
			}else{
				Cell precioPropio=row.createCell(9);
				precioPropio.setCellValue(comparacion.getProductoPropio().getPrecioDescuento());
				precioPropio.setCellStyle(green);
				Cell precioCompetencia=row.createCell(10);
				precioCompetencia.setCellValue(comparacion.getProductoCompetencia().getPrecioDescuento());
				precioCompetencia.setCellStyle(red);
			}
			if(comparacion.getProductoPropio().getPrecioDescuento2()>comparacion.getProductoCompetencia().getPrecioDescuento2()){
				Cell precioPropio=row.createCell(11);
				precioPropio.setCellValue(comparacion.getProductoPropio().getPrecioDescuento2());
				precioPropio.setCellStyle(red);
				Cell precioCompetencia=row.createCell(12);
				precioCompetencia.setCellValue(comparacion.getProductoCompetencia().getPrecioDescuento2());
				precioCompetencia.setCellStyle(green);
			}else{
				Cell precioPropio=row.createCell(11);
				precioPropio.setCellValue(comparacion.getProductoPropio().getPrecioDescuento2());
				precioPropio.setCellStyle(green);
				Cell precioCompetencia=row.createCell(12);
				precioCompetencia.setCellValue(comparacion.getProductoCompetencia().getPrecioDescuento2());
				precioCompetencia.setCellStyle(red);
			}
		}
		FileOutputStream out = new FileOutputStream(new File(path+".xlsx"));
		workbook.write(out);
		workbook.close();
		out.close();
	}

	public Connection conexionDb(String user, String password, String uri) throws SQLException{
		String url = "jdbc:postgresql://"+uri+"/comparativo";
		Properties props = new Properties();
		props.setProperty("user",user);
		props.setProperty("password",password);
		return DriverManager.getConnection(url,props);

	}

	public ListaComparaciones obtenerHistoricoComparaciones(boolean estaConectado) throws SQLException, ParseException{
		if(estaConectado){	
			historicoComparaciones.getListaComparaciones().clear();
			PreparedStatement ps = connection.prepareStatement("SELECT pp.referencia, pc.codigo_hijo,"+
			 "cmp.fecha_comparacion, cmp.precio_propio, cmp.descuento_propio, cmp.precio_competencia, cmp.descuento_competencia,"+
			 "cmp.descuento_propio2, cmp.descuento_competencia2, pp.nombre, pc.codigo_padre, pc.nombre FROM historico_comparaciones as cmp INNER JOIN PRODUCTOS_PROPIOS as pp on cmp.referencia_propio = pp.referencia "+
			"INNER JOIN PRODUCTOS_COMPETENCIA as pc on pc.codigo_hijo = cmp.referencia_competencia");
			ResultSet rs = ps.executeQuery();
			String referenciaPropio;
			String referenciaCompetencia;
			String fechaComparacion;
			double precioPropio;
			double descuentoPropio;
			double precioCompetencia;
			double descuentoCompetencia;
			double descuentoPropio2;
			String nombrePropio;
			String codigoPadreCompetencia;
			String nombreCompetencia;
			double descuentoCompetencia2;
			
			while(rs.next()){
					referenciaPropio = (String) rs.getObject(1);
					referenciaCompetencia = (String) rs.getObject(2);
					fechaComparacion = rs.getObject(3).toString();
					precioPropio = ((BigDecimal) rs.getObject(4))==null ? 0 : ((BigDecimal) rs.getObject(4)).doubleValue();
					descuentoPropio = ((BigDecimal) rs.getObject(5))== null ? 0 :((BigDecimal) rs.getObject(5)).doubleValue();
					precioCompetencia = ((BigDecimal) rs.getObject(6)) == null ? 0 :((BigDecimal) rs.getObject(6)).doubleValue();
					descuentoCompetencia = ((BigDecimal) rs.getObject(7)) == null ? 0 : ((BigDecimal) rs.getObject(7)).doubleValue();
					descuentoPropio2 = ((BigDecimal) rs.getObject(8)) == null ? 0 : ((BigDecimal) rs.getObject(8)).doubleValue();
					descuentoCompetencia2 = ((BigDecimal) rs.getObject(9))== null ? 0 : ((BigDecimal) rs.getObject(9)).doubleValue();
					nombrePropio = (String) rs.getObject(10);
					codigoPadreCompetencia = (String) rs.getObject(11);
					nombreCompetencia = (String) rs.getObject(12);
					Producto propio = new Producto(nombrePropio, referenciaPropio, precioPropio, descuentoPropio,descuentoPropio2);
					propio.setDescuento(descuentoPropio);
					historicoComparaciones.agregarComparacion(propio, new ProductoCompetencia(referenciaCompetencia, codigoPadreCompetencia, nombreCompetencia, precioCompetencia, descuentoCompetencia, descuentoCompetencia2), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fechaComparacion));
				
			}
			return historicoComparaciones;
		}else{
			return historicoComparaciones;
		}
		
	}

	public Comparacion crearComparacion(Producto productoPropio, ProductoCompetencia productoCompetencia, String pFecha, boolean persistir) throws SQLException, ParseException{
		if(productoPropio==null || productoCompetencia == null){
			return null;
		}else{
			if(persistir){
				PreparedStatement ps = connection.prepareStatement("INSERT INTO productos_propios(referencia,nombre,precio) "+
				"VALUES(?,?,?) ON CONFLICT(referencia) DO UPDATE SET precio = ? "
				);
				ps.setString(1, productoPropio.getReferencia());
				ps.setString(2, productoPropio.getNombre());
				ps.setDouble(3, productoPropio.getPrecio1());
				ps.setDouble(4, productoPropio.getPrecio1());
				PreparedStatement ps2 = connection.prepareStatement("INSERT INTO productos_competencia(codigo_hijo,codigo_padre,nombre,precio) "+
				"VALUES(?,?,?,?) ON CONFLICT(codigo_hijo) DO UPDATE SET precio = ?");
				ps2.setString(1, productoCompetencia.getCodigoHijo());
				ps2.setString(2, productoCompetencia.getCodigoPadre());
				ps2.setString(3, productoCompetencia.getNombre());
				ps2.setDouble(4, productoCompetencia.getPrecioBase());
				ps2.setDouble(5, productoCompetencia.getPrecioBase());
				PreparedStatement ps3 = connection.prepareStatement("INSERT INTO historico_comparaciones(referencia_propio,referencia_competencia,fecha_comparacion,precio_propio,descuento_propio,precio_competencia,descuento_competencia, descuento_propio2, descuento_competencia2) "+
				"VALUES(?,?,?,?,?,?,?,?,?) ON CONFLICT (referencia_propio, referencia_competencia, fecha_comparacion) DO UPDATE SET descuento_propio=?, descuento_competencia=?, descuento_propio2=?, descuento_competencia2=?");
				ps3.setString(1, productoPropio.getReferencia());
				ps3.setString(2, productoCompetencia.getCodigoHijo());
				ps3.setString(3, pFecha.toString());
				ps3.setDouble(4, productoPropio.getPrecio1());
				ps3.setDouble(5, productoPropio.getDescuento());
				ps3.setDouble(6, productoCompetencia.getPrecioBase());
				ps3.setDouble(7, productoCompetencia.getDescuento());
				ps3.setDouble(8, productoPropio.getDescuento2());
				ps3.setDouble(9, productoCompetencia.getDescuento2());
				ps3.setDouble(10, productoPropio.getDescuento());
				ps3.setDouble(11, productoCompetencia.getDescuento());
				ps3.setDouble(12, productoPropio.getDescuento2());
				ps3.setDouble(13, productoCompetencia.getDescuento2());

				PreparedStatement ps4 = connection.prepareStatement("INSERT INTO comparaciones(referencia_propio,referencia_competencia,descuento_propio,descuento_competencia," +
				"descuento_propio2,descuento_competencia2)" +
				"SELECT * FROM (SELECT ? AS referencia_propio, ? as referencia_competencia, ? as descuento_propio, ? as descuento_competencia, ? as descuento_propio2, ? as descuento_competencia2) AS temp "+
				"WHERE NOT EXISTS ( "+
				"SELECT referencia_propio, referencia_competencia FROM comparaciones WHERE referencia_propio = ? and referencia_competencia = ?"+
				") LIMIT 1");
				ps4.setString(1, productoPropio.getReferencia());
				ps4.setString(2, productoCompetencia.getCodigoHijo());
				ps4.setDouble(3, productoPropio.getDescuento());
				ps4.setDouble(4, productoCompetencia.getDescuento());
				ps4.setDouble(5, productoPropio.getDescuento2());
				ps4.setDouble(6, productoCompetencia.getDescuento2());
				ps4.setString(7, productoPropio.getReferencia());
				ps4.setString(8, productoCompetencia.getCodigoHijo());

				ps.executeUpdate();
				ps2.executeUpdate();
				int rs4 = ps3.executeUpdate();
				int rs5 = ps4.executeUpdate();
				if(rs4>0 || rs5>0){
					ps3.executeUpdate();
					Comparacion rta = listaComparaciones.agregarComparacion(productoPropio, productoCompetencia, null);
					return rta;
				}
				else{
					return null;
				}
			}else{
				Comparacion rta = listaComparaciones.agregarComparacion(productoPropio, productoCompetencia, null);
				return rta;
			}
		}
	}

	public boolean actualizarDescuentoComparacion(Comparacion comparacion, double descuentoPropio, double descuentoCompetencia, double descuentoPropio2, double descuentoCompetencia2, boolean estaConectado) throws SQLException{
		if(estaConectado){
			SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			if(descuentoPropio>0) {
				comparacion.getProductoPropio().setDescuento(descuentoPropio);
			}if(descuentoCompetencia>0) {
				comparacion.getProductoCompetencia().setDescuento(descuentoCompetencia);
			}if(descuentoPropio2>0) {
				comparacion.getProductoPropio().setDescuento2(descuentoPropio2);
			}if(descuentoCompetencia2>0) {
				comparacion.getProductoCompetencia().setDescuento2(descuentoCompetencia2);
			}
        	java.util.Date date = new java.util.Date();
				PreparedStatement ps = connection.prepareStatement("UPDATE comparaciones SET descuento_propio = ? , "+
				"descuento_competencia = ?, descuento_propio2 = ?, descuento_competencia2 = ? WHERE referencia_propio = ? AND referencia_competencia = ?");
				ps.setDouble(1, comparacion.getProductoPropio().getDescuento());
				ps.setDouble(2, comparacion.getProductoCompetencia().getDescuento());
				ps.setDouble(3, comparacion.getProductoPropio().getDescuento2());
				ps.setDouble(4, comparacion.getProductoCompetencia().getDescuento2());
				ps.setString(5, comparacion.getProductoPropio().getReferencia());
				ps.setString(6, comparacion.getProductoCompetencia().getCodigoHijo());
				PreparedStatement ps3 = connection.prepareStatement("INSERT INTO historico_comparaciones(referencia_propio,referencia_competencia,fecha_comparacion,precio_propio,descuento_propio,precio_competencia,descuento_competencia, descuento_propio2, descuento_competencia2) "+
				"VALUES(?,?,?,?,?,?,?,?,?) ON CONFLICT (referencia_propio, referencia_competencia, fecha_comparacion) DO UPDATE SET descuento_propio=?, descuento_competencia=?, descuento_propio2=?, descuento_competencia2=?");
				ps3.setString(1, comparacion.getProductoPropio().getReferencia());
				ps3.setString(2, comparacion.getProductoCompetencia().getCodigoHijo());
				ps3.setString(3, formatter.format(date));
				ps3.setDouble(4, comparacion.getProductoPropio().getPrecio1());
				ps3.setDouble(5, comparacion.getProductoPropio().getDescuento());
				ps3.setDouble(6, comparacion.getProductoCompetencia().getPrecioBase());
				ps3.setDouble(7, comparacion.getProductoCompetencia().getDescuento());
				ps3.setDouble(8, comparacion.getProductoPropio().getDescuento2());
				ps3.setDouble(9, comparacion.getProductoCompetencia().getDescuento2());
				ps3.setDouble(10, comparacion.getProductoPropio().getDescuento());
				ps3.setDouble(11, comparacion.getProductoCompetencia().getDescuento());
				ps3.setDouble(12, comparacion.getProductoPropio().getDescuento2());
				ps3.setDouble(13, comparacion.getProductoCompetencia().getDescuento2());
				ps3.executeUpdate();
				int response = ps.executeUpdate();
				if(response>0){
					return true;
				}else{
					return false;
				}
		}
		else{
			return false;
		}
	}


	public boolean eliminarComparacion(Comparacion comparacion, boolean estaConectado, boolean esHistorico) throws SQLException{
		if(estaConectado){

			connection.setAutoCommit(false);
			PreparedStatement ps = connection.prepareStatement("DELETE FROM comparaciones "+
				"WHERE referencia_propio = ? AND referencia_competencia = ?");
				
				ps.setString(1, comparacion.getProductoPropio().getReferencia());
				ps.setString(2, comparacion.getProductoCompetencia().getCodigoHijo());
				int response=0;

				if(esHistorico){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					PreparedStatement ps2 = connection.prepareStatement("DELETE FROM historico_comparaciones "+
					"WHERE referencia_propio = ? AND referencia_competencia = ? and fecha_comparacion=?");
					
					ps2.setString(1, comparacion.getProductoPropio().getReferencia());
					ps2.setString(2, comparacion.getProductoCompetencia().getCodigoHijo());
					ps2.setString(3, formatter.format(comparacion.getFechaComparacion()));
					response = ps2.executeUpdate();
				}else{
					 response = ps.executeUpdate();

				}
				if(response>0){
					boolean eliminacion= false;
					if(esHistorico){
						eliminacion = historicoComparaciones.eliminarComparacion(comparacion);
					}else{
						eliminacion=listaComparaciones.eliminarComparacion(comparacion);
					}
					if(eliminacion){
						connection.commit();
						connection.setAutoCommit(true);
						return true;
					}else{
						connection.rollback();
						return false;
					}
				}
				else{
					connection.setAutoCommit(true);
					return false;
				}
		}else{
			return listaComparaciones.eliminarComparacion(comparacion) || historicoComparaciones.eliminarComparacion(comparacion);
		}
	}

	public ListaComparaciones obtenerListaComparaciones(){
		return listaComparaciones;
	}
	public void obtenerListaComparacionesBaseDeDatos() throws SQLException{
			listaComparaciones.getListaComparaciones().clear();
		
			PreparedStatement ps = connection.prepareStatement("SELECT pp.referencia, pc.codigo_hijo, pp.precio, cmp.descuento_propio,"+
			"pc.precio, cmp.descuento_competencia,"+
			"cmp.descuento_propio2, cmp.descuento_competencia2, pp.nombre, pc.codigo_padre, pc.nombre FROM COMPARACIONES as cmp INNER JOIN PRODUCTOS_PROPIOS as pp on cmp.referencia_propio = pp.referencia "+
			"INNER JOIN PRODUCTOS_COMPETENCIA as pc on pc.codigo_hijo = cmp.referencia_competencia");
			ResultSet rs = ps.executeQuery();
			String referenciaPropio;
			String referenciaCompetencia;
			double precioPropio;
			double descuentoPropio;
			double precioCompetencia;
			double descuentoCompetencia;
			double descuentoPropio2;
			String nombrePropio;
			String codigoPadreCompetencia;
			String nombreCompetencia;
			double descuentoCompetencia2;
			
			while(rs.next()){
					referenciaPropio = (String) rs.getObject(1);
					referenciaCompetencia = (String) rs.getObject(2);
					precioPropio = ((BigDecimal) rs.getObject(3))==null ? 0 : ((BigDecimal) rs.getObject(3)).doubleValue();
					descuentoPropio = ((BigDecimal) rs.getObject(4))== null ? 0 :((BigDecimal) rs.getObject(4)).doubleValue();
					precioCompetencia = ((BigDecimal) rs.getObject(5)) == null ? 0 :((BigDecimal) rs.getObject(5)).doubleValue();
					descuentoCompetencia = ((BigDecimal) rs.getObject(6)) == null ? 0 : ((BigDecimal) rs.getObject(6)).doubleValue();
					descuentoPropio2 = ((BigDecimal) rs.getObject(7)) == null ? 0 : ((BigDecimal) rs.getObject(7)).doubleValue();
					descuentoCompetencia2 = ((BigDecimal) rs.getObject(8))== null ? 0 : ((BigDecimal) rs.getObject(8)).doubleValue();
					nombrePropio = (String) rs.getObject(9);
					codigoPadreCompetencia = (String) rs.getObject(10);
					nombreCompetencia = (String) rs.getObject(11);
					Producto propio = new Producto(nombrePropio, referenciaPropio, precioPropio, descuentoPropio,descuentoPropio2);
					propio.setDescuento(descuentoPropio);
					listaComparaciones.agregarComparacion(propio, new ProductoCompetencia(referenciaCompetencia, codigoPadreCompetencia, nombreCompetencia, precioCompetencia, descuentoCompetencia, descuentoCompetencia2), null);
			}

	}

	
	public ArrayList<ProductoCompetencia> obtenerInformacionExcelCompetencia(String path) throws Exception{

		ArrayList<ProductoCompetencia> listaCompetencia = new ArrayList<>();
		File excelFile = new File(path);
		FileInputStream inputStream = new FileInputStream(excelFile);
		Workbook workbook = new XSSFWorkbook(excelFile);
		Sheet sheet = workbook.getSheetAt(0);
		int firstRow = sheet.getFirstRowNum();
		int lastRow = sheet.getLastRowNum();
		int noColumaCodigoHijo=-1;
		int noColumnaCodigoPadre=-1;
		int noColumnaNombre=-1;
		int noColumnaPrecio=-1;
		int noFilaHeaders=-1;
		for(int i = firstRow; i < lastRow; i++){
			Row row = sheet.getRow(i);
			for(int j = row.getFirstCellNum(); j< row.getLastCellNum(); j++){
				Cell cell = row.getCell(j, MissingCellPolicy.CREATE_NULL_AS_BLANK);
				if (cell.getCellType() == CellType.STRING) {
					String text = StringUtils.stripAccents(cell.getStringCellValue()).toUpperCase().trim().replaceAll("\\s+","");
					if (Campos.CODIGOHIJO.toString().equals(text)) {
						noColumaCodigoHijo=j;
						noFilaHeaders=i;
					}
					if (Campos.CODIGOPADRE.toString().equals(text)) {
						noColumnaCodigoPadre=j;
					 }
					 if (Campos.NOMBRE.toString().equals(text)) {
						noColumnaNombre=j;
					 }
					 if (Campos.PRECIOBASE.toString().equals(text)) {
						noColumnaPrecio=j;
					 }
				 }	
			}
			if(noColumaCodigoHijo!=-1 && noColumnaCodigoPadre!=-1 && noColumnaNombre!=-1 && noColumnaPrecio!=-1 &&noFilaHeaders!=-1){
				break;
			}
		}
		firstRow=noFilaHeaders;

		for(int i = firstRow+1; i <= lastRow; i++){
			Cell cellCodigoHijo = sheet.getRow(i).getCell(noColumaCodigoHijo, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			Cell cellCodigoPadre = sheet.getRow(i).getCell(noColumnaCodigoPadre, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			Cell cellNombre = sheet.getRow(i).getCell(noColumnaNombre, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			Cell cellPrecio = sheet.getRow(i).getCell(noColumnaPrecio, MissingCellPolicy.CREATE_NULL_AS_BLANK);
			String codigoHijo;
			String codigoPadre;
			String nombre;
			double precio;
			if (cellCodigoHijo.getCellType() == CellType.NUMERIC){
				codigoHijo = (cellCodigoHijo.getNumericCellValue()+"").trim().replaceAll("\\s+","");
			}else{
				codigoHijo = (cellCodigoHijo.getStringCellValue()).trim().replaceAll("\\s+","");
			}
			if (cellCodigoPadre.getCellType() == CellType.NUMERIC){
				codigoPadre = cellCodigoPadre.getNumericCellValue()+"";
			}else{
				codigoPadre = cellCodigoPadre.getStringCellValue();
			}
			if (cellNombre.getCellType() == CellType.NUMERIC){
				nombre = cellNombre.getNumericCellValue()+"";
			}else{
				nombre = cellNombre.getStringCellValue();
			}
			if(cellPrecio.getCellType() == CellType.STRING){
				try{
					precio = Integer.parseInt(cellPrecio.getStringCellValue());
				} catch (NumberFormatException e){
					precio = 0;
				}
			}else{
				precio=cellPrecio.getNumericCellValue();
			}
			if(codigoHijo != null && codigoPadre != null && nombre != null && !codigoHijo.isEmpty()
			&& !codigoPadre.isEmpty() && !nombre.isEmpty()){
				listaCompetencia.add(new ProductoCompetencia(codigoHijo, 
					codigoPadre, nombre, precio));
			}
			
		}
		workbook.close();
		inputStream.close();
		catalogoCompetencia=listaCompetencia;
		return listaCompetencia;
	}

	public ArrayList<ProductoCompetencia> obtenerCatalogoCompetencia(){
		return catalogoCompetencia;
	}

	public ListaComparaciones obtenerHistoricoComparaciones(){
		return historicoComparaciones;
	}

	public Catalogo obtenerInformacionApiCategorias() throws ForbiddenException, ProcessingException, UnknownHostException{
		client=(ResteasyClient) ClientBuilder.newClient();
		ResteasyWebTarget target = this.client.target("https://api.cataprom.com/rest/categorias");
		String response = target.request(MediaType.APPLICATION_JSON).get(String.class);
		CategoriaResponse categorias = new Gson().fromJson(response, CategoriaResponse.class);
	
		return new Catalogo(categorias.getResultado());
	}

	public Producto obtenerProductoPorReferencia(String pReferencia) throws UnknownHostException{
		Producto rta = null;
		ArrayList<Producto> productos = new ArrayList<>();
		boolean termino=false;
		for(int i = 0; i < catalogoPropio.getCatalogo().size() && !termino; i++){
			ResteasyWebTarget target = client.target("https://api.cataprom.com/rest/categorias/"+catalogoPropio.getCatalogo().get(i).getId()+"/productos");
			String response = target.request(MediaType.APPLICATION_JSON).get(String.class);
			productos = new Gson().fromJson(response, ProductosResponse.class).getResultado();
			for(int j = 0; j<productos.size() && !termino; j++){
				if(productos.get(j).getReferencia().equalsIgnoreCase(pReferencia)){
					rta=productos.get(j);
					categoriaPorReferencia=catalogoPropio.getCatalogo().get(i);
					termino=true;
				}
			}
		}
		return rta;
	}
	
	public Categoria getCategoriaPorReferencia() {
		return categoriaPorReferencia;
	}
	
	public void obtenerProductosCategoria(Categoria pCategoria){
		if(pCategoria.getProductos()==null){
			ResteasyWebTarget target = client.target("https://api.cataprom.com/rest/categorias/"+pCategoria.getId()+"/productos");
			String response = target.request(MediaType.APPLICATION_JSON).get(String.class);
			ProductosResponse productos = new Gson().fromJson(response, ProductosResponse.class);
			pCategoria.setProductos(productos.getResultado());
			for(int i = 0; i<pCategoria.getProductos().size();i++){
				pCategoria.getProductos().get(i).setDescuento(35);
			}
		}
	}
	
	public Catalogo obtenerCatalogoPropio() throws ForbiddenException, ProcessingException, UnknownHostException{
		if(catalogoPropio==null){
			catalogoPropio=obtenerInformacionApiCategorias();
		}
		return catalogoPropio;
	}

	public Catalogo obtenerCatalogoPropioSinRefrescar(){
		return catalogoPropio;
	}

	public ProductoCompetencia obtenerProductoCompetencia(String pCodigoHijo){
		ProductoCompetencia rta = null;
		boolean termino = false;
		for(int i =0;i<catalogoCompetencia.size() && !termino;i++){
			if(catalogoCompetencia.get(i).getCodigoHijo().equalsIgnoreCase(pCodigoHijo)){
				rta=catalogoCompetencia.get(i);
				termino=true;
			}
		}
		return rta;

	}

	public void actualizarComparaciones() throws SQLException, UnknownHostException{
		PreparedStatement ps = connection.prepareStatement("UPDATE productos_propios SET precio = ? "+
				" WHERE referencia = ? AND precio != ?");
		PreparedStatement ps2 = connection.prepareStatement("UPDATE productos_competencia SET precio = ? "+
				" WHERE codigo_hijo = ? AND precio != ?");
		PreparedStatement ps3 = connection.prepareStatement("INSERT INTO historico_comparaciones(referencia_propio,referencia_competencia,fecha_comparacion,precio_propio,descuento_propio,precio_competencia,descuento_competencia, descuento_propio2, descuento_competencia2) "+
				"VALUES(?,?,?,?,?,?,?,?,?) ON CONFLICT (referencia_propio, referencia_competencia, fecha_comparacion) DO UPDATE SET descuento_propio=?, descuento_competencia=?, descuento_propio2=?, descuento_competencia2=?");
		for(int i=0; i<listaComparaciones.getListaComparaciones().size();i++){
			Comparacion comp = listaComparaciones.getListaComparaciones().get(i);
			Producto propio = obtenerProductoPorReferencia(comp.getProductoPropio().getReferencia());
			ProductoCompetencia competencia = obtenerProductoCompetencia(comp.getProductoCompetencia().getCodigoHijo());
			ps.setDouble(1, propio.getPrecio1());
			ps.setString(2, propio.getReferencia());
			ps.setDouble(3, propio.getPrecio1());
			ps2.setDouble(1, competencia.getPrecioBase());
			ps2.setString(2, competencia.getCodigoHijo());
			ps2.setDouble(3, competencia.getPrecioBase());
			ps3.setString(1, comp.getProductoPropio().getReferencia());
			ps3.setString(2, comp.getProductoCompetencia().getCodigoHijo());
			ps3.setString(3, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			ps3.setDouble(4, propio.getPrecio1());
			ps3.setDouble(5, comp.getProductoPropio().getDescuento());
			ps3.setDouble(6, competencia.getPrecioBase());
			ps3.setDouble(7, comp.getProductoCompetencia().getDescuento());
			ps3.setDouble(8, comp.getProductoPropio().getDescuento2());
			ps3.setDouble(9, comp.getProductoCompetencia().getDescuento2());
			ps3.setDouble(10, comp.getProductoPropio().getDescuento());
			ps3.setDouble(11, comp.getProductoCompetencia().getDescuento());
			ps3.setDouble(12, comp.getProductoPropio().getDescuento2());
			ps3.setDouble(13, comp.getProductoCompetencia().getDescuento2());
			int up1=ps.executeUpdate();
			int up2=ps2.executeUpdate();
			ps3.executeUpdate();
			if(up1>0){
				productosCambiaron.add(propio.getReferencia());
			}if(up2>0){
				productosCompetenciaCambiaron.add(competencia.getCodigoHijo());
			}
		}
		obtenerListaComparacionesBaseDeDatos();
	}




}

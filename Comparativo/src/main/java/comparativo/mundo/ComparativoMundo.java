package comparativo.mundo;

import java.io.File;
import java.io.FileInputStream;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.Row.MissingCellPolicy;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import org.postgresql.ds.PGSimpleDataSource;

import com.google.gson.Gson;



public class ComparativoMundo {
	
	private Catalogo catalogoPropio;
	private ArrayList<ProductoCompetencia> catalogoCompetencia;
	private ListaComparaciones listaComparaciones;
	private ResteasyClient client;
	private Connection connection;

	enum Campos {
		CODIGOHIJO,
		CODIGOPADRE,
		NOMBRE,
		PRECIOBASE,
	  }

	
	public ComparativoMundo(String user, String password, String uri) throws SQLException{
		super();
		this.catalogoPropio=obtenerInformacionApiCategorias();
		this.catalogoCompetencia=null;
		this.listaComparaciones = new ListaComparaciones();
		this.connection = conexionDb(user, password, uri);
	}

	public Connection conexionDb(String user, String password, String uri) throws SQLException{
		String url = "jdbc:postgresql://"+uri+"/comparativo";
		Properties props = new Properties();
		props.setProperty("user",user);
		props.setProperty("password",password);
		return DriverManager.getConnection(url,props);

	}

	public ListaComparaciones obtenerHistoricoComparaciones(java.sql.Date pFecha) throws SQLException{
		PreparedStatement ps = connection.prepareStatement("SELECT * FROM COMPARACIONES as cmp INNER JOIN PRODUCTOS_PROPIOS as pp on cmp.referencia_propio = pp.referencia "+
		 "INNER JOIN PRODUCTOS_COMPETENCIA as pc on pc.codigo_hijo = cmp.referencia_competencia WHERE cmp.fecha_comparacion = TO_DATE(?, 'yyyy-MM')");
		ps.setDate(1, pFecha);
		ResultSet rs = ps.executeQuery();

		ListaComparaciones listaComparaciones = new ListaComparaciones();
		String referenciaPropio;
		String referenciaCompetencia;
		java.sql.Date fechaComparacion;
		double precioPropio;
		double descuentoPropio;
		double precioCompetencia;
		double descuentoCompetencia;
		String nombrePropio;
		String codigoPadreCompetencia;
		String nombreCompetencia;

		
		while(rs.next()){
			referenciaPropio = (String) rs.getObject(1);
			referenciaCompetencia = (String) rs.getObject(2);
			fechaComparacion = (java.sql.Date) rs.getObject(3);
			precioPropio = ((BigDecimal) rs.getObject(4)).doubleValue();
			descuentoPropio = ((BigDecimal) rs.getObject(5)).doubleValue();
			precioCompetencia = ((BigDecimal) rs.getObject(6)).doubleValue();
			descuentoCompetencia = ((BigDecimal) rs.getObject(7)).doubleValue();
			referenciaPropio = (String) rs.getObject(8);
			nombrePropio = (String) rs.getObject(9);
			codigoPadreCompetencia = (String) rs.getObject(11);
			nombreCompetencia = (String) rs.getObject(12);
			listaComparaciones.agregarComparacion(new Producto(nombrePropio, referenciaPropio, precioPropio, descuentoPropio), new ProductoCompetencia(referenciaCompetencia, codigoPadreCompetencia, nombreCompetencia, precioCompetencia, descuentoCompetencia), fechaComparacion);
		}
		return listaComparaciones;
		
	}

	public boolean crearComparacion(Producto productoPropio, ProductoCompetencia productoCompetencia, Date pFecha) throws SQLException{
		if(productoPropio==null || productoCompetencia == null){
			return false;
		}else{
			PreparedStatement ps = connection.prepareStatement("INSERT INTO productos_propios(referencia,nombre) "+
			"SELECT * FROM (SELECT '?' AS referencia, '?' AS nombre) AS temp "+
			"WHERE NOT EXISTS ( "+
				"SELECT referencia FROM productos_propios WHERE customer_name = ? "+
			") LIMIT 1");
			ps.setString(1, productoPropio.getReferencia());
			ps.setString(2, productoPropio.getNombre());
			ps.setString(3, productoPropio.getReferencia());
			PreparedStatement ps2 = connection.prepareStatement("INSERT INTO productos_competencia(codigo_hijo,codigo_padre,nombre) "+
			"SELECT * FROM (SELECT '?' AS codigo_hijo, '?' as codigo_padre, '?' AS nombre) AS temp "+
			"WHERE NOT EXISTS ( "+
			"SELECT referencia FROM productos_competencia WHERE codigo_hijo = ? "+
			") LIMIT 1");
			ps2.setString(1, productoCompetencia.getCodigoHijo());
			ps2.setString(2, productoCompetencia.getCodigoPadre());
			ps2.setString(3, productoCompetencia.getNombre());
			ps2.setString(4, productoCompetencia.getCodigoHijo());
			PreparedStatement ps3 = connection.prepareStatement("INSERT INTO comparaciones(referencia_propio,referencia_competencia,fecha_comparacion,precio_propio,descuento_propio,precio_competencia,descuento_competencia) "+
			"SELECT * FROM (SELECT '?' AS referencia_propio, '?' as referencia_competencia, TO_DATE('?','yyyy-MM') AS fecha_comparacion, '?' as precio_propio, '?' as descuento_propio, '?' as precio_competencia, '?' as descuento_competencia) AS temp "+
			"WHERE NOT EXISTS ( "+
			"SELECT referencia FROM productos_competencia WHERE referencia_pripio = ? and referencia_competencia = ? and fecha_comparacion = TO_DATE('?','yyyy-MM') "+
			") LIMIT 1");
			ps3.setString(1, productoPropio.getReferencia());
			ps3.setString(2, productoCompetencia.getCodigoHijo());
			ps3.setString(3, pFecha.toString());
			ps3.setDouble(4, productoPropio.getPrecio1());
			ps3.setDouble(5, productoPropio.getDescuento());
			ps3.setDouble(6, productoCompetencia.getPrecioBase());
			ps3.setDouble(7, productoCompetencia.getDescuento());
			ps3.setString(8, productoPropio.getReferencia());
			ps3.setString(9, productoCompetencia.getCodigoHijo());

			ps.executeQuery();
			ps2.executeQuery();
			int rs3 = ps3.executeUpdate();
			if(rs3>0){
				listaComparaciones.agregarComparacion(productoPropio, productoCompetencia, pFecha);
				return true;
			}
			else{
				return false;
			}

		}
	}

	public boolean eliminarComparacion(Comparacion comparacion){
		return listaComparaciones.eliminarComparacion(comparacion);
	}

	public ListaComparaciones getListaComparaciones(){
		return listaComparaciones;
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
				codigoHijo = (cellCodigoHijo.getNumericCellValue()+"").trim().replaceAll("\\s+","");;
			}else{
				codigoHijo = (cellCodigoHijo.getStringCellValue()).trim().replaceAll("\\s+","");;
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
					codigoPadre, nombre, precio, 0));
			}
			
		}
		workbook.close();
		inputStream.close();
		catalogoCompetencia=listaCompetencia;
		return listaCompetencia;
	}

	public ArrayList<ProductoCompetencia> getCatalogoCompetencia(){
		return catalogoCompetencia;
	}

	public Catalogo obtenerInformacionApiCategorias(){
		client=(ResteasyClient) ClientBuilder.newClient();
		ResteasyWebTarget target = this.client.target("https://api.cataprom.com/rest/categorias");
		String response = target.request(MediaType.APPLICATION_JSON).get(String.class);
		CategoriaResponse categorias = new Gson().fromJson(response, CategoriaResponse.class);
	
		return new Catalogo(categorias.getResultado());
	}
	
	public void obtenerProductosCategoria(Categoria pCategoria){
		if(pCategoria.getProductos()==null){
			ResteasyWebTarget target = client.target("https://api.cataprom.com/rest/categorias/"+pCategoria.getId()+"/productos");
			String response = target.request(MediaType.APPLICATION_JSON).get(String.class);
			ProductosResponse productos = new Gson().fromJson(response, ProductosResponse.class);
			pCategoria.setProductos(productos.getResultado());
		}
	}
	
	public Catalogo getCatalogoPropio(){
		return catalogoPropio;
	}

}
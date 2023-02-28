package comparativo.mundo;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.UnknownHostException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Properties;

import com.google.gson.Gson;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;
import comparativo.mundo.model.ComparacionMarpico;
import comparativo.mundo.model.Producto;
import comparativo.mundo.model.Stock;
import comparativo.mundo.persistence.CatalogoCatalogosPromocionales;
import comparativo.mundo.persistence.CatalogoMarpicoPersistence;
import comparativo.mundo.persistence.DataBaseConection;
import comparativo.mundo.persistence.ProductosMarpico;
import comparativo.mundo.response.CatalogoMarpico;
import comparativo.mundo.response.InventarioTransitoMarpico;
import comparativo.mundo.response.MaterialesMarpico;
import comparativo.mundo.response.StockResponse;
import jakarta.mail.Authenticator;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.Multipart;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

public class ComparativoMarpicoMundo {

	private ResteasyClient client;
	private CatalogoMarpico catalogo;
	private DataBaseConection connection;
	private ArrayList<ComparacionMarpico> comparaciones;
	private ArrayList<ComparacionMarpico> historicoComparaciones;
	private ArrayList<Producto> productosCambiaron;
	private ArrayList<ProductosMarpico> productosCompetenciaCambiaron;

	public ComparativoMarpicoMundo(String user, String password, String uri) throws SQLException {
		this.catalogo = cargarCatalogoMarpico();
		this.connection = new DataBaseConection(user, password, uri);
		this.comparaciones = new ArrayList<>();
		this.historicoComparaciones = new ArrayList<>();
		this.productosCambiaron = new ArrayList<>();
		this.productosCompetenciaCambiaron = new ArrayList<>();
	}

	public String enviarEmail(String email) throws MessagingException{
		String text = "No hubo actualización de precios en los productos";
		if(productosCambiaron.size()>0&&productosCompetenciaCambiaron.size()>0){
			text="Hubo una actualización en los precios de los productos propios y de la competencia:\nProductos propios:\n";
			for (Producto producto : productosCambiaron) {
				if(producto.getIndicadorSubio()==1){
					text+=producto.getReferencia()+" subio de precio,\n";
				}else if(producto.getIndicadorSubio()==-1){
					text+=producto.getReferencia()+" bajo de precio,\n";
				}
			}
			text+="Productos competencia:\n";
			for (ProductosMarpico producto : productosCompetenciaCambiaron) {
				if(producto.getIndicadorSubio()==1){
					text+=producto.getFamilia()+" subio de precio,\n";
				}else if(producto.getIndicadorSubio()==-1){
					text+=producto.getFamilia()+" bajo de precio,\n";
				}
			}
		}
		else if(this.productosCambiaron.size()>0){
			text="Hubo una actualizacion de productos propios, los siguientes productos cambiaron de precio: \n";
			for (Producto producto : productosCambiaron) {
				if(producto.getIndicadorSubio()==1){
					text+=producto.getReferencia()+" subio de precio,\n";
				}else if(producto.getIndicadorSubio()==-1){
					text+=producto.getReferencia()+" bajo de precio,\n";
				}
			}
		}else if(this.productosCompetenciaCambiaron.size()>0){
			text="Hubo una actualización de productos de Promopciones, los siguientes productos cambiaron de precio:\n";
			for (ProductosMarpico producto : productosCompetenciaCambiaron) {
				if(producto.getIndicadorSubio()==1){
					text+=producto.getFamilia()+" subio de precio,\n";
				}else if(producto.getIndicadorSubio()==-1){
					text+=producto.getFamilia()+" bajo de precio,\n";
				}
			}
		}
		if(this.productosCompetenciaCambiaron.size()>0||this.productosCambiaron.size()>0){
			Properties prop = new Properties();
			prop.put("mail.smtp.auth", true);
			prop.put("mail.smtp.starttls.enable", "true");
			prop.put("mail.smtp.host", "smtp-mail.outlook.com");
			prop.put("mail.smtp.port", "587");
			prop.put("mail.smtp.ssl.trust", "smtp-mail.outlook.com");
			Session session = Session.getInstance(prop, new Authenticator() {
				@Override
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("comparadorcataprom@outlook.com", "cataprom2023");
				}
			});
			Message message = new MimeMessage(session);
			message.setFrom(new InternetAddress("comparadorcataprom@outlook.com"));
			message.setRecipients(
			Message.RecipientType.TO, InternetAddress.parse(email));
			message.setSubject("[COMPARADOR] Actualización de precios");
	
	
			MimeBodyPart mimeBodyPart = new MimeBodyPart();
			mimeBodyPart.setContent(text, "text/html; charset=utf-8");
	
			Multipart multipart = new MimeMultipart();
			multipart.addBodyPart(mimeBodyPart);
	
			message.setContent(multipart);
	
			Transport.send(message);
		}
		return text;
	}

	public void determinarSubio(Producto propioActual, Producto propioActualizado, ProductosMarpico marpicoActual, ProductosMarpico marpicoActualizado, int numeroPrecio){
		if(marpicoActual.getPrecio()<marpicoActualizado.getPrecio()){
			marpicoActualizado.setIndicadorSubio(1);
		}if(marpicoActual.getPrecio()>marpicoActualizado.getPrecio()){
			marpicoActualizado.setIndicadorSubio(-1);
		}
		if(numeroPrecio==1){
			if(propioActual.getPrecio1()>propioActualizado.getPrecio1()){
				propioActualizado.setIndicadorSubio(-1);
			}else if(propioActual.getPrecio1()<propioActualizado.getPrecio1()){
				propioActualizado.setIndicadorSubio(1);
			}
		}
		else if(numeroPrecio==2){
			if(propioActual.getPrecio2()>propioActualizado.getPrecio2()){
				propioActualizado.setIndicadorSubio(-1);
			}else if(propioActual.getPrecio2()<propioActualizado.getPrecio2()){
				propioActualizado.setIndicadorSubio(1);
			}
		}
		else if(numeroPrecio==3){
			if(propioActual.getPrecio3()>propioActualizado.getPrecio3()){
				propioActualizado.setIndicadorSubio(-1);
			}else if(propioActual.getPrecio3()<propioActualizado.getPrecio3()){
				propioActualizado.setIndicadorSubio(1);
			}
		}
		else if(numeroPrecio==4){
			if(propioActual.getPrecio4()>propioActualizado.getPrecio4()){
				propioActualizado.setIndicadorSubio(-1);
			}else if(propioActual.getPrecio4()<propioActualizado.getPrecio4()){
				propioActualizado.setIndicadorSubio(1);
			}
		}
		else if(numeroPrecio==5){
			if(propioActual.getPrecio5()>propioActualizado.getPrecio5()){
				propioActualizado.setIndicadorSubio(-1);
			}else if(propioActual.getPrecio5()<propioActualizado.getPrecio5()){
				propioActualizado.setIndicadorSubio(1);
			}
		}
	}

	public void actualizarComparaciones() throws SQLException,
	UnknownHostException{
		ComparativoMundo mundo = new ComparativoMundo();
		productosCambiaron.clear();
		productosCompetenciaCambiaron.clear();
		CatalogoCatalogosPromocionales propioPersistence = new CatalogoCatalogosPromocionales(this.connection.getConnection());
		CatalogoMarpicoPersistence marpicoPersistence = new CatalogoMarpicoPersistence(this.connection.getConnection());
		
		for(int i = 0; i< comparaciones.size(); i++){
			ComparacionMarpico comparacion = comparaciones.get(i);

			Producto productoPropio =
			mundo.obtenerProductoPorReferencia(comparacion.getProductoPropio().getReferencia());
			double precio=productoPropio.getPrecio1();;
			if(comparacion.getNumeroPrecio()==2){
				precio=productoPropio.getPrecio2();
			}
			if(comparacion.getNumeroPrecio()==3){
				precio=productoPropio.getPrecio3();
			}
			if(comparacion.getNumeroPrecio()==4){
				precio=productoPropio.getPrecio4();
			}
			if(comparacion.getNumeroPrecio()==5){
				precio=productoPropio.getPrecio5();
			}

			ArrayList<Stock> stockPropio = getStocks(comparacion.getProductoPropio().getReferencia());
			propioPersistence.deleteStockProducto(productoPropio.getReferencia());
			for (int j = 0; j < stockPropio.size(); j++) {
				Stock propioStock = stockPropio.get(j);
				propioPersistence.insertStockProducto(propioStock);
			}

			ProductosMarpico productoCompetencia = buscarProductoPorFamilia(comparacion.getProductoCompetencia().getFamilia());
			setMenorPrecio(productoCompetencia);
			for(int j = 0; j<productoCompetencia.getMateriales().size();j++){
				MaterialesMarpico material = productoCompetencia.getMateriales().get(j);
				marpicoPersistence.insertMaterialesMarpico(material, comparacion.getProductoCompetencia().getFamilia());
				
				for(int k=0; k<material.getTrackings_importacion().size();k++){
					InventarioTransitoMarpico inventario =
					material.getTrackings_importacion().get(k);
					marpicoPersistence.insertInventarioTransito(inventario, material.getCodigo());
				}	
			}
			

			int up1=propioPersistence.updateProductosPropios(productoPropio.getPrecio1(), productoPropio.getPrecio2(), productoPropio.getPrecio3(), productoPropio.getPrecio4(), productoPropio.getPrecio5(), productoPropio.getReferencia());
			int up2=marpicoPersistence.updateProducto(productoCompetencia.getFamilia(), productoCompetencia.getPrecio());
			marpicoPersistence.insertHistoricoComparacion(productoPropio, productoCompetencia, comparacion.getNumeroPrecio(), 1, precio);
			determinarSubio(comparacion.getProductoPropio(), productoPropio, comparacion.getProductoCompetencia(), productoCompetencia, comparacion.getNumeroPrecio());
			if(up1>0){
				productosCambiaron.add(productoPropio);
			}if(up2>0){
				productosCompetenciaCambiaron.add(productoCompetencia);
			}
		}
		obtenerComparacionesBaseDeDatos();
	}

	public ArrayList<Producto> getProductosPropiosCambiaron(){
		return this.productosCambiaron;
	}
	public ArrayList<ProductosMarpico> getProductosCompetenciaCambiaron(){
		return this.productosCompetenciaCambiaron;
	}

	public boolean actualizarDescuentoComparacion(ComparacionMarpico comparacion,
	double descuentoPropio, double descuentoCompetencia, double descuentoPropio2,
	double descuentoCompetencia2) throws SQLException{
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		CatalogoMarpicoPersistence marpicoPersistence = new CatalogoMarpicoPersistence(this.connection.getConnection());
		if(descuentoPropio>0) {
			comparacion.getProductoPropio().setDescuento(descuentoPropio);
		}if(descuentoCompetencia>0) {
			comparacion.getProductoCompetencia().setDescuento1(descuentoCompetencia);
		}if(descuentoPropio2>0) {
			comparacion.getProductoPropio().setDescuento2(descuentoPropio2);
		}if(descuentoCompetencia2>0) {
			comparacion.getProductoCompetencia().setDescuento2(descuentoCompetencia2);
		}
		java.util.Date date = new java.util.Date();
		
		int response = marpicoPersistence.updateDescuentoComparacion(comparacion, formatter.format(date));
		if(response>0){
			return true;
		}else{
			return false;
		}
	}

	public void obtenerHistoricoComparaciones() throws SQLException,
			ParseException {
		historicoComparaciones.clear();
		CatalogoMarpicoPersistence marpicoPersistence = new CatalogoMarpicoPersistence(this.connection.getConnection());
		ResultSet rs = marpicoPersistence.getHistoricoComparaciones();
		String referenciaPropio;
		String referenciaCompetencia;
		double precioPropio;
		double descuentoPropio;
		double precioCompetencia;
		double descuentoCompetencia;
		double descuentoPropio2;
		String nombrePropio;
		String descripcion_comercial;
		double descuentoCompetencia2;
		int numeroPrecio;
		int numeroPrecioMarpico;
		String fechaComparacion;
		while (rs.next()) {
			referenciaPropio = (String) rs.getObject(1);
			referenciaCompetencia = (String) rs.getObject(2);
			numeroPrecio = ((BigDecimal) rs.getObject(11) == null ? -1 : ((BigDecimal) rs.getObject(11)).intValue());
			numeroPrecioMarpico = ((BigDecimal) rs.getObject(16) == null ? -1
					: ((BigDecimal) rs.getObject(16)).intValue());
			precioPropio = ((BigDecimal) rs.getObject(3)) == null ? 0 : ((BigDecimal) rs.getObject(3)).doubleValue();
			if (numeroPrecio == 2) {
				precioPropio = ((BigDecimal) rs.getObject(12)) == null ? 0
						: ((BigDecimal) rs.getObject(12)).doubleValue();
			}
			if (numeroPrecio == 3) {
				precioPropio = ((BigDecimal) rs.getObject(13)) == null ? 0
						: ((BigDecimal) rs.getObject(13)).doubleValue();
			}
			if (numeroPrecio == 4) {
				precioPropio = ((BigDecimal) rs.getObject(14)) == null ? 0
						: ((BigDecimal) rs.getObject(14)).doubleValue();
			}
			if (numeroPrecio == 5) {
				precioPropio = ((BigDecimal) rs.getObject(15)) == null ? 0
						: ((BigDecimal) rs.getObject(15)).doubleValue();
			}
			descuentoPropio = ((BigDecimal) rs.getObject(4)) == null ? 0 : ((BigDecimal) rs.getObject(4)).doubleValue();
			precioCompetencia = ((BigDecimal) rs.getObject(5)) == null ? 0
					: ((BigDecimal) rs.getObject(5)).doubleValue();
			descuentoCompetencia = ((BigDecimal) rs.getObject(6)) == null ? 0
					: ((BigDecimal) rs.getObject(6)).doubleValue();
			descuentoPropio2 = ((BigDecimal) rs.getObject(7)) == null ? 0
					: ((BigDecimal) rs.getObject(7)).doubleValue();
			descuentoCompetencia2 = ((BigDecimal) rs.getObject(8)) == null ? 0
					: ((BigDecimal) rs.getObject(8)).doubleValue();
			nombrePropio = (String) rs.getObject(9);
			descripcion_comercial = (String) rs.getObject(10);
			fechaComparacion = (String) rs.getObject(17);
			Producto propio = new Producto(nombrePropio, referenciaPropio, precioPropio,
					0, 0, 0, 0, descuentoPropio, descuentoPropio2, null);
			ArrayList<MaterialesMarpico> materiales = new ArrayList<>();
			ResultSet rs2 = marpicoPersistence.getMateriales(referenciaCompetencia);
			int identificador;
			String colorNombre;
			double precioMaterial;
			double descuentoMaterial;
			int inventarioMaterial;

			while (rs2.next()) {

				identificador = ((BigDecimal) rs2.getObject(1) == null ? -1
						: ((BigDecimal) rs2.getObject(1)).intValue());
				colorNombre = (String) rs2.getObject(2);
				precioMaterial = ((BigDecimal) rs2.getObject(3)) == null ? 0
						: ((BigDecimal) rs.getObject(3)).doubleValue();
				descuentoMaterial = ((BigDecimal) rs2.getObject(4)) == null ? 0
						: ((BigDecimal) rs.getObject(4)).doubleValue();
				inventarioMaterial = ((BigDecimal) rs2.getObject(5) == null ? -1
						: ((BigDecimal) rs2.getObject(5)).intValue());
				ResultSet rs3 = marpicoPersistence.getInventarios(identificador);
				ArrayList<InventarioTransitoMarpico> inventarioTransito = new ArrayList<>();
				String fecha;
				int cantidad;
				String ultimaActualizacion;

				while (rs3.next()) {
					fecha = (String) rs3.getObject(1);
					cantidad = ((BigDecimal) rs3.getObject(2) == null ? -1
							: ((BigDecimal) rs3.getObject(2)).intValue());
					ultimaActualizacion = (String) rs3.getObject(3);
					inventarioTransito.add(new InventarioTransitoMarpico(fecha, cantidad, ultimaActualizacion));
				}
				materiales.add(new MaterialesMarpico(identificador, colorNombre, inventarioTransito, precioMaterial,
						descuentoMaterial, inventarioMaterial, ""));
			}

			ProductosMarpico competencia = new ProductosMarpico(referenciaCompetencia, descripcion_comercial,
					materiales, precioCompetencia, descuentoCompetencia, descuentoCompetencia2);
			propio.setDescuento(descuentoPropio);
			historicoComparaciones.add(new ComparacionMarpico(competencia, propio, fechaComparacion,
					numeroPrecio, numeroPrecioMarpico));
		}
	}

	public void obtenerComparacionesBaseDeDatos() throws SQLException {
		comparaciones.clear();
		CatalogoCatalogosPromocionales propioPersistence = new CatalogoCatalogosPromocionales(
				this.connection.getConnection());
		CatalogoMarpicoPersistence catalogoMarpicoPersistence = new CatalogoMarpicoPersistence(
				this.connection.getConnection());
		ResultSet rs = catalogoMarpicoPersistence.getComparaciones();
		String referenciaPropio;
		String referenciaCompetencia;
		double precioPropio;
		double descuentoPropio;
		double precioCompetencia;
		double descuentoCompetencia;
		double descuentoPropio2;
		String nombrePropio;
		String descripcion_comercial;
		double descuentoCompetencia2;
		String color, llegadaBodegaLocal, estadoOrden;
		int bodegaLocal, bodegaZonaFranca, totalDisponible, cantidadTransito;
		int numeroPrecio;
		int numeroPrecioMarpico;
		while (rs.next()) {
			ArrayList<Stock> stock = new ArrayList<>();
			referenciaPropio = (String) rs.getObject(1);
			referenciaCompetencia = (String) rs.getObject(2);
			numeroPrecio = ((BigDecimal) rs.getObject(11) == null ? -1 : ((BigDecimal) rs.getObject(11)).intValue());
			numeroPrecioMarpico = ((BigDecimal) rs.getObject(16) == null ? -1
					: ((BigDecimal) rs.getObject(16)).intValue());
			precioPropio = ((BigDecimal) rs.getObject(3)) == null ? 0 : ((BigDecimal) rs.getObject(3)).doubleValue();

			if (numeroPrecio == 2) {
				precioPropio = ((BigDecimal) rs.getObject(12)) == null ? 0
						: ((BigDecimal) rs.getObject(12)).doubleValue();
			}
			if (numeroPrecio == 3) {
				precioPropio = ((BigDecimal) rs.getObject(13)) == null ? 0
						: ((BigDecimal) rs.getObject(13)).doubleValue();
			}
			if (numeroPrecio == 4) {
				precioPropio = ((BigDecimal) rs.getObject(14)) == null ? 0
						: ((BigDecimal) rs.getObject(14)).doubleValue();
			}
			if (numeroPrecio == 5) {
				precioPropio = ((BigDecimal) rs.getObject(15)) == null ? 0
						: ((BigDecimal) rs.getObject(15)).doubleValue();
			}

			descuentoPropio = ((BigDecimal) rs.getObject(4)) == null ? 0 : ((BigDecimal) rs.getObject(4)).doubleValue();
			precioCompetencia = ((BigDecimal) rs.getObject(5)) == null ? 0
					: ((BigDecimal) rs.getObject(5)).doubleValue();
			descuentoCompetencia = ((BigDecimal) rs.getObject(6)) == null ? 0
					: ((BigDecimal) rs.getObject(6)).doubleValue();
			descuentoPropio2 = ((BigDecimal) rs.getObject(7)) == null ? 0
					: ((BigDecimal) rs.getObject(7)).doubleValue();
			descuentoCompetencia2 = ((BigDecimal) rs.getObject(8)) == null ? 0
					: ((BigDecimal) rs.getObject(8)).doubleValue();
			nombrePropio = (String) rs.getObject(9);
			descripcion_comercial = (String) rs.getObject(10);

			ResultSet rs2 = propioPersistence.getStock(referenciaPropio);
			while (rs2.next()) {
				color = ((String) rs2.getObject(1));
				bodegaLocal = ((BigDecimal) rs2.getObject(2)) == null ? 0 : ((BigDecimal) rs2.getObject(2)).intValue();
				bodegaZonaFranca = ((BigDecimal) rs2.getObject(3)) == null ? 0
						: ((BigDecimal) rs2.getObject(3)).intValue();
				totalDisponible = ((BigDecimal) rs2.getObject(4)) == null ? 0
						: ((BigDecimal) rs2.getObject(4)).intValue();
				llegadaBodegaLocal = ((String) rs2.getObject(5));
				cantidadTransito = ((BigDecimal) rs2.getObject(6)) == null ? 0
						: ((BigDecimal) rs2.getObject(6)).intValue();
				estadoOrden = (String) rs2.getObject(7);
				stock.add(new Stock(referenciaPropio, color, bodegaLocal, bodegaZonaFranca, totalDisponible,
						llegadaBodegaLocal, cantidadTransito, estadoOrden));
			}
			Producto propio = new Producto(nombrePropio, referenciaPropio, precioPropio,
					0, 0, 0, 0, descuentoPropio, descuentoPropio2, stock);
			ArrayList<MaterialesMarpico> materiales = new ArrayList<>();

			rs2 = catalogoMarpicoPersistence.getMateriales(referenciaCompetencia);
			int identificador;
			String colorNombre;
			double precioMaterial;
			double descuentoMaterial;
			int inventarioMaterial;

			while (rs2.next()) {
				identificador = ((BigDecimal) rs2.getObject(1) == null ? -1
						: ((BigDecimal) rs2.getObject(1)).intValue());
				colorNombre = (String) rs2.getObject(2);
				precioMaterial = ((BigDecimal) rs2.getObject(3)) == null ? 0
						: ((BigDecimal) rs.getObject(3)).doubleValue();
				descuentoMaterial = ((BigDecimal) rs2.getObject(4)) == null ? 0
						: ((BigDecimal) rs.getObject(4)).doubleValue();
				inventarioMaterial = ((BigDecimal) rs2.getObject(5) == null ? -1
						: ((BigDecimal) rs2.getObject(5)).intValue());

				ResultSet rs3 = catalogoMarpicoPersistence.getInventarios(identificador);
				ArrayList<InventarioTransitoMarpico> inventariosTransito = new ArrayList<>();
				String fecha;
				int cantidad;
				String ultimaActualizacion;

				while (rs3.next()) {
					fecha = (String) rs3.getObject(1);
					cantidad = ((BigDecimal) rs3.getObject(2) == null ? -1
							: ((BigDecimal) rs3.getObject(2)).intValue());
					ultimaActualizacion = (String) rs3.getObject(3);
					inventariosTransito.add(new InventarioTransitoMarpico(fecha, cantidad, ultimaActualizacion));
				}
				materiales.add(new MaterialesMarpico(identificador, colorNombre, inventariosTransito, precioMaterial,
						descuentoMaterial, inventarioMaterial, ""));
			}

			ProductosMarpico competencia = new ProductosMarpico(referenciaCompetencia, descripcion_comercial,
					materiales, precioCompetencia, descuentoCompetencia, descuentoCompetencia2);
			propio.setDescuento(descuentoPropio);
			comparaciones.add(new ComparacionMarpico(competencia, propio, null,
					numeroPrecio, numeroPrecioMarpico));
		}
	}

	public boolean eliminarComparacion(ComparacionMarpico comparacion, boolean
	esHistorico) throws SQLException{
		CatalogoMarpicoPersistence persistenceMarpico = new CatalogoMarpicoPersistence(this.connection.getConnection());
		int response = persistenceMarpico.deleteComparacion(comparacion, esHistorico);
		if(response>0){
			if(esHistorico){
				return historicoComparaciones.remove(comparacion);
			}else{
				return comparaciones.remove(comparacion);
			}
		}else{
			return false;
		}

	}

	public void setMenorPrecio(ProductosMarpico productoCompetencia){
		ArrayList<MaterialesMarpico> materiales = productoCompetencia.getMateriales();
		double precio=Double.POSITIVE_INFINITY;
		double descuento=0;
		for (MaterialesMarpico materialesMarpico : materiales) {
			if(materialesMarpico.getPrecio()<precio){
				precio=materialesMarpico.getPrecio();
			}
			if(materialesMarpico.getDescuento()>descuento){
				descuento=materialesMarpico.getDescuento();
			}
		}
		productoCompetencia.setPrecio(precio);
		productoCompetencia.setDescuento1(descuento);
	}

	public ComparacionMarpico crearComparacion(Producto productoPropio, ProductosMarpico productoCompetencia,
			String fecha, int pPrecio) throws SQLException {
		setMenorPrecio(productoCompetencia);
		CatalogoMarpicoPersistence marpicoPersistence = new CatalogoMarpicoPersistence(this.connection.getConnection());
		CatalogoCatalogosPromocionales propioPersistence = new CatalogoCatalogosPromocionales(
				this.connection.getConnection());

		propioPersistence.insertProducto(productoPropio.getReferencia(), productoPropio.getNombre(),
				productoPropio.getPrecio1(), productoPropio.getPrecio2(), productoPropio.getPrecio3(),
				productoPropio.getPrecio4(), productoPropio.getPrecio5());
		
		propioPersistence.deleteStockProducto(productoPropio.getReferencia());
		ArrayList<Stock> stocks = getStocks(productoPropio.getReferencia());
		productoPropio.setStock(stocks);
		for (int i = 0; i < stocks.size(); i++) {
			propioPersistence.insertStockProducto(stocks.get(i));
		}
		marpicoPersistence.insertProductoMarpico(productoCompetencia);

		for (int i = 0; i < productoCompetencia.getMateriales().size(); i++) {
			marpicoPersistence.insertMaterialesMarpico(productoCompetencia.getMateriales().get(i),
					productoCompetencia.getFamilia());

			for (int j = 0; j < productoCompetencia.getMateriales().get(i).getTrackings_importacion().size(); j++) {
				marpicoPersistence.insertInventarioTransito(
						productoCompetencia.getMateriales().get(i).getTrackings_importacion().get(j),
						productoCompetencia.getMateriales().get(i).getCodigo());
			}
		}
		double precioPropio = productoPropio.getPrecio1();
		if (pPrecio == 2) {
			precioPropio = productoPropio.getPrecio2();
		}
		if (pPrecio == 3) {
			precioPropio = productoPropio.getPrecio3();
		}
		if (pPrecio == 4) {
			precioPropio = productoPropio.getPrecio4();
		}
		if (pPrecio == 5) {
			precioPropio = productoPropio.getPrecio5();
		}
		marpicoPersistence.insertComparacion(productoPropio, productoCompetencia, pPrecio, 1);

		marpicoPersistence.insertHistoricoComparacion(productoPropio, productoCompetencia, pPrecio, 1, precioPropio);

		ComparacionMarpico comparacion = new ComparacionMarpico(productoCompetencia, productoPropio, fecha, pPrecio,
				1);
		comparaciones.add(comparacion);
		return comparacion;
	}


	public CatalogoMarpico cargarCatalogoMarpico() {
		client = (ResteasyClient) ClientBuilder.newClient();
		ResteasyWebTarget target = client.target("https://marpicoprod.azurewebsites.net/api/inventarios/materialesAPI");
		String response = target.request(MediaType.APPLICATION_JSON)
				.header("Authorization", "Api-Key ijCDQW5tFfEQPjqlZVfqyrT0grs938KRPOTictTvB6EKzXPProgJpTnOFcR3HO8R")
				.get(String.class);
		catalogo = new Gson().fromJson(response, CatalogoMarpico.class);
		return catalogo;
	}

	public ArrayList<ComparacionMarpico> getComparaciones() {
		return this.comparaciones;
	}

	public ArrayList<ComparacionMarpico> getHistoricoComparaciones() {
		return this.historicoComparaciones;
	}

	public CatalogoMarpico getCatalogoMarpico() {
		return this.catalogo;
	}

	public ComparacionMarpico obtenerComparacionPorReferencias(String referencia, String familia, int numeroPrecio,
			String fecha, boolean esHistorico) {
		ComparacionMarpico rta = null;
		boolean termino = false;
		ComparacionMarpico actual;
		if (!esHistorico) {
			for (int i = 0; i < comparaciones.size() && !termino; i++) {
				actual = comparaciones.get(i);
				if ((referencia.equalsIgnoreCase(actual.getProductoPropio().getReferencia())
						&& familia.equalsIgnoreCase(actual.getProductoCompetencia().getFamilia())
						&& numeroPrecio == actual.getNumeroPrecio()
						|| (referencia.equals(actual.getProductoPropio().getReferencia())
								&& familia.equals(actual.getProductoCompetencia().getFamilia())
								&& numeroPrecio == actual.getNumeroPrecio())
						|| (referencia.contains(actual.getProductoPropio().getReferencia())
								&& familia.contains(actual.getProductoCompetencia().getFamilia())
								&& numeroPrecio == actual.getNumeroPrecio()))) {
					rta = actual;
					termino = true;
				}
			}
		} else {
			for (int i = 0; i < historicoComparaciones.size() && !termino; i++) {
				actual = historicoComparaciones.get(i);
				if ((referencia.equalsIgnoreCase(actual.getProductoPropio().getReferencia())
						&& familia.equalsIgnoreCase(actual.getProductoCompetencia().getFamilia())
						&& fecha.equalsIgnoreCase(
								actual.getFechaComparacion()))
						|| (referencia.equals(actual.getProductoPropio().getReferencia())
								&& fecha.equals(actual.getFechaComparacion())
								&& familia.equals(actual.getProductoCompetencia().getFamilia()))
						|| (referencia.contains(actual.getProductoPropio().getReferencia())
								&& fecha.contains(actual.getFechaComparacion())
								&& familia.contains(actual.getProductoCompetencia().getFamilia()))) {
					rta = actual;
					termino = true;
				}
			}
		}
		return rta;
	}


	public void exportarCsv(String path, boolean esHistorico) throws IOException{
		ArrayList<ComparacionMarpico> lista = null;
		if(esHistorico){
			lista = historicoComparaciones;
		}else{
			lista=comparaciones;
		}
		Workbook workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet("listaComparaciones");
		sheet.setDefaultColumnWidth(25);
		sheet.setDefaultRowHeight((short)-1);
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
		row.createCell(14).setCellValue("Numero Precio");
		row.createCell(15).setCellValue("Inventario Propio");
		row.createCell(16).setCellValue("Inventario Competencia");
		CellStyle green = workbook.createCellStyle();
		green.setFillForegroundColor(IndexedColors.GREEN.getIndex());
		green.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		CellStyle red = workbook.createCellStyle();
		red.setFillForegroundColor(IndexedColors.RED.getIndex());
		red.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		rowNum++;
		DataFormat fmt = workbook.createDataFormat();
		CellStyle cellStyle = workbook.createCellStyle();
		cellStyle.setWrapText(true);
		cellStyle.setDataFormat(
			fmt.getFormat("@"));

		for(int i = 0 ; i<lista.size(); i++){
			ComparacionMarpico comparacion = lista.get(i);
			row=sheet.createRow(rowNum++);
			row.createCell(0).setCellValue(comparacion.getProductoPropio().getNombre());
			row.createCell(1).setCellValue(comparacion.getProductoPropio().getReferencia());
			row.createCell(2).setCellValue(comparacion.getProductoPropio().getDescuento());
			row.createCell(3).setCellValue(comparacion.getProductoPropio().getDescuento2());
			row.createCell(4).setCellValue(comparacion.getProductoCompetencia().getFamilia());
			row.createCell(5).setCellValue(comparacion.getProductoCompetencia().getDescuento1());
			row.createCell(6).setCellValue(comparacion.getProductoCompetencia().getDescuento2());
			row.createCell(13).setCellValue(comparacion.getFechaComparacion()!=null ? comparacion.getFechaComparacion(): new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			row.createCell(14).setCellValue(comparacion.getNumeroPrecio());
			
			Cell stock1 = row.createCell(15);
			stock1.setCellValue(comparacion.getProductoPropio().getStockToString());
			stock1.setCellStyle(cellStyle);
			Cell stock2 =row.createCell(16);
			stock2.setCellValue(comparacion.getProductoCompetencia().getStockToString());
			stock2.setCellStyle(cellStyle);
			row.setHeight((short) -1);
			
			if(comparacion.getProductoPropio().getPrecio1()>comparacion.getProductoCompetencia().getPrecio()){
				Cell precioPropio=row.createCell(7);
				precioPropio.setCellValue(comparacion.getProductoPropio().getPrecio1());
				precioPropio.setCellStyle(red);
				Cell precioCompetencia=row.createCell(8);
				precioCompetencia.setCellValue(comparacion.getProductoCompetencia().getPrecio());
				precioCompetencia.setCellStyle(green);
			}else{
				Cell precioPropio=row.createCell(7);
				precioPropio.setCellValue(comparacion.getProductoPropio().getPrecio1());
				precioPropio.setCellStyle(green);
				Cell precioCompetencia=row.createCell(8);
				precioCompetencia.setCellValue(comparacion.getProductoCompetencia().getPrecio());
				precioCompetencia.setCellStyle(red);
			}
			if(comparacion.getProductoPropio().getPrecioDescuento()>comparacion.getProductoCompetencia().getPrecioDescuento1()){
				Cell precioPropio=row.createCell(9);
				precioPropio.setCellValue(comparacion.getProductoPropio().getPrecioDescuento());
				precioPropio.setCellStyle(red);
				Cell precioCompetencia=row.createCell(10);
				precioCompetencia.setCellValue(comparacion.getProductoCompetencia().getPrecioDescuento1());
				precioCompetencia.setCellStyle(green);
			}else{
				Cell precioPropio=row.createCell(9);
				precioPropio.setCellValue(comparacion.getProductoPropio().getPrecioDescuento());
				precioPropio.setCellStyle(green);
				Cell precioCompetencia=row.createCell(10);
				precioCompetencia.setCellValue(comparacion.getProductoCompetencia().getPrecioDescuento1());
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
		sheet.autoSizeColumn(15);
		sheet.autoSizeColumn(16);
		FileOutputStream out = new FileOutputStream(new File(path+".xlsx"));
		workbook.write(out);
		workbook.close();
		out.close();
	}

	public ArrayList<Stock> getStocks(String referencia){
		client= (ResteasyClient) ClientBuilder.newBuilder().build();
		ResteasyWebTarget target = this.client.target("https://api.cataprom.com/rest/stock/"+referencia);
		String response = target.request(MediaType.APPLICATION_JSON).get(String.class);
		StockResponse stocks = new Gson().fromJson(response, StockResponse.class);
		return stocks.getResultado();
		
	}

	public ProductosMarpico buscarProductoPorFamilia(String pFamilia){
        ProductosMarpico rta = null;
        client=(ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target("https://marpicoprod.azurewebsites.net/api/inventarios/materialesAPIByProducto?producto="+pFamilia);
		String response = target.request(MediaType.APPLICATION_JSON).header("Authorization", "Api-Key ijCDQW5tFfEQPjqlZVfqyrT0grs938KRPOTictTvB6EKzXPProgJpTnOFcR3HO8R").get(String.class);
        response = response.substring(1, response.length() - 1);
        rta = new Gson().fromJson(response, ProductosMarpico.class);
        return rta;
    }
}

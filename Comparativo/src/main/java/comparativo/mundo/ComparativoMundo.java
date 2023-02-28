package comparativo.mundo;

import java.io.File;
import java.io.FileInputStream;
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

import comparativo.mundo.model.Catalogo;
import comparativo.mundo.model.Categoria;
import comparativo.mundo.model.Comparacion;
import comparativo.mundo.model.ListaComparaciones;
import comparativo.mundo.model.Producto;
import comparativo.mundo.model.ProductoCompetencia;
import comparativo.mundo.model.Stock;
import comparativo.mundo.persistence.CatalogoCatalogosPromocionales;
import comparativo.mundo.persistence.CatalogoPromopciones;
import comparativo.mundo.persistence.ComparacionesPromopciones;
import comparativo.mundo.persistence.DataBaseConection;
import comparativo.mundo.response.CategoriaResponse;
import comparativo.mundo.response.ProductosResponse;
import comparativo.mundo.response.StockPromopcionesResponse;
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
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormat;
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
	private ArrayList<Producto> productosCambiaron;
	private ArrayList<ProductoCompetencia> productosCompetenciaCambiaron;
	private ListaComparaciones listaComparaciones;
	private ListaComparaciones historicoComparaciones;
	private ResteasyClient client;
	private DataBaseConection connection;
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
		this.connection = new DataBaseConection(user, password, uri);
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
			for (ProductoCompetencia producto : productosCompetenciaCambiaron) {
				if(producto.getIndicadorSubio()==1){
					text+=producto.getCodigoHijo()+" subio de precio,\n";
				}else if(producto.getIndicadorSubio()==-1){
					text+=producto.getCodigoHijo()+" bajo de precio,\n";
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
			for (ProductoCompetencia producto : productosCompetenciaCambiaron) {
				if(producto.getIndicadorSubio()==1){
					text+=producto.getCodigoHijo()+" subio de precio,\n";
				}else if(producto.getIndicadorSubio()==-1){
					text+=producto.getCodigoHijo()+" bajo de precio,\n";
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
	
	public ArrayList<Producto> getProductosActualizados(){
		return productosCambiaron;
	}
	
	public ArrayList<ProductoCompetencia> getProductosCompetenciaActualizaron(){
		return productosCompetenciaCambiaron;
	}
	
	public Comparacion obtenerComparacionPorReferencias(String referencia, String codigoHijo,int numeroPrecio, String fecha ,boolean esHistorico){
		Comparacion rta = null;
		boolean termino=false;
		Comparacion actual;
		if(!esHistorico){
			for(int i = 0;i<listaComparaciones.getListaComparaciones().size() && !termino;i++){
				actual = listaComparaciones.getListaComparaciones().get(i);
				if((referencia.equalsIgnoreCase(actual.getProductoPropio().getReferencia())
				&& codigoHijo.equalsIgnoreCase(actual.getProductoCompetencia().getCodigoHijo()) && numeroPrecio==actual.getNumeroPrecio() || (referencia.equals(actual.getProductoPropio().getReferencia()) && codigoHijo.equals(actual.getProductoCompetencia().getCodigoHijo()) && numeroPrecio==actual.getNumeroPrecio()) 
				|| (referencia.contains(actual.getProductoPropio().getReferencia()) && codigoHijo.contains(actual.getProductoCompetencia().getCodigoHijo()) && numeroPrecio==actual.getNumeroPrecio())
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
			row.createCell(14).setCellValue(comparacion.getNumeroPrecio());
			
			Cell stock1 = row.createCell(15);
			stock1.setCellValue(comparacion.getProductoPropio().getStockToString());
			stock1.setCellStyle(cellStyle);
			Cell stock2 =row.createCell(16);
			stock2.setCellValue(comparacion.getProductoCompetencia().getStockString());
			stock2.setCellStyle(cellStyle);
			row.setHeight((short) -1);
			
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
		sheet.autoSizeColumn(15);
		sheet.autoSizeColumn(16);
		FileOutputStream out = new FileOutputStream(new File(path+".xlsx"));
		workbook.write(out);
		workbook.close();
		out.close();
	}


	public ListaComparaciones obtenerHistoricoComparaciones(boolean estaConectado) throws SQLException, ParseException{
		if(estaConectado){	
			historicoComparaciones.getListaComparaciones().clear();
			ComparacionesPromopciones persistence = new ComparacionesPromopciones(this.connection.getConnection());
			ResultSet rs = persistence.getHistoricoComparaciones();
			String referenciaPropio,referenciaCompetencia,fechaComparacion,nombrePropio,codigoPadreCompetencia,nombreCompetencia;
			double precioPropio,descuentoPropio,precioCompetencia,descuentoCompetencia,descuentoPropio2,descuentoCompetencia2;
			int numeroPrecio;
			while(rs.next()){
					ArrayList<Stock> stocks = new ArrayList<>();
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
					numeroPrecio = (int) rs.getObject(13);
					Producto propio = new Producto(nombrePropio, referenciaPropio, precioPropio,0,0,0,0, descuentoPropio,descuentoPropio2,stocks);
					propio.setDescuento(descuentoPropio);
					historicoComparaciones.agregarComparacion(propio, new ProductoCompetencia(referenciaCompetencia, codigoPadreCompetencia, nombreCompetencia, precioCompetencia, descuentoCompetencia, descuentoCompetencia2,"El historico no posee stock"), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(fechaComparacion),numeroPrecio);
			}
			return historicoComparaciones;
		}else{
			return historicoComparaciones;
		}
		
	}

	public Comparacion crearComparacion(Producto productoPropio, ProductoCompetencia productoCompetencia, String pFecha, boolean persistir, int pPrecio) throws SQLException, ParseException{
		if(productoPropio==null || productoCompetencia == null){
			return null;
		}else{
			if(persistir){
				
				CatalogoPromopciones competenciaPersistence = new CatalogoPromopciones(this.connection.getConnection());
				ArrayList<StockPromopcionesResponse> stockResponse = competenciaPersistence.getStockInformation(productoCompetencia.getCodigoPadre(), this.catalogoCompetencia);
				productoCompetencia.processStock(stockResponse);
				competenciaPersistence.insertProducto(productoCompetencia.getCodigoHijo(), productoCompetencia.getCodigoPadre(), productoCompetencia.getNombre(), productoCompetencia.getPrecioBase(),productoCompetencia.getStockString());
				
				CatalogoCatalogosPromocionales propioPersistence = new CatalogoCatalogosPromocionales(this.connection.getConnection());
				propioPersistence.insertProducto(productoPropio.getReferencia(), productoPropio.getNombre(), productoPropio.getPrecio1(), productoPropio.getPrecio2(), productoPropio.getPrecio3(), productoPropio.getPrecio4(), productoPropio.getPrecio5());
				
				ArrayList<Stock> stocks = getStocks(productoPropio.getReferencia());
				productoPropio.setStock(stocks);

				for (int i = 0; i < stocks.size(); i++) {
					propioPersistence.insertStockProducto(stocks.get(i));
				}

				double precioPropio=productoPropio.getPrecio1();
				if(pPrecio==2){
					precioPropio=productoPropio.getPrecio2();
				}if(pPrecio==3){
					precioPropio= productoPropio.getPrecio3();
				}if(pPrecio==4){
					precioPropio= productoPropio.getPrecio4();
				}if(pPrecio==5){
					precioPropio= productoPropio.getPrecio5();
				}

				ComparacionesPromopciones comparacionPersistence = new ComparacionesPromopciones(this.connection.getConnection());
				int rs5 = comparacionPersistence.insertComparacion(productoPropio.getReferencia(), productoCompetencia.getCodigoHijo(), productoPropio.getDescuento(), productoCompetencia.getDescuento(), productoPropio.getDescuento2(), productoCompetencia.getDescuento2(), pPrecio);
				int rs4 = comparacionPersistence.insertHistoricoComparacion(productoPropio.getReferencia(), productoCompetencia.getCodigoHijo(), pFecha.toString(), 
				precioPropio, productoPropio.getDescuento(), productoCompetencia.getPrecioBase(), productoCompetencia.getDescuento(), productoPropio.getDescuento2(), productoCompetencia.getDescuento2(), pPrecio);
				
				if(rs4>0 || rs5>0){
					Comparacion rta = listaComparaciones.agregarComparacion(productoPropio, productoCompetencia, null, pPrecio);
					return rta;
				}
				else{
					return null;
				}
			}else{
				Comparacion rta = listaComparaciones.agregarComparacion(productoPropio, productoCompetencia, null,1);
				return rta;
			}
		}
	}

	public ArrayList<Stock> getStocks(String referencia){
		client= (ResteasyClient) ClientBuilder.newBuilder().build();
		ResteasyWebTarget target = this.client.target("https://api.cataprom.com/rest/stock/"+referencia);
		String response = target.request(MediaType.APPLICATION_JSON).get(String.class);
		StockResponse stocks = new Gson().fromJson(response, StockResponse.class);
		return stocks.getResultado();
		// ArrayList<Stock> stock = new ArrayList<>();
		// stock.add(new Stock(referencia, "Rojo", 1000, 1000, 2000, "2023-02-19",1000, "En Transito"));
		// stock.add(new Stock(referencia, "Amarillo", 2000, 700, 3000, "2023-03-01",300, "En Transito"));
		// stock.add(new Stock(referencia, "Verde", 600, 1000, 2500, "2023-02-25",2800, "En Transito"));
		// return stock;
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

			ComparacionesPromopciones comparacionPersistence = new ComparacionesPromopciones(this.connection.getConnection());
				
			int response = comparacionPersistence.updateDescuentoComparacion(comparacion.getProductoPropio().getDescuento(), comparacion.getProductoCompetencia().getDescuento(), comparacion.getProductoPropio().getDescuento2(), comparacion.getProductoCompetencia().getDescuento2(), comparacion.getProductoPropio().getReferencia(), comparacion.getProductoCompetencia().getCodigoHijo(), comparacion.getNumeroPrecio());
			double precioPropio=comparacion.getProductoPropio().getPrecio1();
			int pPrecio=comparacion.getNumeroPrecio();
			if(pPrecio==2){
				precioPropio=comparacion.getProductoPropio().getPrecio2();
			}if(pPrecio==3){
				precioPropio= comparacion.getProductoPropio().getPrecio3();
			}if(pPrecio==4){
				precioPropio= comparacion.getProductoPropio().getPrecio4();
			}if(pPrecio==5){
				precioPropio= comparacion.getProductoPropio().getPrecio5();
			}
				
			comparacionPersistence.updateDescuentoHistoricoComparacion(comparacion.getProductoPropio().getReferencia(), comparacion.getProductoCompetencia().getCodigoHijo(), formatter.format(date), precioPropio, comparacion.getProductoPropio().getDescuento(), comparacion.getProductoCompetencia().getPrecioBase(), comparacion.getProductoCompetencia().getDescuento(), comparacion.getProductoPropio().getDescuento2(), comparacion.getProductoCompetencia().getDescuento2(), pPrecio);
				
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
				boolean eliminacion;
				ComparacionesPromopciones comparacionesPersistence = new ComparacionesPromopciones(connection.getConnection());

				if(esHistorico){
					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
					eliminacion = comparacionesPersistence.deleteHistoricoComparacion(comparacion.getProductoPropio().getReferencia(), comparacion.getProductoCompetencia().getCodigoHijo(), formatter.format(comparacion.getFechaComparacion()));
				}else{
					eliminacion = comparacionesPersistence.deleteComparacion(comparacion.getProductoPropio().getReferencia(), comparacion.getProductoCompetencia().getCodigoHijo(), comparacion.getNumeroPrecio());
				}
				if(eliminacion){
					if(esHistorico){
						historicoComparaciones.eliminarComparacion(comparacion);
					}else{
						listaComparaciones.eliminarComparacion(comparacion);
					}
					if(eliminacion){
						return true;
					}else{
						return false;
					}
				}
				else{
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
			ComparacionesPromopciones comparacionesPersistence = new ComparacionesPromopciones(connection.getConnection());
			CatalogoCatalogosPromocionales propiosPersistence = new CatalogoCatalogosPromocionales(connection.getConnection());
			ResultSet rs = comparacionesPersistence.getListaComparaciones();
			String referenciaPropio,referenciaCompetencia,nombrePropio,codigoPadreCompetencia,nombreCompetencia;
			double precioPropio,descuentoPropio,precioCompetencia,descuentoCompetencia,descuentoPropio2,descuentoCompetencia2;
			String color,llegadaBodegaLocal,estadoOrden,stockString;
			int bodegaLocal,bodegaZonaFranca,totalDisponible,cantidadTransito;
			int numeroPrecio;
			
			while(rs.next()){
					ArrayList<Stock> stock = new ArrayList<>();
					referenciaPropio = (String) rs.getObject(1);
					referenciaCompetencia = (String) rs.getObject(2);
					numeroPrecio = (int) rs.getObject(12);
					precioPropio = ((BigDecimal) rs.getObject(3))==null ? 0 : ((BigDecimal) rs.getObject(3)).doubleValue();
					if(numeroPrecio==2){
						precioPropio = ((BigDecimal) rs.getObject(13))==null ? 0 : ((BigDecimal) rs.getObject(13)).doubleValue();
					}if(numeroPrecio==3){
						precioPropio = ((BigDecimal) rs.getObject(14))==null ? 0 : ((BigDecimal) rs.getObject(14)).doubleValue();
					}if(numeroPrecio==4){
						precioPropio = ((BigDecimal) rs.getObject(15))==null ? 0 : ((BigDecimal) rs.getObject(15)).doubleValue();
					}if(numeroPrecio==5){
						precioPropio = ((BigDecimal) rs.getObject(16))==null ? 0 : ((BigDecimal) rs.getObject(16)).doubleValue();
					}
					descuentoPropio = ((BigDecimal) rs.getObject(4))== null ? 0 :((BigDecimal) rs.getObject(4)).doubleValue();
					precioCompetencia = ((BigDecimal) rs.getObject(5)) == null ? 0 :((BigDecimal) rs.getObject(5)).doubleValue();
					descuentoCompetencia = ((BigDecimal) rs.getObject(6)) == null ? 0 : ((BigDecimal) rs.getObject(6)).doubleValue();
					descuentoPropio2 = ((BigDecimal) rs.getObject(7)) == null ? 0 : ((BigDecimal) rs.getObject(7)).doubleValue();
					descuentoCompetencia2 = ((BigDecimal) rs.getObject(8))== null ? 0 : ((BigDecimal) rs.getObject(8)).doubleValue();
					nombrePropio = (String) rs.getObject(9);
					codigoPadreCompetencia = (String) rs.getObject(10);
					nombreCompetencia = (String) rs.getObject(11);
					stockString = (String) rs.getObject(17);

					ResultSet rs2 = propiosPersistence.getStock(referenciaPropio);
					while(rs2.next()){
						color = ((String) rs2.getObject(1));
						bodegaLocal = ((BigDecimal) rs2.getObject(2)) == null ? 0 :((BigDecimal) rs2.getObject(2)).intValue();
						bodegaZonaFranca = ((BigDecimal) rs2.getObject(3)) == null ? 0 : ((BigDecimal) rs2.getObject(3)).intValue();
						totalDisponible = ((BigDecimal) rs2.getObject(4)) == null ? 0 : ((BigDecimal) rs2.getObject(4)).intValue();
						llegadaBodegaLocal = ((String) rs2.getObject(5));
						cantidadTransito = ((BigDecimal) rs2.getObject(6)) == null ? 0 : ((BigDecimal) rs2.getObject(6)).intValue();
						estadoOrden = (String) rs2.getObject(7);
						stock.add(new Stock(referenciaPropio, color, bodegaLocal, bodegaZonaFranca, totalDisponible, llegadaBodegaLocal, cantidadTransito, estadoOrden));
					}
					Producto propio = new Producto(nombrePropio, referenciaPropio, precioPropio, 0,0,0,0,descuentoPropio,descuentoPropio2,stock);
					propio.setDescuento(descuentoPropio);
					listaComparaciones.agregarComparacion(propio, new ProductoCompetencia(referenciaCompetencia, codigoPadreCompetencia, nombreCompetencia, precioCompetencia, descuentoCompetencia, descuentoCompetencia2,stockString), null, numeroPrecio);
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
				codigoHijo = (cellCodigoHijo.getNumericCellValue()+"");
			}else{
				codigoHijo = (cellCodigoHijo.getStringCellValue());
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

	public Catalogo obtenerInformacionApiCategorias() /*throws ForbiddenException, ProcessingException, UnknownHostException*/{
		client= (ResteasyClient) ClientBuilder.newBuilder().build();
		ResteasyWebTarget target = this.client.target("https://api.cataprom.com/rest/categorias");
		String response = target.request(MediaType.APPLICATION_JSON).get(String.class);
		CategoriaResponse categorias = new Gson().fromJson(response, CategoriaResponse.class);
		return new Catalogo(categorias.getResultado());


		// ArrayList<Stock> stock = new ArrayList<>();
		// stock.add(new Stock("t01", "Rojo", 100, 100, 300, "2023-02-19", 100, "En Camino"));
		// stock.add(new Stock("t01", "Verde", 300, 300, 300, "2023-02-22", 100, "En Camino"));
		// Categoria newCategoria = new Categoria("Test1", 1);
		// Producto newProducto1 = new Producto("Producto Test1", "t01", 10000, 0, 0, 0, 0,stock);
		// stock.removeAll(stock);
		// stock.add(new Stock("t02", "Amarillo", 100, 100, 300, "2023-02-19", 100, "En Camino"));
		// Producto newProducto2 = new Producto("Producto Test2", "t02", 20000, 0, 0, 0, 0,stock);
		// Producto newProducto3 = new Producto("Producto Test3", "t03", 30000, 32000, 33000, 0, 35000,null);
		// ArrayList<Producto> productosList = new ArrayList<>();
		// productosList.add(newProducto1);
		// productosList.add(newProducto2);
		// productosList.add(newProducto3);
		// newCategoria.setProductos(productosList);

		// Categoria newCategoria2 = new Categoria("Test2", 1);
		// stock.removeAll(stock);
		// stock.add(new Stock("t011", "Rosado", 100, 100, 300, "2023-02-19", 100, "En Camino"));
		// Producto newProducto11 = new Producto("Producto Test11", "t011", 5000, 0, 3000, 0, 0,stock);
		// Producto newProducto12 = new Producto("Producto Test12", "t012", 70000, 0, 60000, 0, 0,null);
		// ArrayList<Producto> productosList2 = new ArrayList<>();
		// productosList2.add(newProducto12);
		// productosList2.add(newProducto11);

		// ArrayList<Categoria> categorias = new ArrayList<>();
		// categorias.add(newCategoria);
		// categorias.add(newCategoria2);
	
		// return new Catalogo(categorias);
	}

	public Producto obtenerProductoPorReferencia(String pReferencia) throws UnknownHostException{
		Producto rta = null;
		ArrayList<Producto> productos = new ArrayList<>();
		boolean termino=false;
		client= (ResteasyClient) ClientBuilder.newBuilder().build();

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

	public void determinarSubio(Producto propioActual, Producto propioActualizado, ProductoCompetencia competenciaActual, ProductoCompetencia competenciaActualizado, int numeroPrecio){
		if(competenciaActual.getPrecioBase()>competenciaActualizado.getPrecioBase()){
			competenciaActualizado.setIndicadorSubio(-1);
		}if(competenciaActual.getPrecioBase()<competenciaActualizado.getPrecioBase()){
			competenciaActualizado.setIndicadorSubio(1);
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

	public void actualizarComparaciones() throws SQLException, UnknownHostException{
		productosCambiaron.clear();
		productosCompetenciaCambiaron.clear();
		CatalogoCatalogosPromocionales propioPersistence = new CatalogoCatalogosPromocionales(connection.getConnection());
		CatalogoPromopciones competenciaPersistence = new CatalogoPromopciones(connection.getConnection());
		ComparacionesPromopciones comparacionesPersistence = new ComparacionesPromopciones(connection.getConnection());
		productosCambiaron.removeAll(productosCambiaron);
		productosCompetenciaCambiaron.removeAll(productosCompetenciaCambiaron);
		for(int i=0; i<listaComparaciones.getListaComparaciones().size();i++){
			Comparacion comp = listaComparaciones.getListaComparaciones().get(i);
			System.out.println(comp.getProductoPropio().getReferencia());
			Producto propio = obtenerProductoPorReferencia(comp.getProductoPropio().getReferencia());
			System.out.println(propio);
			ProductoCompetencia competencia = obtenerProductoCompetencia(comp.getProductoCompetencia().getCodigoHijo());
			ArrayList<StockPromopcionesResponse> stockCompetencia = competenciaPersistence.getStockInformation(competencia.getCodigoPadre(), catalogoCompetencia);
			competencia.processStock(stockCompetencia);
			
			ArrayList<Stock> stockPropio = getStocks(comp.getProductoPropio().getReferencia());
			propioPersistence.deleteStockProducto(propio.getReferencia());
			for (int j = 0; j < stockPropio.size(); j++) {
				Stock propioStock = stockPropio.get(j);
				propioPersistence.insertStockProducto(propioStock);
			}
			int up1=propioPersistence.updateProductosPropios(propio.getPrecio1(), propio.getPrecio2(), propio.getPrecio3(), propio.getPrecio4(), propio.getPrecio5(), propio.getReferencia());
			int up2=0;
			up2=competenciaPersistence.updateProductosCompetencia(competencia.getPrecioBase(),competencia.getCodigoHijo(),competencia.getStockString());
			comparacionesPersistence.insertHistoricoComparacion(comp.getProductoPropio().getReferencia(), comp.getProductoCompetencia().getCodigoHijo(), new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()), propio.getPrecio1(), comp.getProductoPropio().getDescuento(), competencia.getPrecioBase(), comp.getProductoCompetencia().getDescuento(), comp.getProductoPropio().getDescuento2(),comp.getProductoCompetencia().getDescuento2(), comp.getNumeroPrecio());
			int up3=0;
			if((int) comp.getProductoCompetencia().getDescuento()!=(int) competencia.getDescuento()){
				up3=comparacionesPersistence.updateDescuentoComparacion(comp.getProductoPropio().getDescuento(), competencia.getDescuento(), comp.getProductoPropio().getDescuento2(), comp.getProductoCompetencia().getDescuento2(), propio.getReferencia(), competencia.getCodigoHijo(), comp.getNumeroPrecio());
			}
			determinarSubio(comp.getProductoPropio(), propio, comp.getProductoCompetencia(), competencia, comp.getNumeroPrecio());
			if(up1>0){
				productosCambiaron.add(propio);
			}if(up2>0||up3>0){
				productosCompetenciaCambiaron.add(competencia);
			}
		}
		obtenerListaComparacionesBaseDeDatos();
	}
}

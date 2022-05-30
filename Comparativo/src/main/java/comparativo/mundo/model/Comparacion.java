package comparativo.mundo.model;

import java.util.Date;
import java.util.Objects;

public class Comparacion {
	
	private Producto productoPropio;
	private ProductoCompetencia productoCompetencia;
	private Date fechaComparacion;
	private int numeroPrecio;

	
	public Comparacion(Producto pProducto1, ProductoCompetencia pProducto2, Date pFechaComparacion, int pNumeroPrecio){
		super();
		this.productoPropio = Objects.requireNonNull(pProducto1, "Tienes que seleccionar un producto tuyo");
		this.productoCompetencia = Objects.requireNonNull(pProducto2, "Tienes que selecciuonar un producto de la competencia");
		this.fechaComparacion= pFechaComparacion;
		this.numeroPrecio=pNumeroPrecio;
	}

	public int getNumeroPrecio(){
		return numeroPrecio;
	}
	public void setNumeroPrecio(int pNumeroPrecio){
		this.numeroPrecio=pNumeroPrecio;
	}
	public Date getFechaComparacion() {
		return this.fechaComparacion;
	}

	public void setFechaComparacion(Date fechaComparacion) {
		this.fechaComparacion = fechaComparacion;
	}

	public Producto getProductoPropio() {
		return productoPropio;
	}

	public void setProductoPropio(Producto productoPropio) {
		this.productoPropio = productoPropio;
	}

	public ProductoCompetencia getProductoCompetencia() {
		return productoCompetencia;
	}

	public void setProductoCompetencia(ProductoCompetencia productoCompetencia) {
		this.productoCompetencia = productoCompetencia;
	}
	
	public void cambiarDescuentoProductoPropio(double pDescuento){
		this.productoPropio.setDescuento(pDescuento);
	}
	
	public void cambiarDescuentoProductoCompetencia(double pDescuento){
		this.productoCompetencia.setDescuento(pDescuento);
	}
	
	

}

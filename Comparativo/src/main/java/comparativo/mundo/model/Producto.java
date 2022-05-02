package comparativo.mundo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Producto {

	private String nombre;
	private String referencia;
	private double precio1;
	private double descuento;
	private double descuento2;
	
	public Producto(String pNombre, String pReferencia, double pPrecio) {
		super();
		this.nombre = pNombre;
		this.referencia = pReferencia;
		this.precio1 = pPrecio;
		this.descuento = 35;
		this.descuento2 = 0;
	}
	public Producto(String pNombre, String pReferencia, double pPrecio, double pDescuento, double pDescuento2) {
		super();
		this.nombre = pNombre;
		this.referencia = pReferencia;
		this.precio1 = pPrecio;
		this.descuento = pDescuento;
		this.descuento2 = pDescuento2;
	}

	public double getPrecioDescuento2(){
		return round(getPrecioDescuento()*(1-(descuento2/100)));
	}
	
	public double getDescuento2() {
		return this.descuento2;
	}
	
	public void setDescuento2(double descuento2) {
		this.descuento2 = descuento2;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public double getPrecio1() {
		return round(precio1);
	}

	public void setPrecio1(double precio) {
		this.precio1 = precio;
	}

	public double getPrecioDescuento() {
		return round(precio1 * (1 - (descuento / 100)));
	}

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}

	public static double round(double value) {
	
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}

	public String toString() {
		return getReferencia() + "     --     " + getNombre();
	}

}

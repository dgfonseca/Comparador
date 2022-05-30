package comparativo.mundo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Producto {

	private String nombre;
	private String referencia;
	private double precio1;
	private double precio2;
	private double precio3;
	private double precio4;
	private double precio5;

	

	private double descuento;
	private double descuento2;
	
	public Producto(String pNombre, String pReferencia, double pPrecio, double pPrecio2, double pPrecio3, double pPrecio4, double pPrecio5) {
		super();
		this.nombre = pNombre;
		this.referencia = pReferencia;
		this.precio1 = pPrecio;
		this.precio2 = pPrecio2;
		this.precio3 = pPrecio3;
		this.precio4 = pPrecio4;
		this.precio5 = pPrecio5;
		this.descuento = 35;
		this.descuento2 = 0;
	}
	public Producto(String pNombre, String pReferencia, double pPrecio, double pPrecio2, double pPrecio3, double pPrecio4, double pPrecio5, double pDescuento, double pDescuento2) {
		super();
		this.nombre = pNombre;
		this.referencia = pReferencia;
		this.precio1 = pPrecio;
		this.precio2 = pPrecio2;
		this.precio3 = pPrecio3;
		this.precio4 = pPrecio4;
		this.precio5 = pPrecio5;
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

	public double getPrecio2() {
		return this.precio2;
	}

	public void setPrecio2(double precio2) {
		this.precio2 = precio2;
	}

	public double getPrecio3() {
		return this.precio3;
	}

	public void setPrecio3(double precio3) {
		this.precio3 = precio3;
	}

	public double getPrecio4() {
		return this.precio4;
	}

	public void setPrecio4(double precio4) {
		this.precio4 = precio4;
	}

	public double getPrecio5() {
		return this.precio5;
	}

	public void setPrecio5(double precio5) {
		this.precio5 = precio5;
	}

	public double getPrecioDescuento() {
		return round(precio1 * (1 - (descuento / 100)));
	}

	public double getPrecio2Descuento() {
		return round(precio2 * (1 - (descuento / 100)));
	}

	public double getPrecio3Descuento() {
		return round(precio3 * (1 - (descuento / 100)));
	}

	public double getPrecio4Descuento() {
		return round(precio4 * (1 - (descuento / 100)));
	}

	public double getPrecio5Descuento() {
		return round(precio5 * (1 - (descuento / 100)));
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

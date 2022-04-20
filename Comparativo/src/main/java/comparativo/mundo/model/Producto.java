package comparativo.mundo.model;

public class Producto {
	
	private String nombre;
	private String referencia;
	private double precio1;
	private double descuento;
	
	public Producto(String pNombre, String pReferencia, double pPrecio) {
		super();
		this.nombre=pNombre;
		this.referencia=pReferencia;
		this.precio1=pPrecio;
		this.descuento=35;
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
		return precio1;
	}

	public void setPrecio1(double precio) {
		this.precio1 = precio;
	}

	public double getPrecioDescuento() {
		return precio1*(1-(descuento/100));
	}

	public double getDescuento() {
		return descuento;
	}

	public void setDescuento(double descuento) {
		this.descuento = descuento;
	}

	public String toString(){
		return getReferencia()+"     --     "+getNombre();
	}
	

}

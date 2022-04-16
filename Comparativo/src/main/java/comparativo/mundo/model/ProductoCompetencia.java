package comparativo.mundo.model;

public class ProductoCompetencia {

    private String codigoHijo;
    private String codigoPadre;
    private String nombre;
    private double precioBase;
    private double descuento;

    

    public ProductoCompetencia(String pCodigoHijo, String pCodigoPadre, String nombre, double precioBase, double pDescuento){
        super();
        this.codigoHijo=pCodigoHijo;
        this.codigoPadre=pCodigoPadre;
        this.nombre=nombre;
        this.precioBase=precioBase;
        this.descuento=pDescuento;
    }

    public double getDescuento() {
        return this.descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public String getCodigoHijo() {
        return this.codigoHijo;
    }

    public void setCodigoHijo(String codigoHijo) {
        this.codigoHijo = codigoHijo;
    }

    public String getCodigoPadre() {
        return this.codigoPadre;
    }

    public void setCodigoPadre(String codigoPadre) {
        this.codigoPadre = codigoPadre;
    }

    public String getNombre() {
        return this.nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public double getPrecioBase() {
        return this.precioBase;
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public double getPrecioDescuento() {
		return precioBase*(1-(descuento/100));
	}

    


    
}

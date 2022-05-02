package comparativo.mundo.model;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ProductoCompetencia {

    private String codigoHijo;
    private String codigoPadre;
    private String nombre;
    private double precioBase;
    private double descuento;
    private double descuento2;




    

    public ProductoCompetencia(String pCodigoHijo, String pCodigoPadre, String nombre, double precioBase){
        super();
        this.codigoHijo=pCodigoHijo;
        this.codigoPadre=pCodigoPadre;
        this.nombre=nombre;
        this.precioBase=precioBase;
        this.descuento=25;
        this.descuento2=0;
    }

    public ProductoCompetencia(String pCodigoHijo, String pCodigoPadre, String nombre, double precioBase, double pDescuento, double pDescuento2){
        super();
        this.codigoHijo=pCodigoHijo;
        this.codigoPadre=pCodigoPadre;
        this.nombre=nombre;
        this.precioBase=precioBase;
        this.descuento=pDescuento;
        this.descuento2=pDescuento2;
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
        return round(this.precioBase);
    }

    public void setPrecioBase(double precioBase) {
        this.precioBase = precioBase;
    }

    public double getPrecioDescuento() {
		return round(precioBase*(1-(descuento/100)));
	}

    public String toString(){
        return getCodigoHijo()+"     --     "+getNombre();
    }

    public static double round(double value) {
    
        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(2, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }   
}

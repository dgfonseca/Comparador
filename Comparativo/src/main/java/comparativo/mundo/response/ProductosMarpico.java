package comparativo.mundo.response;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;

public class ProductosMarpico {

    private String familia;
    private String descripcion_comercial;
    private ArrayList<MaterialesMarpico> materiales;
    private double precio;
    private double descuento1;
    private double descuento2;

   
    public ProductosMarpico(){
        this.descuento1=0;
        this.descuento2=0;
    }


    public double getPrecio() {
        return this.precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getDescuento1() {
        return this.descuento1;
    }

    public void setDescuento1(double descuento1) {
        this.descuento1 = descuento1;
    }

    public double getDescuento2() {
        return this.descuento2;
    }

    public void setDescuento2(double descuento2) {
        this.descuento2 = descuento2;
    }

    public String getFamilia() {
        return this.familia;
    }

    public void setFamilia(String familia) {
        this.familia = familia;
    }

    public String getDescripcion_comercial() {
        return this.descripcion_comercial;
    }

    public void setDescripcion_comercial(String descripcion_comercial) {
        this.descripcion_comercial = descripcion_comercial;
    }

    public ArrayList<MaterialesMarpico> getMateriales() {
        return this.materiales;
    }

    public void setMateriales(ArrayList<MaterialesMarpico> materiales) {
        this.materiales = materiales;
    }

    public double getPrecioDescuento1(){
		return round(precio * (1 - (descuento1 / 100)));
    }

    public double getPrecioDescuento2(){
		return round(precio * (1 - (descuento2 / 100)));
    }

    public static double round(double value) {
	
		BigDecimal bd = BigDecimal.valueOf(value);
		bd = bd.setScale(2, RoundingMode.HALF_UP);
		return bd.doubleValue();
	}





    
}

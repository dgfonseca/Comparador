package comparativo.mundo.response;

import java.util.ArrayList;

public class MaterialesMarpico {
    
    private String color_nombre;
    private ArrayList<InventarioTransitoMarpico> trackings_importacion;
    private double precio;
    private double descuento;
    private int inventario;

    public String getColor_nombre() {
        return this.color_nombre;
    }

    public void setColor_nombre(String color_nombre) {
        this.color_nombre = color_nombre;
    }

    public ArrayList<InventarioTransitoMarpico> getTrackings_importacion() {
        return this.trackings_importacion;
    }

    public void setTrackings_importacion(ArrayList<InventarioTransitoMarpico> trackings_importacion) {
        this.trackings_importacion = trackings_importacion;
    }

    public double getPrecio() {
        return this.precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public double getDescuento() {
        return this.descuento;
    }

    public void setDescuento(double descuento) {
        this.descuento = descuento;
    }

    public int getInventario() {
        return this.inventario;
    }

    public void setInventario(int inventario) {
        this.inventario = inventario;
    }




    
    
    

}

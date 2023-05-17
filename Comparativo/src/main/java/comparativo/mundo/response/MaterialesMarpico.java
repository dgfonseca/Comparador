package comparativo.mundo.response;

import java.util.ArrayList;

public class MaterialesMarpico {
    
    private int codigo;
    private String variedad;
    private String color_nombre;
    private ArrayList<InventarioTransitoMarpico> trackings_importacion;
    private double precio;
    private double descuento;
    private int inventario;

    public MaterialesMarpico(){

    }
    public MaterialesMarpico(int pCodigo,String pColor, ArrayList<InventarioTransitoMarpico> importacion, double pprecio, double pdescuento, int pinventario, String pVariedad){
        this.color_nombre=pColor;
        this.trackings_importacion=importacion;
        this.precio=pprecio;
        this.descuento=pdescuento;
        this.inventario=pinventario;
        this.variedad=pVariedad;
        this.codigo=pCodigo;
    }

    public int getCodigo() {
        return this.codigo;
    }

    public void setCodigo(int codigo) {
        this.codigo = codigo;
    }
    public String getVariedad() {
        return this.variedad==null?"":this.variedad;
    }

    public void setVariedad(String variedad) {
        this.variedad = variedad;
    }

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

    public String getStockTransito(){
        String rta = "";
        for (int i = 0; i < trackings_importacion.size(); i++) {
            rta+="| Inventario en transito: "+trackings_importacion.get(i).getCantidad() +", Fecha: "+trackings_importacion.get(i).getFecha();
        }
        return rta;
    }




    
    
    

}

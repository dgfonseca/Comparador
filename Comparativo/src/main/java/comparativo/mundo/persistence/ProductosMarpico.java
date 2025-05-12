package comparativo.mundo.persistence;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import comparativo.mundo.response.MaterialesMarpico;

public class ProductosMarpico {

    private String familia;
    private String descripcion_comercial;
    private ArrayList<MaterialesMarpico> materiales;
    private double precio;
    private double descuento1;
    private double descuento2;

    private int indicadorSubio;
   
    public ProductosMarpico(){
        this.descuento1=0;
        this.descuento2=0;
        this.indicadorSubio=0;
    }
    public ProductosMarpico(String pFamilia, String pDescripcion, ArrayList<MaterialesMarpico> pMateriales, double pPrecio, double pDescuento, double pDescuento2){
        this.familia=pFamilia;
        this.descripcion_comercial=pDescripcion;
        this.materiales=pMateriales;
        this.precio=pPrecio;
        this.descuento1=pDescuento;
        this.descuento2=pDescuento2;
        this.indicadorSubio=0;
    }

    public int getIndicadorSubio() {
        return this.indicadorSubio;
    }

    public void setIndicadorSubio(int indicadorSubio) {
        this.indicadorSubio = indicadorSubio;
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
		return round(getPrecioDescuento1() * (1 - (descuento2 / 100)));
    }

    public static double round(double value) {
        return Math.ceil(value);
    }

    public String toString(){
        return this.familia +"  ---  "+this.descripcion_comercial;
    }

    public String getPrecioCalculado(){
        String rta="";
        ArrayList<Double> precios = new ArrayList<>();
        for (int i = 0; i < materiales.size(); i++) {
            MaterialesMarpico material = materiales.get(i);
            if(!precios.contains(material.getPrecio())){
                precios.add(material.getPrecio());
                rta+= material.getPrecio()+"$, ";
            }
        }
        return rta;

    }

    public ArrayList<Double> getPrecios(){
        ArrayList<Double> precios = new ArrayList<>();
        for (int i = 0; i < materiales.size(); i++) {
            if(!precios.contains(materiales.get(i).getPrecio())){
                precios.add(materiales.get(i).getPrecio());
            }
        }
        return precios;
    }

    public Map<Integer,Double> getMaterialPrecios(){
        Map<Integer,Double> precios = new HashMap<>();
        for (int i = 0; i < materiales.size(); i++) {
            if(!precios.containsValue(materiales.get(i).getPrecio())){
                precios.put(materiales.get(i).getCodigo(),materiales.get(i).getPrecio());
            }
        }
        return precios;
    }

    public void setPrecioDeMaterial(int codigo){
        for (MaterialesMarpico materialesMarpico : materiales) {
            if(materialesMarpico.getCodigo()==codigo){
                System.out.println("Actualizar Comparaciones :::: Precio de material :::: Precio Anterior: "+this.precio+" Precio Nuevo: "+materialesMarpico.getPrecio());
                this.precio=materialesMarpico.getPrecio();
                if(materialesMarpico.getDescuento()!=0){
                    this.descuento1=materialesMarpico.getDescuento();
                }
                if(this.descuento1==0){
                    this.descuento2=0;
                }
                break;
            }
        }
    }

    public String getStockToString(){
        String respuesta="";
        for (int i = 0; i < materiales.size(); i++) {
            MaterialesMarpico material = materiales.get(i);
            respuesta+="Color: "+material.getColor_nombre()+", Inventario: "+material.getInventario()+ " "+material.getStockTransito()+"\n";
        }
        return respuesta;
    }
}

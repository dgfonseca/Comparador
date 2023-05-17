package comparativo.mundo.model;

import java.math.BigDecimal;
import java.math.*;
import java.util.ArrayList;

import comparativo.mundo.response.StockPromopcionesResponse;

public class ProductoCompetencia {

    private String codigoHijo;
    private String codigoPadre;
    private String nombre;
    private double precioBase;
    private double descuento;
    private double descuento2;
    private String stockString;
    private int indicadorSubio;


    public ProductoCompetencia(String pCodigoHijo, String pCodigoPadre, String nombre, double precioBase){
        super();
        this.codigoHijo=pCodigoHijo;
        this.codigoPadre=pCodigoPadre;
        this.nombre=nombre;
        this.precioBase=precioBase;
        this.descuento=25;
        this.descuento2=0;
        this.indicadorSubio=0;
    }

    public ProductoCompetencia(String pCodigoHijo, String pCodigoPadre, String nombre, double precioBase, double pDescuento, double pDescuento2, String stockString){
        super();
        this.codigoHijo=pCodigoHijo;
        this.codigoPadre=pCodigoPadre;
        this.nombre=nombre;
        this.precioBase=precioBase;
        this.descuento=pDescuento;
        this.descuento2=pDescuento2;
        this.stockString=stockString;
        this.indicadorSubio=0;
    }
    
    public int getIndicadorSubio() {
        return this.indicadorSubio;
    }

    public void setIndicadorSubio(int indicadorSubio) {
        this.indicadorSubio = indicadorSubio;
    }

    public String getStockString() {
        return this.stockString;
    }

    public void setStockString(String stockString) {
        this.stockString = stockString;
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
        return Math.ceil(value);
    }
    
    public void processStock(ArrayList<StockPromopcionesResponse> stockResponse){

        this.stockString="";
        for (StockPromopcionesResponse stockPromopcionesResponse : stockResponse) {
            int inventarioTotal = (stockPromopcionesResponse.getStock_total());
            int inventarioTransito = 0;
            String fechaArriboBodega = "N/A";

            String html = stockPromopcionesResponse.getStock();
            String html2 = stockPromopcionesResponse.getPrice();

            int indexTransito = html.lastIndexOf("nsito a Bodega:");
            int indexFinTransito = html.indexOf("<br>", indexTransito);
            int indexInicioFechaArribo = html.indexOf("arribo a Bodega:",indexFinTransito);
            int indexFinFechaArribo = html.indexOf("\n",indexInicioFechaArribo);
    
            int indexInicioPrecioBase = html2.indexOf("$",html2.indexOf("Precio base:"));
            int indexFinPrecioBase = html2.indexOf("<",indexInicioPrecioBase);
            int indexInicioPrecioDescuento = html2.indexOf("$", html2.indexOf("Precio NIVEL:"));
            int indexFinPrecioDescuento = html2.indexOf("<",indexInicioPrecioDescuento);

            if(indexTransito>0&&indexFinTransito>0&&indexInicioFechaArribo>0&&indexFinFechaArribo>0){
                String inventarioTransitoString = html.substring(indexTransito+20, indexFinTransito);
                inventarioTransito = Integer.parseInt(inventarioTransitoString.replace(",", ""));
                fechaArriboBodega = html.substring(indexInicioFechaArribo+21,indexFinFechaArribo);
            }if(indexInicioPrecioBase>0&&indexFinPrecioBase>0){
                this.precioBase=Double.parseDouble(html2.substring(indexInicioPrecioBase+1, indexFinPrecioBase).replace(",", ""));
            }if(indexInicioPrecioDescuento>0&&indexFinPrecioDescuento>0){
                double precioDescuento = Double.parseDouble(html2.substring(indexInicioPrecioDescuento+1, indexFinPrecioDescuento).replace(",", ""));
                double descuento = 100-(precioDescuento*100/this.precioBase);
                this.descuento=descuento;
            }
            this.stockString+="Codigo: "+stockPromopcionesResponse.getCodigoHijo()+", Inventario Total: "+inventarioTotal+", Inventario Transito: "+inventarioTransito+", Fecha Arribo Bodega: "+fechaArriboBodega+"\n";
        }
    }
}

package comparativo.mundo.model;

public class Stock {

    private String referencia;
    private String color;
    private int bodegaLocal;
    private int bodegaZonaFranca;
    private int totalDisponible;
    private String llegadaBodegaLocal;
    private int cantidadTransito;
    private String estadoOrden;

    
    public Stock(String referencia, String color, int bodegaLocal, int bodegaZonaFranca, int totalDisponible,
            String llegadaBodegaLocal, int cantidadTransito, String estadoOrden) {
        this.referencia = referencia;
        this.color = color;
        this.bodegaLocal = bodegaLocal;
        this.bodegaZonaFranca = bodegaZonaFranca;
        this.totalDisponible = totalDisponible;
        this.llegadaBodegaLocal = llegadaBodegaLocal;
        this.cantidadTransito = cantidadTransito;
        this.estadoOrden = estadoOrden;
    }

    public Stock(){
        super();
    }

    public String getReferencia() {
        return this.referencia;
    }

    public void setReferencia(String referencia) {
        this.referencia = referencia;
    }

    public String getColor() {
        return this.color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public int getBodegaLocal() {
        return this.bodegaLocal;
    }

    public void setBodegaLocal(int bodegaLocal) {
        this.bodegaLocal = bodegaLocal;
    }

    public int getBodegaZonaFranca() {
        return this.bodegaZonaFranca;
    }

    public void setBodegaZonaFranca(int bodegaZonaFranca) {
        this.bodegaZonaFranca = bodegaZonaFranca;
    }

    public int getTotalDisponible() {
        return this.totalDisponible;
    }

    public void setTotalDisponible(int totalDisponible) {
        this.totalDisponible = totalDisponible;
    }

    public String getLlegadaBodegaLocal() {
        return this.llegadaBodegaLocal;
    }

    public void setLlegadaBodegaLocal(String llegadaBodegaLocal) {
        this.llegadaBodegaLocal = llegadaBodegaLocal;
    }

    public int getCantidadTransito() {
        return this.cantidadTransito;
    }

    public void setCantidadTransito(int cantidadTransito) {
        this.cantidadTransito = cantidadTransito;
    }

    public String getEstadoOrden() {
        return this.estadoOrden;
    }

    public void setEstadoOrden(String estadoOrden) {
        this.estadoOrden = estadoOrden;
    }

    @Override
    public String toString(){
        return "Referencia: "+ getReferencia() +", Color: "+getColor() + ", Total Disponible: "+ getTotalDisponible() 
        +", Llegada Bodega Local: " + getLlegadaBodegaLocal() +", Cantidad Transito: "+ getCantidadTransito();
    }
    
    
}

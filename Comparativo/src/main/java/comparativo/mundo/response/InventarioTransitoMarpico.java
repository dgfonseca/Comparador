package comparativo.mundo.response;

public class InventarioTransitoMarpico {
    private String estado;
    private String fecha;
    private int cantidad;
    private String unidad;
    private String ultima_actualizacion;
    
    public InventarioTransitoMarpico(){
        
    }
    public InventarioTransitoMarpico(String pEstado,String pUnidad,String pFecha, int pCantidad, String pActualizacion){
        this.fecha=pFecha;
        this.cantidad=pCantidad;
        this.ultima_actualizacion=pActualizacion;
        this.estado=pEstado;
        this.unidad=pUnidad;
    }
    public InventarioTransitoMarpico(String pFecha, int pCantidad, String pActualizacion){
        this.fecha=pFecha;
        this.cantidad=pCantidad;
        this.ultima_actualizacion=pActualizacion;
    }
    
    public String getUnidad() {
        return this.unidad;
    }

    public void setUnidad(String unidad) {
        this.unidad = unidad;
    }
    public String getEstado() {
        return this.estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
    public String getFecha() {
        return this.fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public int getCantidad() {
        return this.cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = cantidad;
    }

    public String getUltima_actualizacion() {
        return this.ultima_actualizacion;
    }

    public void setUltima_actualizacion(String ultima_actualizacion) {
        this.ultima_actualizacion = ultima_actualizacion;
    }


}

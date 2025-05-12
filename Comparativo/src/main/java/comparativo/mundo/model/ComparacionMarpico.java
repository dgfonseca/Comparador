package comparativo.mundo.model;

import java.util.Objects;

import comparativo.mundo.persistence.ProductosMarpico;

public class ComparacionMarpico {
    private ProductosMarpico productoCompetencia;
    private Producto productoPropio;
    private String fechaComparacion;
    private int numeroPrecio;
    private int numeroPrecioMarpico;

    public ComparacionMarpico(ProductosMarpico pProductosCompetencia, Producto pProductoPropio, String pFecha, int pNumeroPrecio, int pNumeroPrecioMarpico){
        this.productoCompetencia=Objects.requireNonNull(pProductosCompetencia);
        this.productoPropio=Objects.requireNonNull(pProductoPropio);
        this.fechaComparacion=pFecha;
        this.numeroPrecio=pNumeroPrecio;
        this.numeroPrecioMarpico=pNumeroPrecioMarpico;

    }

    public ProductosMarpico getProductoCompetencia() {
        return this.productoCompetencia;
    }

    public void setProductoCompetencia(ProductosMarpico productoCompetencia) {
        this.productoCompetencia = productoCompetencia;
    }

    public Producto getProductoPropio() {
        return this.productoPropio;
    }

    public void setProductoPropio(Producto productoPropio) {
        this.productoPropio = productoPropio;
    }

    public String getFechaComparacion() {
        return this.fechaComparacion;
    }

    public void setFechaComparacion(String fechaComparacion) {
        this.fechaComparacion = fechaComparacion;
    }

    public int getNumeroPrecio() {
        return this.numeroPrecio;
    }

    public void setNumeroPrecio(int numeroPrecio) {
        this.numeroPrecio = numeroPrecio;
    }

    public int getNumeroPrecioMarpico() {
        return this.numeroPrecioMarpico;
    }

    public void setNumeroPrecioMarpico(int numeroPrecio) {
        this.numeroPrecioMarpico = numeroPrecio;
    }
    public int compareTo(String referencia) {
        return this.productoPropio.getReferencia().compareTo(referencia);
    }
}

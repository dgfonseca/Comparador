package comparativo.mundo.response;

import java.util.ArrayList;

import comparativo.mundo.model.Stock;

public class StockResponse {
    private boolean hayError;
    private String mensajeError;
    private ArrayList<Stock> resultado;

    public boolean isHayError() {
        return this.hayError;
    }

    public void setHayError(boolean hayError) {
        this.hayError = hayError;
    }

    public String getMensajeError() {
        return this.mensajeError;
    }

    public void setMensajeError(String mensajeError) {
        this.mensajeError = mensajeError;
    }

    public ArrayList<Stock> getResultado() {
        return this.resultado;
    }

    public void setResultado(ArrayList<Stock> resultado) {
        this.resultado = resultado;
    }

    
    
}

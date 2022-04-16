package comparativo.mundo.response;

import java.util.ArrayList;

import comparativo.mundo.model.Producto;

public class ProductosResponse {

	private boolean hayError;
	private String mensajeError;
	private ArrayList<Producto> resultado;
	public boolean isHayError() {
		return hayError;
	}
	public void setHayError(boolean hayError) {
		this.hayError = hayError;
	}
	public String getMensajeError() {
		return mensajeError;
	}
	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}
	public ArrayList<Producto> getResultado() {
		return resultado;
	}
	public void setResultado(ArrayList<Producto> resultado) {
		this.resultado = resultado;
	}
	
}

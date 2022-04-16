package comparativo.mundo.response;

import java.util.ArrayList;

import comparativo.mundo.model.Categoria;

public class CategoriaResponse {
	
	private boolean hayError;
	private String mensajeError;
	private ArrayList<Categoria> resultado;
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
	public ArrayList<Categoria> getResultado() {
		return resultado;
	}
	public void setResultado(ArrayList<Categoria> resultado) {
		this.resultado = resultado;
	}
	
	

}

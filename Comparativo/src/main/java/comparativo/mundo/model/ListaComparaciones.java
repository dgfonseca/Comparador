package comparativo.mundo.model;

import java.util.ArrayList;
import java.util.Date;

public class ListaComparaciones {
	
	private ArrayList<Comparacion> listaComparaciones;
	
	public ListaComparaciones() {
		super();
		this.listaComparaciones = new ArrayList<>();
	}

	public ArrayList<Comparacion> getListaComparaciones() {
		return listaComparaciones;
	}

	public void setListaComparaciones(ArrayList<Comparacion> listaComparaciones) {
		this.listaComparaciones = listaComparaciones;
	}
	
	public void agregarComparacion(Producto productoA, ProductoCompetencia productoB, Date pFecha){
		listaComparaciones.add(new Comparacion(productoA, productoB, pFecha));
	}
	public boolean eliminarComparacion(Comparacion pComparacion){
		return listaComparaciones.remove(pComparacion);
	}
	
	public ArrayList<Comparacion> buscarComparacionPorReferencia(String pReferencia){
		ArrayList<Comparacion> respuesta=new ArrayList<>();
		for (int i = 0; i < listaComparaciones.size(); i++) {
			if(listaComparaciones.get(i).getProductoPropio().getReferencia().equalsIgnoreCase(pReferencia) || listaComparaciones.get(i).getProductoCompetencia().getCodigoHijo().equalsIgnoreCase(pReferencia) || listaComparaciones.get(i).getProductoCompetencia().getCodigoPadre().equalsIgnoreCase(pReferencia)){
				respuesta.add(listaComparaciones.get(i));
			}
		}
		return respuesta;
	}
	

}

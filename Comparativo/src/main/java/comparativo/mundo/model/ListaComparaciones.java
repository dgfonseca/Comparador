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
	
	public Comparacion agregarComparacion(Producto productoA, ProductoCompetencia productoB, Date pFecha, int numeroPrecio){
		Comparacion nuevo = new Comparacion(productoA, productoB, pFecha, numeroPrecio);
		listaComparaciones.add(nuevo);
		return nuevo;
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
	
	public ArrayList<Comparacion> getSortedList(){
	    return quickSort(this.listaComparaciones);
	}
	
	public ArrayList<Comparacion> quickSort(ArrayList<Comparacion> comparacion) {
	    if(!comparacion.isEmpty()) {
	        ArrayList<Comparacion> sorted;
	        ArrayList<Comparacion> smaller=new ArrayList<>();
	        ArrayList<Comparacion> greater=new ArrayList<>();
	        Comparacion pivot = comparacion.get(0);
	        int i;
	        Comparacion j;
	        for(i=1;i<comparacion.size();i++) {
	            j=comparacion.get(i);
	            if(j.compareTo(pivot.getProductoPropio().getReferencia())<0) {
	                smaller.add(j);
	            }else {
	                greater.add(j);
	            }
	        }
	        smaller=quickSort(smaller);
	        greater=quickSort(greater);
	        smaller.add(pivot);
	        smaller.addAll(greater);
	        sorted=smaller;
	        return sorted;
	    }
	    return comparacion;
	    
	}
}

package comparativo.mundo.model;

import java.util.ArrayList;

public class Categoria {

	private String nombre;
	private int id;
	private ArrayList<Producto> productos;
	
	public Categoria(String pNombre, int pId){
		this.nombre=pNombre;
		this.id=pId;
		this.productos=new ArrayList<>();

	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public ArrayList<Producto> getProductos() {
		return productos;
	}

	public void setProductos(ArrayList<Producto> productos) {
		this.productos = productos;
	}

	public Producto buscarProductoPorReferencia(String pReferencia){
		Producto respuesta = null;
		boolean termino = false;
		for (int i = 0; i < productos.size() && !termino; i++) {
			if(productos.get(i).getReferencia().equalsIgnoreCase(pReferencia)){
				respuesta = productos.get(i);
			}
		}
		return respuesta;
	}
	
	
}

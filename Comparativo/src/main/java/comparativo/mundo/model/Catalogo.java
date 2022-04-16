package comparativo.mundo.model;

import java.util.ArrayList;

public class Catalogo {
	
	private ArrayList<Categoria> catalogo;
	
	public Catalogo() {
		this.catalogo=new ArrayList<>();
	}
	
	public Catalogo(ArrayList<Categoria> pCatalogo){
		this.catalogo=pCatalogo;
	}

	public ArrayList<Categoria> getCatalogo() {
		return catalogo;
	}

	public void setCatalogo(ArrayList<Categoria> catalogo) {
		this.catalogo = catalogo;
	}
	
	
	
	

}

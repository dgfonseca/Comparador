package comparativo.mundo.response;

import java.util.ArrayList;

import comparativo.mundo.persistence.ProductosMarpico;

public class CatalogoMarpico {
    
    private ArrayList<ProductosMarpico> results;

    public ArrayList<ProductosMarpico> getResults() {
        return this.results;
    }

    public void setResults(ArrayList<ProductosMarpico> results) {
        this.results = results;
    }
}

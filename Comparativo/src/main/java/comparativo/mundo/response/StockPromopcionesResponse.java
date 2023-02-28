package comparativo.mundo.response;

public class StockPromopcionesResponse {

    private String codigoHijo;
    private int stock_total;
    private String stock;
    private String price;
    
    public String getPrice() {
        return this.price;
    }
    
    public String getCodigoHijo() {
        return this.codigoHijo;
    }

    public void setCodigoHijo(String codigoHijo) {
        this.codigoHijo = codigoHijo;
    }
    public void setPrice(String price) {
        this.price = price;
    }

    public int getStock_total() {
        return this.stock_total;
    }

    public void setStock_total(int stock_total) {
        this.stock_total = stock_total;
    }

    public String getStock() {
        return this.stock;
    }

    public void setStock(String stock) {
        this.stock = stock;
    }


    
}

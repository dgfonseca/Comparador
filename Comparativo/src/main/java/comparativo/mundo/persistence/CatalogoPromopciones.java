package comparativo.mundo.persistence;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import com.google.gson.Gson;

import comparativo.mundo.model.ProductoCompetencia;
import comparativo.mundo.response.StockPromopcionesResponse;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.client.Entity;
import jakarta.ws.rs.core.Form;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.NewCookie;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.RuntimeDelegate;

public class CatalogoPromopciones {

    private Connection connection;

    public CatalogoPromopciones(Connection connection) {
        this.connection = connection;
    }

    public int insertProducto(String codigo_hijo,String codigo_padre,String nombre,Double precio, String stock) throws SQLException{
        PreparedStatement ps = this.connection.prepareStatement("INSERT INTO productos_competencia(codigo_hijo,codigo_padre,nombre,precio,stock_string) "+
        "VALUES(?,?,?,?,?) ON CONFLICT(codigo_hijo) DO UPDATE SET precio = ?, stock_string=?");
        ps.setString(1, codigo_hijo);
		ps.setString(2, codigo_padre);
		ps.setString(3, nombre);
		ps.setDouble(4, precio);
		ps.setString(5, stock);
		ps.setDouble(6, precio);
		ps.setString(7, stock);

        return ps.executeUpdate();
    }

    public int updateProductosCompetencia(Double precio, String referencia, String stockString)throws SQLException{
        PreparedStatement ps = connection.prepareStatement("UPDATE productos_competencia SET precio = ?, stock_string = ?"+
				" WHERE codigo_hijo = ? AND (precio != ? OR stock_string != ?)");
        
        System.out.println(stockString);
            ps.setDouble(1, precio);
            ps.setString(2, stockString);
            ps.setString(3, referencia);
            ps.setDouble(4, precio);
            ps.setString(5, stockString);
		return ps.executeUpdate();
	}

    public ArrayList<StockPromopcionesResponse> getStockInformation(String codigoPadre, ArrayList<ProductoCompetencia> productosCompetencia){
        ResteasyClient client=(ResteasyClient) ClientBuilder.newClient();
		ResteasyWebTarget target = client.target("https://www.promoopcioncolombia.co/validaLogin.php?op=0&psw=OP81570M");
		Response response = target.request().get();
        NewCookie cookieResponse = response.getCookies().get("PHPSESSID");
        String cookieString = RuntimeDelegate.getInstance().createHeaderDelegate(NewCookie.class).toString(cookieResponse);
		target=client.target("https://www.promoopcioncolombia.co/item/plp_price.php");
        Form requestForm = new Form();
        ArrayList<StockPromopcionesResponse> stockList = new ArrayList<>();
        for (int i = 0; i < productosCompetencia.size(); i++) {
            if(productosCompetencia.get(i).getCodigoPadre().equals(codigoPadre)){
                requestForm.param("parent_code", codigoPadre).param("item_code", productosCompetencia.get(i).getCodigoHijo()).param("cat", "").param("p10", "").param("p35", "");
                Entity<Form> request = Entity.form(requestForm);
                String responseString = target.request(MediaType.APPLICATION_FORM_URLENCODED).header("cookie", cookieString).post(request,String.class);
                StockPromopcionesResponse stockPromopciones = new Gson().fromJson(responseString, StockPromopcionesResponse.class);
                stockPromopciones.setCodigoHijo(productosCompetencia.get(i).getCodigoHijo());
                stockList.add(stockPromopciones);
            }
        }
        return stockList;
    }
    
}

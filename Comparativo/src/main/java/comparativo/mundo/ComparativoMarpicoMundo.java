package comparativo.mundo;

import java.sql.Connection;

import com.google.gson.Gson;

import org.apache.commons.math3.stat.descriptive.summary.Product;
import org.jboss.resteasy.client.jaxrs.ResteasyClient;
import org.jboss.resteasy.client.jaxrs.ResteasyWebTarget;

import comparativo.mundo.model.Catalogo;
import comparativo.mundo.response.CatalogoMarpico;
import comparativo.mundo.response.ProductosMarpico;
import jakarta.ws.rs.client.ClientBuilder;
import jakarta.ws.rs.core.MediaType;

public class ComparativoMarpicoMundo {

    private ResteasyClient client;
    private CatalogoMarpico catalogo;
    private Connection connection;



    public ComparativoMarpicoMundo(Connection pConnection){
        this.catalogo = null;
        this.connection=pConnection;
    }

    public ProductosMarpico buscarProductoPorFamilia(String pFamilia){
        ProductosMarpico rta = null;
        client=(ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target("https://marpicoprod.azurewebsites.net/api/inventarios/materialesAPIByProducto?producto="+pFamilia);
		String response = target.request(MediaType.APPLICATION_JSON).header("Authorization", "Api-Key ijCDQW5tFfEQPjqlZVfqyrT0grs938KRPOTictTvB6EKzXPProgJpTnOFcR3HO8R").get(String.class);
        response = response.substring(1, response.length() - 1);
        rta = new Gson().fromJson(response, ProductosMarpico.class);
        return rta;
    }

    public CatalogoMarpico cargarCatalogoMarpico(){
        client=(ResteasyClient) ClientBuilder.newClient();
        ResteasyWebTarget target = client.target("https://marpicoprod.azurewebsites.net/api/inventarios/materialesAPI");
		String response = target.request(MediaType.APPLICATION_JSON).header("Authorization", "Api-Key ijCDQW5tFfEQPjqlZVfqyrT0grs938KRPOTictTvB6EKzXPProgJpTnOFcR3HO8R").get(String.class);
        catalogo = new Gson().fromJson(response, CatalogoMarpico.class);
        return catalogo;
    }


    
}

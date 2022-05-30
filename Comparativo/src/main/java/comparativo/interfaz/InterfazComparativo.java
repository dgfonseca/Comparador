package comparativo.interfaz;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.*;
import java.io.IOException;
import java.net.UnknownHostException;

import comparativo.mundo.ComparativoMarpicoMundo;
import comparativo.mundo.ComparativoMundo;
import comparativo.mundo.model.Catalogo;
import comparativo.mundo.model.Categoria;
import comparativo.mundo.model.Comparacion;
import comparativo.mundo.model.ListaComparaciones;
import comparativo.mundo.model.Producto;
import comparativo.mundo.model.ProductoCompetencia;
import comparativo.mundo.response.CatalogoMarpico;
import comparativo.mundo.response.ProductosMarpico;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.ProcessingException;

public class InterfazComparativo extends JFrame{

    private PanelMenuOpciones panelMenuOpciones;
    private PanelCredenciales panelCredenciales;
    private PanelListaCategoriasPropios panelCategorias;
    private PanelListaProductosPropios panelProductosPropios;
    private ComparativoMundo comparativo;
    private Categoria categoriaActual;
    private Producto productoPropioSeleccionado;
    private Comparacion comparacionActual;
    private ListaComparaciones listaComparaciones;
    private ArrayList<ProductoCompetencia> productosCompetencia;
    private ProductoCompetencia productoCompetenciaSeleccionado;
    private PanelListaProductosCompetencia panelProductosCompetencia;
    private PanelProductosSeleccionados panelProductosSeleccionados;
    private PanelMenuOpcionesProductosSeleccionados panelMenuOpcionesProductosSeleccionados;
    private PanelListaComparaciones panelListaComparaciones;
    private PanelComparacionSeleccionada panelComparacionSeleccionada;
    private PanelMenuOpcionesComparacion panelMenuOpcionesComparacion;
    private CardLayout card;
    private JPanel cardPane;
    private JPanel panelIzquierdo;
    private JPanel panelDerecha;
    private boolean estaConectadoBaseDeDatos;
    private boolean esHistorico;

    InterfazComparativo(){
        setIconImage(new ImageIcon(getClass().getResource("Imagenes/Logo.jpg")).getImage());
        esHistorico=false;
        categoriaActual=null;
        productosCompetencia=null;
        productoCompetenciaSeleccionado=null;
        comparacionActual=null;
        listaComparaciones=null;
        estaConectadoBaseDeDatos=false;
        panelCredenciales = new PanelCredenciales(true,this);
        panelCredenciales.setVisible(true);


        panelMenuOpciones=new PanelMenuOpciones(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Catalogos Promocionales");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setResizable(true);
        setLayout(new BorderLayout());
        add(panelMenuOpciones,BorderLayout.NORTH);

        panelCategorias = new PanelListaCategoriasPropios(this);
        panelProductosPropios = new PanelListaProductosPropios(this);
        panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new GridLayout(2,1));
        panelIzquierdo.add(panelCategorias);
        panelIzquierdo.add(panelProductosPropios);
        add(panelIzquierdo,BorderLayout.WEST);

        panelProductosCompetencia = new PanelListaProductosCompetencia(this);
        panelDerecha = new JPanel();
        panelDerecha.setLayout(new BorderLayout());
        panelDerecha.add(panelProductosCompetencia, BorderLayout.CENTER);
        add(panelDerecha,BorderLayout.EAST);

        panelProductosSeleccionados = new PanelProductosSeleccionados();
        panelMenuOpcionesComparacion = new PanelMenuOpcionesComparacion(this);
        panelMenuOpcionesProductosSeleccionados = new PanelMenuOpcionesProductosSeleccionados(this);
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new GridLayout(4,1));
        JPanel panelCentro22 = new JPanel(new BorderLayout());
        panelCentro22.add(panelProductosSeleccionados, BorderLayout.CENTER);
        panelCentro22.add(panelMenuOpcionesProductosSeleccionados, BorderLayout.SOUTH);
        panelCentro.add(panelCentro22);


        cardPane = new JPanel();
        card=new CardLayout();
        cardPane.setLayout(card);
        cardPane.add(panelCentro,"Productos");
        
        panelListaComparaciones = new PanelListaComparaciones(this);
        panelComparacionSeleccionada = new PanelComparacionSeleccionada(this);
        JPanel panelCentro2 = new JPanel();
        panelCentro2.setLayout(new BorderLayout());
        panelCentro2.add(panelListaComparaciones,BorderLayout.CENTER);
        panelCentro2.add(panelComparacionSeleccionada, BorderLayout.NORTH);
        panelCentro2.add(panelMenuOpcionesComparacion, BorderLayout.SOUTH);
        cardPane.add(panelCentro2, "Comparaciones");


        add(cardPane,BorderLayout.CENTER);
        pack();

    }

    public Producto getProductoPropioSeleccionado(){
        return this.productoPropioSeleccionado;
    }

    public void setEsHistorico(boolean pHistorico){
        this.esHistorico=pHistorico;
    }
    public void exportarComparaciones(String path, boolean esHistorico){
        try {
            path=path.replace("\\", "/");
            comparativo.exportarCsv(path+"/"+new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()),esHistorico);
            
        } catch (IOException e) {
        	e.printStackTrace();
            JOptionPane.showMessageDialog(new JFrame(), "No se pudo exportar el archivo", "Error",
            JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarDescuentos(double descuentoPropio, double descuentoCompetencia, double descuentoPropio2, double descuentoCompetencia2){
        if(comparacionActual==null){
            JOptionPane.showMessageDialog(new JFrame(), "Debe seleccionar una comparacion para modificar sus descuentos", "Advertencia",
            JOptionPane.WARNING_MESSAGE);
        }else if(esHistorico) {
        	JOptionPane.showMessageDialog(new JFrame(), "Debe actualizar las comparaciones parametrizadas, no el historico", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
        else{
            ListaComparaciones lista = comparativo.obtenerListaComparaciones();
            Comparacion actual;
            boolean termino = false;
            for(int i = 0; i<lista.getListaComparaciones().size()&&!termino;i++){
                actual = lista.getListaComparaciones().get(i);
                if(actual.equals(comparacionActual)||(actual.getProductoCompetencia().equals(comparacionActual.getProductoCompetencia()) && actual.getProductoPropio().equals(comparacionActual.getProductoPropio()))){
                    if(descuentoPropio>=0 && descuentoCompetencia>=0){
                        actual.getProductoPropio().setDescuento(descuentoPropio);
                        actual.getProductoCompetencia().setDescuento(descuentoCompetencia);
                    }
                    if(descuentoPropio>=0 && descuentoCompetencia<0){
                        actual.getProductoPropio().setDescuento(descuentoPropio);
                    }
                    if(descuentoPropio<0 && descuentoCompetencia>=0){
                        actual.getProductoCompetencia().setDescuento(descuentoCompetencia);
                    }if(descuentoPropio2>=0 && descuentoCompetencia2>=0){
                        actual.getProductoPropio().setDescuento2(descuentoPropio2);
                        actual.getProductoCompetencia().setDescuento2(descuentoCompetencia2);
                    }
                    if(descuentoPropio2>=0 && descuentoCompetencia2<0){
                        actual.getProductoPropio().setDescuento2(descuentoPropio2);
                    }
                    if(descuentoPropio2<0 && descuentoCompetencia2>=0){
                        actual.getProductoCompetencia().setDescuento2(descuentoCompetencia2);
                    }
                    boolean rta;
                    try {
                        rta = comparativo.actualizarDescuentoComparacion(actual, descuentoPropio, descuentoCompetencia,descuentoPropio2, descuentoCompetencia2, estaConectadoBaseDeDatos);
                        if(rta){
                            JOptionPane.showMessageDialog(new JFrame(), "Actualizado correctamente", "Informacion",
                            JOptionPane.INFORMATION_MESSAGE);
                            termino=true;
                        }else{
                            JOptionPane.showMessageDialog(new JFrame(), "No se actualizo en la base de datos", "Advertencia",
                            JOptionPane.WARNING_MESSAGE);
                        }
                    } catch (SQLException e) {
                        e.printStackTrace();
                        JOptionPane.showMessageDialog(new JFrame(), "Error de conexion con la base de datos", "ERROR",
                        JOptionPane.ERROR_MESSAGE);
                    }
                    
                }
            }
            if(esHistorico){
                obtenerHistoricoComparaciones();
            }else{
                refrescarListaComparaciones();
            }
        }
        refrescarComparacionSeleccionada();
    }


    public void obtenerComparacionesGuardadas(){
        try{
            comparativo.obtenerListaComparacionesBaseDeDatos();
            refrescarListaComparaciones();
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error de base de datos, los datos no se almacenaran en la base de datos", "Advertencia", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void actualizarListaComparaciones(){
        try {
        	if(comparativo.obtenerCatalogoCompetencia()==null) {
                JOptionPane.showMessageDialog(new JFrame(), "Debe cargar el excel de la competencia", "Error", JOptionPane.ERROR_MESSAGE);
        	}else {
	            comparativo.actualizarComparaciones();
	            comparacionActual=null;
	            refrescarComparacionSeleccionada();
	            obtenerComparacionesGuardadas();
	            JOptionPane.showMessageDialog(new JFrame(), "Termino de calcular comparacion", "INFO", JOptionPane.INFORMATION_MESSAGE);
        	}
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error al comunicarse con el api, por favor verificar su conexion o el acceso al api", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error de base de datos, los datos no se almacenaran en la base de datos", "Advertencia", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void limpiarProductosSeleccionados(){
        productoPropioSeleccionado=null;
        productoCompetenciaSeleccionado=null;
        refrescarProductosSeleccionados();
    }

    public void limpiarComparacionSeleccionada(){
        comparacionActual=null;
        refrescarComparacionSeleccionada();
    }
    public void actualizarComparacionSeleccionada(String referencia, String codigoHijo, String fecha, int numeroPrecio){
        comparacionActual = comparativo.obtenerComparacionPorReferencias(referencia, codigoHijo, numeroPrecio, fecha, esHistorico);
        refrescarComparacionSeleccionada();
    }
    public void crearComparacion(int numeroPrecio){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        java.util.Date date = new java.util.Date();
        if(productoPropioSeleccionado!=null && productoCompetenciaSeleccionado!=null){
            try {
                Comparacion rta = comparativo.crearComparacion(productoPropioSeleccionado, productoCompetenciaSeleccionado, formatter.format(date),estaConectadoBaseDeDatos, numeroPrecio);
                if(rta==null){
                    JOptionPane.showMessageDialog(new JFrame(), "Ya existe una comparacion con dichos productos", "Advertencia",
            JOptionPane.WARNING_MESSAGE);
                }else{
                    comparacionActual=rta;
                    listaComparaciones=comparativo.obtenerListaComparaciones();
                    panelListaComparaciones.refrescar(listaComparaciones);
                    JOptionPane.showMessageDialog(new JFrame(), "Comparacion agregada exitosamente", "Informacion",
            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error al crear comparacion", "Error",
            JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(new JFrame(), "Debe seleccionar un producto propio y uno de la competencia", "Advertencia",
            JOptionPane.WARNING_MESSAGE);
        }
    }

    public void eliminarComparacion(){
        if(comparacionActual!=null){
            try {
                boolean rta = comparativo.eliminarComparacion(comparacionActual,estaConectadoBaseDeDatos,esHistorico);
                if(rta){
                    JOptionPane.showMessageDialog(new JFrame(), "Comparacion eliminada exitosamente", "Informacion",
            JOptionPane.INFORMATION_MESSAGE);
                    comparacionActual=null;
                    
                    if(esHistorico){
                        panelListaComparaciones.refrescar(comparativo.obtenerHistoricoComparaciones());
                        refrescarComparacionSeleccionada();
                    }else{
                        listaComparaciones=comparativo.obtenerListaComparaciones();
                        panelListaComparaciones.refrescar(listaComparaciones);
                        refrescarComparacionSeleccionada();
                    }
                }
                else{
                    JOptionPane.showMessageDialog(new JFrame(), "No se pudo eliminar la comparacion", "Advertencia",
            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(new JFrame(), "Error al eliminar comparacion", "Error",
            JOptionPane.ERROR_MESSAGE);
            }
        }else{
            JOptionPane.showMessageDialog(new JFrame(), "Seleccione una comparacion a eliminar", "Informacion",
            JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void cambiarPanelComparaciones(){
        CardLayout layout = (CardLayout) cardPane.getLayout();
        panelIzquierdo.setVisible(false);
        panelDerecha.setVisible(false);
        obtenerComparacionesGuardadas();
        layout.show(cardPane,"Comparaciones");
    }
    public void cambiarPanelProductos(){
        CardLayout layout = (CardLayout) cardPane.getLayout();
        panelIzquierdo.setVisible(true);
        panelDerecha.setVisible(true);
        layout.show(cardPane,"Productos");
    }

    public boolean inicializarConexion(String uri, String usuario, String contrasena){
        try {
            comparativo = new ComparativoMundo(usuario, contrasena, uri);
            estaConectadoBaseDeDatos=true;
            refrescarCategorias();
            return true;
        } catch(SQLException e){
            if(e.getMessage().contains("autentificaci") && e.getMessage().contains("password")){
                JOptionPane.showMessageDialog(new JFrame(), "Credenciales Invalidas", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }else{
                try {
                    comparativo = new ComparativoMundo();
                } catch (UnknownHostException e1) {
                    JOptionPane.showMessageDialog(new JFrame(), "Error al comunicarse con el api, por favor verificar su conexion o el acceso al api", "Error", JOptionPane.ERROR_MESSAGE);
                }
                estaConectadoBaseDeDatos=false;
                refrescarCategorias();
                JOptionPane.showMessageDialog(new JFrame(), "Error de base de datos, los datos no se almacenaran en la base de datos", "Advertencia", JOptionPane.ERROR_MESSAGE);
                return true;
            }
        }
        catch (ForbiddenException e){
            JOptionPane.showMessageDialog(new JFrame(), "Error al comunicarse con el api, por favor verificar su conexion o el acceso al api", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch(ProcessingException | UnknownHostException e){
            JOptionPane.showMessageDialog(new JFrame(), "Verifique su conexion a internet", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void refrescarComparacionSeleccionada(){
        panelComparacionSeleccionada.refrescar(comparacionActual);
    }

    public void refrescarCategoriaActual(Categoria pCategoria){
         categoriaActual = pCategoria;
         try{
         comparativo.obtenerProductosCategoria(categoriaActual);
         }catch(Exception e){
            JOptionPane.showMessageDialog(new JFrame(), "Error de conexion al API", "Advertencia", JOptionPane.ERROR_MESSAGE);
         }
    }

    public void refrescarListaComparaciones(){
        panelListaComparaciones.refrescar(comparativo.obtenerListaComparaciones());
}

    public ArrayList<ProductoCompetencia> obtenerProductosCompetencia(){
        return productosCompetencia;
    }
    
    public Categoria obtenerCategoriaActual(){
        return categoriaActual;
    }
    
    public Catalogo obtenerCategorias(){
        try{
        Catalogo comp = comparativo.obtenerCatalogoPropio();
        refrescarCategorias();
        return comp;
        }catch(Exception e){
            JOptionPane.showMessageDialog(new JFrame(), "Error de conexion al API", "Advertencia", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void refrescarCategorias(){
        try{
        panelCategorias.refrescar(comparativo.obtenerCatalogoPropioSinRefrescar());
        }catch(NullPointerException e){
            JOptionPane.showMessageDialog(new JFrame(), "Error de conexion al API", "Advertencia", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void refrescarProductosSeleccionados(){
        panelProductosSeleccionados.refrescar(productoPropioSeleccionado, productoCompetenciaSeleccionado, categoriaActual);
    }
    public void refrescarProductos(){
        panelProductosPropios.refrescar(categoriaActual);
    }
    public void refrescarProductosCompetencia(){
        panelProductosCompetencia.refrescar(productosCompetencia);
    }
    public void setProductoPropio(Producto pProducto){
        productoPropioSeleccionado=pProducto;
        refrescarProductosSeleccionados();
    }
    public void setProductoCompetencia(ProductoCompetencia pProducto){
        productoCompetenciaSeleccionado=pProducto;
        refrescarProductosSeleccionados();
    }
    
    public void obtenerHistoricoComparaciones(){
        try {
            if(estaConectadoBaseDeDatos){
                comparativo.obtenerHistoricoComparaciones(estaConectadoBaseDeDatos);
                panelListaComparaciones.refrescar(comparativo.obtenerHistoricoComparaciones(estaConectadoBaseDeDatos));
            }else{
                JOptionPane.showMessageDialog(new JFrame(), "No esta conectado a la base de datos", "Advertencia", JOptionPane.WARNING_MESSAGE);
            }
        } catch (SQLException | ParseException | NullPointerException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(new JFrame(), "Error al consultar en la base de datos historica", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void cargarProductosCompetencia(String path){
        try {
            productosCompetencia=comparativo.obtenerInformacionExcelCompetencia(path);
            refrescarProductosCompetencia();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error al cargar excel", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public void buscarProductoPorReferencia(String pReferencia){
        try{
            productoPropioSeleccionado=comparativo.obtenerProductoPorReferencia(pReferencia);
            categoriaActual=comparativo.getCategoriaPorReferencia();
            if(productoPropioSeleccionado == null){
                JOptionPane.showMessageDialog(new JFrame(), "No se encontro el producto con referencia: "+pReferencia, "Informacion", JOptionPane.INFORMATION_MESSAGE);
            }else {
            	refrescarProductosSeleccionados();
            }
        }catch(NullPointerException | UnknownHostException | ProcessingException e){
            JOptionPane.showMessageDialog(new JFrame(), "Error de conexion al API", "Advertencia", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean getEsHistorico(){
        return esHistorico;
    }

	
	public static void main( String[] args ) 
    {
        
        // javax.swing.SwingUtilities.invokeLater(new Runnable() {
        //     public void run() {
        //         new InterfazComparativo();            }
        // });

        ComparativoMarpicoMundo comp = new ComparativoMarpicoMundo(null);
        ProductosMarpico prod = comp.buscarProductoPorFamilia("GO0023");
        prod.setPrecio(prod.getMateriales().get(0).getPrecio());
        System.out.println(prod.getPrecioDescuento1());
        System.out.println(prod.getPrecioDescuento2());

        
    }

    

}

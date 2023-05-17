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
import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLSession;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import java.awt.*;
import java.io.IOException;
import java.net.UnknownHostException;

import comparativo.mundo.ComparativoMarpicoMundo;
import comparativo.mundo.ComparativoMundo;
import comparativo.mundo.model.Catalogo;
import comparativo.mundo.model.Categoria;
import comparativo.mundo.model.Comparacion;
import comparativo.mundo.model.ComparacionMarpico;
import comparativo.mundo.model.ListaComparaciones;
import comparativo.mundo.model.Producto;
import comparativo.mundo.model.ProductoCompetencia;
import comparativo.mundo.persistence.ProductosMarpico;
import jakarta.mail.MessagingException;
import jakarta.ws.rs.ForbiddenException;
import jakarta.ws.rs.ProcessingException;

public class InterfazComparativo extends JFrame{

    private PanelMenuOpciones panelMenuOpciones;
    private PanelCredenciales panelCredenciales;
    private PanelListaCategoriasPropios panelCategorias;
    private PanelListaProductosPropios panelProductosPropios;
    private ComparativoMundo comparativo;
    private ComparativoMarpicoMundo comparativoMarpico;
    private Categoria categoriaActual;
    private Producto productoPropioSeleccionado;
    private Comparacion comparacionActual;
    private ListaComparaciones listaComparaciones;
    private ArrayList<ComparacionMarpico> listaComparacionesMarpico;
    private ArrayList<ProductoCompetencia> productosPromopciones;
    private ArrayList<ProductosMarpico> productosMarpico;
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
    private String comparador;
    private ProductosMarpico productoMarpicoSeleccionado;
    private ComparacionMarpico comparacionActualMarpico;
    private String email;

    private final static String PROMOPCIONES="Promopciones";
    private final static String MARPICO="Marpico";

    InterfazComparativo(){
        setIconImage(new ImageIcon(getClass().getResource("Imagenes/Logo.jpg")).getImage());
        esHistorico=false;
        categoriaActual=null;
        productosPromopciones=null;
        productosMarpico=null;
        productoCompetenciaSeleccionado=null;
        productoMarpicoSeleccionado=null;
        comparacionActual=null;
        comparacionActualMarpico=null;
        listaComparaciones=null;
        estaConectadoBaseDeDatos=false;
        panelCredenciales = new PanelCredenciales(true,this);
        panelCredenciales.setVisible(true);
        this.email=null;


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

    public void setEmail(String pEmail){
        this.email=pEmail;
    }
    public String getComparador(){
        return this.comparador;
    }
    public Producto getProductoPropioSeleccionado(){
        return this.productoPropioSeleccionado;
    }

    public ProductosMarpico getProductoMarpicoSeleccionado(){
        return this.productoMarpicoSeleccionado;
    }

    public void setEsHistorico(boolean pHistorico){
        this.esHistorico=pHistorico;
    }
    public void exportarComparaciones(String path, boolean esHistorico){
        try {
            path=path.replace("\\", "/");
            if(this.comparador.equalsIgnoreCase(PROMOPCIONES)){
                comparativo.exportarCsv(path+"/"+new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()),esHistorico);
            }else if(this.comparador.equalsIgnoreCase(MARPICO)){
                comparativoMarpico.exportarCsv(path+"/"+new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(new Date()),esHistorico);
            }
            
        } catch (IOException e) {
        	e.printStackTrace();
            JOptionPane.showMessageDialog(new JFrame(), "No se pudo exportar el archivo", "Error",
            JOptionPane.ERROR_MESSAGE);
        }
    }

    public void actualizarDescuentos(double descuentoPropio, double descuentoCompetencia, double descuentoPropio2, double descuentoCompetencia2){
        if(comparacionActual==null&&comparacionActualMarpico==null){
            JOptionPane.showMessageDialog(new JFrame(), "Debe seleccionar una comparacion para modificar sus descuentos", "Advertencia",
            JOptionPane.WARNING_MESSAGE);
        }else if(esHistorico) {
        	JOptionPane.showMessageDialog(new JFrame(), "Debe actualizar las comparaciones parametrizadas, no el historico", "Advertencia",
                    JOptionPane.WARNING_MESSAGE);
        }
        else{
            boolean rta=false;
            if(this.comparador.equalsIgnoreCase(PROMOPCIONES)){
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
                        try {
                            rta = comparativo.actualizarDescuentoComparacion(actual, descuentoPropio, descuentoCompetencia,descuentoPropio2, descuentoCompetencia2, estaConectadoBaseDeDatos);
                            termino=true;
                        } catch (SQLException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(new JFrame(), "Error de conexion con la base de datos", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                        }
                        
                    }
                }
            }else if(this.comparador.equalsIgnoreCase(MARPICO)){
                ArrayList<ComparacionMarpico> listaMarpico = comparativoMarpico.getComparaciones();
                ComparacionMarpico actual;
                boolean termino = false;
                for(int i = 0; i<listaMarpico.size()&&!termino;i++){
                    actual = listaMarpico.get(i);
                    if(actual.equals(comparacionActualMarpico)||(actual.getProductoCompetencia().equals(comparacionActualMarpico.getProductoCompetencia()) && actual.getProductoPropio().equals(comparacionActualMarpico.getProductoPropio()))){
                        if(descuentoPropio>=0 && descuentoCompetencia>=0){
                            actual.getProductoPropio().setDescuento(descuentoPropio);
                            actual.getProductoCompetencia().setDescuento1(descuentoCompetencia);
                        }
                        if(descuentoPropio>=0 && descuentoCompetencia<0){
                            actual.getProductoPropio().setDescuento(descuentoPropio);
                        }
                        if(descuentoPropio<0 && descuentoCompetencia>=0){
                            actual.getProductoCompetencia().setDescuento1(descuentoCompetencia);
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
                        try {
                            rta = comparativoMarpico.actualizarDescuentoComparacion(actual, descuentoPropio, descuentoCompetencia,descuentoPropio2, descuentoCompetencia2);
                            termino=true;
                        } catch (SQLException e) {
                            e.printStackTrace();
                            JOptionPane.showMessageDialog(new JFrame(), "Error de conexion con la base de datos", "ERROR",
                            JOptionPane.ERROR_MESSAGE);
                        }
                        
                    }
                }
            }
            if(rta){
                JOptionPane.showMessageDialog(new JFrame(), "Actualizado correctamente", "Informacion",
                JOptionPane.INFORMATION_MESSAGE);
            }else{
                JOptionPane.showMessageDialog(new JFrame(), "No se actualizo en la base de datos", "Advertencia",
                JOptionPane.WARNING_MESSAGE);
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
            if(this.comparador.equalsIgnoreCase(PROMOPCIONES)){
                comparativo.obtenerListaComparacionesBaseDeDatos();
            }else if(this.comparador.equalsIgnoreCase(MARPICO)){
                comparativoMarpico.obtenerComparacionesBaseDeDatos();
            }
            refrescarListaComparaciones();
        }
        catch (SQLException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error de base de datos, los datos no se almacenaran en la base de datos", "Advertencia", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public String textoEmail(ArrayList<Producto> productosCambiaron, ArrayList<ProductoCompetencia>promopcionesCambiaron,ArrayList<ProductosMarpico> marpicoProductos ){
        return "";
    }

    public void actualizarListaComparaciones(){
        try {
            if(this.comparador.equals(PROMOPCIONES)){
                if(comparativo.obtenerCatalogoCompetencia()==null) {
                    JOptionPane.showMessageDialog(new JFrame(), "Debe cargar el excel de la competencia", "Error", JOptionPane.ERROR_MESSAGE);
                }else {
                    comparativo.actualizarComparaciones();
                    comparacionActual=null;
                    refrescarComparacionSeleccionada();
                    obtenerComparacionesGuardadas();
                    String texto = comparativo.enviarEmail(email);
                    JOptionPane.showMessageDialog(new JFrame(), texto, "INFO", JOptionPane.INFORMATION_MESSAGE);
                }   
            } else if(this.comparador.equalsIgnoreCase(MARPICO)){
                    comparativoMarpico.actualizarComparaciones();
                    comparacionActualMarpico=null;
                    refrescarComparacionSeleccionada();
                    obtenerComparacionesGuardadas();
                    String texto = comparativoMarpico.enviarEmail(email);
                    JOptionPane.showMessageDialog(new JFrame(), texto, "INFO", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (UnknownHostException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error al comunicarse con el api, por favor verificar su conexion o el acceso al api", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error de base de datos, los datos no se almacenaran en la base de datos", "Advertencia", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        } catch (MessagingException e){
            JOptionPane.showMessageDialog(new JFrame(), "Error al enviar el correo electronico, contacte al administrador", "Advertencia", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void limpiarProductosSeleccionados(){
        productoPropioSeleccionado=null;
        productoCompetenciaSeleccionado=null;
        productoMarpicoSeleccionado=null;
        refrescarProductosSeleccionados();
    }

    public void limpiarComparacionSeleccionada(){
        comparacionActual=null;
        refrescarComparacionSeleccionada();
    }
    public void actualizarComparacionSeleccionada(String referencia, String codigoHijo, String fecha, int numeroPrecio){
        if(this.comparador.equalsIgnoreCase(PROMOPCIONES)){
            comparacionActual = comparativo.obtenerComparacionPorReferencias(referencia, codigoHijo, numeroPrecio, fecha, esHistorico);
        }else if(this.comparador.equalsIgnoreCase(MARPICO)){
            comparacionActualMarpico = comparativoMarpico.obtenerComparacionPorReferencias(referencia,codigoHijo,numeroPrecio,fecha,esHistorico);
        }
        refrescarComparacionSeleccionada();
    }
    public void crearComparacion(int numeroPrecio,int numeroMaterial){
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
                    panelListaComparaciones.refrescar(listaComparaciones,null);
                    JOptionPane.showMessageDialog(new JFrame(), "Comparacion agregada exitosamente", "Informacion",
            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException | ParseException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error al crear comparacion", "Error",
            JOptionPane.ERROR_MESSAGE);
            }
        }else if(productoMarpicoSeleccionado!=null && productoPropioSeleccionado!=null){
            try {
                ComparacionMarpico rta = comparativoMarpico.crearComparacion(productoPropioSeleccionado, productoMarpicoSeleccionado, formatter.format(date), numeroPrecio,numeroMaterial);
                if(rta==null){
                    JOptionPane.showMessageDialog(new JFrame(), "Ya existe una comparacion con dichos productos", "Advertencia",
            JOptionPane.WARNING_MESSAGE);
                }else{
                    comparacionActualMarpico=rta;
                    listaComparacionesMarpico=comparativoMarpico.getComparaciones();
                    panelListaComparaciones.refrescar(null,listaComparacionesMarpico);
                    JOptionPane.showMessageDialog(new JFrame(), "Comparacion agregada exitosamente", "Informacion",
            JOptionPane.INFORMATION_MESSAGE);
                }
            } catch (SQLException e) {
                e.printStackTrace();
                JOptionPane.showMessageDialog(new JFrame(), "Error al crear comparacion", "Error",
            JOptionPane.ERROR_MESSAGE);
            }
        }
        else{
            JOptionPane.showMessageDialog(new JFrame(), "Debe seleccionar un producto propio y uno de la competencia", "Advertencia",
            JOptionPane.WARNING_MESSAGE);
        }
    }

    public void eliminarComparacion(){
        if(comparacionActual!=null||comparacionActualMarpico!=null){
            try {
                boolean rta=false;
                if(this.comparador.equals(PROMOPCIONES)){
                    rta = comparativo.eliminarComparacion(comparacionActual,estaConectadoBaseDeDatos,esHistorico);
                }else if(this.comparador.equalsIgnoreCase(MARPICO)){
                    rta = comparativoMarpico.eliminarComparacion(comparacionActualMarpico, esHistorico);
                }
                if(rta){
                    JOptionPane.showMessageDialog(new JFrame(), "Comparacion eliminada exitosamente", "Informacion",
            JOptionPane.INFORMATION_MESSAGE);
                    comparacionActual=null;
                    comparacionActualMarpico=null;
                    
                    if(esHistorico){
                        if(this.comparador.equals(PROMOPCIONES)){
                            panelListaComparaciones.refrescar(comparativo.obtenerHistoricoComparaciones(),null);
                        }else{
                            panelListaComparaciones.refrescar(null,comparativoMarpico.getHistoricoComparaciones());
                        }
                    }else{
                        if(this.comparador.equals(PROMOPCIONES)){
                            listaComparaciones=comparativo.obtenerListaComparaciones();
                            panelListaComparaciones.refrescar(listaComparaciones,null);
                        }else{
                            listaComparacionesMarpico=comparativoMarpico.getComparaciones();
                            panelListaComparaciones.refrescar(null,listaComparacionesMarpico);
                        }
                    }
                    refrescarComparacionSeleccionada();
                }
                else{
                    JOptionPane.showMessageDialog(new JFrame(), "No se pudo eliminar la comparacion", "Advertencia",
            JOptionPane.WARNING_MESSAGE);
                }
            } catch (SQLException e) {
                System.out.println(e);
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

    public boolean inicializarConexion(String uri, String usuario, String contrasena,String competencia){
        try {
            if(competencia.equalsIgnoreCase(MARPICO)){
                comparativoMarpico=new ComparativoMarpicoMundo(usuario, contrasena, uri);
                this.comparador=MARPICO;
            }else{
                this.comparador=PROMOPCIONES;
            }
            comparativo = new ComparativoMundo(usuario, contrasena, uri);
            estaConectadoBaseDeDatos=true;
            refrescarCategorias();
            return true;
        } catch(SQLException e){
            System.out.println(e);
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
            System.out.println(e);
            JOptionPane.showMessageDialog(new JFrame(), "Verifique su conexion a internet", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    public void refrescarComparacionSeleccionada(){
        if(this.comparador.equalsIgnoreCase(PROMOPCIONES)){
            panelComparacionSeleccionada.refrescar(comparacionActual,null);
        }else if(this.comparador.equalsIgnoreCase(MARPICO)){
            panelComparacionSeleccionada.refrescar(null,comparacionActualMarpico);
        }
    }

    public void refrescarCategoriaActual(Categoria pCategoria){
         categoriaActual = pCategoria;
         try{
         comparativo.obtenerProductosCategoria(categoriaActual);
         }catch(Exception e){
            System.out.println(e);
            JOptionPane.showMessageDialog(new JFrame(), "Error de conexion al API", "Advertencia", JOptionPane.ERROR_MESSAGE);
         }
    }

    public void refrescarListaComparaciones(){
        if(this.comparador.equalsIgnoreCase(PROMOPCIONES)){
            panelListaComparaciones.refrescar(comparativo.obtenerListaComparaciones(),null);
        }else if(this.comparador.equalsIgnoreCase(MARPICO)){
            panelListaComparaciones.refrescar(null,comparativoMarpico.getComparaciones());
        }
    }

    public ArrayList<ProductoCompetencia> obtenerProductosCompetencia(){
        return productosPromopciones;
    }

    public ArrayList<ProductosMarpico> obtenerProductosMarpico(){
        return productosMarpico;
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
            System.out.println(e);
            JOptionPane.showMessageDialog(new JFrame(), "Error de conexion al API", "Advertencia", JOptionPane.ERROR_MESSAGE);
            return null;
        }
    }

    public void refrescarCategorias(){
        try{
        panelCategorias.refrescar(comparativo.obtenerCatalogoPropio());
        }catch(NullPointerException | UnknownHostException e){
            System.out.println("refrescar categorias"+e);
            JOptionPane.showMessageDialog(new JFrame(), "Error de conexion al API", "Advertencia", JOptionPane.ERROR_MESSAGE);
        }
    }
    public void refrescarProductosSeleccionados(){
        panelProductosSeleccionados.refrescar(productoPropioSeleccionado, productoCompetenciaSeleccionado, categoriaActual,productoMarpicoSeleccionado);
    }
    public void refrescarProductos(){
        panelProductosPropios.refrescar(categoriaActual);
    }
    public void refrescarProductosCompetencia(){
        panelProductosCompetencia.refrescar(productosPromopciones,productosMarpico);
    }
    public void setProductoPropio(Producto pProducto){
        productoPropioSeleccionado=pProducto;
        refrescarProductosSeleccionados();
    }
    public void setProductoCompetencia(ProductoCompetencia pProducto){
        productoCompetenciaSeleccionado=pProducto;
        refrescarProductosSeleccionados();
    }
    public void setProductoCompetenciaMarpico(ProductosMarpico pProducto){
        productoMarpicoSeleccionado=pProducto;
        refrescarProductosSeleccionados();
    }
    
    public void obtenerHistoricoComparaciones(){
        try {
            if(estaConectadoBaseDeDatos){
                if(this.comparador.equalsIgnoreCase(PROMOPCIONES)){
                    comparativo.obtenerHistoricoComparaciones(estaConectadoBaseDeDatos);
                    panelListaComparaciones.refrescar(comparativo.obtenerHistoricoComparaciones(),null);
                }else if(this.comparador.equalsIgnoreCase(MARPICO)){
                    comparativoMarpico.obtenerHistoricoComparaciones();
                    panelListaComparaciones.refrescar(null,comparativoMarpico.getHistoricoComparaciones());
                }
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
            if(comparador.equalsIgnoreCase(PROMOPCIONES)){
                productosPromopciones=comparativo.obtenerInformacionExcelCompetencia(path);
            }else{
                productosMarpico=comparativoMarpico.cargarCatalogoMarpico().getResults();
            }
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
            System.out.println(e);
            JOptionPane.showMessageDialog(new JFrame(), "Error de conexion al API", "Advertencia", JOptionPane.ERROR_MESSAGE);
        }
    }

    public boolean getEsHistorico(){
        return esHistorico;
    }

    public ProductoCompetencia obtenerProductoCompetenciaSeleccionado(){
        return this.productoCompetenciaSeleccionado;
    }

    public Comparacion getComparacionPromopciones()
    {
        return this.comparacionActual;
    }
    public ComparacionMarpico getComparacionMarpico(){
        return this.comparacionActualMarpico;
    }
	
	public static void main( String[] args ) throws UnknownHostException, SQLException, ParseException, KeyManagementException, NoSuchAlgorithmException, MessagingException 
    {
       
     
         HostnameVerifier allHostsValid = new HostnameVerifier() {
             public boolean verify(String hostname, SSLSession session) {
               return true;
             }
         };
         HttpsURLConnection.setDefaultHostnameVerifier(allHostsValid);
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                new InterfazComparativo();            }
        });

    }

    

}

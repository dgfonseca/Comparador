package comparativo.interfaz;

import java.sql.Date;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.*;

import comparativo.mundo.ComparativoMundo;
import comparativo.mundo.model.Catalogo;
import comparativo.mundo.model.Categoria;
import comparativo.mundo.model.Comparacion;
import comparativo.mundo.model.Producto;
import comparativo.mundo.model.ProductoCompetencia;
import javafx.event.ActionEvent;

public class InterfazComparativo extends JFrame{

    private PanelMenuOpciones panelMenuOpciones;
    private PanelCredenciales panelCredenciales;
    private PanelListaCategoriasPropios panelCategorias;
    private PanelListaProductosPropios panelProductosPropios;
    private ComparativoMundo comparativo;
    private Categoria categoriaActual;
    private Producto productoPropioSeleccionado;
    private ArrayList<ProductoCompetencia> productosCompetencia;
    private ProductoCompetencia productoCompetenciaSeleccionado;
    private PanelListaProductosCompetencia panelProductosCompetencia;
    private PanelProductosSeleccionados panelProductosSeleccionados;

    InterfazComparativo(){
        categoriaActual=null;
        productosCompetencia=null;
        productoCompetenciaSeleccionado=null;
        panelCredenciales = new PanelCredenciales(true,this);
        panelCredenciales.setVisible(true);


        panelMenuOpciones=new PanelMenuOpciones(this);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Comparador");
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize);
        setResizable(true);
        setLayout(new BorderLayout());
        add(panelMenuOpciones,BorderLayout.NORTH);

        panelCategorias = new PanelListaCategoriasPropios(this);
        panelProductosPropios = new PanelListaProductosPropios(this);
        JPanel panelIzquierdo = new JPanel();
        panelIzquierdo.setLayout(new GridLayout(2,1));
        panelIzquierdo.add(panelCategorias);
        panelIzquierdo.add(panelProductosPropios);
        add(panelIzquierdo,BorderLayout.WEST);

        panelProductosCompetencia = new PanelListaProductosCompetencia(this);
        JPanel panelDerecha = new JPanel();
        panelDerecha.setLayout(new BorderLayout());
        panelDerecha.add(panelProductosCompetencia, BorderLayout.CENTER);
        add(panelDerecha,BorderLayout.EAST);

        panelProductosSeleccionados = new PanelProductosSeleccionados(this);
        JPanel panelCentro = new JPanel();
        panelCentro.setLayout(new BorderLayout());
        panelCentro.add(panelProductosSeleccionados, BorderLayout.NORTH);
        add(panelCentro,BorderLayout.CENTER);


    }

    public boolean inicializarConexion(String uri, String usuario, String contrasena){
        try {
            comparativo = new ComparativoMundo(usuario, contrasena, uri);
            refrescarCategorias();
            return true;
        } catch (SQLException e) {
            return false;
        }
    }

    public void obtenerCategoriaPorIndice(int indice){
         categoriaActual = comparativo.obtenerCategoriaPorIndice(indice);
         comparativo.obtenerProductosCategoria(categoriaActual);
    }

    public ArrayList<ProductoCompetencia> obtenerProductosCompetencia(){
        return productosCompetencia;
    }

    public Categoria obtenerCategoriaActual(){
        return categoriaActual;
    }

    public Catalogo obtenerCategorias(){
        return comparativo.obtenerCatalogoPropio();
    }

    public void refrescarCategorias(){
        panelCategorias.refrescar(comparativo.obtenerCatalogoPropio());
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

    public void cargarProductosCompetencia(String path){
        try {
            productosCompetencia=comparativo.obtenerInformacionExcelCompetencia(path);
            refrescarProductosCompetencia();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(new JFrame(), "Error al cargar excel", "Dialog", JOptionPane.ERROR_MESSAGE);
        }
    }

	
	public static void main( String[] args ) 
    {
        // try {
        //     ComparativoMundo comparativo = new ComparativoMundo("postgres","santafe","127.0.0.1");
        // } catch (SQLException e) {
        // }
        // boolean x = comparativo.crearComparacion(new comparativo.mundo.model.Producto("Mango", "1032", 2500), new ProductoCompetencia("usb-01", "usb", "USB", 2300), "2022-04");
        // comparativo.exportarCsv("C:/Users/Administrador/Desktop/catalogo");
        InterfazComparativo iec = new InterfazComparativo();

        
    }

}

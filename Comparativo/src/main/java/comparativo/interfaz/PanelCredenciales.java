package comparativo.interfaz;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import java.awt.*;
import java.awt.event.*;

public class PanelCredenciales extends JDialog{

    private InterfazComparativo interfaz;
    private final JLabel uri = new JLabel("Host");
    private final JLabel usuario = new JLabel("Usuario");
    private final JLabel password = new JLabel("Contrasena");

    private final JTextField tfUri = new JTextField(20);
    private final JTextField tfUsuario = new JTextField(20);
    private final JPasswordField tfPassword = new JPasswordField();
    private final JButton botonConectar = new JButton("Conectar");

    private final JLabel status = new JLabel("");

    public PanelCredenciales(InterfazComparativo pInterfaz){
        this(true,null);
    }
    public PanelCredenciales( boolean modal, final InterfazComparativo pInterfaz){
        tfUri.setText("127.0.0.1");
        tfUsuario.setText("postgres");
        tfPassword.setText("santafe");
        setTitle("Credenciales Base de datos");
        interfaz = pInterfaz;
        JPanel p3 = new JPanel(new GridLayout(3,1));
        p3.add(uri);
        p3.add(usuario);
        p3.add(password);

        JPanel p4 = new JPanel(new GridLayout(3,1));
        p4.add(tfUri);
        p4.add(tfUsuario);
        p4.add(tfPassword);

        JPanel p1 = new JPanel();
        p1.add(p3);
        p1.add(p4);

        JPanel p2 = new JPanel();
        p2.add(botonConectar);

        JPanel p5 = new JPanel(new BorderLayout());
        p5.add(p2,BorderLayout.CENTER);
        p5.add(status,BorderLayout.NORTH);
        status.setForeground(Color.RED);
        status.setHorizontalAlignment(SwingConstants.CENTER);

        setLayout(new BorderLayout());
        add(p1,BorderLayout.CENTER);
        add(p5, BorderLayout.SOUTH);
        pack();
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        addWindowListener(new WindowAdapter(){
            @Override
            public void windowClosing(WindowEvent e) {  
                System.exit(0);  
            } 
        });

        botonConectar.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){

                if(interfaz.inicializarConexion(tfUri.getText(), tfUsuario.getText(),String.valueOf(tfPassword.getPassword()))){
                    pInterfaz.setVisible(true);
                    setVisible(false);
                }
                else{
                    status.setText("Credenciales Invalidas");
                }

            }
        });

    }
    
}

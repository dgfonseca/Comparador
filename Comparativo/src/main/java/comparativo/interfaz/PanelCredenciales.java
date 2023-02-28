package comparativo.interfaz;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
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
    private final JLabel email = new JLabel("Email's");

    private final JTextField tfUri = new JTextField(20);
    private final JTextField tfEmail = new JTextField(20);
    private final JTextField tfUsuario = new JTextField(20);
    private final JPasswordField tfPassword = new JPasswordField();
    private final JButton botonConectar = new JButton("Conectar Promopciones");
    private final JButton botonConectar2 = new JButton("Conectar Marpico");

    private final JLabel status = new JLabel("");

    public PanelCredenciales(InterfazComparativo pInterfaz){
        this(true,null);
    }
    public PanelCredenciales( boolean modal, final InterfazComparativo pInterfaz){
        setIconImage(new ImageIcon(getClass().getResource("Imagenes/Logo.jpg")).getImage());
        tfUri.setText("127.0.0.1");
        tfUsuario.setText("postgres");
        tfPassword.setText("admin");
        setTitle("Credenciales Base de datos");
        interfaz = pInterfaz;
        JPanel p3 = new JPanel(new GridLayout(4,1));
        p3.add(uri);
        p3.add(usuario);
        p3.add(password);
        p3.add(email);

        JPanel p4 = new JPanel(new GridLayout(4,1));
        p4.add(tfUri);
        p4.add(tfUsuario);
        p4.add(tfPassword);
        p4.add(tfEmail);

        JPanel p1 = new JPanel();
        p1.add(p3);
        p1.add(p4);

        JPanel p2 = new JPanel(new GridLayout(1,2));
        p2.add(botonConectar);
        p2.add(botonConectar2);

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
                    if(interfaz.inicializarConexion(tfUri.getText(), tfUsuario.getText(),String.valueOf(tfPassword.getPassword()),"Promopciones")){
                        pInterfaz.setEmail(tfEmail.getText());
                        pInterfaz.setVisible(true);
                        setVisible(false);
                    }
                    else{
                        status.setText("Credenciales Invalidas");
                    }
            }
        });
        botonConectar2.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e){
                    if(interfaz.inicializarConexion(tfUri.getText(), tfUsuario.getText(),String.valueOf(tfPassword.getPassword()),"Marpico")){
                        pInterfaz.setEmail(tfEmail.getText());
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

package aprilbutiquestore;

import org.mindrot.jbcrypt.BCrypt;
import javax.swing.*;
import java.awt.*;
import java.sql.*;
import java.util.Arrays;

public class LogIn extends JFrame {
    private JTextField txtUsuario;
    private JPasswordField txtPassword;
    private static final Color PRIMARY_COLOR = new Color(44, 62, 80); // Azul oscuro
    private static final Color TEXT_COLOR = Color.WHITE;

    public LogIn() {
        setTitle("Inicio de Sesión");
        setSize(400, 450);
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        initComponents();
    }

    private void initComponents() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setBackground(PRIMARY_COLOR);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo
        ImageIcon icon = new ImageIcon("resources/logo.png");
        Image img = icon.getImage().getScaledInstance(150, 93, Image.SCALE_SMOOTH);
        JLabel lblLogo = new JLabel(new ImageIcon(img));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        panel.add(lblLogo, gbc);

        // Usuario
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        JLabel lblUsuario = new JLabel("Usuario:");
        lblUsuario.setForeground(TEXT_COLOR);
        panel.add(lblUsuario, gbc);

        gbc.gridx = 1;
        txtUsuario = new JTextField(15);
        panel.add(txtUsuario, gbc);

        // Contraseña
        gbc.gridx = 0;
        gbc.gridy = 2;
        JLabel lblPassword = new JLabel("Contraseña:");
        lblPassword.setForeground(TEXT_COLOR);
        panel.add(lblPassword, gbc);

        gbc.gridx = 1;
        txtPassword = new JPasswordField(15);
        panel.add(txtPassword, gbc);

        // Botón de inicio de sesión
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        JButton btnLogin = new JButton("Ingresar");
        btnLogin.setBackground(new Color(52, 152, 219)); // Azul claro
        btnLogin.setForeground(Color.WHITE);
        btnLogin.setFocusPainted(false);
        btnLogin.addActionListener(e -> realizarLogin());
        panel.add(btnLogin, gbc);

        add(panel);
    }

    private void realizarLogin() {
        String usuario = txtUsuario.getText().trim();
        char[] password = txtPassword.getPassword();

        try (Connection conn = BDConnection.getTempConnection();
             PreparedStatement stmt = conn.prepareStatement(
                 "SELECT password_hash, rol, db_user, db_password FROM ADMIN.USUARIOS WHERE username = ?")) {

            stmt.setString(1, usuario);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String storedHash = rs.getString("password_hash");
                
                if (BCrypt.checkpw(new String(password), storedHash)) {
                    // Autenticación exitosa
                    String rol = rs.getString("rol");
                    String dbUser = rs.getString("db_user");
                    String dbPasswordEncrypted = rs.getString("db_password");

                    // Descifrar contraseña de DB
                    char[] dbPassword = CryptoUtil.decrypt(dbPasswordEncrypted);

                    // Establecer credenciales dinámicas
                    BDConnection.setCredentials(usuario, rol, dbUser, dbPassword);

                    // Actualizar último login
                    actualizarUltimoLogin(usuario);

                    dispose();
                    if("VENDEDOR".equals(dbUser)){
                        new VentanaVentas().setVisible(true);
                    }else{
                        new Maestro().setVisible(true);
                    }
                    return;
                }
            }
            JOptionPane.showMessageDialog(this, "Credenciales inválidas", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        } finally {
            Arrays.fill(password, '0');  // Limpiar la contraseña en memoria
        }
    }

    private void actualizarUltimoLogin(String usuario) {
        String query = "UPDATE usuarios SET ultimo_login = CURRENT_TIMESTAMP WHERE username = ?";
        try (Connection conn = BDConnection.getConnection();
            PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setString(1, usuario);
            stmt.executeUpdate();
        } catch (SQLException ex) {
            System.err.println("Error al actualizar último login: " + ex.getMessage());
        }
    }
}
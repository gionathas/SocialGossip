package client.view;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextField;
import javax.swing.JLabel;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import javax.swing.JList;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import java.awt.Font;
import javax.swing.SwingConstants;
import java.awt.Color;
import javax.swing.JSeparator;

/**
 * Form di registrazione di un nuovo utente
 * @author gio
 *
 */
public class Register extends JFrame 
{
	private JPanel contentPane;
	private JTextField usernameField;
	private JTextField textField;
	private JTextField textField_1;

	public Register() 
	{
		showWindowContent();
	}
	
	private void showWindowContent() 
	{
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPassword.setBounds(50, 192, 165, 35);
		contentPane.add(lblPassword);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 20));
		lblUsername.setBounds(50, 112, 165, 15);
		contentPane.add(lblUsername);
		
		JLabel lblRepeatPassword = new JLabel("Conferma Password");
		lblRepeatPassword.setFont(new Font("Dialog", Font.BOLD, 20));
		lblRepeatPassword.setBounds(50, 287, 268, 25);
		contentPane.add(lblRepeatPassword);
		
		JButton btnInvia = new JButton("Invia");
		btnInvia.setBounds(50, 460, 180, 50);
		contentPane.add(btnInvia);
		
		JComboBox comboBox = new JComboBox();
		comboBox.setModel(new DefaultComboBoxModel(new String[] {"it", "en", "fr", "de", "es", "ja", "la", "pt", "ro", "ru", "sk", "sl", "sq"}));
		comboBox.setBounds(270, 390, 101, 35);
		contentPane.add(comboBox);
		
		JLabel linguaLabel = new JLabel("Seleziona Lingua");
		linguaLabel.setFont(new Font("Dialog", Font.BOLD, 20));
		linguaLabel.setBounds(50, 390, 216, 29);
		contentPane.add(linguaLabel);
		
		usernameField = new JTextField();
		usernameField.setToolTipText("Inserisci Username");
		usernameField.setForeground(new Color(150, 150, 150));
		usernameField.setFont(new Font("Dialog", Font.PLAIN, 18));
		usernameField.setColumns(10);
		usernameField.setBounds(50, 133, 310, 48);
		contentPane.add(usernameField);
		
		textField = new JTextField();
		textField.setToolTipText("Inserisci Username");
		textField.setForeground(new Color(150, 150, 150));
		textField.setFont(new Font("Dialog", Font.PLAIN, 18));
		textField.setColumns(10);
		textField.setBounds(50, 223, 310, 48);
		contentPane.add(textField);
		
		textField_1 = new JTextField();
		textField_1.setToolTipText("Inserisci Username");
		textField_1.setForeground(new Color(150, 150, 150));
		textField_1.setFont(new Font("Dialog", Font.PLAIN, 18));
		textField_1.setColumns(10);
		textField_1.setBounds(50, 313, 310, 48);
		contentPane.add(textField_1);
		
		JLabel lblRegistratiSuSocialgossip = new JLabel("Registrati su SocialGossip");
		lblRegistratiSuSocialgossip.setHorizontalAlignment(SwingConstants.CENTER);
		lblRegistratiSuSocialgossip.setFont(new Font("DejaVu Serif", Font.BOLD, 27));
		lblRegistratiSuSocialgossip.setBounds(190, 22, 464, 48);
		contentPane.add(lblRegistratiSuSocialgossip);
		
		JSeparator separator = new JSeparator();
		separator.setForeground(new Color(0, 0, 0));
		separator.setOrientation(SwingConstants.VERTICAL);
		separator.setBackground(new Color(0, 0, 0));
		separator.setBounds(400, 100, 1, 460);
		contentPane.add(separator);
	}
}

package client;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import com.jgoodies.forms.layout.FormLayout;
import com.jgoodies.forms.layout.ColumnSpec;
import com.jgoodies.forms.layout.RowSpec;
import java.awt.GridLayout;
import javax.swing.GroupLayout;
import javax.swing.GroupLayout.Alignment;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import java.awt.Font;
import javax.swing.JPasswordField;
import javax.swing.JButton;
import javax.swing.JTextField;

public class Login {

	private JFrame frame;
	private JPasswordField passwordField;
	private JTextField textField;

	/**
	 * Create the application.
	 */
	public Login() {
		initialize();
	}
	
	public void showWindow()
	{
		frame.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 800, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(null);
		
		JLabel lblUsername = new JLabel("Username");
		lblUsername.setFont(new Font("Dialog", Font.BOLD, 20));
		lblUsername.setBounds(160, 193, 128, 26);
		frame.getContentPane().add(lblUsername);
		
		JLabel lblPassword = new JLabel("Password");
		lblPassword.setFont(new Font("Dialog", Font.BOLD, 20));
		lblPassword.setBounds(160, 286, 110, 26);
		frame.getContentPane().add(lblPassword);
		
		passwordField = new JPasswordField();
		passwordField.setFont(new Font("Dialog", Font.PLAIN, 18));
		passwordField.setBounds(284, 277, 310, 48);
		frame.getContentPane().add(passwordField);
		
		JButton btnLogin = new JButton("Login");
		btnLogin.setBounds(312, 397, 117, 48);
		frame.getContentPane().add(btnLogin);
		
		textField = new JTextField();
		textField.setFont(new Font("Dialog", Font.PLAIN, 18));
		textField.setBounds(284, 183, 310, 48);
		frame.getContentPane().add(textField);
		textField.setColumns(10);
		
		JLabel lblBenvenutoInSocialgossip = new JLabel("Benvenuto in SocialGossip");
		lblBenvenutoInSocialgossip.setFont(new Font("DejaVu Serif", Font.BOLD, 26));
		lblBenvenutoInSocialgossip.setBounds(224, 51, 409, 55);
		frame.getContentPane().add(lblBenvenutoInSocialgossip);
	}
}

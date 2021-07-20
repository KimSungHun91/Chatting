package UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import Client.Client;
import Client.Request;
import Server.IProtocol;

public class LoginUI extends JFrame implements IProtocol {
	private Client owner;

	private JPanel contentPane;
	private JTextField tfNickname;
	private JButton btnLogin;
	private JLabel lblNickname;

	public LoginUI() {
		init();
		addListener();
		showFrame();
	}

	public LoginUI(Client owner) {
		this();
		this.owner = owner;
	}

	public void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		contentPane = new JPanel();
		contentPane.setBackground(new Color(102, 205, 170));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);

		btnLogin = new JButton("로그인");
		btnLogin.setFont(new Font("굴림", Font.BOLD, 16));
		btnLogin.setBounds(214, 19, 97, 20);
		contentPane.add(btnLogin);

		tfNickname = new JTextField();
		tfNickname.setBounds(105, 19, 105, 21);
		contentPane.add(tfNickname);
		tfNickname.setColumns(10);

		lblNickname = new JLabel("NICKNAME");
		lblNickname.setFont(new Font("굴림", Font.BOLD, 16));
		lblNickname.setBounds(12, 23, 100, 15);
		contentPane.add(lblNickname);

	}

	private void addListener() {

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				System.exit(-1);
			}
		});
		ActionListener l = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				String id = tfNickname.getText().replaceAll(" ", "");
				if (!id.equals("")) {

					Request req = new Request(REQ_LOGIN, new Object[] { id });
					try {
						owner.getOos().writeObject(req);
						owner.getOos().flush();
						owner.getOos().reset();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(null, "닉네임을 입력하세요.");
				}
			}
		};
		btnLogin.addActionListener(l);
		tfNickname.addActionListener(l);
	}

	public void showFrame() {
		setTitle("로그인");
		setSize(334, 88);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

}

package UI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JRadioButton;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import Client.Client;
import Client.Request;
import Client.Room;
import Server.IProtocol;

public class MakeOrSettingRoomUI extends JDialog implements IProtocol {
	public static final int CREATEROOMMODE = 0;
	public static final int SETTINGROOMMODE = 1;

	private int mode;
	private Client owner;

	public MakeOrSettingRoomUI() {
		init();
		addListener();
		showFrame();
	}

	public MakeOrSettingRoomUI(int mode) {
		this();
		this.mode = mode;
	}

	public MakeOrSettingRoomUI(Client owner, int mode) {
		this(mode);
		this.owner = owner;
	}

	private JPanel contentPane;

	private JTextArea taCreateChat;
	private JTextField tfCreateChat;
	private JTextField tfTitle;
	private JPasswordField pfPassword;
	private JLabel lblTitle;
	private JLabel lblPassword;
	private JLabel lblPeople;
	private JLabel lblCategory;
	private JRadioButton rdbtnPublic;
	private JRadioButton rdbtnSecret;
	private JComboBox cbPeople;
	private JComboBox cbCategory;
	private JButton btnNew;
	private JButton btnCancel;

	private String category[] = { "공 부", "연 애", "게 임", "수 다" };
	private String Personnel[] = { "1", "2", "3", "4", "5" };

	public void init() {
		contentPane = new JPanel(new BorderLayout());
		contentPane.setOpaque(true);
		contentPane.setBackground(new Color(102, 205, 170));
		contentPane.setBorder(new EmptyBorder(15, 15, 15, 15));

		JPanel pnlC = new JPanel(new GridLayout(0, 1, 5, 5));
		pnlC.setBackground(new Color(250, 235, 215));
		JPanel pnlW = new JPanel(new GridLayout(0, 1, 5, 5));
		pnlW.setBackground(new Color(250, 235, 215));

		JPanel pnlC1 = new JPanel();
		pnlC1.setBackground(new Color(250, 235, 215));
		lblTitle = new JLabel("채팅방 이름");
		lblTitle.setBorder(new EmptyBorder(5, 5, 5, 5));
		lblTitle.setBackground(new Color(250, 235, 215));
		lblTitle.setFont(new Font("굴림", Font.BOLD, 16));
		lblTitle.setOpaque(true);
		pnlW.add(lblTitle, BorderLayout.NORTH);
		pnlC1.setLayout(null);

		tfTitle = new JTextField(20);
		tfTitle.setBounds(5, 5, 229, 21);
		tfTitle.setColumns(10);
		pnlC1.add(tfTitle);

		JPanel pnlC2 = new JPanel();
		pnlC2.setBackground(new Color(250, 235, 215));

		lblCategory = new JLabel("카 테 고 리");
		lblCategory.setBorder(new EmptyBorder(5, 5, 5, 5));
		lblCategory.setBackground(new Color(250, 235, 215));
		lblCategory.setFont(new Font("굴림", Font.BOLD, 16));
		lblCategory.setOpaque(true);
		pnlW.add(lblCategory);
		pnlC2.setLayout(null);

		cbCategory = new JComboBox(category);
		cbCategory.setBounds(5, 5, 229, 21);
		pnlC2.add(cbCategory);

		JPanel pnlC3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlC3.setBackground(new Color(250, 235, 215));

		lblPassword = new JLabel("\uBE44 \uBC00 \uBC88 \uD638");
		lblPassword.setBorder(new EmptyBorder(5, 5, 5, 5));
		lblPassword.setBackground(new Color(250, 235, 215));
		lblPassword.setFont(new Font("굴림", Font.BOLD, 16));
		lblPassword.setOpaque(true);
		pnlW.add(lblPassword);

		pfPassword = new JPasswordField();
		pfPassword.setColumns(10);
		pfPassword.setEditable(false);
		pnlC3.add(pfPassword);

		ButtonGroup group = new ButtonGroup();

		rdbtnPublic = new JRadioButton("공개", true);
		rdbtnPublic.setBackground(new Color(250, 235, 215));
		pnlC3.add(rdbtnPublic);
		group.add(rdbtnPublic);

		rdbtnSecret = new JRadioButton("비공개");
		rdbtnSecret.setBackground(new Color(250, 235, 215));
		pnlC3.add(rdbtnSecret);
		group.add(rdbtnSecret);

		JPanel pnlC4 = new JPanel();
		pnlC4.setBackground(new Color(250, 235, 215));

		lblPeople = new JLabel("인 원  수 (명)");
		lblPeople.setBorder(new EmptyBorder(5, 5, 5, 5));
		lblPeople.setBackground(new Color(250, 235, 215));
		lblPeople.setFont(new Font("굴림", Font.BOLD, 16));
		lblPeople.setOpaque(true);
		pnlW.add(lblPeople);
		pnlC4.setLayout(null);

		cbPeople = new JComboBox(Personnel);
		cbPeople.setBounds(5, 5, 229, 21);
		pnlC4.add(cbPeople);
		JPanel pnlS = new JPanel(new FlowLayout(FlowLayout.CENTER));
		pnlS.setBackground(new Color(250, 235, 215));

		btnNew = new JButton("생 성 하 기");
		btnNew.setFont(new Font("굴림", Font.BOLD, 14));
		btnNew.setPreferredSize(new Dimension(170, 30));
		pnlS.add(btnNew);

		btnCancel = new JButton("취 소");
		btnCancel.setFont(new Font("굴림", Font.BOLD, 14));
		btnCancel.setPreferredSize(new Dimension(170, 30));
		pnlS.add(btnCancel);

		pnlC.add(pnlC1);
		pnlC.add(pnlC2);
		pnlC.add(pnlC3);
		pnlC.add(pnlC4);

		contentPane.add(pnlW, BorderLayout.WEST);
		contentPane.add(pnlC, BorderLayout.CENTER);
		contentPane.add(pnlS, BorderLayout.SOUTH);

		getContentPane().add(contentPane);
	}

	private void addListener() {

		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});

		btnNew.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				String title = tfTitle.getText();
				String category = (String) cbCategory.getSelectedItem();

				int maxUser = Integer.parseInt((String) cbPeople.getSelectedItem());
				Room room = null;
				if (!title.equals("") || title.length() > 0) {
					if (rdbtnPublic.isSelected()) {
						room = new Room(title, category, maxUser);
						newAndSet(room);
					} else if (rdbtnSecret.isSelected()) {
						String lock = pfPassword.getText().trim();
						if (!lock.equals("")) {
							room = new Room(title, category, lock, maxUser);
							newAndSet(room);
						} else {
							JOptionPane.showMessageDialog(null, "비밀번호는 1글자 이상입니다.");
						}
					}
				} else {
					JOptionPane.showMessageDialog(null, "방의 이름을 다시 설정해주세요.");
				}
			}
		});

		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				tfTitle.setText("");
				pfPassword.setText("");
			}
		});

		rdbtnPublic.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pfPassword.setEditable(false);
			}
		});
		rdbtnSecret.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				pfPassword.setEditable(true);
			}
		});
	}

	private void showFrame() {
		setTitle("채팅방 생성");
		// setSize(467, 370);
		pack();
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(false);
	}

	public int getMode() {
		return mode;
	}

	public void setMode(int mode) {
		this.mode = mode;
		if (mode == CREATEROOMMODE) {
			btnNew.setText("생성");
			setTitle("생성");
		} else if (mode == SETTINGROOMMODE) {
			btnNew.setText("수정");
			setTitle("수정");
		}
	}

	private void newAndSet(Room room) {
		if (mode == CREATEROOMMODE) {
			try {
				owner.getOos().writeObject(new Request(REQ_MAKE_ROOM, new Object[] { room }));
				owner.getOos().flush();
				owner.getOos().reset();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		} else {
			try {
				owner.getOos().writeObject(new Request(REQ_SETTING_ROOM, new Object[] { room }));
				owner.getOos().flush();
				owner.getOos().reset();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		setVisible(false);
	}

	public void clear() {
		tfTitle.setText("");
		pfPassword.setText("");
		pfPassword.setEditable(false);
		cbCategory.setSelectedIndex(0);
		rdbtnPublic.setSelected(true);
		cbPeople.setSelectedIndex(0);
	}
}
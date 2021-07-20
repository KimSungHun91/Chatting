package UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

import Client.Client;
import Client.Request;
import Server.IProtocol;

public class InviteUI extends JDialog implements IProtocol {
	public static final int INVITEMODE = 0;
	public static final int PERSONALCHATMODE = 1;

	private Vector<ClickedLabel> lbls = new Vector<ClickedLabel>();
	private JPanel Panel;
	private JPanel pnlList;

	private JTextField tfSearch;

	private JButton btnInvite;
	private JButton btnExit;
	private JButton btnSearch;

	private Client owner;
	private int mode;
	private String focus;

	public InviteUI() {
		focus = "";
		init();
		addListener();
		showFrame();
	}

	public InviteUI(int mode) {
		this();
		this.mode = mode;
	}

	public InviteUI(Client owner, int mode) {
		this(mode);
		this.owner = owner;
	}

	public void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		Panel = new JPanel();
		Panel.setBackground(new Color(102, 205, 170));
		Panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(Panel);
		Panel.setLayout(null);

		pnlList = new JPanel(new GridLayout(0, 1));
		pnlList.setBounds(35, 77, 243, 298);
		Panel.add(pnlList);

		tfSearch = new JTextField();
		tfSearch.setBorder(new LineBorder(new Color(250, 235, 215), 2));
		tfSearch.setBounds(46, 35, 145, 24);
		Panel.add(tfSearch);
		tfSearch.setColumns(10);

		btnSearch = new JButton("검 색");
		btnSearch.setFont(new Font("굴림", Font.BOLD, 12));
		btnSearch.setBounds(199, 34, 63, 24);
		Panel.add(btnSearch);

		btnInvite = new JButton("초 대");

		btnInvite.setFont(new Font("굴림", Font.BOLD, 13));
		btnInvite.setBounds(12, 400, 138, 40);
		Panel.add(btnInvite);

		btnExit = new JButton("나 가 기");
		btnExit.setFont(new Font("굴림", Font.BOLD, 13));
		btnExit.setBounds(162, 400, 138, 40);
		Panel.add(btnExit);
	}

	public void makeUserList(Vector<String> userList) {

		pnlList.removeAll();
		for (String user : userList) {
			ClickedLabel lbl = new ClickedLabel(this);
			lbl.setText(user);
			lbl.setOpaque(true);
			lbl.setBackground(Color.WHITE);
			lbl.setBorder(new LineBorder(Color.GRAY));
			lbl.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
			lbl.setSize(243, 50);
			// lbl.setSize(243, 50);
			pnlList.add(lbl);
			lbls.add(lbl);
		}
		if (userList.size() < 5) {
			for (int i = 1; i < 5; i++) {
				pnlList.add(new JLabel());
			}
		}
		pnlList.updateUI();
	}

	private void addListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});

		btnInvite.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mode == INVITEMODE) {
					if (!focus.equals("")) {
						Request req = new Request(REQ_INVITE_USER, new Object[] { focus });
						try {
							owner.getOos().writeObject(req);
							owner.getOos().flush();
							owner.getOos().reset();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						setVisible(false);
					} else {
						JOptionPane.showMessageDialog(null, "초대할 대상을 선택해주세요.");
					}
				} else {
					if (!focus.equals("")) {
						setVisible(false);
						owner.getPersonal().add(focus);
					} else {
						JOptionPane.showMessageDialog(null, "추가할 대상을 선택해주세요.");
					}
				}
			}
		});

		tfSearch.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (mode == INVITEMODE) {
					Request req = new Request(REQ_FIND_USER, new Object[] { tfSearch.getText() });
					try {
						owner.getOos().writeObject(req);
						owner.getOos().flush();
						owner.getOos().reset();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
			}
		});

		btnExit.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				owner.getInvite().setVisible(false);
			}
		});
	}

	private void showFrame() {
		setTitle("초대");
		setSize(330, 489);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(false);
	}

	public int getMode() {
		return mode;
	}

	public String getFocus() {
		return focus;
	}

	public void setFocus(String focus) {
		this.focus = focus;
	}

	public void setMode(int mode) {
		this.mode = mode;
		if (mode == INVITEMODE) {
			btnInvite.setText("초 대");
			setTitle("초대");
		} else {
			btnInvite.setText("선 택");
			setTitle("귓속말");
		}
	}

	public void checkFocus() {
		for (ClickedLabel lbl : lbls) {
			if (lbl.getText() == focus) {
				lbl.setBackground(Color.RED);
			} else {
				lbl.setBackground(null);
			}
		}
	}
}
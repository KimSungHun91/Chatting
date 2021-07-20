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
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.LineBorder;

import Client.Client;
import Client.Request;
import Client.Room;
import Server.IProtocol;

public class PersonalUI extends JFrame implements IProtocol {

	private Client owner;
	private HashMap<String, StringBuffer> personalMsg;
	private String focus;
	// UI
	private JPanel pnlMain;
	private JPanel pnlUserList;
	private JPanel panelSearch;
	private JPanel panelUser;
	private JPanel panelBtn;

	private JTextArea taChatRecord;

	private JButton btnSend;
	private JButton btnOut;
	private JButton btnAdd;
	private JButton btnCancel;
	private JButton btnSearch;

	private HashMap<String, PersonalUserLbl> personalUsers;

	private LineBorder border;

	private JTextField tfSearch;
	private JTextField tfChat;

	private JLabel lblToUser;
	private JLabel lblMyNickname;
	private JLabel myNickName;
	private JLabel focusUser;

	public PersonalUI(Client owner) {
		this.owner = owner;
		focus = "";
		init();
		setDisplay();
		addListeners();
		showFrame();
	}

	private void init() {
		personalMsg = new HashMap<>();
		personalUsers = new HashMap<>();

		pnlMain = new JPanel(new BorderLayout());

		btnSend = new JButton("전 송");
		btnOut = new JButton("나가기");
		focusUser = new JLabel();
		focusUser.setBorder(new LineBorder(new Color(0, 0, 0)));
		focusUser.setHorizontalAlignment(JLabel.CENTER);
		focusUser.setOpaque(true);
		focusUser.setPreferredSize(new Dimension(100, 23));
		focusUser.setBackground(new Color(255, 255, 255));

		taChatRecord = new JTextArea();
		taChatRecord.setEditable(false);
		tfChat = new JTextField(23);

		border = new LineBorder(Color.BLACK);
	}

	private void setDisplay() {

		JPanel pnlC = new JPanel(new BorderLayout());

		JPanel pnlCN = new JPanel();
		pnlCN.setBackground(new Color(255, 192, 203));
		pnlCN.setLayout(new FlowLayout(FlowLayout.LEFT));

		lblMyNickname = new JLabel("My NickName");
		lblMyNickname.setForeground(Color.BLUE);
		lblMyNickname.setFont(new Font("굴림", Font.BOLD, 12));
		pnlCN.add(lblMyNickname);

		myNickName = new JLabel();
		myNickName.setPreferredSize(new Dimension(100, 23));
		myNickName.setOpaque(true);
		myNickName.setHorizontalAlignment(SwingConstants.CENTER);
		myNickName.setBorder(new LineBorder(new Color(0, 0, 0)));
		myNickName.setBackground(Color.WHITE);
		pnlCN.add(myNickName);

		lblToUser = new JLabel("대 화  상 대");
		lblToUser.setFont(new Font("굴림", Font.BOLD, 12));
		pnlCN.add(lblToUser);
		pnlCN.add(focusUser);
		pnlC.add(pnlCN, BorderLayout.NORTH);

		JPanel pnlCC = new JPanel(new GridLayout(1, 1, 5, 5));
		JScrollPane taScroll = new JScrollPane(taChatRecord);
		pnlCC.add(taScroll);
		pnlC.add(pnlCC, BorderLayout.CENTER);

		JPanel pnlCS = new JPanel(new FlowLayout(FlowLayout.LEFT));
		pnlCS.setBackground(new Color(255, 192, 203));
		pnlCS.add(tfChat);
		pnlCS.add(btnSend);
		pnlCS.add(btnOut);
		pnlC.add(pnlCS, BorderLayout.SOUTH);

		pnlMain.add(pnlC, BorderLayout.CENTER);
		btnAdd = new JButton("추 가");
		btnAdd.setVerticalAlignment(SwingConstants.BOTTOM);
		btnCancel = new JButton("삭 제");
		JPanel pnlW = new JPanel(new BorderLayout());

		/// North
		panelSearch = new JPanel();
		panelSearch.setBackground(new Color(255, 192, 203));
		pnlW.add(panelSearch, BorderLayout.NORTH);

		tfSearch = new JTextField();
		panelSearch.add(tfSearch);
		tfSearch.setColumns(10);

		btnSearch = new JButton("검 색");
		panelSearch.add(btnSearch);
		/// Center
		pnlUserList = new JPanel(new GridLayout(0, 1, 5, 5));
		pnlUserList.setBackground(new Color(255, 192, 203));

		panelUser = new JPanel(new FlowLayout());
		panelUser.add(pnlUserList);

		JScrollPane scroll = new JScrollPane(pnlUserList);
		JScrollBar vBar = scroll.getVerticalScrollBar();
		scroll.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
		vBar.setUnitIncrement(20);

		pnlW.add(scroll, BorderLayout.CENTER);

		// SOUTH
		panelBtn = new JPanel();
		panelBtn.setBackground(new Color(255, 192, 203));
		panelBtn.add(btnAdd);
		panelBtn.add(btnCancel);
		pnlW.add(panelBtn, BorderLayout.SOUTH);

		pnlMain.add(pnlW, BorderLayout.WEST);

		getContentPane().add(pnlMain);
	}

	private void addListeners() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});

		btnOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});

		ActionListener alistener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (existUser(focus)) {
					if (!tfChat.getText().equals("")) {
						owner.getMakeOrSetting().setMode(MakeOrSettingRoomUI.SETTINGROOMMODE);
						Request req = new Request(REQ_PERSONAL_CHAT, new Object[] { focus, tfChat.getText() });
						try {
							owner.getOos().writeObject(req);
							owner.getOos().flush();
							owner.getOos().reset();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
						tfChat.setText("");
					}
				} else {
					personalMsg.remove(focus);
					personalUsers.remove(focus);
					setUser();
					pnlUserList.updateUI();
					focus = "";
					taChatRecord.setText("");
					focusUser.setText("");
					JOptionPane.showMessageDialog(PersonalUI.this, "유저가 존재하지 않습니다.");
				}
			}
		};

		btnSend.addActionListener(alistener);
		tfChat.addActionListener(alistener);

		btnAdd.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				owner.getInvite().setMode(InviteUI.PERSONALCHATMODE);
				Request req = new Request(REQ_PERSONAL_CHAT_USERLIST);
				try {
					owner.getOos().writeObject(req);
					owner.getOos().flush();
					owner.getOos().reset();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		ActionListener searchlistener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Vector<String> temp = new Vector<String>();
				for (String id : personalMsg.keySet()) {
					if (id.contains(tfSearch.getText())) {
						temp.add(id);
					}
				}
				if (temp.size() == 0) {
					JOptionPane.showMessageDialog(PersonalUI.this, "검색 대상이 없습니다.");
				} else {
					setUser(temp);
				}
			}
		};
		tfSearch.addActionListener(searchlistener);
		btnSearch.addActionListener(searchlistener);

		btnCancel.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (personalUsers.size() > 0) {
					personalMsg.remove(focus);
					personalUsers.remove(focus);
					setUser();
					focus = null;
					pnlUserList.updateUI();
				}
			}
		});
	}

	private void showFrame() {
		setTitle("귓속말");
		setSize(620, 500);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(false);
	}

	public JTextArea getTaChat() {
		return taChatRecord;
	}

	public String getFocus() {
		return focus;
	}

	public void setFocus(String id) {
		this.focus = id;
	}

	public HashMap<String, StringBuffer> getPersonal() {
		return personalMsg;
	}

	public void personalMsg(String id, String msg) {
		if (!personalMsg.containsKey(id)) {
			personalMsg.put(id, new StringBuffer());
			pnlUserList.removeAll();
			for (String personalUser : personalMsg.keySet()) {
				PersonalUserLbl userID = new PersonalUserLbl(this);
				JPanel pnl = new JPanel(new FlowLayout());
				pnl.add(userID);
				userID.setText(personalUser);
				personalUsers.put(personalUser, userID);
				pnlUserList.add(pnl);
				pnlUserList.updateUI();
				pnlMain.updateUI();
			}
		}
		focus = id;
		focusUser.setText(focus);
		personalUsers.get(id).setOpaque(true);
		personalUsers.get(id).setBackground(new Color(255, 192, 203));
		personalMsg.get(id).append(id + " : " + msg + "\n");
		taChatRecord.setText(personalMsg.get(id).toString());
		taChatRecord.setCaretPosition(taChatRecord.getDocument().getLength());
		focusType();
	}

	public void focusType() {
		for (String user : personalUsers.keySet()) {
			if (user == focus) {
				personalUsers.get(user).setBorder(border);
			} else {
				personalUsers.get(user).setBorder(null);
			}
		}
	}

	public void showClickMsg() {
		focusUser.setText(focus);
		taChatRecord.setText(personalMsg.get(focus).toString());
	}

	public void add(String id) {
		focus = id;
		if (!personalMsg.containsKey(id)) {
			personalMsg.put(id, new StringBuffer());
		}
		setUser();
		focusType();
	}

	private void setUser() {
		pnlUserList.removeAll();
		for (String personalUser : personalMsg.keySet()) {
			PersonalUserLbl userID = new PersonalUserLbl(this);
			JPanel pnl = new JPanel();
			pnl.add(userID);
			userID.setText(personalUser);
			personalUsers.put(personalUser, userID);
			pnlUserList.add(pnl);
			pnlUserList.updateUI();
			pnlMain.updateUI();
		}
		focusUser.setText(focus);
		if (personalMsg.keySet().size() < 10) {
			for (int i = 1; i < 10; i++) {
				pnlUserList.add(new JLabel());
			}
		}

	}

	private void setUser(Vector<String> user) {
		pnlUserList.removeAll();
		for (String personalUser : user) {
			PersonalUserLbl userID = new PersonalUserLbl(this);
			JPanel pnl = new JPanel();
			pnl.add(userID);
			userID.setText(personalUser);
			personalUsers.put(personalUser, userID);
			pnlUserList.add(pnl);
			pnlUserList.updateUI();
			pnlMain.updateUI();
		}
		focusUser.setText(focus);
		personalUsers.get(focus).setOpaque(true);
		if (personalMsg.keySet().size() < 10) {
			for (int i = 1; i < 10; i++) {
				pnlUserList.add(new JLabel());
			}
		}
	}

	public void setMyId(String myId) {
		myNickName.setText(myId);
	}

	private boolean existUser(String id) {
		Set<Integer> keys = owner.getWaitRoom().getRoomList().keySet();
		Iterator<Integer> itr = keys.iterator();
		while (itr.hasNext()) {
			Integer key = itr.next();
			Room value = owner.getWaitRoom().getRoomList().get(key);
			if (value.getUserList().contains(id)) {
				return true;
			}
		}
		return false;
	}
}
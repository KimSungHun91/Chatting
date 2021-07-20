package UI;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.LayoutManager;
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
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;

import Client.Client;
import Client.Request;
import Client.Room;
import Server.IProtocol;

public class ChatingRoomUI extends JFrame implements IProtocol {
	private Room room;
	private Client owner;

	private JPanel Panel;
	private JPanel pnlWaitChat;
	private JPanel pnlList;

	private JTextField tfchat;
	private JTextField tfWaitList;
	private JTextArea taRoomChat;

	private JButton btnSend;
	private JButton btnWhisper;

	private JButton btnKick;
	private JButton btnDelegate;

	private JButton btnInvite;
	private JButton btnSet;
	private JButton btnExit;

	private ChoiceLbl[] choiceLbl;

	private String focus;
	private JTextField tfInfo;
	private JLabel leader;
	private JLabel lblPassword;
	private JLabel title;
	private JLabel lblLeader;
	private JLabel password;

	private JTextField tfMyInfo;
	private JPanel pnlMyNickname;
	private JLabel lblMyNickname;
	private JLabel myNickname;

	public ChatingRoomUI() {
		init();
		addListener();
		showFrame();
	}

	public ChatingRoomUI(Client owner) {
		this();
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

		pnlWaitChat = new JPanel();
		pnlWaitChat.setBorder(new TitledBorder(null, "채 팅", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		pnlWaitChat.setBounds(34, 38, 473, 474);
		Panel.add(pnlWaitChat);
		pnlWaitChat.setLayout(null);

		taRoomChat = new JTextArea();
		taRoomChat.setBounds(12, 10, 449, 443);
		pnlWaitChat.add(taRoomChat);
		taRoomChat.setEnabled(false);
		taRoomChat.setBackground(new Color(250, 240, 230));

		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(taRoomChat);
		scroll.setBounds(12, 21, 449, 443);
		scroll.setBorder(new LineBorder(new Color(250, 235, 215), 2));
		pnlWaitChat.add(scroll);

		pnlList = new JPanel(new GridLayout(0, 1));
		pnlList.setBackground(new Color(250, 235, 215));
		pnlList.setBounds(542, 328, 243, 148);
		Panel.add(pnlList);

		tfchat = new JTextField();
		tfchat.setBorder(new LineBorder(new Color(250, 235, 215), 2));
		tfchat.setBounds(34, 522, 309, 24);
		Panel.add(tfchat);
		tfchat.setColumns(10);

		btnSend = new JButton("전 송");
		btnSend.setFont(new Font("굴림", Font.BOLD, 12));
		btnSend.setBounds(345, 522, 79, 24);
		Panel.add(btnSend);

		btnWhisper = new JButton("귓속말");
		btnWhisper.setFont(new Font("굴림", Font.BOLD, 12));
		btnWhisper.setBounds(428, 522, 76, 24);
		Panel.add(btnWhisper);

		tfWaitList = new JTextField();
		tfWaitList.setFont(new Font("굴림", Font.BOLD, 16));
		tfWaitList.setEnabled(false);
		tfWaitList.setColumns(10);
		tfWaitList.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "참 여 자", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(102, 205, 170)));
		tfWaitList.setBounds(519, 302, 291, 196);
		Panel.add(tfWaitList);

		btnInvite = new JButton("초 대");
		btnInvite.setFont(new Font("굴림", Font.BOLD, 13));
		btnInvite.setBounds(519, 506, 85, 40);
		Panel.add(btnInvite);

		btnSet = new JButton("방  설 정");
		btnSet.setFont(new Font("굴림", Font.BOLD, 13));
		btnSet.setBounds(616, 506, 97, 40); // (639+(639+97))/2=>687.5
		Panel.add(btnSet);

		btnExit = new JButton("나 가 기");
		btnExit.setFont(new Font("굴림", Font.BOLD, 13));
		btnExit.setBounds(725, 506, 85, 40);
		Panel.add(btnExit);

		JPanel pnlInfo = new JPanel((LayoutManager) null);
		pnlInfo.setBorder(new LineBorder(new Color(250, 235, 215)));
		pnlInfo.setBackground(new Color(250, 235, 215));
		pnlInfo.setBounds(542, 127, 243, 113);
		Panel.add(pnlInfo);
		pnlInfo.setLayout(null);

		password = new JLabel();
		password.setOpaque(true);
		password.setBorder(new LineBorder(new Color(0, 0, 0)));
		password.setBackground(Color.WHITE);
		password.setBounds(100, 86, 131, 21);
		pnlInfo.add(password);

		JLabel lblTitle = new JLabel("방 제목");
		lblTitle.setFont(new Font("굴림", Font.BOLD, 15));
		lblTitle.setBounds(12, 52, 57, 15);
		pnlInfo.add(lblTitle);

		leader = new JLabel();
		leader.setOpaque(true);
		leader.setBorder(new LineBorder(new Color(0, 0, 0)));
		leader.setBackground(Color.WHITE);
		leader.setBounds(100, 14, 131, 21);
		pnlInfo.add(leader);

		lblPassword = new JLabel("비밀 번호 ");
		lblPassword.setFont(new Font("굴림", Font.BOLD, 15));
		lblPassword.setBounds(12, 88, 76, 15);
		pnlInfo.add(lblPassword);

		title = new JLabel();
		title.setOpaque(true);
		title.setBorder(new LineBorder(new Color(0, 0, 0)));
		title.setBackground(Color.WHITE);
		title.setBounds(100, 50, 131, 21);
		pnlInfo.add(title);

		lblLeader = new JLabel("방 장");
		lblLeader.setFont(new Font("굴림", Font.BOLD, 15));
		lblLeader.setBounds(12, 16, 57, 15);
		pnlInfo.add(lblLeader);

		btnKick = new JButton("강 퇴");
		btnKick.setBounds(552, 250, 105, 29);
		Panel.add(btnKick);
		btnKick.setFont(new Font("굴림", Font.BOLD, 13));

		btnDelegate = new JButton("위 임");
		btnDelegate.setBounds(672, 250, 105, 29);
		Panel.add(btnDelegate);
		btnDelegate.setFont(new Font("굴림", Font.BOLD, 13));

		tfInfo = new JTextField();
		tfInfo.setFont(new Font("굴림", Font.BOLD, 16));
		tfInfo.setEnabled(false);
		tfInfo.setColumns(10);
		tfInfo.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "방 정 보", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(102, 205, 170)));
		tfInfo.setBounds(519, 103, 291, 189);
		Panel.add(tfInfo);

		pnlMyNickname = new JPanel((LayoutManager) null);
		pnlMyNickname.setLayout(null);
		pnlMyNickname.setBorder(new LineBorder(new Color(250, 235, 215)));
		pnlMyNickname.setBackground(new Color(250, 235, 215));
		pnlMyNickname.setBounds(542, 51, 243, 40);
		Panel.add(pnlMyNickname);

		lblMyNickname = new JLabel("NICKNAME");
		lblMyNickname.setFont(new Font("굴림", Font.BOLD, 15));
		lblMyNickname.setBounds(12, 13, 91, 15);
		pnlMyNickname.add(lblMyNickname);

		// 이름 들어가는 레이블
		myNickname = new JLabel();
		myNickname.setOpaque(true);
		myNickname.setBorder(new LineBorder(new Color(0, 0, 0)));
		myNickname.setBackground(Color.WHITE);
		myNickname.setBounds(100, 9, 131, 21);
		pnlMyNickname.add(myNickname);

		tfMyInfo = new JTextField();
		tfMyInfo.setFont(new Font("굴림", Font.BOLD, 16));
		tfMyInfo.setEnabled(false);
		tfMyInfo.setColumns(10);
		tfMyInfo.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "나 의 정 보", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(102, 205, 170)));
		tfMyInfo.setBounds(519, 38, 291, 59);
		Panel.add(tfMyInfo);

	}

	private void addListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				roomExit();
			}
		});

		ActionListener l = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (ae.getSource() == btnSend || ae.getSource() == tfchat) {
					// 채팅 요청
					String msg = tfchat.getText();
					if (!msg.trim().equals("")) {
						Request req = new Request(REQ_ROOM_CHAT, new Object[] { msg });
						try {
							owner.getOos().writeObject(req);
							owner.getOos().flush();
							owner.getOos().reset();
							tfchat.setText("");
							tfchat.grabFocus();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		};
		tfchat.addActionListener(l);
		btnSend.addActionListener(l);

		btnInvite.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (room.getUserList().size() < room.getMaxUser()) {
					owner.getInvite().setMode(InviteUI.INVITEMODE);
					Request req = new Request(REQ_ALL_USER); // 150
					try {
						owner.getOos().writeObject(req);
						owner.getOos().flush();
						owner.getOos().reset();
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					JOptionPane.showMessageDialog(ChatingRoomUI.this, "인원이 가득참, 초대 불가");
				}

			}
		});

		btnSet.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (owner.getId().equals(room.getLeader())) {
					owner.getMakeOrSetting().setMode(MakeOrSettingRoomUI.SETTINGROOMMODE);
					owner.getMakeOrSetting().setVisible(true);
				} else {
					JOptionPane.showMessageDialog(ChatingRoomUI.this, "방장의 권한입니다.");
				}
			}
		});

		btnKick.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (owner.getId().equals(room.getLeader())) {
					if (focus != null) {
						Request req = new Request(REQ_BAN_USER, new Object[] { focus });
						try {
							owner.getOos().writeObject(req);
							owner.getOos().flush();
							owner.getOos().reset();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					JOptionPane.showMessageDialog(ChatingRoomUI.this, "방장의 권한입니다.");
				}
			}
		});

		btnDelegate.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (owner.getId().equals(room.getLeader())) {
					if (focus != null) {
						Request req = new Request(REQ_CHANGE_LEADER, new Object[] { focus, room.getLeader() });
						try {
							owner.getOos().writeObject(req);
							owner.getOos().flush();
							owner.getOos().reset();
						} catch (IOException e1) {
							e1.printStackTrace();
						}
					}
				} else {
					JOptionPane.showMessageDialog(ChatingRoomUI.this, "방장의 권한입니다.");
				}
			}
		});

		btnExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				roomExit();
			}
		});

		btnWhisper.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				owner.getInvite().setMode(InviteUI.PERSONALCHATMODE);
				owner.getPersonal().setMyId(owner.getId());
				owner.getPersonal().setVisible(true);
			}
		});
	}

	private void showFrame() {
		setTitle("채팅방");
		setSize(864, 616);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		setVisible(false);
	}

	public void updateUser() {

		pnlList.removeAll();
		choiceLbl = new ChoiceLbl[room.getUserList().size()];
		for (int i = 0; i < choiceLbl.length; i++) {
			choiceLbl[i] = new ChoiceLbl(this);
			choiceLbl[i].setText(room.getUserList().get(i));
			choiceLbl[i].setOpaque(true);
			choiceLbl[i].setBackground(Color.WHITE);
			choiceLbl[i].setBorder(new LineBorder(Color.GRAY));
			choiceLbl[i].setFont(new Font(Font.DIALOG, Font.BOLD, 20));
			choiceLbl[i].setSize(243, 50);
			pnlList.add(choiceLbl[i]);
		}
		if (room.getUserList().size() < 4) {
			for (int i = 1; i < 4; i++) {
				pnlList.add(new JLabel());
			}
		}
		pnlList.updateUI();
	}

	public void focusColor() {
		for (ChoiceLbl lbl : choiceLbl) {
			if (lbl.getText() == focus) {
				lbl.setBackground(Color.YELLOW);
			} else {
				lbl.setBackground(Color.WHITE);
			}
		}
	}

	public void roomChat(String msg) {
		taRoomChat.append(msg + "\n");
		taRoomChat.setCaretPosition(taRoomChat.getDocument().getLength());
	}

	public void roomChat(String id, String msg) {
		taRoomChat.append(id + " : " + msg + "\n");
		taRoomChat.setCaretPosition(taRoomChat.getDocument().getLength());
	}

	public Room getRoom() {
		return room;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public Client getOwn() {
		return owner;
	}

	public void setOwner(Client owner) {
		this.owner = owner;
	}

	private void update() {
		Panel.updateUI();
	}

	public String getFocus() {
		return focus;
	}

	public void setFocus(String choiceId) {
		this.focus = choiceId;
	}

	private void roomExit() {
		Request req = new Request(REQ_EXIT_ROOM);
		try {
			owner.getOos().writeObject(req);
			owner.getOos().flush();
			owner.getOos().reset();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	public JLabel getRoomTitle() {
		return title;
	}

	public JLabel getRoomLeader() {
		return leader;
	}

	public JLabel getRoomPw() {
		return password;
	}

	public void setMyId(String myId) {
		myNickname.setText(myId);
	}

	public void resetChat() {
		taRoomChat.setText("");
	}
}

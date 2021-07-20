package UI;

import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
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

public class WaitingRoomUI extends JFrame implements IProtocol {
	private HashMap<Integer, Room> roomList;
	private Client owner;
	private Room room;

	private int focus;

	private JPanel Panel;
	private JPanel pnlWaitChat;
	private JPanel pnlRoomList;
	private JPanel pnlList;

	private JTextField tfRoomList;
	private JTextField tfWaitChat;
	private JTextField tfchat;
	private JTextField tfWaitList;
	private JTextField tfSearch;

	private JTextArea taRoomList;
	private JTextArea taWaitChat;
	private JTextArea taWaitList;

	private JButton btnNew;
	private JButton btnEnter;
	private JButton btnSend;
	private JButton btnWhisper;
	private JButton btnQuit;
	private JButton btnSearch;

	private JPanel rooms;
	private JPanel pnlRLC;
	private JComboBox cbNum;
	private JComboBox cbTitle;
	private JComboBox cbcategory;
	private JComboBox cbPersonnel;

	private String num[] = { "번호", "오름차순▲", "내림차순▼" };
	private String Title[] = { "방 제목", "오름차순▲", "내림차순▼" };
	private String category[] = { "카테고리", "공 부", "연 애", "게 임", "수 다" };
	private String Personnel[] = { "인원", "1", "2", "3", "4", "5" };

	private LineBorder border;

	private HashMap<Integer, JPanel> pnls;
	private JTextField textField;
	private JLabel myNickName;

	private ComparatorTool com;

	public WaitingRoomUI() {
		init();
		addListener();
		showFrame();
	}

	public WaitingRoomUI(Client owner) {
		this();
		this.owner = owner;
	}

	public void init() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
		}
		com = new ComparatorTool(ComparatorTool.UPNUM);
		pnls = new HashMap<>();

		border = new LineBorder(Color.BLACK);

		Panel = new JPanel();
		Panel.setBackground(new Color(102, 205, 170));
		Panel.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(Panel);
		Panel.setLayout(null);

		pnlWaitChat = new JPanel();
		pnlWaitChat.setBounds(34, 338, 473, 174);
		pnlWaitChat.setLayout(null);

		taWaitChat = new JTextArea();
		taWaitChat.setBounds(12, 10, 449, 154);
		pnlWaitChat.add(taWaitChat);
		taWaitChat.setEnabled(false);
		taWaitChat.setBackground(new Color(250, 240, 230));

		JScrollPane scroll = new JScrollPane();
		scroll.setBackground(new Color(250, 235, 215));
		scroll.setViewportView(taWaitChat);
		scroll.setBounds(34, 338, 470, 170);
		scroll.setBorder(new LineBorder(new Color(250, 235, 215), 2));
		Panel.add(scroll);

		pnlRoomList = new JPanel();
		pnlRoomList.setBorder(new LineBorder(new Color(250, 235, 215)));
		pnlRoomList.setBounds(31, 55, 473, 191);
		Panel.add(pnlRoomList);
		pnlRoomList.setBackground(new Color(250, 240, 230));
		pnlRoomList.setLayout(null);

		JPanel pnlSet = new JPanel();
		pnlSet.setBounds(12, 10, 449, 21);

		pnlRoomList.add(pnlSet);
		pnlSet.setLayout(null);

		cbNum = new JComboBox(num);
		cbNum.setBounds(0, 0, 69, 21);
		pnlSet.add(cbNum);

		cbTitle = new JComboBox(Title);
		cbTitle.setBounds(67, 0, 220, 21);
		pnlSet.add(cbTitle);

		cbcategory = new JComboBox(category);
		cbcategory.setBounds(286, 0, 102, 21);
		pnlSet.add(cbcategory);

		cbPersonnel = new JComboBox(Personnel);
		cbPersonnel.setBounds(385, 0, 64, 21);
		pnlSet.add(cbPersonnel);

		pnlRLC = new JPanel(new FlowLayout());
		pnlRLC.setBorder(new LineBorder(new Color(192, 192, 192)));
		pnlRLC.setBackground(new Color(250, 235, 215));
		pnlRLC.setBounds(12, 30, 449, 151);
		pnlRoomList.add(pnlRLC);

		rooms = new JPanel(new GridLayout(0, 1));
		pnlRLC.add(rooms);

		taRoomList = new JTextArea();
		taRoomList.setBounds(12, 10, 449, 188);
		pnlRoomList.add(taRoomList);
		taRoomList.setEnabled(false);
		taRoomList.setBackground(new Color(250, 240, 230));

		pnlList = new JPanel(new GridLayout(0, 1));
		pnlList.setBounds(560, 125, 260, 383);
		Panel.add(pnlList);

		JScrollPane scroll1 = new JScrollPane();
		scroll1.setBackground(new Color(250, 235, 215));
		scroll1.setViewportView(pnlList);
		scroll1.setBounds(560, 125, 260, 383);
		scroll1.setBorder(new LineBorder(new Color(250, 235, 215), 2));
		Panel.add(scroll1);

		taWaitList = new JTextArea();
		taWaitList.setBounds(12, 10, 219, 443);
		pnlList.add(taWaitList);
		taWaitList.setEnabled(false);
		taWaitList.setBackground(new Color(250, 240, 230));
		taWaitList.setBorder(new LineBorder(new Color(250, 235, 215), 2));

		btnNew = new JButton("방  만 들 기 ");
		btnNew.setFont(new Font("굴림", Font.BOLD, 13));
		btnNew.setBounds(104, 256, 117, 36);
		Panel.add(btnNew);

		btnEnter = new JButton("들 어 가 기");
		btnEnter.setFont(new Font("굴림", Font.BOLD, 13));
		btnEnter.setBounds(323, 256, 117, 36);
		Panel.add(btnEnter);

		tfSearch = new JTextField();
		tfSearch.setBackground(Color.WHITE);
		tfSearch.setBounds(221, 24, 161, 23);
		tfSearch.setColumns(10);

		tfRoomList = new JTextField();
		tfRoomList.setEnabled(false);
		tfRoomList.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "채 팅 방  목 록",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(102, 205, 170)));
		tfRoomList.setFont(new Font("굴림", Font.BOLD, 16));
		tfRoomList.setBounds(12, 10, 518, 291);
		tfRoomList.setColumns(10);

		tfWaitChat = new JTextField();
		tfWaitChat.setEnabled(false);
		tfWaitChat.setFont(new Font("굴림", Font.BOLD, 16));
		tfWaitChat.setColumns(10);
		tfWaitChat.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "대 기 실", TitledBorder.LEADING,
				TitledBorder.TOP, null, new Color(102, 205, 170)));
		tfWaitChat.setBounds(12, 311, 518, 251);

		tfWaitList = new JTextField();
		tfWaitList.setFont(new Font("굴림", Font.BOLD, 16));
		tfWaitList.setEnabled(false);
		tfWaitList.setColumns(10);
		tfWaitList.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "대 기 실  사 용 자  목 록",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(102, 205, 170)));
		tfWaitList.setBounds(542, 97, 291, 419);

		btnQuit = new JButton("나 가 기");
		btnQuit.setFont(new Font("굴림", Font.BOLD, 13));
		btnQuit.setBounds(542, 522, 291, 40);

		Panel.add(btnQuit);
		Panel.add(tfSearch);
		Panel.add(tfWaitList);

		tfchat = new JTextField();
		tfchat.setBorder(new LineBorder(new Color(250, 235, 215), 2));
		tfchat.setBounds(34, 522, 309, 24);
		tfchat.setColumns(10);
		Panel.add(tfchat);

		btnWhisper = new JButton("귓속말");
		btnWhisper.setFont(new Font("굴림", Font.BOLD, 12));
		btnWhisper.setBounds(428, 522, 76, 24);
		Panel.add(btnWhisper);

		btnSend = new JButton("전 송");
		btnSend.setFont(new Font("굴림", Font.BOLD, 12));
		btnSend.setBounds(345, 522, 79, 24);
		Panel.add(btnSend);
		Panel.add(tfWaitChat);

		btnSearch = new JButton("제 목 검 색");
		btnSearch.setBounds(400, 24, 92, 23);
		Panel.add(btnSearch);
		Panel.add(tfRoomList);

		JPanel panel = new JPanel();
		panel.setBorder(new LineBorder(new Color(250, 235, 215)));
		panel.setBackground(new Color(250, 240, 230));
		panel.setBounds(560, 32, 260, 40);
		Panel.add(panel);
		panel.setLayout(null);

		JLabel label = new JLabel("NICKNAME");
		label.setFont(new Font("굴림", Font.BOLD, 18));
		label.setBounds(12, 10, 96, 15);
		panel.add(label);

		myNickName = new JLabel();
		myNickName.setFont(new Font("굴림", Font.BOLD, 18));
		myNickName.setBounds(115, 8, 133, 21);
		myNickName.setOpaque(true);
		myNickName.setBorder(new LineBorder(new Color(0, 0, 0)));
		myNickName.setBackground(Color.WHITE);
		panel.add(myNickName);

		textField = new JTextField();
		textField.setFont(new Font("굴림", Font.BOLD, 16));
		textField.setEnabled(false);
		textField.setColumns(10);
		textField.setBorder(new TitledBorder(UIManager.getBorder("TitledBorder.border"), "\uB098 \uC758  \uC815 \uBCF4",
				TitledBorder.LEADING, TitledBorder.TOP, null, new Color(102, 205, 170)));
		textField.setBounds(542, 10, 291, 77);
		Panel.add(textField);
	}

	class ComparatorTool implements Comparator<Room> {
		public static final int UPNUM = 0;
		public static final int DOWNNUM = 1;
		public static final int UPTITLE = 2;
		public static final int DOWNTITLE = 3;

		private int mode;

		public ComparatorTool(int mode) {
			this.mode = mode;
		}

		@Override
		public int compare(Room o1, Room o2) {
			if (mode == UPNUM) {
				return o1.getRoomNum() - o2.getRoomNum();
			} else if (mode == DOWNNUM) {
				return o2.getRoomNum() - o1.getRoomNum();
			} else if (mode == DOWNTITLE) {
				return o2.getTitle().compareTo(o1.getTitle());
			} else {
				return o1.getTitle().compareTo(o2.getTitle());
			}
		}

		public void setMode(int mode) {
			this.mode = mode;
		}

		public Vector<Room> sort(int mode, Vector<Room> temp) {
			setMode(mode);
			Collections.sort(temp, this);
			return temp;
		}
	}

	public void updateUser() {
		pnlList.removeAll();
		for (String user : room.getUserList()) {
			JLabel lbl = new JLabel(user);
			lbl.setOpaque(true);
			lbl.setBackground(new Color(250, 240, 230));
			lbl.setBorder(new LineBorder(new Color(192, 192, 192)));
			lbl.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
			lbl.setSize(243, 50);
			pnlList.add(lbl);
		}
		if (room.getUserList().size() < 6) {
			for (int i = 1; i < 6; i++) {
				pnlList.add(new JLabel());
			}
		}
		pnlList.updateUI();
	}

	public void updateWaitRoomChat(String msg) {
		taWaitChat.append(msg + "\n");
		taWaitChat.setCaretPosition(taWaitChat.getDocument().getLength());

	}

	public void updateRoom() {
		rooms.removeAll();
		Set<Integer> keys = roomList.keySet();
		Iterator<Integer> itr = keys.iterator();
		while (itr.hasNext()) {
			Integer roomNum = itr.next();
			if (roomNum != 0) {
				Room room = roomList.get(roomNum);
				RoomPnl pnl = new RoomPnl(this, room);
				pnls.put(roomNum, pnl);
				rooms.add(pnl);
			}
		}
		rooms.updateUI();
		pnlRLC.updateUI();
	}

	private void addListener() {
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				Request req = new Request(REQ_EXIT_PROGRAM);
				try {
					owner.getOos().writeObject(req);
					owner.getOos().flush();
					owner.getOos().reset();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnNew.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				owner.getMakeOrSetting().setMode(MakeOrSettingRoomUI.CREATEROOMMODE);
				owner.getMakeOrSetting().setVisible(true);
			}

		});

		ActionListener l = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent ae) {
				if (ae.getSource() == btnSend || ae.getSource() == tfchat) {
					// 대기실 채팅 요청
					String msg = tfchat.getText();
					if (!msg.equals("")) {
						Request req = new Request(REQ_WAIT_CHAT, new Object[] { msg });
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

		btnWhisper.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// 160 요청
				owner.getInvite().setMode(InviteUI.INVITEMODE);
				owner.getPersonal().setMyId(owner.getId());
				owner.getPersonal().setVisible(true);
			}
		});

		btnQuit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Request req = new Request(REQ_EXIT_PROGRAM);
				try {
					owner.getOos().writeObject(req);
					owner.getOos().flush();
					owner.getOos().reset();
				} catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		});

		btnEnter.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if (focus != 0) {
					if (roomList.containsKey(focus)) {
						if (roomList.get(focus).getLock().equals("")) {
							enter();
						} else {
							String pw = JOptionPane.showInputDialog("비밀번호를 입력해주세요.");
							if (pw.equals(roomList.get(focus).getLock())) {
								enter();
							} else {
								JOptionPane.showMessageDialog(null, "비밀번호가 잘못되었습니다.");
								updateRoom();
							}
						}
					} else {
						JOptionPane.showMessageDialog(WaitingRoomUI.this, "방이 존재하지 않습니다.");
						updateRoom();
					}
				}
			}
		});

		ActionListener aListener = new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				Vector<Room> titles = new Vector<Room>();
				for (Room r : roomList.values()) {
					if (!(r.getRoomNum() == 0)) {
						if (r.getTitle().contains(tfSearch.getText())) {
							titles.add(r);
						}
					}
				}

				rooms.removeAll();
				for (Room r : titles) {
					if (r.getRoomNum() != 0) {
						RoomPnl pnl = new RoomPnl(WaitingRoomUI.this, r);
						pnls.put(r.getRoomNum(), pnl);
						rooms.add(pnl);
					}
				}
				rooms.updateUI();
				pnlRLC.updateUI();

			}
		};

		tfSearch.addActionListener(aListener);
		btnSearch.addActionListener(aListener);

		ItemListener iListener = new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if ((e.getSource() == cbcategory) || (e.getSource() == cbNum) || (e.getSource() == cbTitle)
						|| (e.getSource() == cbPersonnel)) {
					sortUpdate();
				}
			}
		};

		cbNum.addItemListener(iListener);
		cbTitle.addItemListener(iListener);
		cbcategory.addItemListener(iListener);
		cbPersonnel.addItemListener(iListener);
	}

	private void enter() {
		Request req = new Request(REQ_ENTER_ROOM, new Object[] { focus });
		try {
			owner.getOos().writeObject(req);
			owner.getOos().flush();
			owner.getOos().reset();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

	private void showFrame() {
		setTitle("채팅방 대기 화면");
		setSize(864, 616);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(false);
	}

	public Room getRoom() {
		return room;
	}

	public HashMap<Integer, Room> getRoomList() {
		return roomList;
	}

	public void setRoom(Room room) {
		this.room = room;
	}

	public void setRoomList(HashMap<Integer, Room> roomList) {
		this.roomList = roomList;
	}

	public void setFocus(int roomNum) {
		this.focus = roomNum;
	}

	public void checkFocus() {
		for (Integer num : pnls.keySet()) {
			if (focus == num) {
				pnls.get(num).setBorder(border);
			} else {
				pnls.get(num).setBorder(null);
			}
		}
	}

	public void setMyId(String myId) {
		myNickName.setText(myId);
	}

	public void resetChat() {
		taWaitChat.setText("");
	}

	public void sortUpdate() {
		Vector<Room> temp = new Vector<>();
		for (Room r : roomList.values()) {
			temp.add(r);
		}
		temp.remove(0);

		Vector<Room> temp2 = new Vector<Room>();
		for (Room r : temp) {
			temp2.add(r);
		}

		if (!(((String) cbcategory.getSelectedItem()).equals(category[0]))) {
			for (Room r : temp2) {
				if (!(r.getCategory().equals((String) cbcategory.getSelectedItem()))) {
					temp.remove(r);
				}
			}
		}

		Vector<Room> temp3 = new Vector<Room>();
		for (Room r : temp) {
			temp3.add(r);
		}

		if (!(Personnel[0]).equals((String) cbPersonnel.getSelectedItem())) {
			for (Room r : temp3) {
				if (!(Integer.parseInt((String) cbPersonnel.getSelectedItem()) == r.getMaxUser())) {
					temp.remove(r);
				}
			}
		}

		if (((String) cbNum.getSelectedItem()).equals(num[1])) {
			temp = com.sort(ComparatorTool.UPNUM, temp);
			if (((String) cbTitle.getSelectedItem()).equals(Title[1])) {
				temp = com.sort(ComparatorTool.UPTITLE, temp);
			} else if (((String) cbTitle.getSelectedItem()).equals(Title[2])) {
				temp = com.sort(ComparatorTool.DOWNTITLE, temp);
			}
		} else if (((String) cbNum.getSelectedItem()).equals(num[2])) {
			temp = com.sort(ComparatorTool.DOWNNUM, temp);
			if (((String) cbTitle.getSelectedItem()).equals(Title[1])) {
				temp = com.sort(ComparatorTool.UPTITLE, temp);
			} else if (((String) cbTitle.getSelectedItem()).equals(Title[2])) {
				temp = com.sort(ComparatorTool.DOWNTITLE, temp);
			}
		}

		rooms.removeAll();
		for (Room r : temp) {
			rooms.add(pnls.get(r.getRoomNum()));
		}
		rooms.updateUI();
		pnlRLC.updateUI();
	}
}
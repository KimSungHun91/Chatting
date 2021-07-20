package UI;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;
import javax.swing.JPanel;

import Client.Room;

public class RoomPnl extends JPanel {
	private WaitingRoomUI owner;
	private Room room;

	private JLabel num;
	private JLabel title;
	private JLabel lock;
	private JLabel category;
	private JLabel population;

	public RoomPnl(WaitingRoomUI owner, Room room) {
		super();
		this.owner = owner;
		this.room = room;
		init();
		addListener();
	}

	private void init() {
		setLayout(new FlowLayout(FlowLayout.LEFT));

		num = new JLabel();
		num.setHorizontalAlignment(JLabel.CENTER);
		num.setText(room.getRoomNum() + "");
		num.setPreferredSize(new Dimension(61, 21));
		num.setToolTipText(users());

		title = new JLabel(room.getTitle());
		title.setPreferredSize(new Dimension(168, 21));
		title.setHorizontalAlignment(JLabel.LEFT);
		title.setToolTipText(users());

		lock = new JLabel(lockCheck());
		lock.setPreferredSize(new Dimension(40, 21));
		lock.setHorizontalAlignment(JLabel.CENTER);
		lock.setToolTipText(users());

		category = new JLabel(room.getCategory());
		category.setPreferredSize(new Dimension(92, 21));
		category.setHorizontalAlignment(JLabel.CENTER);
		category.setToolTipText(users());

		population = new JLabel("(" + room.getUserList().size() + "/" + room.getMaxUser() + ")");
		population.setPreferredSize(new Dimension(54, 21));
		population.setHorizontalAlignment(JLabel.CENTER);
		population.setToolTipText(users());

		add(num);
		add(title);
		add(lock);
		add(category);
		add(population);
	}

	private void addListener() {
		MouseAdapter ma = new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				owner.setFocus(room.getRoomNum());
				owner.checkFocus();
			};
		};
		addMouseListener(ma);
		num.addMouseListener(ma);
		title.addMouseListener(ma);
		lock.addMouseListener(ma);
		category.addMouseListener(ma);
		population.addMouseListener(ma);
	}

	public Room getRoom() {
		return room;
	}

	private String lockCheck() {
		if (room.getLock().equals("")) {
			return "공개";
		} else {
			return "비공개";
		}
	}

	private String users() {
		String info = "유저목록 : ";
		for (String user : room.getUserList()) {
			info += "[" + user + "]";
		}
		return info;
	}
}
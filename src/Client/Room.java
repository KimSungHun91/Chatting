package Client;

import java.io.Serializable;
import java.util.Vector;

public class Room implements Serializable, Comparable<Room> {
	private int roomNum;
	private String title;
	private String lock;
	private Vector<String> banList;
	private Vector<String> userList;
	private String category;
	private String leader;
	private int maxUser;

	public Room() {
		banList = new Vector<String>();
		userList = new Vector<String>();
	}

	public Room(String title, String category, int maxUser) {
		this();
		this.title = title;
		this.category = category;
		this.maxUser = maxUser;
		lock = "";
	}

	public Room(String title, String category, String lock, int maxUser) {
		this(title, category, maxUser);
		this.lock = lock;
	}

	public int getRoomNum() {
		return roomNum;
	}

	public void setRoomNum(int roomNum) {
		this.roomNum = roomNum;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getLock() {
		return lock;
	}

	public void setLock(String lock) {
		this.lock = lock;
	}

	public Vector<String> getBanList() {
		return banList;
	}

	public void setBanList(Vector<String> banList) {
		this.banList = banList;
	}

	public Vector<String> getUserList() {
		return userList;
	}

	public void setUserList(Vector<String> userList) {
		this.userList = userList;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	public String getLeader() {
		return leader;
	}

	public void setLeader(String leader) {
		this.leader = leader;
	}

	public int getMaxUser() {
		return maxUser;
	}

	public void setMaxUser(int maxUser) {
		this.maxUser = maxUser;
	}

	@Override
	public boolean equals(Object o) {
		if ((o == null) || !(o instanceof Room)) {
			return false;
		}
		Room r = (Room) o;

		return roomNum == r.roomNum;
	}

	@Override
	public int compareTo(Room o) {
		return roomNum - o.roomNum;
	}

	@Override
	public String toString() {
		return "Room [roomNum=" + roomNum + ", title=" + title + ", lock=" + lock + ", banList=" + banList
				+ ", userList=" + userList + ", category=" + category + ", leader=" + leader + ", maxUser=" + maxUser
				+ "]";
	}
}

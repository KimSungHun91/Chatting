package Client;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import javax.swing.JOptionPane;

import Server.IProtocol;
import Server.MyUtils;
import Server.Response;
import UI.ChatingRoomUI;
import UI.InviteUI;
import UI.LoginUI;
import UI.MakeOrSettingRoomUI;
import UI.PersonalUI;
import UI.WaitingRoomUI;

public class Client {
	private Socket socket;
	private ObjectOutputStream oos = null;
	private ObjectInputStream ois = null;
	private LoginUI login;
	private WaitingRoomUI waitingRoom;
	private ChatingRoomUI chatingRoom;
	private MakeOrSettingRoomUI makeAndSetting;
	private InviteUI invite;
	private PersonalUI personal;
	private String id;

	public Client() {
		login = new LoginUI(this);
		waitingRoom = new WaitingRoomUI(this);
		chatingRoom = new ChatingRoomUI(this);
		makeAndSetting = new MakeOrSettingRoomUI(this, MakeOrSettingRoomUI.CREATEROOMMODE);
		invite = new InviteUI(this, InviteUI.INVITEMODE);
		personal = new PersonalUI(this);

		ClientThread clThread = new ClientThread();
		clThread.start();
	}

	class ClientThread extends Thread implements IProtocol {
		private ClientThread() {
			try {
				socket = new Socket("127.0.0.1", 10001);
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());// 여기서 멈춤
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			try {
				Object obj = null;
				while ((obj = ois.readObject()) != null) {
					Response res = (Response) obj;
					switch (res.getCode()) {
					case RES_LOGIN_OK:
						id = (String) res.getObjs()[0];
						HashMap<Integer, Room> roomList = (HashMap<Integer, Room>) res.getObjs()[1];
						waitingRoom.setRoom(roomList.get(0));
						waitingRoom.setRoomList(roomList);
						login.setVisible(false);
						waitingRoom.setMyId(id);
						waitingRoom.setVisible(true);
						waitingRoom.updateUser();
						waitingRoom.updateRoom();
						break;
					case RES_LOGIN_NO:
						JOptionPane.showMessageDialog(login, "이미 존재하는 닉네임입니다.");
						break;
					case RES_UPDATE_WAITROOM_USER:
						String userId = (String) res.getObjs()[0];
						synchronized (waitingRoom) {
							if (waitingRoom.getRoom().getUserList().contains(userId) == false) {
								waitingRoom.getRoom().getUserList().add(userId);
								waitingRoom.updateUser();
								waitingRoom.updateRoom();
								waitingRoom.sortUpdate();
							}
						}
						break;
					case RES_SUCCESS_MAKE_UPDATE_ROOM:
						Room room = (Room) res.getObjs()[0];
						waitingRoom.setVisible(false);
						waitingRoom.getRoomList().put(room.getRoomNum(), room);
						chatingRoom.setRoom(room);
						chatingRoom.setMyId(id);
						chatingRoom.resetChat();
						chatingRoom.setVisible(true);
						chatingRoom.getRoomTitle().setText(room.getTitle());
						chatingRoom.getRoomLeader().setText(room.getLeader());
						chatingRoom.getRoomPw().setText(room.getLock());
						makeAndSetting.setVisible(false);
						chatingRoom.updateUser();
						break;
					case RES_REMOVEUSER_WAITROOM:
						String user = (String) res.getObjs()[0];
						Room updateRoom = (Room) res.getObjs()[1];
						waitingRoom.getRoom().getUserList().remove(user);
						waitingRoom.getRoomList().put(updateRoom.getRoomNum(), updateRoom);
						waitingRoom.updateUser();
						waitingRoom.updateRoom();
						waitingRoom.sortUpdate();
						break;
					case RES_UPDATE_ROOM_LIST:
						Room settingRoom = (Room) res.getObjs()[0];
						chatingRoom.setRoom(settingRoom);
						chatingRoom.getRoomTitle().setText(settingRoom.getTitle());
						chatingRoom.getRoomLeader().setText(settingRoom.getLeader());
						chatingRoom.getRoomPw().setText(settingRoom.getLock());
						chatingRoom.updateUser();
						break;
					case RES_ADD_ROOM_USER:
						String updateID = (String) res.getObjs()[0];
						chatingRoom.getRoom().getUserList().add(updateID);
						chatingRoom.roomChat(updateID + "님이 입장하였습니다.");
						chatingRoom.updateUser();
						break;
					case RES_UPDATE_WAITROOM_ROOMLIST:
						Room settingRoom2 = (Room) res.getObjs()[0];
						waitingRoom.getRoomList().put(settingRoom2.getRoomNum(), settingRoom2);
						waitingRoom.updateRoom();
						waitingRoom.sortUpdate();
						waitingRoom.updateUser();
						break;
					case RES_ENTER_ROOM:
						Room enterRoom = (Room) res.getObjs()[0];
						chatingRoom.setRoom(enterRoom);
						chatingRoom.getRoomTitle().setText(enterRoom.getTitle());
						chatingRoom.getRoomLeader().setText(enterRoom.getLeader());
						chatingRoom.getRoomPw().setText(enterRoom.getLock());
						waitingRoom.setVisible(false);
						chatingRoom.setMyId(id);
						chatingRoom.resetChat();
						chatingRoom.setVisible(true);
						break;
					case RES_UPDATE_USER:
						String enterUser = (String) res.getObjs()[0];
						chatingRoom.getRoom().getUserList().add(enterUser);
						chatingRoom.roomChat(enterUser + "님이 입장하였습니다.");
						chatingRoom.updateUser();
						break;
					case RES_ENTER_ROOM_FAIL1:
						JOptionPane.showMessageDialog(waitingRoom, "인원이 초과 되었습니다.");
						break;
					case RES_ENTER_ROOM_FAIL2:
						JOptionPane.showMessageDialog(waitingRoom, "강퇴된 유저입니다.");
						break;
					case RES_ENTER_ROOM_FAIL3:
						JOptionPane.showMessageDialog(waitingRoom, "방이 존재하지 않습니다.");
						waitingRoom.updateRoom();
						waitingRoom.sortUpdate();
						waitingRoom.updateUser();
						break;
					case RES_WAIT_CHAT:
						String chatId = (String) res.getObjs()[0];
						String msg = (String) res.getObjs()[1];
						waitingRoom.updateWaitRoomChat(chatId + " : " + msg);
						break;
					case RES_ALL_USER:
						Vector<String> userList = (Vector<String>) res.getObjs()[0];
						invite.makeUserList(userList);
						invite.setVisible(true);
						break;
					case RES_FIND_USER:
						Vector<String> users = (Vector<String>) res.getObjs()[0];
						invite.makeUserList(users);
						break;
					// 10160, 응답 처리
					case RES_BAN_USER:
						HashMap<Integer, Room> banSetting = (HashMap<Integer, Room>) res.getObjs()[0];
						JOptionPane.showMessageDialog(null, "강퇴되었습니다.");
						chatingRoom.setVisible(false);
						waitingRoom.setRoom(banSetting.get(0));
						waitingRoom.setRoomList(banSetting);
						waitingRoom.setVisible(true);
						waitingRoom.updateUser();
						waitingRoom.updateRoom();
						break;
					case RES_MINUS_USER:
						String minusUser = (String) res.getObjs()[0];
						chatingRoom.getRoom().getUserList().remove(minusUser);
						chatingRoom.roomChat(minusUser + "님이 강퇴되었습니다.");
						chatingRoom.updateUser();
						break;
					case RES_PERSONAL_CHAT_USERLIST:
						Vector<String> chatUserList = (Vector<String>) res.getObjs()[0];
						Set<String> personalUser = personal.getPersonal().keySet();
						for (String sameUser : personalUser) {
							if (chatUserList.contains(sameUser)) {
								chatUserList.remove(sameUser);
							}
						}
						invite.setMode(InviteUI.PERSONALCHATMODE);
						invite.makeUserList(chatUserList);
						invite.setVisible(true);
						break;
					case RES_PERSONAL_CHAT_ME:
						String from = (String) res.getObjs()[0];
						String to = (String) res.getObjs()[1];
						String sendMsg = (String) res.getObjs()[2];
						personal.add(to);
						personal.getPersonal().get(to).append(from + " : " + sendMsg + "\n");
						personal.getTaChat().setText(personal.getPersonal().get(to).toString());
						personal.getTaChat().setCaretPosition(personal.getTaChat().getDocument().getLength());
						break;
					case RES_PERSONAL_CHAT_YOU:
						String sendToYou = (String) res.getObjs()[0];
						String sendYMsg = (String) res.getObjs()[1];
						personal.personalMsg(sendToYou, sendYMsg);
						personal.setMyId(id);
						personal.setVisible(true);
						break;
					case RES_EXIT_PROGRAM:
						System.exit(-1);
						break;
					case RES_WAIT_USER_DELETE:
						String exitUser = (String) res.getObjs()[0];
						waitingRoom.getRoom().getUserList().remove(exitUser);
						waitingRoom.updateUser();
						break;
					case RES_ROOM_CHAT:
						String roomSendedId = (String) res.getObjs()[0];
						String resMsg = (String) res.getObjs()[1];
						chatingRoom.roomChat(roomSendedId, resMsg);
						break;
					case REQ_FIND_USER:
						Vector<String> findUser = (Vector<String>) res.getObjs()[0];
						invite.makeUserList(findUser);
						break;
					case RES_INVITE_CONFIRM:
						String inviteUser = (String) res.getObjs()[0];
						int inviteRoomNum = (int) res.getObjs()[1];
						int yes = JOptionPane.showConfirmDialog(null, inviteUser + "님이 초대를 보냈습니다.", "초대 요청",
								JOptionPane.YES_NO_OPTION);
						if (yes == JOptionPane.OK_OPTION) {
							Request req = new Request(REQ_INVITE_ACCEPT, new Object[] { inviteRoomNum });
							oos.writeObject(req);
						} else {
							Request req = new Request(REQ_INVITE_DENY, new Object[] { inviteUser });
							oos.writeObject(req);
						}
						oos.flush();
						oos.reset();
						break;
					case RES_INVITE_ACCEPT:
						Room inviteRoom = (Room) res.getObjs()[0];
						chatingRoom.setRoom(inviteRoom);
						waitingRoom.setVisible(false);
						chatingRoom.getRoomTitle().setText(inviteRoom.getTitle());
						chatingRoom.getRoomLeader().setText(inviteRoom.getLeader());
						chatingRoom.getRoomPw().setText(inviteRoom.getLock());
						chatingRoom.setMyId(id);
						chatingRoom.resetChat();
						chatingRoom.roomChat(id + "님이 입장하였습니다.");
						chatingRoom.setVisible(true);
						chatingRoom.updateUser();
						break;
					case RES_INVITE_FAIL:
						JOptionPane.showMessageDialog(null, "초대에 실패하였습니다.(정원초과)");
						break;
					case RES_INVITE_DENY:
						JOptionPane.showMessageDialog(null, "상대방이 초대를 거절하였습니다.");
						break;
					case RES_CHANGE_LEADER:
						String changeLeader = (String) res.getObjs()[0];
						chatingRoom.getRoomLeader().setText(changeLeader);
						JOptionPane.showMessageDialog(null, changeLeader + "님이 방장이 되셨습니다.");
						break;
					case RES_UPDATE_LEADER:
						String changeID = (String) res.getObjs()[0];
						Room leaderChangeRoom = (Room) res.getObjs()[1];
						chatingRoom.setRoom(leaderChangeRoom);
						chatingRoom.getRoomLeader().setText(changeID);
						chatingRoom.updateUser();
						chatingRoom.roomChat("방장이 " + changeID + "님으로 변경이 되었습니다.");
						break;
					case RES_EXIT_ROOM:
						HashMap<Integer, Room> rooms = (HashMap<Integer, Room>) res.getObjs()[0];
						waitingRoom.setRoomList(rooms);
						waitingRoom.setRoom(rooms.get(0));
						chatingRoom.setVisible(false);
						waitingRoom.updateRoom();
						waitingRoom.updateUser();
						waitingRoom.resetChat();
						waitingRoom.setVisible(true);
						break;
					case RES_ADDUSER_UPDATEROOM:
						String outId = (String) res.getObjs()[0];
						waitingRoom.getRoom().getUserList().add(outId);
						Room upRoom = (Room) res.getObjs()[1];
						waitingRoom.getRoomList().put(upRoom.getRoomNum(), upRoom);
						waitingRoom.updateUser();
						waitingRoom.updateRoom();
						waitingRoom.sortUpdate();
						break;
					case RES_UPDATEROOM_OUTMSG:
						String outUser = (String) res.getObjs()[0];
						chatingRoom.getRoom().getUserList().remove(outUser);
						chatingRoom.updateUser();
						chatingRoom.roomChat(outUser + "님이 나가셨습니다.");
						break;
					case RES_LEADER_OUT_ROOM:
						HashMap<Integer, Room> leaderOutRooms = (HashMap<Integer, Room>) res.getObjs()[0];
						Room outRoom = (Room) res.getObjs()[1];
						leaderOutRooms.remove(outRoom.getRoomNum());
						waitingRoom.setRoomList(leaderOutRooms);
						waitingRoom.setRoom(leaderOutRooms.get(0));
						chatingRoom.setVisible(false);
						waitingRoom.updateRoom();
						waitingRoom.updateUser();
						waitingRoom.resetChat();
						makeAndSetting.clear();
						waitingRoom.setVisible(true);
						break;
					case RES_LEADER_OUT_WAITROOM:
						Room eraserRoom = (Room) res.getObjs()[0];
						Vector<String> vUser = (Vector<String>) res.getObjs()[1];
						waitingRoom.getRoomList().remove(eraserRoom.getRoomNum());
						waitingRoom.getRoom().setUserList(vUser);
						waitingRoom.updateUser();
						waitingRoom.updateRoom();
						waitingRoom.sortUpdate();
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				MyUtils.closeAll(oos, ois, socket);
			}
		}

		// 본인 소켓에 요청에 쓰는 것
		private void writeObj(int code) {
			Request req = new Request(code);
			try {
				oos.writeObject(req);
				oos.flush();
				oos.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void writeObj(int code, Object[] objs) {
			Request req = new Request(code, objs);
			try {
				oos.writeObject(req);
				oos.flush();
				oos.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public ObjectOutputStream getOos() {
		return oos;
	}

	public ObjectInputStream getOis() {
		return ois;
	}

	public MakeOrSettingRoomUI getMakeOrSetting() {
		return makeAndSetting;
	}

	public void personal(String id, String msg) {
		personal.personalMsg(id, msg + "\n");
	}

	public PersonalUI getPersonal() {
		return personal;
	}

	public WaitingRoomUI getWaitRoom() {
		return waitingRoom;
	}

	public String getId() {
		return id;
	}

	public InviteUI getInvite() {
		return invite;
	}

	public static void main(String[] args) {
		new Client();
	}
}

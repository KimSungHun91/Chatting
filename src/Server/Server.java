package Server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import java.util.Vector;

import Client.Request;
import Client.Room;

public class Server {
	public static final int WAITROOM = 0;

	private HashMap<Integer, Room> rooms;
	private HashMap<String, ObjectOutputStream> allUser;

	public Server() {
		rooms = new HashMap<Integer, Room>();
		allUser = new HashMap<String, ObjectOutputStream>();
		rooms.put(0, new Room());
		try {
			ServerSocket server = new ServerSocket(10001);
			while (true) {
				Socket socket = server.accept();
				ServerThread svThread = new ServerThread(socket);
				svThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	class ServerThread extends Thread implements IProtocol {
		Socket socket;
		String id;
		int currentRoom;
		ObjectInputStream ois = null;
		ObjectOutputStream oos = null;

		private ServerThread(Socket socket) {
			this.socket = socket;
			try {
				oos = new ObjectOutputStream(socket.getOutputStream());
				ois = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		@Override
		public void run() {
			Object obj = null;
			try {
				while ((obj = ois.readObject()) != null) {
					Request req = (Request) obj;
					switch (req.getCode()) {
					case REQ_LOGIN: // 로그인 요청
						boolean checkID = allUser.containsKey((String) req.getObjs()[0]);
						if (!checkID) {
							id = (String) req.getObjs()[0];
							currentRoom = 0;
							synchronized (allUser) {
								allUser.put(id, oos);
							}
							synchronized (rooms) {
								rooms.get(WAITROOM).getUserList().add(id);
							}
							writeObj(RES_LOGIN_OK, new Object[] { id, rooms });
							for (String waitUser : rooms.get(WAITROOM).getUserList()) {
								synchronized (this) {
									if (waitUser != id) {
										allUser.get(waitUser).writeObject(
												new Response(RES_UPDATE_WAITROOM_USER, new Object[] { id }));
										allUser.get(waitUser).flush();
										allUser.get(waitUser).reset();
									}
								}
							}
						} else {
							writeObj(RES_LOGIN_NO);
						}
						break;

					case REQ_FIND_ROOM:
						writeObj(RES_SEARCH_ROOM, new Object[] {});
						break;

					case REQ_MAKE_ROOM: // 방생성
						Room makeRoom = (Room) req.getObjs()[0];
						boolean flag = true;

						int roomNum = 1;
						while (flag) {
							if (rooms.containsKey(roomNum)) {
								roomNum++;
							} else {
								flag = false;
							}
						}
						currentRoom = roomNum;// 수정 -> 확인
						makeRoom.setRoomNum(roomNum);
						makeRoom.setLeader(id);
						rooms.get(WAITROOM).getUserList().remove(id);// 이거 순서다름
						makeRoom.getUserList().add(id);

						synchronized (rooms) {
							rooms.put(roomNum, makeRoom);
						}
						roomNum++;

						writeObj(RES_SUCCESS_MAKE_UPDATE_ROOM, new Object[] { makeRoom });// 다름

						roomUserWrite(WAITROOM, RES_REMOVEUSER_WAITROOM, new Object[] { id, makeRoom });// 다름
						break;

					case REQ_SETTING_ROOM:
						Room room2 = (Room) req.getObjs()[0];
						room2.setRoomNum(currentRoom);
						if (id.equals(rooms.get(currentRoom).getLeader())) {
							rooms.get(currentRoom).setTitle(room2.getTitle());
							rooms.get(currentRoom).setLock(room2.getLock());
							rooms.get(currentRoom).setMaxUser(room2.getMaxUser());
							rooms.get(currentRoom).setCategory(room2.getCategory());
							roomUserWrite(room2.getRoomNum(), RES_UPDATE_ROOM_LIST, rooms.get(currentRoom));

							roomUserWrite(WAITROOM, RES_UPDATE_WAITROOM_ROOMLIST, rooms.get(currentRoom));
						}
						break;

					case REQ_ENTER_ROOM:
						int roomNumber = (int) req.getObjs()[0];
						if (rooms.containsKey(roomNumber)) {
							if (rooms.get(roomNumber).getUserList().size() < rooms.get(roomNumber).getMaxUser()) {
								if (!rooms.get(roomNumber).getBanList().contains(id)) {
									rooms.get(currentRoom).getUserList().remove(id);
									currentRoom = roomNumber;
									writeObj(RES_ENTER_ROOM, new Object[] { rooms.get(roomNumber) });
									rooms.get(roomNumber).getUserList().add(id);
									roomUserWrite(roomNumber, RES_UPDATE_USER, new Object[] { id });
									roomUserWrite(WAITROOM, RES_REMOVEUSER_WAITROOM,
											new Object[] { id, rooms.get(currentRoom) });
								} else {
									writeObj(RES_ENTER_ROOM_FAIL2);
								}
							} else if (rooms.get(roomNumber).getUserList().size() >= rooms.get(roomNumber)
									.getMaxUser()) {
								writeObj(RES_ENTER_ROOM_FAIL1);
							}
						} else {
							writeObj(RES_ENTER_ROOM_FAIL3);
						}
						break;

					case REQ_WAIT_CHAT: // 대기실 채팅 요청 처리
						String msg = (String) req.getObjs()[0];
						for (String waitUser : rooms.get(WAITROOM).getUserList()) {
							synchronized (this) {
								allUser.get(waitUser)
										.writeObject(new Response(RES_WAIT_CHAT, new Object[] { id, msg }));
								allUser.get(waitUser).flush();
								allUser.get(waitUser).reset();
							}
						}
						break;

					case REQ_ALL_USER:
						Collection<String> collection = allUser.keySet();
						Iterator<String> iter = collection.iterator();
						Vector<String> userList = new Vector<String>();
						while (iter.hasNext()) {
							userList.add(iter.next());
						}
						// 현재 자신이 포함된 방의 인원들을 제외한 모든 유저목록이 나와야한다.
						for (String containUser : rooms.get(currentRoom).getUserList()) {
							userList.remove(containUser);// 포함되는 유저제외
						}
						// 여기까지하면 모든 유저목록 Vector<String> userList 담음
						// 이 다음 Vector<String> userList에서 본인 방의 인원은 제외해야함
						allUser.get(id).writeObject(new Response(RES_ALL_USER, new Object[] { userList }));
						allUser.get(id).flush();
						allUser.get(id).reset();
						break;

					case REQ_FIND_USER:
						String findUser = (String) req.getObjs()[0];
						Set<String> find = allUser.keySet();
						Vector<String> findUsers = new Vector<String>();
						for (String user : find) {
							if (user.contains(findUser)) {
								findUsers.add(user);
							}
						}
						writeObj(RES_FIND_USER, new Object[] { findUsers });
						break;

					case REQ_PERSONAL_CHAT:
						String personalID = (String) req.getObjs()[0];
						if (allUser.containsKey(personalID)) {
							String personalMsg = (String) req.getObjs()[1];

							writeObj(RES_PERSONAL_CHAT_ME, new Object[] { id, personalID, personalMsg });
							Response personalChat = new Response(RES_PERSONAL_CHAT_YOU,
									new Object[] { id, personalMsg });
							synchronized (allUser) {
								allUser.get(personalID).writeObject(personalChat);
								allUser.get(personalID).flush();
								allUser.get(personalID).reset();
							}
						}
						break;

					case REQ_PERSONAL_CHAT_USERLIST:
						Vector<String> ChatUserList = new Vector<String>();
						Collection<String> col = allUser.keySet();
						Iterator<String> itr = col.iterator();
						while (itr.hasNext()) {
							ChatUserList.add(itr.next());
						}
						ChatUserList.remove(id);
						// 10160 응답
						Response res = new Response(RES_PERSONAL_CHAT_USERLIST, new Object[] { ChatUserList });
						synchronized (allUser) {
							allUser.get(id).writeObject(res);
							allUser.get(id).flush();
							allUser.get(id).reset();
						}
						break;

					case REQ_EXIT_PROGRAM:
						rooms.get(WAITROOM).getUserList().remove(id);
						allUser.remove(id);
						writeObj(RES_EXIT_PROGRAM);
						roomUserWrite(WAITROOM, RES_WAIT_USER_DELETE, new Object[] { id });
						break;

					case REQ_ROOM_CHAT:
						String sendedMsg = (String) req.getObjs()[0];
						Response reSend = new Response(RES_ROOM_CHAT, new Object[] { id, sendedMsg });
						for (String roomUser : rooms.get(currentRoom).getUserList()) {
							synchronized (this) {
								allUser.get(roomUser).writeObject(reSend);
								allUser.get(roomUser).flush();
								allUser.get(roomUser).reset();
							}
						}
						break;

					case REQ_INVITE_USER:
						String invitedUser = (String) req.getObjs()[0];
						int key = 0;
						for (Room r : rooms.values()) {
							if (r.getUserList().contains(invitedUser)) {
								key = r.getRoomNum();
							}
						}
						Response resInvite = new Response(RES_INVITE_CONFIRM, new Object[] { id, currentRoom });
						synchronized (allUser) {
							allUser.get(invitedUser).writeObject(resInvite);
							allUser.get(invitedUser).flush();
							allUser.get(invitedUser).reset();
						}
						break;

					case REQ_INVITE_ACCEPT:
						int inviteRoomNum = (int) req.getObjs()[0];
						Room room = rooms.get(inviteRoomNum);
						if (inviteRoomNum != currentRoom) {
							// 강퇴 되었나요?
							if (room.getBanList().contains(id)) {
								Response banedUser = new Response(RES_ENTER_ROOM_FAIL2);
								allUser.get(id).writeObject(banedUser);
								// 현재 인원이 최대인원보다 적은가요?
								// JOptionPane.showMessageDialog(null, "?????");
							} else if (room.getUserList().size() >= room.getMaxUser()) {
								Response fullUser = new Response(RES_INVITE_FAIL);
								allUser.get(id).writeObject(fullUser);
							} else {
								synchronized (rooms) {
									// 수락을 하면 본인을 수락한 방에 추가해준다.
									roomUserWrite(inviteRoomNum, RES_ADD_ROOM_USER, new Object[] { id });// 에러
									rooms.get(inviteRoomNum).getUserList().add(id);
									// 대기실에서 수락할 경우와 방에서 수락할 경우
									if (currentRoom != WAITROOM) {
										// 현재방은 나간다.
										rooms.get(currentRoom).getUserList().remove(id);
									} else {
										// 대기방에서 나간인원을 제외해준다.
										rooms.get(WAITROOM).getUserList().remove(id);
										// 갱신할 방을 업데이트 해준다.
										roomUserWrite(WAITROOM, RES_REMOVEUSER_WAITROOM, new Object[] { id, room });
										// 들어갈 방은 인원을 추가한다.
									}
									currentRoom = inviteRoomNum;
								}
								Response accept = new Response(RES_INVITE_ACCEPT, new Object[] { room });
								allUser.get(id).writeObject(accept);
							}
							allUser.get(id).flush();
							allUser.get(id).reset();
							// 현재 방정보 변경
						}
						break;

					case REQ_INVITE_DENY:
						String denyUser = (String) req.getObjs()[0];
						allUser.get(denyUser).writeObject(new Response(RES_INVITE_DENY));
						allUser.get(denyUser).flush();
						allUser.get(denyUser).reset();
						break;

					case REQ_BAN_USER:
						String kickNickName = (String) req.getObjs()[0];
						if (rooms.get(currentRoom).getLeader().equals(id)
								&& !(kickNickName.equals(rooms.get(currentRoom).getLeader()))) {
							if (rooms.get(currentRoom).getUserList().contains(kickNickName)) {
								synchronized (rooms) {
									rooms.get(currentRoom).getUserList().remove(kickNickName);
									rooms.get(currentRoom).getBanList().add(kickNickName);
									roomUserWrite(currentRoom, RES_MINUS_USER, new Object[] { kickNickName });
									roomUserWrite(WAITROOM, RES_ADDUSER_UPDATEROOM,
											new Object[] { kickNickName, rooms.get(currentRoom) });
									rooms.get(WAITROOM).getUserList().add(kickNickName);
								}
								synchronized (allUser) {
									allUser.get(kickNickName)
											.writeObject(new Response(RES_BAN_USER, new Object[] { rooms }));
									allUser.get(kickNickName).flush();
									allUser.get(kickNickName).reset();
								}
							}
						}
						break;

					case REQ_CHANGE_LEADER:
						// 위임을 받을 아이디가 필요함
						String delegateLeader = (String) req.getObjs()[0];
						String roomLeader = (String) req.getObjs()[1];
						// 방장만 사용가능
						if (roomLeader.equals(id) && !(delegateLeader.equals(roomLeader))) {
							// 클라이언트 방장 정보와 서버의 방장정보가 다르다.
							// 유저가 다르다.
							// 다른 작업과 겹치지 않게 싱크로나이즈
							synchronized (rooms.get(currentRoom)) {
								// 방장을 위임 받을 아이디로 셋팅
								rooms.get(currentRoom).setLeader(delegateLeader);
							}
							// 다른 작업과 겹치지 않게 싱크로 나이즈
							synchronized (allUser) {
								// 상대방이 자기가 방장이 된지 알아야됩니다.
								allUser.get(delegateLeader)
										.writeObject(new Response(RES_CHANGE_LEADER, new Object[] { delegateLeader }));
								allUser.get(delegateLeader).flush();
								allUser.get(delegateLeader).reset();
							}

							// 방장이 다른 유저들에게 방장이 바뀌었다는것을 알려줘야합니다.
							roomUserWrite(currentRoom, RES_UPDATE_LEADER,
									new Object[] { delegateLeader, rooms.get(currentRoom) });
						}
						break;

					case REQ_EXIT_ROOM:
						if (!(rooms.get(currentRoom).getLeader().equals(id))) {
							synchronized (rooms) {
								rooms.get(currentRoom).getUserList().remove(id);
								rooms.get(WAITROOM).getUserList().add(id);
								roomUserWrite(WAITROOM, RES_ADDUSER_UPDATEROOM,
										new Object[] { id, rooms.get(currentRoom) });
							}
							synchronized (allUser) {
								allUser.get(id).writeObject(new Response(RES_EXIT_ROOM, new Object[] { rooms }));
								allUser.get(id).flush();
								allUser.get(id).reset();
							}
							roomUserWrite(currentRoom, RES_UPDATEROOM_OUTMSG, new Object[] { id });
						} else {
							synchronized (rooms) {
								Room temp = rooms.get(currentRoom);
								for (String outUser : temp.getUserList()) {
									rooms.get(WAITROOM).getUserList().add(outUser);
								}
								roomUserWrite(currentRoom, RES_LEADER_OUT_ROOM, new Object[] { rooms, temp });
								roomUserWrite(WAITROOM, RES_LEADER_OUT_WAITROOM,
										new Object[] { rooms.get(currentRoom), rooms.get(WAITROOM).getUserList() });
								rooms.remove(currentRoom);
							}
						}
						currentRoom = WAITROOM;
						break;
					}
				}
			} catch (ClassNotFoundException e) {
				// e.printStackTrace();
			} catch (IOException e) {
				// e.printStackTrace();
			} finally {
				MyUtils.closeAll(oos, ois, socket);
			}
		}

		// 본123인 소켓에 응답 쓰는 것
		private void writeObj(int code) {
			Response res = new Response(code);
			try {
				oos.writeObject(res);
				oos.flush();
				oos.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void writeObj(int code, Object[] objs) {
			Response res = new Response(code, objs);
			try {
				oos.writeObject(res);
				oos.flush();
				oos.reset();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		private void roomUserWrite(int roomNum, int code) {
			Response res = new Response(code);
			for (String user : rooms.get(code).getUserList()) {
				try {
					synchronized (allUser) {
						allUser.get(user).writeObject(res);
						allUser.get(user).flush();
						allUser.get(user).reset();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		private void roomUserWrite(int roomNum, int code, Object... objs) {
			Response res = new Response(code, objs);
			for (String user : rooms.get(roomNum).getUserList()) {
				try {
					synchronized (allUser) {
						allUser.get(user).writeObject(res);
						allUser.get(user).flush();
						allUser.get(user).reset();
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	public static void main(String[] args) {
		new Server();
	}
}

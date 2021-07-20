package UI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

public class ChoiceLbl extends JLabel {
	private ChatingRoomUI owner;

	public ChoiceLbl(ChatingRoomUI owner) {
		this.owner = owner;
		addListener();
	}

	private void addListener() {
		addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				owner.setFocus(getText());
				owner.focusColor();
			};
		});

	}
}

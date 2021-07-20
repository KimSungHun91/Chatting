package UI;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

public class PersonalUserLbl extends JLabel {
	private PersonalUI owner;

	public PersonalUserLbl(PersonalUI owner) {
		this.owner = owner;
		setFont(new Font("±¼¸²", Font.BOLD, 12));
		setPreferredSize(new Dimension(150, 60));
		addListener();
	}

	private void addListener() {
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				owner.setFocus(PersonalUserLbl.this.getText());
				owner.showClickMsg();
				owner.focusType();
				setBackground(null);
			}
		});
	}
}

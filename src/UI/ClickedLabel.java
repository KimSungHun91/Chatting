package UI;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

public class ClickedLabel extends JLabel {
	private InviteUI owner;

	public ClickedLabel(InviteUI owner) {
		super();
		this.owner = owner;
		addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (owner.getMode() == owner.INVITEMODE) {
					owner.setFocus(ClickedLabel.this.getText());
					owner.checkFocus();
				} else {
					owner.setFocus(ClickedLabel.this.getText());
					owner.checkFocus();
				}
			}
		});
	}
}

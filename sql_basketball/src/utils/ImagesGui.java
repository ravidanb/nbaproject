package utils;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

public interface ImagesGui {

	ImageIcon HEADLINE_ICON = new ImageIcon("Bagget.png");
	ImageIcon TEAMS_ICON = new ImageIcon("rsz_team3.png");
	ImageIcon PLAYERS_ICON = new ImageIcon("rsz_player.png");
	ImageIcon GAMES_ICON = new ImageIcon("rsz_game.png");
	ImageIcon MANAGEMENT_ICON = new ImageIcon("rsz_wrench.png");
	ImageIcon BASKETBALL_ICON = new ImageIcon("basketball_icon.png");
	ImageIcon USER_ICON = new ImageIcon("rsz_user.png");
	ImageIcon ADD_ICON = new ImageIcon("rsz_add.png");
	ImageIcon REMOVE_ICON =new ImageIcon("rsz_remove.png");
	ImageIcon UPDATE_ICON = new ImageIcon("rsz_update.png");
	ImageIcon TRADE_ICON = new ImageIcon("rsz_trade.png");
	ImageIcon INSERT_ICON =new ImageIcon("rsz_insert.png");
	
	public static BufferedImage resize(BufferedImage img, int newW, int newH) {
		Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
		BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g2d = dimg.createGraphics();
		g2d.drawImage(tmp, 0, 0, null);
		g2d.dispose();
		return dimg;
	}
}

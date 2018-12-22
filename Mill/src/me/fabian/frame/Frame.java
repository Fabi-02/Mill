package me.fabian.frame;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

import me.fabian.utils.Player;

/**
 * @author Fabian
 */

@SuppressWarnings("serial")
public class Frame extends JPanel implements ActionListener {

	public static Player player = Player.BLACK;
	JFrame frame;
	public static int[][] waiting_black = { { -100, 100 }, { -100, 200 }, { -100, 300 }, { -100, 400 }, { -100, 500 },
			{ -100, 600 }, { -100, 700 }, { -100, 800 }, { -100, 900 } };
	public static int[][] waiting_white = { { 1100, 100 }, { 1100, 200 }, { 1100, 300 }, { 1100, 400 }, { 1100, 500 },
			{ 1100, 600 }, { 1100, 700 }, { 1100, 800 }, { 1100, 900 } };
	public static int[][] positions = { { 100, 100 }, { 500, 100 }, { 900, 100 }, { 250, 250 }, { 500, 250 },
			{ 750, 250 }, { 400, 400 }, { 500, 400 }, { 600, 400 }, { 100, 500 }, { 250, 500 }, { 400, 500 },
			{ 600, 500 }, { 750, 500 }, { 900, 500 }, { 400, 600 }, { 500, 600 }, { 600, 600 }, { 250, 750 },
			{ 500, 750 }, { 750, 750 }, { 100, 900 }, { 500, 900 }, { 900, 900 } };
	public static ArrayList<int[]> pos_black = new ArrayList<>();
	public static ArrayList<int[]> pos_white = new ArrayList<>();
	public static ArrayList<int[]> highlight = new ArrayList<>();
	public static ArrayList<int[]> currentMuehle = new ArrayList<>();

	public static int black_count = 0;
	public static int white_count = 0;

	public Frame() {
		frame = new JFrame("Mill Board Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		// frame.setResizable(false);
		frame.setSize(800, 600);
		frame.setMinimumSize(new Dimension(300, 242));
		frame.setVisible(true);
		frame.add(this);

		init();
	}

	Timer time;

	BufferedImage brett;
	BufferedImage black;
	BufferedImage white;
	BufferedImage hblack;
	BufferedImage hwhite;
	BufferedImage icon;

	private void init() {

		try {
			brett = ImageIO.read(getClass().getClassLoader().getResource("me/fabian/textures/Board.png"));
			black = ImageIO.read(getClass().getClassLoader().getResource("me/fabian/textures/Black.png"));
			white = ImageIO.read(getClass().getClassLoader().getResource("me/fabian/textures/White.png"));
			hblack = ImageIO.read(getClass().getClassLoader().getResource("me/fabian/textures/Black_Green.png"));
			hwhite = ImageIO.read(getClass().getClassLoader().getResource("me/fabian/textures/White_Green.png"));
			icon = ImageIO.read(getClass().getClassLoader().getResource("me/fabian/textures/Icon.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}

		frame.setIconImage(icon);

		frame.addKeyListener(new keyType());
		frame.addMouseListener(new mouseClick());

		time = new Timer(50, this);
		time.start();
	}

	public static int brettX;
	public static int brettY;
	public static double scale;

	@SuppressWarnings("static-access")
	@Override
	public void paint(Graphics g) {
		super.paint(g);

		if (brett != null) {
			int i = 0;
			int brettHigh = brett.getHeight();
			int brettWidth = brett.getWidth();
			if (getHeight() * 1.4 < getWidth()) {
				i = getHeight();
				scale = (double) getHeight() / (double) brettHigh;
				brettX = (int) ((getWidth() - i) / 2 - (scale) * 200);
				brettY = (getHeight() - i) / 2;
			} else {
				i = getWidth();
				scale = (double) getWidth() / (double) brettWidth;
				brettX = (getWidth() - i) / 2;
				brettY = (int) ((getHeight() - i) / 2 + (scale) * 200);
			}
			g.drawImage(brett, brettX, brettY, (int) (scale * brett.getWidth()), (int) (scale * brett.getHeight()),
					null);
			int black_count = waiting_black.length;
			for (int[] pos : waiting_black) {
				black_count--;
				// if (black_count >= pos_black.size()) {
				if (black_count >= this.black_count) {
					if (player == Player.BLACK && black_count == this.black_count && currentMuehle.isEmpty()) {
						drawPlayer(g, pos[0], pos[1], hblack);
					} else {
						drawPlayer(g, pos[0], pos[1], black);
					}
				}

			}
			int white_count = waiting_white.length;
			for (int[] pos : waiting_white) {
				white_count--;
				if (white_count >= this.white_count) {
					if (player == Player.WHITE && white_count == this.white_count && currentMuehle.isEmpty()) {
						drawPlayer(g, pos[0], pos[1], hwhite);
					} else {
						drawPlayer(g, pos[0], pos[1], white);
					}
				}

			}
			for (int[] pos : pos_black) {
				if (highlight.contains(pos) || currentMuehle.contains(pos)) {
					drawPlayer(g, pos[0], pos[1], hblack);
				} else {
					drawPlayer(g, pos[0], pos[1], black);
				}

			}
			for (int[] pos : pos_white) {
				if (highlight.contains(pos) || currentMuehle.contains(pos)) {
					drawPlayer(g, pos[0], pos[1], hwhite);
				} else {
					drawPlayer(g, pos[0], pos[1], white);
				}
			}
		}
	}

	public static void drawPlayer(Graphics g, int x, int y, BufferedImage image) {
		int playerHeigh = (int) (scale * 70);
		int playerWidth = (int) (scale * 70);
		g.drawImage(image, (int) (brettX + (scale * 200) + (x * scale) - playerWidth / 2),
				(int) (brettY + (y * scale) - playerHeigh / 2), playerWidth, playerHeigh, null);
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		repaint();

	}

	private class keyType extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
		}

		public void keyReleased(KeyEvent e) {
		}
	}

	private class mouseClick extends JComponent implements MouseListener {

		@Override
		public void mouseClicked(MouseEvent e) {
		}

		@Override
		public void mouseEntered(MouseEvent e) {
		}

		@Override
		public void mouseExited(MouseEvent e) {
		}

		@Override
		public void mousePressed(MouseEvent e) {

			int x = e.getX() - 7;
			int y = e.getY() - 28;

			x = x - brettX;
			y = y - brettY;

			x = (int) (x - (scale * 200));

			x = (int) (x / scale);
			y = (int) (y / scale);

			if (!currentMuehle.isEmpty()) {
				for (int[] pos : positions) {
					int minX = pos[0] - 35;
					int minY = pos[1] - 35;
					int maxX = pos[0] + 35;
					int maxY = pos[1] + 35;
					if (x >= minX && y >= minY && x <= maxX && y <= maxY) {
						if (player == Player.BLACK) {
							if (Frame.pos_white.contains(pos) && !OnlyTestMuehleOther(pos)) {
								Frame.pos_white.remove(pos);
								player = Player.WHITE;
								currentMuehle.clear();
							}
						} else if (player == Player.WHITE) {
							if (Frame.pos_black.contains(pos) && !OnlyTestMuehleOther(pos)) {
								Frame.pos_black.remove(pos);
								player = Player.BLACK;
								currentMuehle.clear();
							}
						}
					}
				}
				return;
			}

			if (Frame.black_count >= Frame.waiting_black.length && Frame.white_count >= Frame.waiting_white.length) {
				for (int[] pos : positions) {
					int minX = pos[0] - 35;
					int minY = pos[1] - 35;
					int maxX = pos[0] + 35;
					int maxY = pos[1] + 35;
					if (x >= minX && y >= minY && x <= maxX && y <= maxY) {
						if (player == Player.BLACK) {
							if (Frame.pos_black.contains(pos)) {
								highlight.clear();
								highlight.add(pos);
								break;
							}
						} else if (player == Player.WHITE) {
							if (Frame.pos_white.contains(pos)) {
								highlight.clear();
								highlight.add(pos);
								break;
							}
						}
					}
				}
			} else {
				for (int[] pos : positions) {
					int minX = pos[0] - 20;
					int minY = pos[1] - 20;
					int maxX = pos[0] + 20;
					int maxY = pos[1] + 20;
					if (x >= minX && y >= minY && x <= maxX && y <= maxY) {
						if (Frame.pos_black.contains(pos) || Frame.pos_white.contains(pos)) {
							continue;
						}
						if (player == Player.BLACK && Frame.black_count < Frame.waiting_black.length) {
							Frame.pos_black.add(pos);
							if (!testMuehle(pos)) {
								player = Player.WHITE;
							} else {
								boolean b = true;
								for (int[] pos_white : pos_white) {
									if (!OnlyTestMuehleOther(pos_white)) {
										b = false;
										break;
									}
								}
								if (b) {
									currentMuehle.clear();
									player = Player.WHITE;
								}
							}
							Frame.black_count++;
						} else if (player == Player.WHITE && Frame.white_count < Frame.waiting_white.length) {
							Frame.pos_white.add(pos);
							if (!testMuehle(pos)) {
								player = Player.BLACK;
							} else {
								boolean b = true;
								for (int[] pos_black : pos_black) {
									if (!OnlyTestMuehleOther(pos_black)) {
										b = false;
										break;
									}
								}
								if (b) {
									currentMuehle.clear();
									player = Player.BLACK;
								}
							}
							Frame.white_count++;
						}
						break;
					}
				}
			}
			for (int[] pos : positions) {
				int minX = pos[0] - 20;
				int minY = pos[1] - 20;
				int maxX = pos[0] + 20;
				int maxY = pos[1] + 20;
				if (x >= minX && y >= minY && x <= maxX && y <= maxY) {
					if (Frame.pos_black.contains(pos) || Frame.pos_white.contains(pos)) {
						continue;
					}
					if (!highlight.isEmpty()) {
						if (player == Player.BLACK && (pos_black.size() <= 3 ? true : canMove(highlight.get(0), pos))) {
							pos_black.remove(highlight.get(0));
							pos_black.add(pos);
							if (!testMuehle(pos)) {
								player = Player.WHITE;
							} else {
								boolean b = true;
								for (int[] pos_white : pos_white) {
									if (!OnlyTestMuehleOther(pos_white)) {
										b = false;
										break;
									}
								}
								if (b) {
									currentMuehle.clear();
									player = Player.WHITE;
								}
							}
							highlight.clear();
						} else if (player == Player.WHITE
								&& (pos_white.size() <= 3 ? true : canMove(highlight.get(0), pos))) {
							pos_white.remove(highlight.get(0));
							pos_white.add(pos);
							if (!testMuehle(pos)) {
								player = Player.BLACK;
							} else {
								boolean b = true;
								for (int[] pos_black : pos_black) {
									if (!OnlyTestMuehleOther(pos_black)) {
										b = false;
										break;
									}
								}
								if (b) {
									currentMuehle.clear();
									player = Player.BLACK;
								}
							}
							highlight.clear();
						}
					}
					break;
				}
			}
		}

		@Override
		public void mouseReleased(MouseEvent e) {
		}
	}

	public static boolean canMove(int[] from, int[] to) {
		boolean b = false;
		if (Frame.pos_black.contains(to) || Frame.pos_white.contains(to)) {
		} else if (player == Player.BLACK) {
			int i1 = 0;
			for (int[] pos : positions) {
				i1++;
				if (pos == from) {
					break;
				}
			}
			int i2 = 0;
			for (int[] pos : positions) {
				i2++;
				if (pos == to) {
					break;
				}
			}
			int[] r;
			switch (i1) {
			case 1:
				r = new int[] { 2, 10 };
				break;
			case 2:
				r = new int[] { 1, 3, 5 };
				break;
			case 3:
				r = new int[] { 2, 15 };
				break;
			case 4:
				r = new int[] { 5, 11 };
				break;
			case 5:
				r = new int[] { 2, 4, 8, 6 };
				break;
			case 6:
				r = new int[] { 5, 14 };
				break;
			case 7:
				r = new int[] { 12, 8 };
				break;
			case 8:
				r = new int[] { 5, 7, 9 };
				break;
			case 9:
				r = new int[] { 8, 13 };
				break;
			case 10:
				r = new int[] { 1, 11, 22 };
				break;
			case 11:
				r = new int[] { 4, 10, 19, 12 };
				break;
			case 12:
				r = new int[] { 7, 11, 16 };
				break;
			case 13:
				r = new int[] { 9, 14, 18 };
				break;
			case 14:
				r = new int[] { 6, 13, 21, 15 };
				break;
			case 15:
				r = new int[] { 3, 14, 24 };
				break;
			case 16:
				r = new int[] { 12, 17 };
				break;
			case 17:
				r = new int[] { 16, 20, 18 };
				break;
			case 18:
				r = new int[] { 13, 17 };
				break;
			case 19:
				r = new int[] { 11, 20 };
				break;
			case 20:
				r = new int[] { 17, 19, 23, 21 };
				break;
			case 21:
				r = new int[] { 14, 20 };
				break;
			case 22:
				r = new int[] { 10, 23 };
				break;
			case 23:
				r = new int[] { 22, 20, 24 };
				break;
			case 24:
				r = new int[] { 23, 15 };
				break;
			default:
				r = new int[] {};
				break;
			}
			for (int pos : r) {
				if (i2 == pos) {
					b = true;
					break;
				}
			}
		} else if (player == Player.WHITE) {
			int i1 = 0;
			for (int[] pos : positions) {
				i1++;
				if (pos == from) {
					break;
				}
			}
			int i2 = 0;
			for (int[] pos : positions) {
				i2++;
				if (pos == to) {
					break;
				}
			}
			int[] r;
			switch (i1) {
			case 1:
				r = new int[] { 2, 10 };
				break;
			case 2:
				r = new int[] { 1, 3, 5 };
				break;
			case 3:
				r = new int[] { 2, 15 };
				break;
			case 4:
				r = new int[] { 5, 11 };
				break;
			case 5:
				r = new int[] { 2, 4, 8, 6 };
				break;
			case 6:
				r = new int[] { 5, 14 };
				break;
			case 7:
				r = new int[] { 12, 8 };
				break;
			case 8:
				r = new int[] { 5, 7, 9 };
				break;
			case 9:
				r = new int[] { 8, 13 };
				break;
			case 10:
				r = new int[] { 1, 11, 22 };
				break;
			case 11:
				r = new int[] { 4, 10, 19, 12 };
				break;
			case 12:
				r = new int[] { 7, 11, 16 };
				break;
			case 13:
				r = new int[] { 9, 14, 18 };
				break;
			case 14:
				r = new int[] { 6, 13, 21, 15 };
				break;
			case 15:
				r = new int[] { 3, 14, 24 };
				break;
			case 16:
				r = new int[] { 12, 17 };
				break;
			case 17:
				r = new int[] { 16, 20, 18 };
				break;
			case 18:
				r = new int[] { 13, 17 };
				break;
			case 19:
				r = new int[] { 11, 20 };
				break;
			case 20:
				r = new int[] { 17, 19, 23, 21 };
				break;
			case 21:
				r = new int[] { 14, 20 };
				break;
			case 22:
				r = new int[] { 10, 23 };
				break;
			case 23:
				r = new int[] { 22, 20, 24 };
				break;
			case 24:
				r = new int[] { 23, 15 };
				break;
			default:
				r = new int[] {};
				break;
			}
			for (int pos : r) {
				if (i2 == pos) {
					b = true;
					break;
				}
			}
		}
		return b;
	}

	public static boolean canMove(int[] pos) {
		int id = getId(pos);
		int[] r;
		switch (id) {
		case 1:
			r = new int[] { 2, 10 };
			break;
		case 2:
			r = new int[] { 1, 3, 5 };
			break;
		case 3:
			r = new int[] { 2, 15 };
			break;
		case 4:
			r = new int[] { 5, 11 };
			break;
		case 5:
			r = new int[] { 2, 4, 8, 6 };
			break;
		case 6:
			r = new int[] { 5, 14 };
			break;
		case 7:
			r = new int[] { 12, 8 };
			break;
		case 8:
			r = new int[] { 5, 7, 9 };
			break;
		case 9:
			r = new int[] { 8, 13 };
			break;
		case 10:
			r = new int[] { 1, 11, 22 };
			break;
		case 11:
			r = new int[] { 4, 10, 19, 12 };
			break;
		case 12:
			r = new int[] { 7, 11, 16 };
			break;
		case 13:
			r = new int[] { 9, 14, 18 };
			break;
		case 14:
			r = new int[] { 6, 13, 21, 15 };
			break;
		case 15:
			r = new int[] { 3, 14, 24 };
			break;
		case 16:
			r = new int[] { 12, 17 };
			break;
		case 17:
			r = new int[] { 16, 20, 18 };
			break;
		case 18:
			r = new int[] { 13, 17 };
			break;
		case 19:
			r = new int[] { 11, 20 };
			break;
		case 20:
			r = new int[] { 17, 19, 23, 21 };
			break;
		case 21:
			r = new int[] { 14, 20 };
			break;
		case 22:
			r = new int[] { 10, 23 };
			break;
		case 23:
			r = new int[] { 22, 20, 24 };
			break;
		case 24:
			r = new int[] { 23, 15 };
			break;
		default:
			r = new int[] {};
			break;
		}
		boolean move = false;
		for (int ids : r) {
			if (move == false) {
				boolean m = true;
				for (int[] pos_white : pos_white) {
					int id_white = getId(pos_white);
					if (ids == id_white) {
						m = false;
						break;
					}
				}
				if (m == true) {
					for (int[] pos_black : pos_black) {
						int id_black = getId(pos_black);
						if (ids == id_black) {
							m = false;
							break;
						}
					}
				}
				move = m;
				if (m = true) {
					break;
				}
			}
		}
		return move;
	}

	public static boolean testMuehle(int[] test) {
		currentMuehle.clear();

		ArrayList<int[]> muehlen = new ArrayList<>();
		muehlen.add(new int[] { 1, 2, 3 });
		muehlen.add(new int[] { 4, 5, 6 });
		muehlen.add(new int[] { 7, 8, 9 });
		muehlen.add(new int[] { 10, 11, 12 });
		muehlen.add(new int[] { 13, 14, 15 });
		muehlen.add(new int[] { 16, 17, 18 });
		muehlen.add(new int[] { 19, 20, 21 });
		muehlen.add(new int[] { 22, 23, 24 });
		muehlen.add(new int[] { 1, 10, 22 });
		muehlen.add(new int[] { 4, 11, 19 });
		muehlen.add(new int[] { 7, 12, 16 });
		muehlen.add(new int[] { 2, 5, 8 });
		muehlen.add(new int[] { 17, 20, 23 });
		muehlen.add(new int[] { 9, 13, 18 });
		muehlen.add(new int[] { 6, 14, 21 });
		muehlen.add(new int[] { 3, 15, 24 });

		int id = getId(test);
		for (int[] positions : muehlen) {
			for (int pos : positions) {
				if (pos == id) {
					if (player == Player.BLACK) {
						int count = 0;
						for (int[] position_black : pos_black) {
							for (int muehle_id : positions) {
								int black_id = getId(position_black);
								if (muehle_id == black_id) {
									count++;
									currentMuehle.add(position_black);
								}
							}
						}
						if (count == 3) {
							return true;
						} else {
							currentMuehle.clear();
						}
					} else if (player == Player.WHITE) {
						int count = 0;
						for (int[] position_white : pos_white) {
							for (int muehle_id : positions) {
								int black_id = getId(position_white);
								if (muehle_id == black_id) {
									count++;
									currentMuehle.add(position_white);
								}
							}
						}
						if (count == 3) {
							return true;
						} else {
							currentMuehle.clear();
						}
					}
					break;
				}
			}
		}
		currentMuehle.clear();
		return false;
	}

	public static boolean OnlyTestMuehleOther(int[] test) {
		ArrayList<int[]> muehlen = new ArrayList<>();
		muehlen.add(new int[] { 1, 2, 3 });
		muehlen.add(new int[] { 4, 5, 6 });
		muehlen.add(new int[] { 7, 8, 9 });
		muehlen.add(new int[] { 10, 11, 12 });
		muehlen.add(new int[] { 13, 14, 15 });
		muehlen.add(new int[] { 16, 17, 18 });
		muehlen.add(new int[] { 19, 20, 21 });
		muehlen.add(new int[] { 22, 23, 24 });
		muehlen.add(new int[] { 1, 10, 22 });
		muehlen.add(new int[] { 4, 11, 19 });
		muehlen.add(new int[] { 7, 12, 16 });
		muehlen.add(new int[] { 2, 5, 8 });
		muehlen.add(new int[] { 17, 20, 23 });
		muehlen.add(new int[] { 9, 13, 18 });
		muehlen.add(new int[] { 6, 14, 21 });
		muehlen.add(new int[] { 3, 15, 24 });
		int id = getId(test);
		for (int[] positions : muehlen) {
			for (int pos : positions) {
				if (pos == id) {
					if (player == Player.WHITE) {
						int count = 0;
						for (int[] position_black : pos_black) {
							for (int muehle_id : positions) {
								int black_id = getId(position_black);
								if (muehle_id == black_id) {
									count++;
								}
							}
						}
						if (count == 3) {
							return true;
						}
					} else if (player == Player.BLACK) {
						int count = 0;
						for (int[] position_white : pos_white) {
							for (int muehle_id : positions) {
								int black_id = getId(position_white);
								if (muehle_id == black_id) {
									count++;
								}
							}
						}
						if (count == 3) {
							return true;
						}
					}
					break;
				}
			}
		}
		return false;
	}

	public static boolean OnlyTestMuehle(int[] test) {
		ArrayList<int[]> muehlen = new ArrayList<>();
		muehlen.add(new int[] { 1, 2, 3 });
		muehlen.add(new int[] { 4, 5, 6 });
		muehlen.add(new int[] { 7, 8, 9 });
		muehlen.add(new int[] { 10, 11, 12 });
		muehlen.add(new int[] { 13, 14, 15 });
		muehlen.add(new int[] { 16, 17, 18 });
		muehlen.add(new int[] { 19, 20, 21 });
		muehlen.add(new int[] { 22, 23, 24 });
		muehlen.add(new int[] { 1, 10, 22 });
		muehlen.add(new int[] { 4, 11, 19 });
		muehlen.add(new int[] { 7, 12, 16 });
		muehlen.add(new int[] { 2, 5, 8 });
		muehlen.add(new int[] { 17, 20, 23 });
		muehlen.add(new int[] { 9, 13, 18 });
		muehlen.add(new int[] { 6, 14, 21 });
		muehlen.add(new int[] { 3, 15, 24 });
		int id = getId(test);
		for (int[] positions : muehlen) {
			for (int pos : positions) {
				if (pos == id) {
					if (player == Player.BLACK) {
						int count = 0;
						for (int[] position_black : pos_black) {
							for (int muehle_id : positions) {
								int black_id = getId(position_black);
								if (muehle_id == black_id) {
									count++;
								}
							}
						}
						if (count == 3) {
							return true;
						}
					} else if (player == Player.WHITE) {
						int count = 0;
						for (int[] position_white : pos_white) {
							for (int muehle_id : positions) {
								int black_id = getId(position_white);
								if (muehle_id == black_id) {
									count++;
								}
							}
						}
						if (count == 3) {
							return true;
						}
					}
					break;
				}
			}
		}
		return false;
	}

	public static int getId(int[] position) {
		int id = 0;
		for (int[] pos : positions) {
			id++;
			if (pos == position) {
				break;
			}
		}
		return id;
	}
}

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JPanel;

public class GamePanel extends JPanel implements Runnable, KeyListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//frame
	public static final int width = 400;
	public static final int height = 400;
	
	//Render
	private Graphics2D g2d;
	private BufferedImage image;
	
	//Game Loop
	private Thread thread;
	private boolean running;
	private long targetTime; 
	
	//Game
	private final int SIZE = 10;
	Entity head, dan;
	ArrayList<Entity> snake;
	private int score;
	private int level;
	private boolean gameover;
	
	//movement
	private int dx, dy;
	
	//key input
	private boolean up, down, right, left, enter;
	
	public GamePanel() {
		setPreferredSize(new Dimension(width,height));
		setFocusable(true);
		requestFocus();
		addKeyListener(this);
	}
	
	public void addNotify() {
		super.addNotify();
		thread = new Thread(this);
		thread.start();
	}
	
	private void setFPS(int fps) {
		targetTime = 1000/fps;
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void keyPressed(KeyEvent e) {
		int k = e.getKeyCode();
		
		if( k == KeyEvent.VK_UP) up = true;
		if( k == KeyEvent.VK_DOWN) down = true;
		if( k == KeyEvent.VK_RIGHT) right = true;
		if( k == KeyEvent.VK_LEFT) left = true;
		if( k == KeyEvent.VK_ENTER) enter = true;

	}

	@Override
	public void keyReleased(KeyEvent e) {
		int k = e.getKeyCode();
		
		if( k == KeyEvent.VK_UP) up = false;
		if( k == KeyEvent.VK_DOWN) down = false;
		if( k == KeyEvent.VK_RIGHT) right = false;
		if( k == KeyEvent.VK_LEFT) left = false;
		if( k == KeyEvent.VK_ENTER) enter = false;

	}

	@Override
	public void run() {
		if(running) {
			return;
		}
		init();
		long startTime;
		long elapsed;
		long wait;
		while(running) {
			startTime = System.nanoTime();
			
			update();
			requestRender();
			
			elapsed = System.nanoTime() - startTime;
			wait = targetTime - elapsed/1000000;
			if(wait > 0) {
				try {
					Thread.sleep(wait);
					
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void init() {
		
		image = new BufferedImage(width,height, BufferedImage.TYPE_INT_ARGB);
		g2d = image.createGraphics();
		
		running = true;
		setUpLevel();
		gameover = false;
		level = 1;
		setFPS(level * 10);
		
	}
	
	private void setUpLevel() {
		snake = new ArrayList<Entity>();
		
		head = new Entity(SIZE);
		head.setPosition(width/2, height/2);
		snake.add(head);
		
		for(int i = 1; i < 3; i++) {
			Entity e = new Entity(SIZE);
			e.setPosition(head.getX() + (i*SIZE), head.getY());
			snake.add(e);
		}
		
		dan = new Entity(SIZE);
		setDan();
		score = 0;
		gameover = false;
	}
	
	public void setDan() {
		int x = (int)(Math.random() * (width - SIZE));
		int y = (int)(Math.random() * (height - SIZE));
		x = x - (x%SIZE);
		y = y - (y%SIZE);
		
		dan.setPosition(x, y);
	}
	
	private void requestRender() {
		// TODO Auto-generated method stub
		render(g2d);
		Graphics g = getGraphics();
		g.drawImage(image, 0, 0, null);
		g.dispose();
	}

	private void update() {
		
		if(gameover) {
			if(enter) {
				setUpLevel();
			}
			return;
		}
		
		if(up && dy == 0) {
			dy = -SIZE;
			dx = 0;
		}
		
		if(down && dy == 0) {
			dy = SIZE;
			dx = 0;
		}
		
		if(left && dx == 0) {
			dy = 0;
			dx = -SIZE;
		}
		
		if(right && dx == 0 && dy != 0) {
			dy = 0;
			dx = SIZE;
		}
		
		
		if(dx != 0 || dy != 0 ) {
		
			for(int i = snake.size() - 1; i > 0; i--) {
				snake.get(i).setPosition(
				snake.get(i-1).getX(),
				snake.get(i-1).getY());
			}
			head.move(dx,dy);
		}
		
		for(Entity e : snake) {
			if(e.isCollision(head)) {
				gameover = true;
				break;
			}
		}
		
		if(dan.isCollision(head)) {
			score++; 
			setDan();
			
			Entity e = new Entity(SIZE);
			e.setPosition(-100,-100);
			snake.add(e);
			if(score % 10 == 0) {
				level ++;
				if(level > 10) level = 10;
				setFPS(level * 10);
			}
		}
		
		if(head.getX() < 0) head.setX(width);
		if(head.getY() < 0) head.setY(height);
		if(head.getX() > width) head.setX(0);
		if(head.getY() > height) head.setY(0);
		
	}
	
	public void render(Graphics2D g2d) {
		g2d.clearRect(0, 0, width, height);
		
		g2d.setColor(Color.YELLOW);
		for(Entity e : snake) {
			e.render(g2d);
		}
		
		g2d.setColor(Color.RED);
		dan.render(g2d);
		g2d.setColor(Color.WHITE);
		g2d.drawString("Score: " + score, 2, 12);
		g2d.drawString("Level: " + level, 2, 25);
		
		if(gameover) {
			g2d.setColor(Color.RED);
			g2d.drawString("Game Over", width/2 - 10, height/2 - 10);
		}
		
		if(dx == 0 && dy == 0) {
			g2d.setColor(Color.RED);
			g2d.drawString("Start", width/2 - 10, height/2 - 10);
		}
		
	}



}

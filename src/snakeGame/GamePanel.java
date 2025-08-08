package snakeGame;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener{

	static final int SCREEN_WIDTH = 600;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 25;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;
	
	//  this array hold all of the x coordinates of the body including the head of the snake
	final int x[] = new int[GAME_UNITS];
	
	//  and this array hold all of the y coordinates 
	final int y[] = new int[GAME_UNITS];
	
	int bodyParts = 2;
	
	// apple position and eaten
	int applesEaten;
	int appleX;
	int appleY;
	
	// the snake start running to Right. U -> Up ; D -> Down ; L -> Left
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random random;
	
	BufferedImage appleImage;
	BufferedImage snakeHead;
	BufferedImage snakeBody;
	BufferedImage snakeTail;
	// constructors
	GamePanel(){
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH,SCREEN_HEIGHT));
		this.setBackground(Color.white);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		loadImage();
		startGame();
	}
	public void startGame() {
		newApple();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}
	
	public void draw(Graphics g) {
		
		if (running) {
			// con este codigo podemos ver cada uno de los units size representados en el panel
			// for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
			// 	g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
			// 	g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
			// }
			

			g.drawImage(appleImage, appleX, appleY, UNIT_SIZE, UNIT_SIZE, null);
			
			for(int i = 0; i < bodyParts; i++) {
				if(i == 0) {
					BufferedImage rotatedHead = snakeHead;
					switch (direction) {
						case 'U':
							rotatedHead = rotateImage(snakeHead, -90);
							break;
						case 'D':
								rotatedHead = rotateImage(snakeHead, 90);
								break;
						case 'L':
							rotatedHead = rotateImage(snakeHead, 180);
							break;
						case 'R':
							rotatedHead = snakeHead;
							break;
					}

					g.drawImage(rotatedHead,x[i], y[i], UNIT_SIZE, UNIT_SIZE, null);
					// g.setColor(Color.orange);
					// g.fillRect(x[i],y[i], UNIT_SIZE, UNIT_SIZE);
				} else if(i == bodyParts-1){
					BufferedImage rotatedTail = snakeTail;
					switch (direction) {
						case 'U':
							rotatedTail = rotateImage(snakeTail, 90);
							break;
						case 'D':
								rotatedTail = rotateImage(snakeTail, -90);
								break;
						case 'L':
							rotatedTail = snakeTail;
							break;
						case 'R':
							rotatedTail = rotateImage(snakeTail, 180);;
							break;
					}

					g.drawImage(rotatedTail,x[i], y[i], UNIT_SIZE, UNIT_SIZE, null);
				} else {
					BufferedImage rotatedBody = snakeBody;
					switch (direction) {
						case 'D':
							rotatedBody = rotateImage(snakeBody, 90);
							break;
						case 'U':
							rotatedBody = rotateImage(snakeBody, 90);
							break;
					
					}
					g.drawImage(rotatedBody,x[i], y[i], UNIT_SIZE, UNIT_SIZE, null);
					// g.setColor(new Color(45,180,0));	
					// g.fillRect(x[i],y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			
			// score
			g.setColor(Color.red);
			g.setFont(new Font("Ink Free", Font.BOLD, 40));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score: "+ applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: "+ applesEaten))/2, g.getFont().getSize());
		} else {
			gameOver(g);
		}
	}
	
	// every time when we generate a new game or eat one apple, it has to call this method to generate new coordinate 
	public void newApple() {
		appleX = random.nextInt((int)(SCREEN_WIDTH / UNIT_SIZE))*UNIT_SIZE;
		appleY = random.nextInt((int)(SCREEN_HEIGHT / UNIT_SIZE))*UNIT_SIZE;
	}	
	
	public void move() {
		for(int i = bodyParts;i>0; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		
		switch(direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
			
		}
	}
	
	public void checkApple() {
		if ((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts++;
			applesEaten++;
			newApple();
		}
	}
	
	public void checkCollisions() {
		
		// checks if head collides with body
		for(int i = bodyParts;i>0;i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		
		// checks if head touches left border
		if(x[0] < 0) {
			running = false;
		}
		
		// checks if head touches right boder
		if(x[0] > SCREEN_WIDTH) {
			running = false;
		}
		
		// checks if head touches top border
		if(y[0] < 0) {
			running = false;
		}
		
		// checks if head touches bottom border
		if(y[0] > SCREEN_HEIGHT) {
			running = false;
		}
		
		if(!running) {
			timer.stop();
		}
	}
	
	public void gameOver(Graphics g) {
		
		// score
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 40));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Score: "+ applesEaten, (SCREEN_WIDTH - metrics1.stringWidth("Score: "+ applesEaten))/2, g.getFont().getSize());
		
		// game over text
		g.setColor(Color.red);
		g.setFont(new Font("Ink Free", Font.BOLD, 75));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Game over", (SCREEN_WIDTH - metrics2.stringWidth("Game over"))/2, SCREEN_HEIGHT/2);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if(running) {
			move();
			checkApple();
			checkCollisions();
		}
		repaint();
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_RIGHT:
				if (direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_UP:
				if (direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_DOWN:
				if (direction != 'U') {
					direction = 'D';
				}
				break;
			}
		}
	}
	
	public void loadImage() {
		try {
			appleImage = ImageIO.read(getClass().getResource("Apple.png"));
			snakeHead = ImageIO.read(getClass().getResource("snakeHead.png"));
			snakeBody = ImageIO.read(getClass().getResource("snakeBody.png"));
			snakeTail = ImageIO.read(getClass().getResource("snakeTail.png"));
			if (appleImage == null || snakeHead == null || snakeBody == null || snakeTail == null) {
				System.err.println("Imagen no encontrada");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private BufferedImage rotateImage(BufferedImage image, double angle){
		int width = image.getWidth();
		int height = image.getHeight();

		BufferedImage rotated = new BufferedImage(width, height, image.getType());
		Graphics2D g2d = rotated.createGraphics();

		g2d.translate(width/2, height/2);
		g2d.rotate(Math.toRadians(angle));
		g2d.translate(-width/2, -height/2);
		g2d.drawImage(image, 0, 0, null);
		g2d.dispose();
		return rotated;
	}

}

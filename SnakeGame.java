import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private static final int TILE_SIZE = 25;
    private static final int INIT_X = 5;
    private static final int INIT_Y = 5;
    private static final int TIMER_DELAY = 100;

    private class Tile {
        int x, y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null || getClass() != obj.getClass())
                return false;
            Tile tile = (Tile) obj;
            return x == tile.x && y == tile.y;
        }

        @Override
        public int hashCode() {
            return 31 * x + y;
        }
    }

    private Tile snakeHead;
    private Tile food;
    private ArrayList<Tile> snakeBody;
    private Random random;
    private Timer gameLoop;
    private boolean gameOver = false;
    private int movX, movY;
    private int width, height;

    SnakeGame(int width, int height) {
        if (width % TILE_SIZE != 0 || height % TILE_SIZE != 0) {
            throw new IllegalArgumentException("Width and height must be multiples of TILE_SIZE");
        }

        this.width = width;
        this.height = height;
        setBackground(Color.black);

        snakeHead = new Tile(INIT_X, INIT_Y);
        snakeBody = new ArrayList<>();
        random = new Random();

        placeFood();

        gameLoop = new Timer(TIMER_DELAY, this);
        gameLoop.start();

        movX = 0;
        movY = 0;

        addKeyListener(this);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    private void draw(Graphics g) {
        g.setColor(Color.white);
        g.drawRect(0, 0, width - 1, height - 1);

        g.setColor(Color.red);
        g.fillRect(food.x * TILE_SIZE, food.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        g.setColor(Color.green);
        g.fillRect(snakeHead.x * TILE_SIZE, snakeHead.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

        g.setColor(Color.green);
        for (Tile t : snakeBody) {
            g.fillRect(t.x * TILE_SIZE, t.y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
        }

        g.setColor(Color.yellow);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score: " + snakeBody.size(), 10, 20);

        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over! Final Score: " + snakeBody.size(), width / 4, height / 2);
            g.drawString("Press 'R' to Restart", width / 4, height / 2 + 30);
        }
    }

    private void move() {
        if (snakeHead.equals(food)) {
            snakeBody.add(new Tile(food.x, food.y));
            placeFood();
        }

        for (int i = snakeBody.size() - 1; i >= 0; i--) {
            Tile snakeBack = snakeBody.get(i);
            if (i == 0) {
                snakeBack.x = snakeHead.x;
                snakeBack.y = snakeHead.y;
            } else {
                Tile prevSnakeBack = snakeBody.get(i - 1);
                snakeBack.x = prevSnakeBack.x;
                snakeBack.y = prevSnakeBack.y;
            }
        }

        snakeHead.x += movX;
        snakeHead.y += movY;

        if (snakeHead.x < 0 || snakeHead.x >= width / TILE_SIZE ||
                snakeHead.y < 0 || snakeHead.y >= height / TILE_SIZE ||
                snakeBody.contains(snakeHead)) {
            gameOver = true;
        }
    }

    private void placeFood() {
        int x = random.nextInt(width / TILE_SIZE);
        int y = random.nextInt(height / TILE_SIZE);
        food = new Tile(x, y);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver) {
            move();
            repaint();
        } else {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_UP && movY == 0) {
            movX = 0;
            movY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && movY == 0) {
            movX = 0;
            movY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && movX == 0) {
            movX = -1;
            movY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && movX == 0) {
            movX = 1;
            movY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_R && gameOver) {
            restartGame();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    private void restartGame() {
        snakeHead = new Tile(INIT_X, INIT_Y);
        snakeBody.clear();
        placeFood();
        movX = 0;
        movY = 0;
        gameOver = false;
        gameLoop.start();
    }

    public static void main(String[] args) {
        int width = 600;
        int height = width;

        JFrame frame = new JFrame("Snake");
        SnakeGame snakeGame = new SnakeGame(width, height);

        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(snakeGame);
        frame.pack();
        frame.setVisible(true);
    }
}

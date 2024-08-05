import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;

public class SnakeGame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x, y;

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
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
    private int tileSize = 25;

    SnakeGame(int width, int height) {
        this.width = width;
        this.height = height;
        setBackground(Color.black);
        snakeHead = new Tile(width / (2 * tileSize), height / (2 * tileSize));
        snakeBody = new ArrayList<>();
        random = new Random();
        placeFood();

        gameLoop = new Timer(100, this);
        gameLoop.start();

        movX = 0;
        movY = 0;

        addKeyListener(this);
        setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(Color.red);
        g.fillRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize);
        g.setColor(Color.green);
        g.fillRect(snakeHead.x * tileSize, snakeHead.y * tileSize, tileSize, tileSize);

        for (Tile t : snakeBody) {
            g.fillRect(t.x * tileSize, t.y * tileSize, tileSize, tileSize);
        }

        g.setColor(Color.yellow);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        g.drawString("Score: " + snakeBody.size(), 10, 20);

        if (gameOver) {
            g.setColor(Color.red);
            g.drawString("Game Over! Final Score: " + snakeBody.size(), width / 4, height / 2);
        }
    }

    private void move() {
        if (snakeHead.x == food.x && snakeHead.y == food.y) {
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

        if (snakeHead.x < 0 || snakeHead.x * tileSize >= width ||
                snakeHead.y < 0 || snakeHead.y * tileSize >= height) {
            gameOver = true;
        }

        for (Tile t : snakeBody) {
            if (snakeHead.x == t.x && snakeHead.y == t.y) {
                gameOver = true;
            }
        }
    }

    private void placeFood() {
        food = new Tile(random.nextInt(width / tileSize), random.nextInt(height / tileSize));
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
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public static void main(String[] args) {
        int width = 600;
        int height = width;

        JFrame frame = new JFrame("Snake Game");
        SnakeGame snakeGame = new SnakeGame(width, height);

        frame.setSize(width, height);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(snakeGame);
        frame.pack();
        frame.setVisible(true);
    }
}

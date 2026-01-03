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
    private static final Color BACKGROUND_COLOR = new Color(30, 30, 30);
    private static final Color GRID_COLOR = new Color(50, 50, 50);
    private static final Color SNAKE_HEAD_COLOR = new Color(0, 200, 0);
    private static final Color SNAKE_BODY_COLOR = new Color(0, 150, 0);
    private static final Color FOOD_COLOR = new Color(220, 50, 50);

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
    private boolean paused = false;
    private boolean gameStarted = false;
    private int movX, movY;
    private int width, height;
    private int highScore = 0;

    SnakeGame(int width, int height) {
        if (width % TILE_SIZE != 0 || height % TILE_SIZE != 0) {
            throw new IllegalArgumentException("Width and height must be multiples of TILE_SIZE");
        }

        this.width = width;
        this.height = height;
        setPreferredSize(new Dimension(width, height));
        setBackground(BACKGROUND_COLOR);

        snakeHead = new Tile(INIT_X, INIT_Y);
        snakeBody = new ArrayList<>();
        random = new Random();

        placeFood();

        gameLoop = new Timer(TIMER_DELAY, this);

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
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Draw grid
        g.setColor(GRID_COLOR);
        for (int i = 0; i < width / TILE_SIZE; i++) {
            g.drawLine(i * TILE_SIZE, 0, i * TILE_SIZE, height);
        }
        for (int i = 0; i < height / TILE_SIZE; i++) {
            g.drawLine(0, i * TILE_SIZE, width, i * TILE_SIZE);
        }

        // Draw border
        g.setColor(Color.white);
        g.drawRect(0, 0, width - 1, height - 1);

        // Draw food with glow effect
        g.setColor(FOOD_COLOR);
        g2d.fillRoundRect(food.x * TILE_SIZE + 2, food.y * TILE_SIZE + 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);

        // Draw snake head
        g.setColor(SNAKE_HEAD_COLOR);
        g2d.fillRoundRect(snakeHead.x * TILE_SIZE + 1, snakeHead.y * TILE_SIZE + 1, TILE_SIZE - 2, TILE_SIZE - 2, 10, 10);

        // Draw snake body
        g.setColor(SNAKE_BODY_COLOR);
        for (Tile t : snakeBody) {
            g2d.fillRoundRect(t.x * TILE_SIZE + 2, t.y * TILE_SIZE + 2, TILE_SIZE - 4, TILE_SIZE - 4, 8, 8);
        }

        // Draw score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.BOLD, 16));
        g.drawString("Score: " + snakeBody.size(), 10, 20);
        g.drawString("High Score: " + highScore, width - 130, 20);

        // Draw start screen
        if (!gameStarted && !gameOver) {
            drawCenteredScreen(g2d, "🐍 SNAKE GAME", 
                new String[]{"Use Arrow Keys or WASD to move", "Press P to Pause", "Press SPACE to Start"}, 
                new Color(0, 100, 0));
        }

        // Draw pause screen
        if (paused && !gameOver) {
            drawCenteredScreen(g2d, "PAUSED", 
                new String[]{"Press P to Resume"}, 
                new Color(100, 100, 0));
        }

        // Draw game over screen
        if (gameOver) {
            if (snakeBody.size() > highScore) {
                highScore = snakeBody.size();
            }
            drawCenteredScreen(g2d, "GAME OVER", 
                new String[]{"Final Score: " + snakeBody.size(), "High Score: " + highScore, "Press R to Restart"}, 
                new Color(150, 0, 0));
        }
    }

    private void drawCenteredScreen(Graphics2D g2d, String title, String[] lines, Color bgColor) {
        // Semi-transparent overlay
        g2d.setColor(new Color(0, 0, 0, 180));
        g2d.fillRect(0, 0, width, height);

        // Title box
        g2d.setColor(bgColor);
        g2d.fillRoundRect(width / 6, height / 3 - 20, width * 2 / 3, 30 + lines.length * 25 + 20, 15, 15);
        g2d.setColor(Color.white);
        g2d.drawRoundRect(width / 6, height / 3 - 20, width * 2 / 3, 30 + lines.length * 25 + 20, 15, 15);

        // Title
        g2d.setFont(new Font("Arial", Font.BOLD, 24));
        FontMetrics fm = g2d.getFontMetrics();
        int titleX = (width - fm.stringWidth(title)) / 2;
        g2d.drawString(title, titleX, height / 3 + 10);

        // Lines
        g2d.setFont(new Font("Arial", Font.PLAIN, 14));
        fm = g2d.getFontMetrics();
        for (int i = 0; i < lines.length; i++) {
            int lineX = (width - fm.stringWidth(lines[i])) / 2;
            g2d.drawString(lines[i], lineX, height / 3 + 40 + i * 25);
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
        int maxAttempts = 100;
        int attempts = 0;
        do {
            int x = random.nextInt(width / TILE_SIZE);
            int y = random.nextInt(height / TILE_SIZE);
            food = new Tile(x, y);
            attempts++;
        } while ((snakeBody.contains(food) || food.equals(snakeHead)) && attempts < maxAttempts);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (!gameOver && !paused && gameStarted) {
            move();
            repaint();
        } else if (gameOver) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        
        // Start game with SPACE
        if (!gameStarted && key == KeyEvent.VK_SPACE) {
            gameStarted = true;
            gameLoop.start();
            return;
        }

        // Pause toggle
        if (key == KeyEvent.VK_P && gameStarted && !gameOver) {
            paused = !paused;
            repaint();
            return;
        }

        // Movement controls (Arrow keys and WASD)
        if ((key == KeyEvent.VK_UP || key == KeyEvent.VK_W) && movY == 0) {
            movX = 0;
            movY = -1;
        } else if ((key == KeyEvent.VK_DOWN || key == KeyEvent.VK_S) && movY == 0) {
            movX = 0;
            movY = 1;
        } else if ((key == KeyEvent.VK_LEFT || key == KeyEvent.VK_A) && movX == 0) {
            movX = -1;
            movY = 0;
        } else if ((key == KeyEvent.VK_RIGHT || key == KeyEvent.VK_D) && movX == 0) {
            movX = 1;
            movY = 0;
        } else if (key == KeyEvent.VK_R && gameOver) {
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
        paused = false;
        gameStarted = true;
        gameLoop.start();
    }

    public static void main(String[] args) {
        int width = 600;
        int height = width;

        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame("🐍 Snake Game");
            SnakeGame snakeGame = new SnakeGame(width, height);

            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setResizable(false);
            frame.add(snakeGame);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
            snakeGame.requestFocusInWindow();
        });
    }
}

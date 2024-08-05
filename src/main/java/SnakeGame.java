import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;
import org.jline.terminal.Attributes;
import org.jline.utils.NonBlockingReader;

import java.io.IOException;
import java.util.Random;

public class SnakeGame {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 20;
    private static final char EMPTY = ' ';
    private static final char SNAKE_HEAD = 'O';
    private static final char SNAKE_BODY = 'o';
    private static final char FOOD = 'X';
    private static char[][] board = new char[HEIGHT][WIDTH];
    private static int[] snakeX = new int[WIDTH * HEIGHT];
    private static int[] snakeY = new int[WIDTH * HEIGHT];
    private static int snakeLength;
    private static int foodX;
    private static int foodY;
    private static int score;
    private static char direction;
    private static Terminal terminal;

    public static void main(String[] args) {
        try {
            terminal = TerminalBuilder.terminal();
            terminal.enterRawMode();
            Attributes attributes = terminal.getAttributes();
            attributes.setControlChar(Attributes.ControlChar.VINTR, null);
            terminal.setAttributes(attributes);

            initializeGame();
            gameLoop();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (terminal != null) {
                    terminal.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void initializeGame() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                board[i][j] = EMPTY;
            }
        }

        snakeLength = 1;
        snakeX[0] = WIDTH / 2;
        snakeY[0] = HEIGHT / 2;
        board[snakeY[0]][snakeX[0]] = SNAKE_HEAD;

        placeFood();

        score = 0;
        direction = 'R';
    }

    private static void placeFood() {
        Random random = new Random();
        do {
            foodX = random.nextInt(WIDTH);
            foodY = random.nextInt(HEIGHT);
        } while (board[foodY][foodX] != EMPTY);

        board[foodY][foodX] = FOOD;
    }

    private static void gameLoop() throws IOException {
        NonBlockingReader reader = terminal.reader();
        boolean gameRunning = true;

        while (gameRunning) {
            printBoard();
            System.out.println("Score: " + score);
            gameRunning = updateGame();
            if (reader.ready()) {
                char input = (char) reader.read();
                switch (Character.toUpperCase(input)) {
                    case 'W':
                        if (direction != 'D')
                            direction = 'U';
                        break;
                    case 'A':
                        if (direction != 'R')
                            direction = 'L';
                        break;
                    case 'S':
                        if (direction != 'U')
                            direction = 'D';
                        break;
                    case 'D':
                        if (direction != 'L')
                            direction = 'R';
                        break;
                }
            }

            try {
                Thread.sleep(200); // Adjust the speed of the snake here
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("Game Over! Final Score: " + score);
    }

    private static boolean updateGame() {
        int nextX = snakeX[0];
        int nextY = snakeY[0];

        switch (direction) {
            case 'U':
                nextY--;
                break;
            case 'D':
                nextY++;
                break;
            case 'L':
                nextX--;
                break;
            case 'R':
                nextX++;
                break;
        }

        if (nextX < 0 || nextX >= WIDTH || nextY < 0 || nextY >= HEIGHT || board[nextY][nextX] == SNAKE_BODY) {
            return false;
        }

        if (nextX == foodX && nextY == foodY) {
            snakeLength++;
            score++;
            placeFood();
        }

        for (int i = snakeLength - 1; i > 0; i--) {
            snakeX[i] = snakeX[i - 1];
            snakeY[i] = snakeY[i - 1];
        }
        snakeX[0] = nextX;
        snakeY[0] = nextY;

        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                if (board[i][j] != FOOD) {
                    board[i][j] = EMPTY;
                }
            }
        }

        for (int i = 0; i < snakeLength; i++) {
            if (i == 0) {
                board[snakeY[i]][snakeX[i]] = SNAKE_HEAD;
            } else {
                board[snakeY[i]][snakeX[i]] = SNAKE_BODY;
            }
        }

        return true;
    }

    private static void printBoard() {
        for (int i = 0; i < HEIGHT; i++) {
            for (int j = 0; j < WIDTH; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }
    }
}

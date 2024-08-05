import java.util.LinkedList;
import java.util.Scanner;

public class SnakeGame {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 10;

    private static char[][] board = new char[HEIGHT][WIDTH];
    private static LinkedList<int[]> snake = new LinkedList<>();
    private static int[] direction = { 0, 1 };
    private static boolean gameRunning = true;

    public static void main(String[] args) {
        initializeBoard();
        initializeSnake();

        Scanner scanner = new Scanner(System.in);

        while (gameRunning) {
            displayBoard();
            handleInput(scanner);
            moveSnake();
            checkCollisions();
            clearBoard();
            if (!gameRunning) {
                System.out.println("Game Over!");
                break;
            }
        }
    }

    private static void initializeBoard() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                board[y][x] = ' ';
            }
        }

        for (int x = 0; x < WIDTH; x++) {
            board[0][x] = '#';
            board[HEIGHT - 1][x] = '#';
        }
        for (int y = 0; y < HEIGHT; y++) {
            board[y][0] = '#';
            board[y][WIDTH - 1] = '#';
        }
    }

    private static void initializeSnake() {
        snake.add(new int[] { HEIGHT / 2, WIDTH / 2 });
    }

    private static void handleInput(Scanner scanner) {
        System.out.println("Enter direction (W/A/S/D): ");
        String input = scanner.nextLine().toUpperCase();

        switch (input) {
            case "W":
                direction = new int[] { -1, 0 };
                break;
            case "S":
                direction = new int[] { 1, 0 };
                break;
            case "A":
                direction = new int[] { 0, -1 };
                break;
            case "D":
                direction = new int[] { 0, 1 };
                break;
            default:
                System.out.println("Invalid input. Use W/A/S/D.");
        }
    }

    private static void moveSnake() {
        int[] head = snake.getFirst();
        int newHeadY = head[0] + direction[0];
        int newHeadX = head[1] + direction[1];

        snake.addFirst(new int[] { newHeadY, newHeadX });
        snake.removeLast();
    }

    private static void checkCollisions() {
        int[] head = snake.getFirst();
        int headY = head[0];
        int headX = head[1];

        if (headY <= 0 || headY >= HEIGHT - 1 || headX <= 0 || headX >= WIDTH - 1) {
            gameRunning = false;
        }

        for (int i = 1; i < snake.size(); i++) {
            int[] segment = snake.get(i);
            if (segment[0] == headY && segment[1] == headX) {
                gameRunning = false;
                break;
            }
        }
    }

    private static void clearBoard() {
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                board[y][x] = ' ';
            }
        }

        for (int x = 0; x < WIDTH; x++) {
            board[0][x] = '#';
            board[HEIGHT - 1][x] = '#';
        }
        for (int y = 0; y < HEIGHT; y++) {
            board[y][0] = '#';
            board[y][WIDTH - 1] = '#';
        }

        for (int[] segment : snake) {
            int y = segment[0];
            int x = segment[1];
            board[y][x] = '*';
        }
    }

    private static void displayBoard() {
        System.out.println("Game Board:");
        for (int y = 0; y < HEIGHT; y++) {
            for (int x = 0; x < WIDTH; x++) {
                System.out.print(board[y][x]);
            }
            System.out.println();
        }
    }
}

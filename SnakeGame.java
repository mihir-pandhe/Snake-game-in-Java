import java.util.Scanner;

public class SnakeGame {
    private static final int WIDTH = 20;
    private static final int HEIGHT = 10;

    private static char[][] board = new char[HEIGHT][WIDTH];

    public static void main(String[] args) {
        initializeBoard();

        Scanner scanner = new Scanner(System.in);

        while (true) {
            displayBoard();
            System.out.println("Press Enter to continue...");
            scanner.nextLine();
            initializeBoard();
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

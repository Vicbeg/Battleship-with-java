package battleship;

import java.util.*;

public class Main {
    static final int ROWS = 10;
    static final int COLS = 10;

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        Player player1 = new Player("Player 1");
        Player player2 = new Player("Player 2");

        System.out.println(player1.name + ", place your ships on the game field");
        printField(player1.grid);
        placeShips(player1, scanner);
        promptEnterKey();

        System.out.println(player2.name + ", place your ships on the game field");
        printField(player2.grid);
        placeShips(player2, scanner);
        promptEnterKey();

        while (true) {
            System.out.println(player1.name + ", it's your turn:");
            printBothFields(player2, player1);
            takeTurn(player1, player2, scanner);
            if (player2.allShipsSunk()) {
                System.out.println("You sank the last ship. You won. Congratulations!");
                break;
            }
            promptEnterKey();

            System.out.println(player2.name + ", it's your turn:");
            printBothFields(player1, player2);
            takeTurn(player2, player1, scanner);
            if (player1.allShipsSunk()) {
                System.out.println("You sank the last ship. You won. Congratulations!");
                break;
            }
            promptEnterKey();
        }
    }

    static class Player {
        String name;
        char[][] grid;
        char[][] fogGrid;
        List<Ship> ships;

        Player(String name) {
            this.name = name;
            this.grid = new char[ROWS][COLS];
            this.fogGrid = new char[ROWS][COLS];
            this.ships = new ArrayList<>();
            initGrid(grid);
            initGrid(fogGrid);
        }

        boolean allShipsSunk() {
            for (Ship ship : ships) {
                if (!ship.isSunk(grid)) return false;
            }
            return true;
        }
    }

    static class Ship {
        int[] start;
        int[] end;

        Ship(int[] start, int[] end) {
            this.start = start;
            this.end = end;
        }

        boolean isSunk(char[][] grid) {
            for (int i = Math.min(start[0], end[0]); i <= Math.max(start[0], end[0]); i++) {
                for (int j = Math.min(start[1], end[1]); j <= Math.max(start[1], end[1]); j++) {
                    if (grid[i][j] == 'O') return false;
                }
            }
            return true;
        }

        boolean contains(int[] coords) {
            int row = coords[0], col = coords[1];
            return row >= Math.min(start[0], end[0]) && row <= Math.max(start[0], end[0]) &&
                    col >= Math.min(start[1], end[1]) && col <= Math.max(start[1], end[1]);
        }
    }

    static void placeShips(Player player, Scanner scanner) {
        String[][] ships = {
                {"Aircraft Carrier", "5"},
                {"Battleship", "4"},
                {"Submarine", "3"},
                {"Cruiser", "3"},
                {"Destroyer", "2"}
        };
        for (String[] ship : ships) {
            while (true) {
                System.out.println("Enter the coordinates of the " + ship[0] + " (" + ship[1] + " cells):");
                String startCoord = scanner.next();
                String endCoord = scanner.next();
                int[] start = parseCoordinates(startCoord);
                int[] end = parseCoordinates(endCoord);

                if (!inBounds(start) || !inBounds(end)) {
                    System.out.println("Error! Coordinates out of bounds. Try again:");
                } else if (!isAligned(start, end)) {
                    System.out.println("Error! Wrong ship location! Try again:");
                } else if (!isCorrectLength(start, end, Integer.parseInt(ship[1]))) {
                    System.out.println("Error! Wrong length of the " + ship[0] + "! Try again:");
                } else if (!isValidPlacement(start, end, player.grid)) {
                    System.out.println("Error! Too close to another ship. Try again:");
                } else {
                    placeShip(start, end, player.grid);
                    player.ships.add(new Ship(start, end));
                    printField(player.grid);
                    break;
                }
            }
        }
    }

    static void takeTurn(Player attacker, Player defender, Scanner scanner) {
        while (true) {
            String shot = scanner.next();
            int[] coords = parseCoordinates(shot);

            if (!inBounds(coords)) {
                System.out.println("Error! You entered the wrong coordinates! Try again:");
                continue;
            }

            int r = coords[0], c = coords[1];
            if (defender.grid[r][c] == 'O') {
                defender.grid[r][c] = 'X';
                defender.fogGrid[r][c] = 'X';

                boolean shipSunk = false;
                for (Ship ship : defender.ships) {
                    if (ship.contains(coords)) {
                        if (ship.isSunk(defender.grid)) {
                            shipSunk = true;
                        }
                        break;
                    }
                }

                if (shipSunk) {
                    System.out.println("You sank a ship!");
                } else {
                    System.out.println("You hit a ship!");
                }
            } else {
                if (defender.grid[r][c] == '~') {
                defender.grid[r][c] = 'M';
            }
                defender.fogGrid[r][c] = 'M';
                System.out.println("You missed!");
            }
            break;
        }
    }

    static void initGrid(char[][] grid) {
        for (int i = 0; i < ROWS; i++) {
            Arrays.fill(grid[i], '~');
        }
    }

    static boolean inBounds(int[] coord) {
        return coord[0] >= 0 && coord[0] < ROWS && coord[1] >= 0 && coord[1] < COLS;
    }

    static int[] parseCoordinates(String s) {
        int row = s.charAt(0) - 'A';
        int col = Integer.parseInt(s.substring(1)) - 1;
        return new int[]{row, col};
    }

    static boolean isAligned(int[] start, int[] end) {
        return start[0] == end[0] || start[1] == end[1];
    }

    static boolean isCorrectLength(int[] start, int[] end, int expectedLength) {
        if (start[0] == end[0]) return Math.abs(start[1] - end[1]) + 1 == expectedLength;
        else if (start[1] == end[1]) return Math.abs(start[0] - end[0]) + 1 == expectedLength;
        return false;
    }

    static boolean isValidPlacement(int[] start, int[] end, char[][] grid) {
        int rowStart = Math.min(start[0], end[0]) - 1;
        int rowEnd = Math.max(start[0], end[0]) + 1;
        int colStart = Math.min(start[1], end[1]) - 1;
        int colEnd = Math.max(start[1], end[1]) + 1;

        for (int i = Math.max(0, rowStart); i <= Math.min(ROWS - 1, rowEnd); i++) {
            for (int j = Math.max(0, colStart); j <= Math.min(COLS - 1, colEnd); j++) {
                if (grid[i][j] == 'O') return false;
            }
        }
        return true;
    }

    static void placeShip(int[] start, int[] end, char[][] grid) {
        for (int i = Math.min(start[0], end[0]); i <= Math.max(start[0], end[0]); i++) {
            for (int j = Math.min(start[1], end[1]); j <= Math.max(start[1], end[1]); j++) {
                grid[i][j] = 'O';
            }
        }
    }

    static void printBothFields(Player opponent, Player current) {
        System.out.println("Opponent's field:");
        printField(opponent.fogGrid);
        System.out.println("---------------------");
        System.out.println("Your field:");
        printField(current.grid);
    }

    static void printField(char[][] grid) {
        System.out.print("  ");
        for (int col = 1; col <= COLS; col++) System.out.print(col + " ");
        System.out.println();

        for (int i = 0; i < ROWS; i++) {
            System.out.print((char)('A' + i) + " ");
            for (int j = 0; j < COLS; j++) {
                System.out.print(grid[i][j] + " ");
            }
            System.out.println();
        }
    }

    static void promptEnterKey() {
        System.out.println("Press Enter and pass the move to another player");
        new Scanner(System.in).nextLine();
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }
}

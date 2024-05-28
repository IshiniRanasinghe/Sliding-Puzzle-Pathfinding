import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
public class Solver {
    // Constants for directions (up, down, left, right)
    private static final int[][] DIRS = {{-1, 0}, {1, 0}, {0, -1}, {0, 1}}; 
    //2D array
    // Constants for directions (up, down, left, right)
    //The first element {-1, 0} represents moving one unit upwards.
    //The second element {1, 0} represents moving one unit downwards.
    //The third element {0, -1} represents moving one unit to the left.
    //The fourth element {0, 1} represents moving one unit to the right.
    private static final String[] DIRECTIONS = {"up", "down", "left", "right"};
    // Constants for  characters_w1953836_KhombalaRanasinghe
    private static final char ROCK = '0';
    private static final char START = 'S';
    private static final char END = 'F';
    private int rows;
    private int cols;//variables
    private int startRow;
    private int startCol;
    private int finishRow;
    private int finishCol;
    private char[][] mapArray;
    // Solution path_w1953836_KhombalaRanasinghe
    private List<Cell> path;
    private List<String> pathDirections;
    //Constructor:Initializes the maze dimensions.
    public Solver(int rows, int cols) {
        this.rows = rows;
        this.cols = cols;
        this.mapArray = new char[rows][cols];
    }
    // Method to read the map from a file_w1953836_KhombalaRanasinghe
    public void parseMapFromFile(String filePath) {
        List<List<Character>> mapList = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                List<Character> row = new ArrayList<>();
                for (char c : line.toCharArray()) {
                    row.add(c);
                }
                mapList.add(row);
            }
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
            return;
        }

        this.rows = mapList.size();
        this.cols = mapList.stream().mapToInt(List::size).max().orElse(0);
        this.mapArray = new char[this.rows][this.cols];
    // Convert the map from List<List<Character>> to char[][]_w1953836_KhombalaRanasinghe
        for (int i = 0; i < this.rows; i++) {
            List<Character> row = mapList.get(i);
            for (int j = 0; j < row.size(); j++) {
                this.mapArray[i][j] = row.get(j);
                if (row.get(j) == START) {
                    this.startRow = i;
                    this.startCol = j;
                } else if (row.get(j) == END) {
                    this.finishRow = i;
                    this.finishCol = j;
                }
            }
        }
    }
    // Method to print the map_w1953836_KhombalaRanasinghe
    public void printMap() {
        System.out.println("***** SOURCE MAZE *****");
        for (int i = 0; i < this.rows; i++) {
            for (int j = 0; j < this.cols; j++) {
                System.out.print(this.mapArray[i][j] + " ");
            }
            System.out.println();
        }
        System.out.println();
    }
    // Method to solve the maze_w1953836_KhombalaRanasinghe
    public void solve() {
        Cell start = new Cell(this.startRow, this.startCol);
        Cell finish = new Cell(this.finishRow, this.finishCol);
        applySlidingBFS(start, finish);
    }
    // Helper method to apply the Sliding BFS algorithm_w1953836_KhombalaRanasinghe
    private void applySlidingBFS(Cell start, Cell finish) {
        int rows = this.mapArray.length;
        int cols = this.mapArray[0].length;
        Map<Cell, Cell> prev = new HashMap<>();
        Queue<Cell> queue = new LinkedList<>();
        Set<Cell> visited = new HashSet<>();
        Map<Cell, String> directionMap = new HashMap<>();

        queue.offer(start);
        visited.add(start);
        // BFS traversal_w1953836_KhombalaRanasinghe
        while (!queue.isEmpty()) {
            Cell current = queue.poll();

            if (current.equals(finish)) {
                constructPath(prev, start, finish, directionMap);
                return;
            }
            for (int[] dir : DIRS) {
                Cell next = slide(current, dir, rows, cols);
                if (!visited.contains(next)) {
                    prev.put(next, current);
                    queue.offer(next);
                    visited.add(next);
                    directionMap.put(next, DIRECTIONS[getDirection(current, next)]);
                }
            }
        }
    }
  //method to slide in a direction until hitting a rock or boundary_w1953836_KhombalaRanasinghe
    private Cell slide(Cell current, int[] dir, int rows, int cols) {
        int newRow = current.row;
        int newCol = current.col;

        while (isValid(newRow + dir[0], newCol + dir[1], rows, cols) &&
                mapArray[newRow + dir[0]][newCol + dir[1]] != ROCK) {
            newRow += dir[0];
            newCol += dir[1];
        }
        return new Cell(newRow, newCol);
    }
    //  method to determine the direction of sliding_w1953836_KhombalaRanasinghe
    private int getDirection(Cell current, Cell next) {
        if (next.row < current.row) return 0; // up
        if (next.row > current.row) return 1; // down
        if (next.col < current.col) return 2; // left
        if (next.col > current.col) return 3; // right
        return -1; // default
    }
    // method to check if a cell is valid_w1953836_KhombalaRanasinghe
    private static boolean isValid(int row, int col, int rows, int cols) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }
    //  method to construct the path from finish to start_w1953836_KhombalaRanasinghe
    private void constructPath(Map<Cell, Cell> prev, Cell start, Cell finish, Map<Cell, String> directionMap) {
        this.path = new ArrayList<>();
        this.pathDirections = new ArrayList<>();
        Cell current = finish;
        while (current != null && !current.equals(start)) {
            this.path.add(current);
            this.pathDirections.add(directionMap.get(current));
            current = prev.get(current);
        }
        if (current != null) {
            this.path.add(start);
            Collections.reverse(this.path);
            Collections.reverse(this.pathDirections);
        }
    }
    // Method to print the solution path_w1953836_KhombalaRanasinghe
    public void printSolution() {
        System.out.println("***** SOLUTION PATH *****");
        if (this.path == null || this.path.isEmpty()) {
            System.out.println("No path found!");
            return;
        }
        System.out.println("\nStart position: (" + (this.startCol + 1) + "," + (this.startRow + 1) + ")");
        System.out.println("Finish position: (" + (this.finishCol + 1) + "," + (this.finishRow + 1) + ")\n");

        for (int i = 0; i < this.path.size(); i++) {
            if (i == 0) {
                System.out.println((i + 1) + ". Start at (" + (this.path.get(i).col + 1) + "," + (this.path.get(i).row + 1) + ")");
            } else {
                System.out.println((i + 1) + ". Move " + this.pathDirections.get(i - 1) + " to (" + (this.path.get(i).col + 1) + "," + (this.path.get(i).row + 1) + ")");
            }
        }
        System.out.println((this.path.size() + 1) + ". Done!");
        System.out.println("\nShortest Path length: " + (this.path.size() - 1));
    }
    //main method_w1953836_KhombalaRanasinghe
    public static void main(String[] args) {
        Solver map = new Solver(0, 0);
        map.parseMapFromFile("./examples/maze10_1.txt"); //  ./examples/maze10_1.txt,./benchmark_series/puzzle_10.txt
        map.printMap();
        map.solve();
        map.printSolution();
    }
}

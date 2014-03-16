package assign3;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.Set;

/*
 * Encapsulates a Sudoku grid to be solved.
 * CS108 Stanford.
 */
public class Sudoku {
	
	int[][] originalGrid;
	private int[][] grid;
	private LinkedList<Spot> spotList;
	private LinkedList<int[][]> solutions;
	private long startTime;
	private long stopTime;
	private int[][] boxes;
	
	// Provided grid data for main/testing
	// The instance variable strategy is up to you.
	
	// Provided easy 1 6 grid
	// (can paste this text into the GUI too)
	public static final int[][] easyGrid = Sudoku.stringsToGrid(
	"1 6 4 0 0 0 0 0 2",
	"2 0 0 4 0 3 9 1 0",
	"0 0 5 0 8 0 4 0 7",
	"0 9 0 0 0 6 5 0 0",
	"5 0 0 1 0 2 0 0 8",
	"0 0 8 9 0 0 0 3 0",
	"8 0 9 0 4 0 2 0 0",
	"0 7 3 5 0 9 0 0 1",
	"4 0 0 0 0 0 6 7 9");
	
	
	// Provided medium 5 3 grid
	public static final int[][] mediumGrid = Sudoku.stringsToGrid(
	 "530070000",
	 "600195000",
	 "098000060",
	 "800060003",
	 "400803001",
	 "700020006",
	 "060000280",
	 "000419005",
	 "000080079");
	
	// Provided hard 3 7 grid
	// 1 solution this way, 6 solutions if the 7 is changed to 0
	public static final int[][] hardGrid = Sudoku.stringsToGrid(
	"3 7 0 0 0 0 0 8 0",
	"0 0 1 0 9 3 0 0 0",
	"0 4 0 7 8 0 0 0 3",
	"0 9 3 8 0 0 0 1 2",
	"0 0 0 0 4 0 0 0 0",
	"5 2 0 0 0 6 7 9 0",
	"6 0 0 0 2 1 0 4 0",
	"0 0 0 5 3 0 9 0 0",
	"0 3 0 0 0 0 0 5 1");
	
	
	public static final int SIZE = 9;  // size of the whole 9x9 puzzle
	public static final int PART = 3;  // size of each 3x3 part
	public static final int MAX_SOLUTIONS = 100;
	
	// Provided various static utility methods to
	// convert data formats to int[][] grid.
	
	/**
	 * Returns a 2-d grid parsed from strings, one string per row.
	 * The "..." is a Java 5 feature that essentially
	 * makes "rows" a String[] array.
	 * (provided utility)
	 * @param rows array of row strings
	 * @return grid
	 */
	public static int[][] stringsToGrid(String... rows) {
		int[][] result = new int[rows.length][];
		for (int row = 0; row<rows.length; row++) {
			result[row] = stringToInts(rows[row]);
		}
		return result;
	}
	
	
	/**
	 * Given a single string containing 81 numbers, returns a 9x9 grid.
	 * Skips all the non-numbers in the text.
	 * (provided utility)
	 * @param text string of 81 numbers
	 * @return grid
	 */
	public static int[][] textToGrid(String text) {
		int[] nums = stringToInts(text);
		if (nums.length != SIZE*SIZE) {
			throw new RuntimeException("Needed 81 numbers, but got:" + nums.length);
		}
		
		int[][] result = new int[SIZE][SIZE];
		int count = 0;
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				result[row][col] = nums[count];
				count++;
			}
		}
		return result;
	}
	
	
	/**
	 * Given a string containing digits, like "1 23 4",
	 * returns an int[] of those digits {1 2 3 4}.
	 * (provided utility)
	 * @param string string containing ints
	 * @return array of ints
	 */
	public static int[] stringToInts(String string) {
		int[] a = new int[string.length()];
		int found = 0;
		for (int i=0; i<string.length(); i++) {
			if (Character.isDigit(string.charAt(i))) {
				a[found] = Integer.parseInt(string.substring(i, i+1));
				found++;
			}
		}
		int[] result = new int[found];
		System.arraycopy(a, 0, result, 0, found);
		return result;
	}


	// Provided -- the deliverable main().
	// You can edit to do easier cases, but turn in
	// solving hardGrid.
	public static void main(String[] args) {
		Sudoku sudoku;
		sudoku = new Sudoku(hardGrid);
		
		System.out.println(sudoku); // print the raw problem
		int count = sudoku.solve();
		System.out.println("solutions:" + count);
		System.out.println("elapsed:" + sudoku.getElapsed() + "ms");
		System.out.println(sudoku.getSolutionText());
	}
	
	
	

	/**
	 * Sets up based on the given ints.
	 */
	public Sudoku(int[][] ints) {
		this.originalGrid = ints;
		spotList = new LinkedList<Spot>();
		solutions = new LinkedList<int[][]>();
		grid = new int[SIZE][SIZE];
		boxes = new int[SIZE][SIZE];
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				if(ints[row][col]==0){
					spotList.add(new Spot(row, col));
				}
				grid[row][col] = ints[row][col];
				int box = (col/3 + 3*(row/3));
				int boxPos = (col%3 + 3*(row%3));
				boxes[box][boxPos] = ints[row][col];
			}
		}
	}
	
	private Spot checkRow(Spot spot, int[][] grid){
		int row = spot.GetRow();
		for(int col=0; col<9; col++){
			if(grid[row][col]>0) {
				spot.candidates.remove(grid[row][col]);
			}
		}
		return spot;
	}
	
	private Spot checkCol(Spot spot, int[][] grid){
		int col = spot.GetCol();
		for(int row=0; row<9; row++){
			if(grid[row][col]>0) {
				spot.candidates.remove(grid[row][col]);
			}
		}
		return spot;
	}
	
	private Spot checkBox(Spot spot, int[][] boxes){
		int[] box = boxes[spot.GetBox()];
		for(int i=0; i<9; i++){
			if(box[i]>0) {
				spot.candidates.remove(box[i]);
			}
		}
		return spot;
	}
	
	private Spot reduceCandidates(Spot spot, int[][] grid, int[][] boxes){
		spot = checkRow(spot,grid);
		spot = checkCol(spot,grid);
		spot = checkBox(spot,boxes);
		return spot;
	}
	
	private LinkedList<int[][]> GetSolutionList(LinkedList<Spot> spotList, int[][] grid, int[][] boxes, LinkedList<int[][]> solutions){
		if(spotList.isEmpty()) {
			//if there are no more spots, a solution is found
			solutions.add(grid);
			return solutions;
		}
		Spot spot = spotList.removeLast();
		spot = reduceCandidates(spot, grid, boxes);
		//all these are pre-determined coords of each spot
		int row = spot.GetRow();
		int col = spot.GetCol();
		int box = spot.GetBox();
		int boxPos = spot.GetBoxPos();
		int size = spot.GetSize(); //numCandidates
		if(size<1){
			spotList.addLast(new Spot(row,col));
			return solutions;//means this is a dead end
		}
		Set<Integer> candidates = spot.candidates;
		//try each candidate
		for(int cand: candidates){
			int[][] newGrid = new int[9][9];
			int[][] newBoxes = new int[9][9];
			for(int i = 0; i<9; i++){
				System.arraycopy(grid[i], 0, newGrid[i], 0, 9);
				System.arraycopy(boxes[i], 0, newBoxes[i], 0, 9);
			}
			newGrid[row][col] = cand;
			newBoxes[box][boxPos] = cand;
			//make a new list each time to check with (super expensive, but maybe works??)
			solutions = (GetSolutionList(spotList, newGrid, newBoxes, solutions));
		}
		spotList.addLast(new Spot(row,col));
		return solutions;
	}
	
	/**
	 * Solves the puzzle, invoking the underlying recursive search.
	 */
	public int solve() {
		//recurse on spotList, keep adding/removing spots until spotList is empty
		startTime = System.currentTimeMillis();
		solutions = GetSolutionList(spotList, grid, boxes, solutions);
		stopTime = System.currentTimeMillis();
		return solutions.size();
	}
	
	public String getSolutionText() {
		if(solutions.isEmpty()) return "WTF";
		int[][] sol = solutions.getFirst();
		String text = "";
		for (int row = 0; row<SIZE; row++) {
			for (int col=0; col<SIZE; col++) {
				text += sol[row][col];
			}
			text += "\n";
		}
		return text;
	}
	
	public long getElapsed() {
		return stopTime - startTime;
	}
	
	public int count(){
		return solutions.size();
	}

}

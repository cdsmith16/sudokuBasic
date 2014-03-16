package assign3;

import java.util.HashSet;
import java.util.Set;


public class Spot{
	public Set<Integer> candidates;
	private int row;
	private int col;

	public Spot(int row, int col) {
		this.row = row;
		this.col = col;
		candidates = new HashSet<Integer>();
		for(int i=1; i<=9; i++) {
			candidates.add(i);
		}
	}
	
	public int GetRow(){
		return row;
	}
	
	public int GetCol(){
		return col;
	}
	
	public int GetBox(){
		return (col/3 + 3*(row/3));
	}
	
	public int GetBoxPos(){
		return (col%3 + 3*(row%3));
	}
	
	public int GetSize(){
		return candidates.size();
	}


}

import java.io.Serializable;
import org.apache.commons.lang3.SerializationUtils;

public class SearchState implements Cloneable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5786043818477755800L;
	
	private final static char AGENT = '\u263B';

	// The state will be remembered in this 2D array
	// Alternative implementation could be a HashMap containing just the important mappings in terms of positions.
	private char table[][];
	
	// The distance between each letter's current position and its desired position.
	private int distanceToSolution;
	
	// Agent position (row and column)
	private int agentR;
	private int agentC;
	
	private char moveToState;
	
	// Setting up the Grid with the default start state.
	public SearchState(int d) {
		
		initGrid(d);

	}
	
	// Setting up the Grid with a specified start state.
	public SearchState(int d, int agentRowPos, int agentColPos, int... letterPositions) {
		
		initGridWithPos(d, agentRowPos, agentColPos, letterPositions);
	}
	
	private void initGrid(int d) {
		
		table = new char[d][d];

		// Populate the grid on the last row with as many letters as possible
		for (int i = 0; i < d - 1; i++) {
			
			char currentLetter = (char) ('A' + i);
			table[d-1][i] = currentLetter;
			
			// distance on row + column from the solution.
			distanceToSolution += Math.abs((d-1) - i - 1) + Math.abs(i-1);

		}
		
		// The agent will be the character '*'
		table[d-1][d-1] = AGENT; // the agent
		agentR = agentC = d-1;

	}
	
	private void initGridWithPos(int d, int agentRowPos, int agentColPos, int... positions) {
		
			
			if (positions.length != (d-1) * 2) {
				
				throw new IllegalArgumentException("Each letter needs to have 2 coordinates, making a total of: " + (d-1)*2 + " numbers needed as arguments.");
			}
			
			else {
				
				agentR = agentRowPos;
				agentC = agentColPos;
				
				if (agentR<0 || agentR >= d || agentC <0 || agentC >= d) {
					
					throw new IllegalArgumentException("Agent position specified is out of the grid's bounds.");

				}
				else {

					table = new char[d][d];
					
					for (int i = 0; i < positions.length; i += 2) {
						
						int curLetterR = positions[i];
						int curLetterC = positions[i+1];
						
						if (curLetterR<0 || curLetterR >= d || curLetterC<0 || curLetterC >= d || (curLetterR == agentR && curLetterC == agentC)) {
							
							throw new IllegalArgumentException("A position specified is out of the grid's bounds or it's overlapping the agent.");
						}
						else {
							
							table[curLetterR][curLetterC] = (char)('A' + i/2);
							distanceToSolution = Math.abs(curLetterR - i/2 - 1) + Math.abs(curLetterC - 1);
						}
					}
					
					
					table[agentR][agentC] = AGENT; // the agent
				
				}
			}

			
		
	}
	
	
	public boolean canMove(char direction) {
		
		// Row/Column move
		int Rmove = 0;
		int Cmove = 0;
		
		// The length of a table's row/column
		int length = table.length;
		
		switch(direction) {
			
			case 'U': {
				Rmove --;
				break;
			}
			case 'D': {
				Rmove ++;
				break;
			}
			case 'L': {
				Cmove --;
				break;
			}
			case 'R': {
				Cmove ++;
				break;
			}
			default: {
				
				throw new UnsupportedOperationException("Unrecognized direction: " + direction);
			}
			
		}
		
		//  Check to see if the move is legal.
		if (agentR + Rmove < 0 || agentR + Rmove >= length || agentC + Cmove < 0 || agentC + Cmove >= length) {
			
			return false;
		}
		
		return true;
	}
	
	
	public void move(char direction) {
		
		// Row/Column move
		int Rmove = 0;
		int Cmove = 0;
		
		
		switch(direction) {
			
			case 'U': {
				Rmove --;
				break;
			}
			case 'D': {
				Rmove ++;
				break;
			}
			case 'L': {
				Cmove --;
				break;
			}
			case 'R': {
				Cmove ++;
				break;
			}
			default: {
				
				throw new UnsupportedOperationException("Unrecognized direction: " + direction);
			}
			
		}
		

		
		char currentLetter = table[agentR + Rmove][agentC + Cmove];
		table[agentR + Rmove][agentC + Cmove] = table[agentR][agentC];
		table[agentR][agentC] = currentLetter;
		
		
		if(currentLetter >= 'A') {
			
			int prevDistanceOfLetterToSolution = Math.abs(agentR + Rmove - (currentLetter - 'A') - 1) + Math.abs(agentC + Cmove - 1);
			int newDistanceOfLetterToSolution = Math.abs(agentR - (currentLetter - 'A') - 1) + Math.abs(agentC - 1);

			distanceToSolution += (newDistanceOfLetterToSolution - prevDistanceOfLetterToSolution) ;
		}
		
		// changing the agent position variables to reflect the change.
		agentR += Rmove;
		agentC += Cmove;
		
		// the last move made for reaching this state.
		moveToState = direction;

	}
	
	
	public boolean checkSolution() {
		
		for (int i = 1; i < table.length ; i++) {
			if (table[i][1] != 'A' + i - 1) {
				return false;
			}
		}
		
		return true;
	}
	
	
	// Will make a deep clone of this object.
	public SearchState clone() {
		
		return SerializationUtils.clone(this);
	}
	
	
	// Will return the Manhattan distance to the solution of this SearchState
	public int getDistanceToSolution() {
		
		
		return distanceToSolution;
	}
	
	
	public String getState() {
		
	    try {
	    	
	        int rows = table.length;
	        int columns = table[0].length;
	        StringBuilder str = new StringBuilder();

	        for(int i=0;i<rows;i++){
	        	
	        	str.append("|  ");
	            for(int j=0;j<columns;j++){
	            	
	            	if (table[i][j] == '\u0000') {
	            		
	            		str.append(".  ");
	            	}
	            	else {

	            		str.append(table[i][j] + "  ");
	            	}

	            }

	            str.append("|\n");
	        }

	        
	        return str.toString();

	    } catch(Exception e) {
	    	
	    	return "Matrix is Empty!!!";
	    }
	
	}
	
	
	public char getLastMove() {
		
		return moveToState;
	}
	
	
}

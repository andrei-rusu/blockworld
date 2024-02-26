
public class BlockworldPuzzleSolver {
	
	// will specify the strategy.
	private char strategy;
	
	// will specify grid dimension (d x d)
	private int dimension;
	
	
	// Flags
	
	// if the algo will do random moves.
	private boolean randomMoves;
	
	// if the solution is displayed in terms of moves or full states
	private boolean fullDisplay;
	
	// allows usage of a custom initial state
	private boolean customInitialState;
	
	// if the search will fully print all states it gets through or not
	// if this is set to false, only solution will be displayed.
	private boolean verbose;

	// custom positions of letters (need to be of even length - X and Y coordinates)
	private int[] customPositions;
	
	// agent custom set row position
	private int customAgentRowPos;
	
	// agent custom set column position.
	private int customAgentColPos;
	
	
	public BlockworldPuzzleSolver() {
		
		this.dimension = 4;
		this.strategy = 'B';
		this.customInitialState = false;
		this.randomMoves = false;
		this.fullDisplay = false;
		this.verbose = false;
	}
	
	// a completely customizable constructor
	public BlockworldPuzzleSolver(int dimension, char strategy, boolean customInitialState, boolean randomMoves, boolean stateDisplay, boolean verbose) {
		
		this.dimension = dimension;
		this.strategy = strategy;
		this.customInitialState = customInitialState;
		this.randomMoves = randomMoves;
		this.fullDisplay = stateDisplay;
		this.verbose = verbose;
	}

	
	public void setGridSize(int d) {
		
		this.dimension = d;
	}
	
	public void setStrategy(char strategy) {
		
		this.strategy = strategy;
			
	}
	
	public void setSolutionDisplayType(boolean fullDisplay) {
		
		this.fullDisplay = fullDisplay;
	}
	
	public void setRandomMoves(boolean randomMoves) {
		
		this.randomMoves = randomMoves;
	}
	
	public void setAllowCustomInitialState(boolean customInitialState) {
		this.customInitialState = customInitialState;
	}
	
	public void setVerbose(boolean verbose) {
		this.verbose = verbose;
	}
	
	
	public int getDimension() {
		return dimension;
	}

	public void setDimension(int dimension) {
		this.dimension = dimension;
	}

	public char getStrategy() {
		return strategy;
	}
	
	public String getStrategyFullName() {
		
		switch(strategy) {
			case 'B': return "Breadth First Search";
			case 'D': return "Depth First Search";
			case 'I': return "Iterative Deepening Search";
			case 'A': return "A* Heuristic Search";
		}
		
		return "Error: Search method unrecognized!";
	}


	public boolean isRandomMoves() {
		return randomMoves;
	}
	
	public boolean isFullDisplay() {
		return fullDisplay;
	}

	
	public void setCustomInitialState(int agentRowPos, int agentColPos, int... customLetterPositions) {

		this.customAgentRowPos = agentRowPos;
		this.customAgentColPos = agentColPos;
		this.customPositions = customLetterPositions;
	}

	// The method employed by the Controller to solve the puzzle with the given configuration.
	// @param : If true, a custom initial state will be used; If false, the original initial state is used.
	// If the search space is too big (especially in BFS when all nodes are kept in the fringe), 
	// this method might make the Heap run out of memory
	public String solve() throws OutOfMemoryError {
		
		TreeSearch treeSearch = new TreeSearch(strategy);
		
		SearchState initialState = null;
		if (!customInitialState) {
			initialState = new SearchState(dimension);
		}
		else {
			initialState = new SearchState(dimension, customAgentRowPos, customAgentColPos, customPositions);
		}
		
		String solution = treeSearch.search(initialState, randomMoves, fullDisplay, verbose);

		
		// Cleaning up the search space.
		treeSearch = null;
		initialState = null;
		
		return solution;
	}
	
	// The method employed by the Controller to test multiple times a search method with the given configuration.
	// @param : If true, a custom initial state will be used; If false, the original initial state is used.
	// If the search space is too big (especially in BFS when all nodes are kept in the fringe), 
	// this method might make the Heap run out of memory
	public String multipleTestSolve(int times) throws OutOfMemoryError  {
		
		TreeSearch treeSearch = new TreeSearch(strategy);
		
		SearchState initialState = null;
		if (!customInitialState) {
			initialState = new SearchState(dimension);
		}
		else {
			initialState = new SearchState(dimension, customAgentRowPos, customAgentColPos, customPositions);
		}
		
		String testResult = treeSearch.multipleSearch(initialState, times, randomMoves);
		
		// Cleaning up the search space.
		treeSearch = null;
		initialState = null;
		
		return testResult;
		
	}
}

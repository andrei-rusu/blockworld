import java.util.*;
import java.util.regex.*;

public class TreeSearch {

	// The list of possible moves.
	// Its order can be randomized and therefore it is not declared as final static.
	private List<Character> possibleMoves = Arrays.asList(new Character[]{'U','L','D','R'});
	
	// The root of this tree; it will always be the initial state.
    private Node<SearchState> root;
    
    private int timeComplexity;   		 // will tell the total time complexity (number of nodes created)
    private int currentSpaceComplexity;  // will tell the current space complexity (number of nodes currently in memory)
    private int spaceComplexity;         // will tell the total space complexity (maximum number of nodes in memory)
    
    private char strategy;

    // will contain the actual nodes in the tree.
    private AbstractCollection<Node<SearchState>> fringe;

    private static class Node<T> implements Comparable<Node<T>> {
    	
		private SearchState element;
		private Node<T> parent;
		private int depth;
		
		// needed only in depth-limited search.
		private int referenced;
	 
		private Node(SearchState element, Node<T> parent) {
		    this.element = element;
		    this.parent = parent;
		    this.referenced = 0;
		    
		    if (parent == null) {
		    	depth = 0;
		    }
		    else {
		    	depth = parent.depth + 1;
		    }
		}

		// The heuristic function will order the elements in the priority queue (Manhattan distance).
		// If the distances to solution are the same for the two nodes, the smaller depth will have priority.
		@Override
		public int compareTo(Node<T> toCompare) {
			
			int thisDistance = element.getDistanceToSolution() + depth;
			int toCompareDistance = toCompare.element.getDistanceToSolution() + toCompare.depth;
			
			return (thisDistance - toCompareDistance != 0) ? thisDistance - toCompareDistance : depth - toCompare.depth;
		}
    }


	public TreeSearch(char strategy) {
    	
		root = null;
		timeComplexity = 0;
		currentSpaceComplexity = 0;
		spaceComplexity = 0;
		
		this.strategy = strategy;
		
		switch(strategy) {
		
			case 'B': 
			case 'D':
			case 'I': {
				// Will act as both Stack and Queue depending on the case
				fringe = new ArrayDeque<Node<SearchState>>();
				break;
			}
			case 'A': {
				// Will act as a priority queue for A* heuristic
				fringe = new PriorityQueue<Node<SearchState>>();
				break;
			}
			default: {
				throw new IllegalArgumentException("This strategy is not recognized: " + strategy);
			}
		}
		
    }
    
    private void addNode(SearchState element, Node<SearchState> parent) {
    	
		if (root == null) {
			
			// The root node is not added to the fringe. 
			// As it is the initial state, we will have a global reference for it.
		    root = new Node<SearchState>(element, null);

		} 
		else {
			
			// based on the strategy, we add nodes to the fringe
			addNodeToFringe(new Node<SearchState>(element, parent));
			
			// we also keep track of how many times the parent is referenced.
			// if the parent is referenced by no child and it is not part of a solution, it will be removed from the memory.
			parent.referenced++;
			
		}
		
		// A node is created and then it is added to the fringe.
		// Therefore the time and space complexity both increase.
	    timeComplexity++;
	    currentSpaceComplexity++;
	    
	    if (currentSpaceComplexity > spaceComplexity) {
	    	spaceComplexity = currentSpaceComplexity;
	    }
	    
	   
    }
    
    private void discardNode(Node<SearchState> discard) {
    	
    	while (discard != root && discard.referenced == 0) {

    		// the following is equivalent to losing the reference to the current discarded node (making it ready for garbage collection),
    		// as the node is no longer needed in any solution; the check is then percolated up the tree.
    		discard = discard.parent;
    		currentSpaceComplexity--;
    		
    		// as its previous child is ready for garbage collection, the previous parent node is referenced one time less.
    		discard.referenced--;
    		
    		// the check will now be performed on the previous parent.
    		
    	}
    }
    
    
    // based on the strategy
    @SuppressWarnings("unchecked")
	private void addNodeToFringe(Node<SearchState> element) {
    	
    	switch(strategy) {
    	
			case 'B': {
				
				((Deque<Node<SearchState>>)fringe).add(element);
				break;
			}
			
			case 'D':
			case 'I': {
				
				((Deque<Node<SearchState>>)fringe).push(element);
				break;
			}
			case 'A': {
				
				((Queue<Node<SearchState>>)fringe).add(element);
				break;
			}
			default: {
				throw new IllegalArgumentException("This strategy is not recognized: " + strategy);
			}
    	}
    }
    
    
    // Expanding current node, adding child nodes to the fringe. It can randomize the order of moves.
	private void expand (Node<SearchState> parent, boolean randomize) {
    	
    	SearchState parentState = parent.element;
    	
    	if (randomize) {

    		Collections.shuffle(possibleMoves);
    	}

    	
    	for (char direction : possibleMoves) {
    		
        	if (parentState.canMove(direction)) {
        		
        		// Creating the new child state.
        		SearchState childState = parentState.clone();
        		childState.move(direction);
        		
        		// Adding the state to the tree.
        		addNode(childState, parent);
        	}
    	}
    	
    }
	
	
	// Returns a solution as String depending on the display type and the initial state.
	// If the search space is too big, this method might run out of Heap memory.
	public String search(SearchState initial, boolean randomMoves, boolean solutionFullStateDisplay, boolean displayAll) 
			throws OutOfMemoryError {
		
    	System.out.println("Finding a solution with (" + strategy + " Search)\n\nSearching...\n");
		
		// Adding the initial state to the tree. It will become the root element.
		addNode(initial, null);
		
		// Will remember the solution node after performing the type of search requested.
		Node<SearchState> solution = null;
		
    	// The strategy used in this search.
    	String strategyUsed = null;
		
		// Continuing towards the solution based on the strategy.
    	switch(strategy) {
    	
			case 'B': {
				
				solution = breadthFirstSearch(randomMoves, displayAll);
				strategyUsed = "Breadth First Search";
				
				break;
			}
			
			case 'D': {
				
				solution = depthFirstSearch(randomMoves, displayAll);
				strategyUsed = "Depth First Search";

				break;
			}
			case 'I': {
				
				solution = iterativeDeepeningSearch(randomMoves, displayAll);
				strategyUsed = "Iterative Deepening Search";

				break;
			}
			case 'A': {
				
				solution = heuristicASearch(randomMoves, displayAll);
				strategyUsed = "A* Heuristic Search";

				break;
			}
			default: {
				throw new IllegalArgumentException("This strategy is not recognized: " + strategy);
			}
    	}
    	
    	int depthOfSolution = solution.depth;
    	
    	
    	StringBuilder solutionDisplay = null;

    	
    	if (!solutionFullStateDisplay) {
    		
    		solutionDisplay = new StringBuilder();
    		
    		while (solution.parent != null) {
    			
    			solutionDisplay.append(solution.element.getLastMove() + " >- ");
    			solution = solution.parent;
    		}
    		
    		// We need the moves from the beginning to the end.
    		solutionDisplay = solutionDisplay.reverse().insert(0, "The " + strategyUsed + " was completed!\n\nHere is the first solution found:\n=================================\n\nInit");
    		solutionDisplay.append("\n\n=================================\n");
    	}
    	
    	else {
    		
    		solutionDisplay = new StringBuilder("The " + strategyUsed + " was completed!\n\nHere is the first solution found:\n=================================\n\n");
    		
    		// The full states will be remembered in a stack (implemented with ArrayDeque).
    		Deque<String> fullStates = new ArrayDeque<String>(depthOfSolution);
    		while (solution != null) {
    			
    			fullStates.push(solution.element.getState());
    			solution = solution.parent;
    			
    		}
    		
    		// The StringBuilder will have the states appended in reverse order, by popping from the stack.
    		while (!fullStates.isEmpty()) {
    			
    			solutionDisplay.append(fullStates.pop());
    			solutionDisplay.append("\n=================================\n\n");
    		}
        	
    	}
    	
    	solutionDisplay.append("\n");
    	solutionDisplay.append("Search Analysis\n");
    	solutionDisplay.append("----------------\n");
    	solutionDisplay.append("Search method used: " + strategyUsed + "\n");
    	solutionDisplay.append("Time complexity (Nodes created): " + timeComplexity + "\n");
    	solutionDisplay.append("Space complexity (Max Nodes ever in the fringe): " + spaceComplexity + "\n");
    	solutionDisplay.append("Depth of the solution: " + depthOfSolution + "\n");
    	
    	return solutionDisplay.toString();
		
	}
	
	
	// This method will NOT return any solution and it is entirely designed for analysing purposes.
	// It will perform the search multiple times, returning an average of the time and space complexity 
	// and an average of the depth of the solution based on each individual search results.
	//
	// @param times : number of times the search is run
	@SuppressWarnings("unused")
	public String multipleSearch(SearchState initial, int times, boolean randomMoves) 
			 throws OutOfMemoryError {
		
		float avgTimeComplex = 0;
		float avgSpaceComplex = 0;
		float avgDepthSol = 0;

		System.out.println("Running (" + strategy + " Search) tests " + times + " time(s):\n\n");
		
		StringBuilder analysis = new StringBuilder();
		
		Pattern pattern = Pattern.compile("\\d+");
		
		for (int i = 0; i < times; i++) {
			
			System.out.println("Test Number: " + (i+1) + "\n");

			String[] solution = search(initial, randomMoves, false, false).split("\n");
			
			System.out.println("Found a solution!\n\n");

			Matcher matchTime = pattern.matcher(solution[solution.length-3]);
			while (matchTime.find()) {
				avgTimeComplex += Float.valueOf(matchTime.group());
			}
			
			
			Matcher matchSpace = pattern.matcher(solution[solution.length-2]); 
			while (matchSpace.find()) {
				avgSpaceComplex += Float.valueOf(matchSpace.group());
			}
			

			Matcher matchDepth = pattern.matcher(solution[solution.length-1]); 
			while (matchDepth.find()) {
				avgDepthSol += Float.valueOf(matchDepth.group());			
			}
			
			
			// Reinitiating the whole search tree (only the strategy and the type of the empty fringe are preserved for the next search).
			
			root = null;
			
			for (Node<SearchState> node: fringe) {
				node = null;
			}
			fringe.clear();
			
			timeComplexity = currentSpaceComplexity = spaceComplexity = 0;

		}
		
		analysis.append("Test Searches Completed!\n");
		analysis.append("========================\n\n");
    	analysis.append("Search Analysis\n");
    	analysis.append("------------------------\n");
		analysis.append("Average Time complexity: " + (avgTimeComplex/times) + "\n");
		analysis.append("Average Space complexity: " + (avgSpaceComplex/times) + "\n");
		analysis.append("Average Depth of the solution: " + (avgDepthSol/times) + "\n");
		
		return analysis.toString();
		
	}
	
	
	
	// As a result of the implementation, the following methods do not differ by much.
	// Having separate methods for each one of them is a design choice to make each method more transparent.
	
	@SuppressWarnings("unchecked")
	private Node<SearchState> breadthFirstSearch(boolean randomMoves, boolean displayAll) 
			 throws OutOfMemoryError {
		
		Node<SearchState> current = root;
		SearchState currentState = root.element;
		
		while (! currentState.checkSolution()) {
			
			// Just for test purposes.
			if (displayAll) {
				if (current.parent != null) {
					System.out.println("From Parent on Depth " + current.parent.depth + ":\n\n" + current.parent.element.getState());
					System.out.println("By performing action : " + current.element.getLastMove());
					System.out.println("We reached the Current State:\n\n" + current.element.getState());
				}
				else {
					System.out.println("The Initial State is:\n\n" + current.element.getState());
				}
				System.out.println("Current Node Depth: " + current.depth + "\n");
				System.out.println("=======================================\n");
			}
			
			expand(current, randomMoves);

			current = ((Deque<Node<SearchState>>)fringe).remove();
			currentState = current.element;
			
		}
		
		return current;

	}

	
	// Since it is a DFS in an infinite tree, the space complexity will never decrease as we move down the branch.
	// As the left branch goes to infinity, the parent nodes will always have exactly one reference, so they will not be garbage collected.
	@SuppressWarnings("unchecked")
	private Node<SearchState> depthFirstSearch(boolean randomMoves, boolean displayAll) 
			 throws OutOfMemoryError {
		
		Node<SearchState> current = root;
		SearchState currentState = root.element;		
		
		while (! currentState.checkSolution()) {
			
			// Just for test purposes.
			if (displayAll) {
				if (current.parent != null) {
					System.out.println("From Parent on Depth " + current.parent.depth + ":\n\n" + current.parent.element.getState());
					System.out.println("By performing action : " + current.element.getLastMove());
					System.out.println("We reached the Current State:\n\n" + current.element.getState());
				}
				else {
					System.out.println("The Initial State is:\n\n" + current.element.getState());
				}
				System.out.println("Current Node Depth: " + current.depth + "\n");
				System.out.println("=======================================\n");
			}
			
			expand(current, randomMoves);

			current = ((Deque<Node<SearchState>>)fringe).pop();

			currentState = current.element;

		}
		
		return current;


	}
	
	
	@SuppressWarnings("unchecked")
	private Node<SearchState> depthLimitedSearch(int depthLimit, boolean randomMoves, boolean displayAll) 
			 throws OutOfMemoryError {
		
		Node<SearchState> current = root;
		SearchState currentState = root.element;
		
		if (displayAll) {
			
			System.out.println("Depth limit: " + depthLimit);
			System.out.println("---------------------------------------\n");
		}
		
		while (! currentState.checkSolution()) {
			
			// Just for test purposes.
			if (displayAll) {
				if (current.parent != null) {
					System.out.println("From Parent on Depth " + current.parent.depth + ":\n\n" + current.parent.element.getState());
					System.out.println("By performing action : " + current.element.getLastMove());
					System.out.println("We reached the Current State:\n\n" + current.element.getState());
				}
				else {
					System.out.println("The Initial State is:\n\n" + current.element.getState());
				}
				System.out.println("Current Node Depth: " + current.depth + "\n");
				System.out.println("=======================================\n");
			}
			
			// if the depthLimit is not exceeded, the nodes are still expanded.
			if (current.depth < depthLimit) {
				
				expand(current, randomMoves);
				
			}
			// else they are discarded together with their no longer referenced parents (if it is the case).
			else {
				
				discardNode(current);
			}
			
			// if the fringe reaches a point when it's empty without any solution found, the depth-limited search fails.
			if (fringe.isEmpty()) {
				
				return null; // equivalent with a failure in the search.
			}
			else {

				current = ((Deque<Node<SearchState>>)fringe).pop();
				currentState = current.element;
				
			}

			
		}

		return current;
	}
	
	@SuppressWarnings("unused")
	private Node<SearchState> iterativeDeepeningSearch(boolean randomMoves, boolean displayAll) 
			 throws OutOfMemoryError {
		
		Node<SearchState> solution = null;
		
		int i = 0;
		while ((solution=depthLimitedSearch(i, randomMoves, displayAll)) == null) {
			
			// Reinitiate the problem (in terms of space complexity).
			for (Node<SearchState> node: fringe) {
				node = null;
			}
			fringe.clear(); 			// Cleaning the fringe
			currentSpaceComplexity = 1; // The root element (initial state) never gets deleted. The space complexity is always restarting from 1. 
			
			++i;
		}
		
		return solution;

	}
	
	@SuppressWarnings("unchecked")
	private Node<SearchState> heuristicASearch(boolean randomMoves, boolean displayAll)
			 throws OutOfMemoryError {
		
		Node<SearchState> current = root;
		SearchState currentState = root.element;		
		
		while (! currentState.checkSolution()) {
			
			// Just for test purposes.
			if (displayAll) {
				if (current.parent != null) {
					System.out.println("From Parent on Depth " + current.parent.depth + " with the Manhattan distance " + current.parent.element.getDistanceToSolution() + ":\n\n" + current.parent.element.getState());
					System.out.println("By performing action : " + current.element.getLastMove() + ", we reach a Manhattan distance of " + current.element.getDistanceToSolution());
					System.out.println("We reached the Current State:\n\n" + current.element.getState());
				}
				else {
					System.out.println("The Initial State is:\n\n" + current.element.getState());
				}
				System.out.println("Current Node Depth: " + current.depth + "\n");
				System.out.println("=======================================\n");
			}
			
			expand(current, randomMoves);

			current = ((Queue<Node<SearchState>>)fringe).remove();

			currentState = current.element;

		}
		
		return current;
		
	}
}


import javax.swing.SwingUtilities;

/*
 * Used to start the Blockworld Puzzle Application
 */

public class BlockworldPuzzle {

	public static void main(String[] args) {
		
		
		// Controller component.
		// It can perform all its operations and functions in this main method without initializing the view.
		// The view does not support the "verbose" and the "custom initial state" options, but they are available
		// by using the controller directly here !!!
		BlockworldPuzzleSolver controller = new BlockworldPuzzleSolver();
		
		SwingUtilities.invokeLater ( new Runnable () {
			
			public void run() {

				// View component
				BlockworldPuzzleView view = new BlockworldPuzzleView("Blockworld Tile Puzzle", controller);
				view.initView();
            }
		});
		
		
//		// The following are examples of how the Blockworld Puzzle Simulation can be customized for tests using just this main method
//		// and the controller, without having to instantiate and use the view:
//		
//		// Setting the strategy to be used:
//		// B = BFS
//		// D = DFS
//		// I = IDS
//		// A = A* Heuristic
//		controller.setStrategy('B');
//		
//		// Setting the Grid size
//		controller.setDimension(4);
//		
//		// Allowing random moves
//		controller.setRandomMoves(true);
//		
//		// Full solution display or "just moves" display
//		controller.setSolutionDisplayType(true);
//
//		// Full Node information display
//		controller.setVerbose(true);
//
//		// Setting up a custom initial state
//		controller.setAllowCustomInitialState(true);
//		controller.setCustomInitialState(0,1,2,0,2,1,3,1);
//		
//		// Printing the first solution found + search analysis
//		System.out.println(controller.solve());
//		
////		// Printing average search analysis for i tests
////		int i = 50;
////		System.out.println(controller.multipleTestSolve(i));
		
	}

}


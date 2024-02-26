import java.awt.BorderLayout;

import java.awt.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.border.EmptyBorder;


public class BlockworldPuzzleView extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1440551688349249236L;
	
	private final static int BUTTONS_WIDTH = 300;
	private final static int BUTTONS_HEIGHT = 10000;
	
	private final static Color DARK_BLUE = new Color(25, 25, 112);
	
	private final static Font BUTTONS_FONT = new Font("Courier", Font.BOLD, 18);
	
	private BlockworldPuzzleSolver solver;
	private int gridSelected;
	private int methodSelected;
	private boolean randomMoves;
	private boolean displayType;
	private int noOfTests;

	public BlockworldPuzzleView(String title, BlockworldPuzzleSolver solver) {
		
		super(title);
		
		// The Controller can be setup separately without the View.
		this.solver = solver;
		this.gridSelected = solver.getDimension();
		
		switch(solver.getStrategy()) {
			case 'B': {methodSelected = 0; break;}
			case 'D': {methodSelected = 1; break;}
			case 'I': {methodSelected = 2; break;}
			case 'A': {methodSelected = 3; break;}
		}
		
		this.displayType = solver.isFullDisplay();
		
		this.randomMoves = solver.isRandomMoves();
		
		// By default, we are interested in seeing a single solution.
		this.noOfTests = 1;
		
	}

	public void initView() {
		
		
		//a nice look and feel package, this lines ensure the server app doesn't get "stuck" if it doesn't have this package
		try {
		    for (LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
		        if ("Nimbus".equals(info.getName())) {
		            UIManager.setLookAndFeel(info.getClassName());
		            break;
		        }
		    }
		} catch (Exception e) {
		    // If Nimbus is not available, here one can set the GUI to another look and feel.
		}
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(800,600);
		setLocation(100,100);
		setResizable(false);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BorderLayout());
		setContentPane(contentPane);
		
		JPanel titlePanel = new JPanel();
		titlePanel.setBackground(Color.BLACK);
		
		JLabel titleField = new JLabel("Welcome to Blockworld Tile Puzzle");
		titleField.setFont(new Font("Broadway", Font.BOLD, 32));
		titleField.setBorder(new EmptyBorder(50,0,50,0));
		titleField.setForeground(Color.CYAN);
		titleField.setHorizontalAlignment(SwingConstants.CENTER);
		
		titlePanel.add(titleField);
		
		JPanel buttonsPanel = new JPanel();
		buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.Y_AXIS));
		buttonsPanel.setBorder(new EmptyBorder(100,0,0,0));
		buttonsPanel.setBackground(Color.darkGray);

		
		JButton startButton = new JButton("Start the Simulation");
		startButton.setForeground(DARK_BLUE);
		startButton.setAlignmentX(CENTER_ALIGNMENT);
		startButton.setMaximumSize(new Dimension(BUTTONS_WIDTH, BUTTONS_HEIGHT));
		startButton.setFont(BUTTONS_FONT);
		startButton.addActionListener(e -> {
			
			JDialog applicationDisplay = new JDialog(this, "Blockworld Search Simulation");
			applicationDisplay.setSize(new Dimension(800,600));
			applicationDisplay.setResizable(false);
			
			JPanel panelContent = new JPanel();
			panelContent.setBackground(Color.BLACK);
			applicationDisplay.setContentPane(panelContent);
			applicationDisplay.setLocationRelativeTo(null);
			
			final JTextArea report = new JTextArea("\nSetting up the Magic Algorithms for solving the Blockworld Puzzle!\n\n");
			report.setForeground(Color.WHITE);
			report.setBackground(Color.DARK_GRAY);
			report.setFont(new Font("Courier New", Font.BOLD, 17));
			report.setEditable(false);
			
			report.append("Grid is of size : " + gridSelected + " x " + gridSelected + "\n");
			report.append("Search method used : " + solver.getStrategyFullName() + "\n");
			report.append("Random moves : " + (randomMoves? "Yes":"No") + "\n");
			
			if (noOfTests == 1) {
				report.append("Solution display type : " + (displayType? "Full Display":"Only Moves") +"\n\n");			
				report.append("Relax! The solution is on its way to you!\n");
			}
			else {
				report.append("Number of Test Searches to be effectuated: " + noOfTests + "\n\n");
				report.append("Relax! The tests will finish soon!\n");
			}
		    report.append("Wait a couple of minutes and enjoy a Cookie!\n");
			report.append("Searching...\n\n");
			
			JScrollPane reportPane = new JScrollPane(report);
			reportPane.setPreferredSize(new Dimension(750,560));
			
			panelContent.add(reportPane);
			
			applicationDisplay.setVisible(true);
			
			
			new Thread(new Runnable() {

				@Override
				public void run() {
					
					try {
						if (noOfTests == 1) {
							report.append(solver.solve());
						}
						else {
							report.append(solver.multipleTestSolve(noOfTests));
						}
					} catch (OutOfMemoryError error) {
						
						JOptionPane optionPane = new JOptionPane("FATAL !!!\nThe Application ran out of memory and has to be force closed!", JOptionPane.ERROR_MESSAGE);    
						JDialog dialog = optionPane.createDialog("Out of Memory Error");
						dialog.setAlwaysOnTop(true);
						dialog.setVisible(true);
						
						System.exit(ERROR);
						
						
					}

				}
				
			}).start();
			
			applicationDisplay.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

		});
		
		JButton optionsButton = new JButton("Simulation Settings");
		optionsButton.setForeground(DARK_BLUE);
		optionsButton.setAlignmentX(CENTER_ALIGNMENT);
		optionsButton.setMaximumSize(new Dimension(BUTTONS_WIDTH, BUTTONS_HEIGHT));
		optionsButton.setFont(BUTTONS_FONT);
		optionsButton.addActionListener(e -> {

			DefaultListCellRenderer dlcr = new DefaultListCellRenderer(); 
			dlcr.setHorizontalAlignment(DefaultListCellRenderer.CENTER);
			
			JPanel panel = new JPanel();
			panel.setPreferredSize(new Dimension(400,200));
			panel.setBorder(new EmptyBorder(20,10,20,10));
			panel.setLayout(new GridLayout(5,2));
			
			
			panel.add(new JLabel("Grid (d x d)"));
 
			JComboBox<Integer> gridSize = new JComboBox<Integer>();
			for (int i = 1; i <= 10; i++) {
				gridSize.addItem(i);
			}
			gridSize.setSelectedItem(gridSelected);
		    gridSize.setRenderer(dlcr);

			panel.add(gridSize);
			
			
			panel.add(new JLabel("Search Method"));
			
			JComboBox<String> searchMeth = new JComboBox<String>();
			searchMeth.addItem("Breadth First Search");
			searchMeth.addItem("Depth First Search");
			searchMeth.addItem("Iterative Deepening");
			searchMeth.addItem("A* heuristic search");
			searchMeth.setSelectedIndex(methodSelected);
			searchMeth.setRenderer(dlcr);

			
			panel.add(searchMeth);
			
			
			panel.add(new JLabel("Random Moves"));
			
			JComboBox<String> randomMovesChoice = new JComboBox<String>();
			randomMovesChoice.addItem("No");
			randomMovesChoice.addItem("Yes");
			randomMovesChoice.setSelectedIndex(randomMoves? 1 : 0);
			randomMovesChoice.setRenderer(dlcr);
			
			panel.add(randomMovesChoice);
			
			
			
			panel.add(new JLabel("Solution Display (Single Search Only)"));
			
			JComboBox<String> fullDisplayChoice = new JComboBox<String>();
			fullDisplayChoice.addItem("Just Moves");
			fullDisplayChoice.addItem("Full Display");
			fullDisplayChoice.setSelectedIndex(displayType? 1 : 0);
			fullDisplayChoice.setRenderer(dlcr);
			
			panel.add(fullDisplayChoice);
			
			
			// No solution will be displayed if more than 1 search is performed.
			panel.add(new JLabel("Multiple Searches (For Testing)"));
			JComboBox<Integer> multipleTests = new JComboBox<Integer>();
			for (int i = 1; i <= 100; i++) {
				multipleTests.addItem(i);
			}
			multipleTests.setSelectedItem(noOfTests);
			multipleTests.setRenderer(dlcr);
		    
		    panel.add(multipleTests);
			
			
			JOptionPane optionPane = new JOptionPane(panel,
	                JOptionPane.PLAIN_MESSAGE,
	                JOptionPane.DEFAULT_OPTION,
	                null);
			
			optionPane.setBackground(Color.darkGray);
			
			JDialog dialog = optionPane.createDialog("Modify simulation");
			dialog.setLocationRelativeTo(BlockworldPuzzleView.this.getContentPane());
			dialog.setVisible(true);
			
			if (optionPane.getValue() != null) {
				int result = Integer.parseInt(optionPane.getValue().toString());
				
				if (result == JOptionPane.OK_OPTION) {
					
					solver.setGridSize(Integer.parseInt(gridSize.getSelectedItem().toString()));
					gridSelected = gridSize.getSelectedIndex() + 1;
					
					// We will identify the search method selected by using the first character
					solver.setStrategy(searchMeth.getSelectedItem().toString().charAt(0));
					methodSelected = searchMeth.getSelectedIndex();
					
					solver.setRandomMoves(randomMovesChoice.getSelectedIndex() == 0 ? false : true);
					randomMoves = solver.isRandomMoves();
					
					solver.setSolutionDisplayType(fullDisplayChoice.getSelectedIndex() == 0 ? false : true);
					displayType = solver.isFullDisplay();
					
					noOfTests = Integer.parseInt(multipleTests.getSelectedItem().toString());
				}
			}

		});
		
		JButton exitButton = new JButton("Exit");
		exitButton.setForeground(DARK_BLUE);
		exitButton.setAlignmentX(CENTER_ALIGNMENT);
		exitButton.addActionListener(e -> {
			System.exit(0);
		});
		exitButton.setMaximumSize(new Dimension(BUTTONS_WIDTH, BUTTONS_HEIGHT));
		exitButton.setFont(BUTTONS_FONT);
		
		buttonsPanel.add(startButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		buttonsPanel.add(optionsButton);
		buttonsPanel.add(Box.createRigidArea(new Dimension(0,10)));
		buttonsPanel.add(exitButton);
		
	    buttonsPanel.add(Box.createVerticalGlue());
	    buttonsPanel.add(Box.createHorizontalGlue());

	    JPanel dummyPanelWest = new JPanel();
	    dummyPanelWest.setBackground(Color.BLACK);
	    
	    JPanel dummyPanelEast = new JPanel();
	    dummyPanelEast.setBackground(Color.BLACK);
	    
	    JPanel dummyPanelSouth = new JPanel();
	    dummyPanelSouth.setBackground(Color.BLACK);
		
		contentPane.add(titlePanel, BorderLayout.NORTH);
		contentPane.add(buttonsPanel, BorderLayout.CENTER);
		contentPane.add(dummyPanelWest, BorderLayout.WEST);
		contentPane.add(dummyPanelEast, BorderLayout.EAST);
		contentPane.add(dummyPanelSouth, BorderLayout.SOUTH);
		
		setVisible(true);
	}
}

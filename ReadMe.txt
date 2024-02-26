=============================================================================================================

Welcome to Blockworld Puzzle Application v1.01

=============================================================================================================

Author: Andrei Rusu
Organization: University of Southampton
Email: ar5g15@soton.ac.uk

=============================================================================================================

Credits for code usage with this distribution (in Libraries folder): 
	- Apache - Apache Commons Library: 
		+ commons-lang3-3.5.jar
		+ commons-lang3-3.5-javadoc.jar
		+ commons-lang3-3.5-sources.jar

=============================================================================================================

This application provides an intuitive GUI and some back-end components to fully test and assess
the capabilities of BFS, DFS, IDS and A* Heuristic Search on solving the Blockworld Puzzle.

To run this application, one can simply use our runnable jar file "Blockworld Puzzle.jar", located in the
"Runnable JAR" folder.

Alternatively, a tester can manipulate the back-end components to customize the search tests, by running
the application in the command line with the following instructions (after changing current directory):

javac -cp ".;Libraries/*" BlockworldPuzzle.java
java -d64 -Xmx8g -cp ".;Libraries/*" BlockworldPuzzle

This will temporarily increase the heap size of the JVM to a maximum of 8GB. If the tester does not want to
modify the heap size, "-d64 -Xmx8g" arguments may be excluded.

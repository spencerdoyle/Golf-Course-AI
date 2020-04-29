import java.util.Scanner;
import java.util.ArrayList;


public class Main {

	public static void main(String[] args) {
		
		Scanner scanner = new Scanner(System.in);
		
		ArrayList<String> input = new ArrayList<String>();
		
		// go ahead and store all the input lines to easily
		// come back to them later
		while (scanner.hasNextLine()) {
			String nextLine = scanner.nextLine();
			// for debugging simply add all input lines when run at command line
			if (nextLine.compareTo("1818") == 0) {
				break;
			} else {
				input.add(nextLine);
			}
			
		}
		
		// make list of all states in input file
		ArrayList<String> state_list = new ArrayList<String>();
		
		// check current state field of file
		for (int i = 0; i < input.size(); i++) {
			String input_line = input.get(i);
			String[] words = input_line.split("/");
			if (!state_list.contains(words[0])) {
				state_list.add(words[0]);
			}
		}
		
		// check resulting state field of file
		for (int i = 0; i < input.size(); i++) {
			String input_line = input.get(i);
			String[] words = input_line.split("/");
			if (!state_list.contains(words[2])) {
				state_list.add(words[2]);
			}
		}
		
		// make list of state objects
		ArrayList<State> state_objs = new ArrayList<State>();
		
		for (int i = 0; i < state_list.size(); i++) {
			state_objs.add(new State(state_list.get(i)));
		}
		
//		for (int i = 0; i < state_objs.size(); i++) {
//			System.out.println(state_objs.get(i).getName());
//		}
		
		// for all lines in the input file
		// associate the first state with
		// its possible actions
		for (int i = 0; i < input.size(); i++) {
			String input_line = input.get(i);
			String[] words = input_line.split("/");
			if (!findStateObject(words[0], state_objs).contains(words[1])) { //if action not yet found then add it
				findStateObject(words[0], state_objs).setPossibleAction(words[1]);
			}
		}
		
		//printPossibleActions(state_objs);
		
		for (int i = 0; i < input.size(); i++) {
			String input_line = input.get(i);
			String[] words = input_line.split("/");
			Action state_specific_action = findStateObject(words[0], state_objs).findActionObject(words[1]);
			state_specific_action.addResultingState(findStateObject(words[2], state_objs));
			state_specific_action.addTransition(findStateObject(words[2], state_objs), Double.parseDouble(words[3]));
		}
		
		//printStateActionTransitions(state_objs);
		
		Environment env = new Environment(state_objs);
		env.setGoalState();
		
		long BEGIN = System.currentTimeMillis();
		
		env.runQLearning();
		
		long END = System.currentTimeMillis();
		System.out.println();
		System.out.println("Model Free Q Learning Algorithm finished in... " + (END - BEGIN) / 1000.0 + "s");
		
		printStateActionUtilities(env.getStates());
		
		env.clearQ();
		
		BEGIN = System.currentTimeMillis();
		
		env.runDynaQ();
		
		END = System.currentTimeMillis();
		System.out.println();
		System.out.println("Model Based Dyna Q Algorithm finished in... " + (END - BEGIN) / 1000.0 + "s");
		
		printStateActionStateProbabilities(env.getStates());
		
		printStateActionUtilities(env.getStates());
		
	}
	
	// prints each state and all of its possible actions it can take
	// used for debugging
	private static void printStateActionUtilities(ArrayList<State> states) {
		//System.out.println();
		System.out.println("-----------------------------------------------------------------");
		System.out.println("STATE / ACTION / UTILITY --> OPTIMAL POLICY INFERRED");
		System.out.println("FROM HIGHEST ACTION UTILITY FOR A GIVEN STATE");
		System.out.println("-----------------------------------------------------------------");
		for (int i = 0; i < states.size() -1; i++) {
			states.get(i).printActions();
		}
		System.out.println("-----------------------------------------------------------------");
	}
	
	private static void printStateActionRewards(ArrayList<State> states) {
		//System.out.println();
		System.out.println("-----------------------------------------------------------------");
		System.out.println("STATE / ACTION / REWARD --> OPTIMAL POLICY INFERRED");
		System.out.println("FROM HIGHEST ACTION UTILITY FOR A GIVEN STATE");
		System.out.println("-----------------------------------------------------------------");
		for (int i = 0; i < states.size() -1; i++) {
			states.get(i).printRewards();
		}
		System.out.println("-----------------------------------------------------------------");
	}
	
	// recites every s/a/s combo and probability as given in input
	// used for debugging
	private static void printStateActionStateProbabilities(ArrayList<State> states) {
		//System.out.println();
		System.out.println("-----------------------------------------------------------------");
		System.out.println("STATE / ACTION / STATE / PROBABILITY --> ");
		System.out.println("////////////////////////////////////////");
		System.out.println("-----------------------------------------------------------------");
		for (int i = 0; i < states.size() -1; i++) {
			states.get(i).printActionTransitions();
		}
		System.out.println("-----------------------------------------------------------------");
	}
	
	// function to identify actual state object from state string
	private static State findStateObject(String state, ArrayList<State> states) {
		for (int i = 0; i < states.size(); i++) {
			if (state.compareTo(states.get(i).getName()) == 0) {
				return states.get(i);
			}
		}
		return null;
	}

}

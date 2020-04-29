import java.util.ArrayList;
import java.util.Random;

public class Environment {

	
	ArrayList<State> states;
	
	// easily change learning rate, discount rate, number of iterations
	// and epsilon value (associated with exploring/exploiting)
	
	// values used for model free Q learning function
	double learning_rate = 0.3;
	double discount = 0.9;
	int num_iterations = 10000;
	double epsilon = .8;
	// lets possibly "explore" for the first half of the iterations
	// and use a decay value to slowly decrease epsilon and our
	// chances of actually exploring as time goes on
	final int startEpsilonDecay = 1;
	final int endEpsilonDecay = num_iterations / 2;
	final double epsilonDecayValue = epsilon / (endEpsilonDecay - startEpsilonDecay);
	
	// values used for model based Dyna Q function
	double learning_rate_mb = 0.3;
	double discount_mb = 0.9;
	double learning_rate_mb_int = 0.5;
	double discount_mb_int = 0.8;
	int num_iterations_mb = 8000;
	int num_fake_iterations = 50; //used inside main loop for Dyna Q algorithm
	double epsilon_mb = .5;
	// lets possibly "explore" for the first half of the iterations
	// and use a decay value to slowly decrease epsilon and our
	// chances of actually exploring as time goes on
	final int startEpsilonDecay_mb = 1;
	final int endEpsilonDecay_mb = num_iterations_mb / 2;
	final double epsilonDecayValue_mb = epsilon_mb / (endEpsilonDecay_mb - startEpsilonDecay_mb);
	
	Environment(ArrayList<State> states) {
		this.states = states;
	}
	
	// Q-Learning Algorithm (Model-Free)
	// implements Equation 21.8 in textbook
	// also found at https://en.wikipedia.org/wiki/Q-learning
	public void runQLearning() {
		
		Random random = new Random();
		
		// loop for certain number of iterations/episodes
		for (int i = 0; i < num_iterations; i++) {
			
			// pick random state
			State state = states.get(random.nextInt(states.size()));
			
			// update Q Learning function until goal state reached
			while (!state.isGoalState()) {
				
				Action action;
				
				// here is where we determine an action to take
                // a random float b/w 1 and 0 is compared against epsilon
                // if it is greater than epsilon it will take the action
                // with the highest Q-value and "exploit", otherwise it will 
                // select an action at random to "explore"
                // note that we will continually decrease the epsilon value
                // so in the long run it explores less and less
				if (random.nextDouble() > epsilon) {
                	action = state.getMaxQAction();
                } else {
                	action = state.randomAction();
                }
				
				// get resulting state based on input probabilities
				State nextState = action.getResultingState();
				
				double q = action.getQ();
				double maxQ = nextState.getMaxQ();
				int r = action.getReward();
				
				// update Q values from Q learning function equation
				double value = q + (learning_rate * (r + (discount * maxQ) - q));
				action.setQ(value);
				
				state = nextState;
				
			}
			
			// after each iteration we will slightly decrease epsilon
            // to eventually stop exploring
			if (i <= endEpsilonDecay) {
            	epsilon -= epsilonDecayValue;
            }
			
		}
		
		
		
	}
	
	// Dyna Q Algorithm (Model-Based)
	public void runDynaQ() {
		
		Random random = new Random();
		
		for (int i = 0; i < num_iterations_mb; i++) {
			
			// pick random state
			State state = states.get(random.nextInt(states.size()));
			
			// continue updating Q for state/actions until goal state reached
			while (state != null) {
				
				Action action;
				
				// exploring/ exploiting
				if (random.nextDouble() > epsilon_mb) {
                	action = state.getMaxQAction();
                } else {
                	action = state.randomAction();
                }
				
				
					// resulting state determined by given probabilities
				State nextState = action.getResultingState();
					
				double q = action.getQ();
					
				double maxQ;
					
				if (nextState == null) {
						maxQ = 100; // try other values
				} else {
					maxQ = nextState.getMaxQ();
				}
					
				int r = action.getReward();
					
				// update Q using Q Learning Algorithm
				// Dyna Q uses the Q learning equation
				double value = q + (learning_rate_mb * (r + (discount_mb * maxQ) - q));
				action.setQ(value);
					
				// MODEL UPDATE
				// incrementing the number of times we have observed the resulting state
				// the transition object is unique to a specific state/action/state triplet
				if (nextState != null) {
					Transition transition = action.getTransition(nextState);
					transition.incrementObservation();
				}
				// MODEL UPDATE
				// updating the reward function for a given state/action
				double expected_reward = action.getExpectedReward();
				double reward_update = ((1 - learning_rate_mb) * expected_reward) + (learning_rate_mb * r);
				action.setExpectedReward(reward_update);
				
				// incrementing action observances used to later calculate
				// the model based resulting probabilities
				action.incrementObservation();
				
				
				
				state = nextState;
				
				// waiting a fair number of iterations to better chances that
				// all s/a pairs have been observed at least once
				if (i > 1000) {
				
					// In the Dyna Q Algorithm we are able to run more "fake"
					// scenarios internally at a cheaper cost because we are 
					// inferring the resulting state from the updated Model.
					// Here we are basically running Q learning algorithm
					// again except always picking a random state and action
					// and inferring the result from updated Reward model
					// in the outer loop
					// Q still gets updated inside here
					for (int j = 0; j < num_fake_iterations; j++) {
						
						State internal_state = states.get(random.nextInt(states.size()-1));
						
						Action internal_action = internal_state.randomAction();
						
						State next_internal_state = internal_action.getHighestObservedResult(); //picking next state by
																								//inferring from T table
																								//the highest probability
						
						int internal_reward = internal_action.getReward();
						double internal_q = internal_action.getQ();
						double max_internal_Q = next_internal_state.getMaxQ();
						
						double internal_value = internal_q + (learning_rate_mb_int * 
								(internal_reward + (discount_mb_int * max_internal_Q) - internal_q));
						
						internal_action.setQ(internal_value);
						
					}
					
				}
				
			}
			
			if (i <= endEpsilonDecay_mb) {
            	epsilon_mb -= epsilonDecayValue_mb;
            }
			
		}
		
	}
	
	public void clearQ() {
		for (int i = 0; i < states.size(); i++) {
			states.get(i).clearQ();
		}
	}
	
	public ArrayList<State> getStates() {
		return this.states;
	}
	
	// this function sets the goal state and creates a fake
	// action from it "WIN"
	// this is used to guide the function towards
	// a clearly desired reward
	public void setGoalState() {
		for (int i = 0; i < states.size(); i++) {
			if (states.get(i).getActionList().size() == 0) {
				states.get(i).setGoalState();
				states.get(i).setPossibleAction("WIN");
				states.get(i).randomAction().setQ(100);
				// set absurdly high reward for fake action "WIN"
				states.get(i).randomAction().setReward(10000000);
			}
		}
	}
	
}

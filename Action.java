import java.util.ArrayList;
import java.util.Random;

public class Action {

	String name;
	ArrayList<State> resulting_states = new ArrayList<State>();
	ArrayList<Transition> transitions = new ArrayList<Transition>();
	double q_value;
	int reward; //immediate reward
	double expected_reward; //learned reward function
	boolean has_been_observed;
	double observations;
	
	Action(String name) {
		this.name = name;
		this.q_value = 0; // maybe random b/w 0 & 1
		this.reward = -1;
		this.expected_reward = 0;
		this.has_been_observed = false;
		this.observations = 0; //maybe try 0.00001
	}
	
	public String getName() {
		return this.name;
	}
	
	public void incrementObservation() {
		this.observations = this.observations + 1;
	}
	
	public void setObservedToTrue() {
		this.has_been_observed = true;
	}
	
	public boolean hasBeenObserved() {
		return has_been_observed;
	}
	
	public void addResultingState(State state) {
		this.resulting_states.add(state);
	}
	
	public void addTransition(State state, double prob) {
		this.transitions.add(new Transition(state, prob));
	}
	
	public void printTransitions(String state) {
		for (int i = 0; i < transitions.size(); i++) {
			double prob = transitions.get(i).getNumObservations() / this.observations;
			System.out.println(state + "/" + this.name + "/" + transitions.get(i).getResultingStateName() + "/" + prob);
		}
		//System.out.println();
	}
	
	public double getQ() {
		return q_value;
	}
	
	public void setQ(double new_q) {
		this.q_value = new_q;
	}
	
	public void clearQ() {
		this.q_value = 0;
	}
	
	//based on given probabilities
	public State getResultingState() {
		if (resulting_states.size() == 0) {
			return null;
		}
		Random rand = new Random();
		double random_dbl = rand.nextDouble(); // b/w 0 and 1
		double prob_sum = 0;
		for (int i = 0; i < transitions.size(); i++) {
			prob_sum = prob_sum + transitions.get(i).getProbability();
			if (random_dbl < prob_sum) {
				return transitions.get(i).getResultingState();
			}
		}
		return transitions.get(0).getResultingState();
	}
	
	public int getReward() {
		return this.reward;
	}
	
	public void setReward(int value) {
		this.reward = value;
	}
	
	public double getExpectedReward() {
		return this.expected_reward;
	}
	
	public void setExpectedReward(double value) {
		this.expected_reward = value;
	}
	
	public Transition getTransition(State state) {
		for (int i = 0; i < transitions.size(); i++) {
			if (transitions.get(i).getResultingState() == state) {
				return transitions.get(i);
			}
		}
		return null;
	}
	
	public State getHighestObservedResult() {
		double max_obs = 0;
		State best_state = null;
		for (int i = 0; i < transitions.size(); i++) {
			if (transitions.get(i).getNumObservations() > max_obs) {
				max_obs = transitions.get(i).getNumObservations();
				best_state = transitions.get(i).getResultingState();
			}
		}
		return best_state;
	}
	
}

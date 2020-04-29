public class Transition {

	double probability;
	State resulting_state;
	double observations;
	
	Transition(State state, double prob) {
		this.probability = prob;
		this.resulting_state = state;
		// will increment this to determine final learned 
		// probability function using a double starting at this 
		// value in case we encounter a divide by zero scenario
		this.observations = 0.00001;
	}
	
	public String getResultingStateName() {
		return this.resulting_state.getName();
	}
	
	public double getProbability() {
		return this.probability;
	}
	
	public State getResultingState() {
		return this.resulting_state;
	}
	
	public void incrementObservation() {
		this.observations = this.observations + 1;
	}
	
	public double getNumObservations() {
		return this.observations;
	}
	
}

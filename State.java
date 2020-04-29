import java.util.ArrayList;
import java.util.Random;

public class State {

	String name;
	ArrayList<Action> possible_actions = new ArrayList<Action>();
	boolean has_been_observed;
	boolean is_goal_state;
	
	State(String name) {
		this.name = name;
		this.has_been_observed = false;
		this.is_goal_state = false;
	}
	
	public String getName() {
		return name;
	}
	
	public void setObservedToTrue() {
		has_been_observed = true;
	}
	
	public boolean hasBeenObserved() {
		return has_been_observed;
	}
	
	public boolean contains(String action) {
		for (int i = 0; i < possible_actions.size(); i++) {
			if (possible_actions.get(i).getName().compareTo(action) == 0) {
				return true;
			}
		}
		return false;
	}
	
	public Action findActionObject(String action) {
		for (int i = 0; i < possible_actions.size(); i++) {
			if (possible_actions.get(i).getName().compareTo(action) == 0) {
				return possible_actions.get(i);
			}
		}
		return null;
	}
	
	public void setPossibleAction(String action) {
		this.possible_actions.add(new Action(action));
	}
	
	public void printActions() {
		for (int i = 0; i < possible_actions.size(); i++) {
			System.out.println(this.name + "/" + possible_actions.get(i).getName() + "/" + possible_actions.get(i).getQ());
		}
	}
	
	public void printRewards() {
		for (int i = 0; i < possible_actions.size(); i++) {
			System.out.println(this.name + "/" + possible_actions.get(i).getName() + "/" + possible_actions.get(i).getExpectedReward());
		}
	}
	
	public void printActionTransitions() {
		for (int i = 0; i < possible_actions.size(); i++) {
			possible_actions.get(i).printTransitions(this.name);
		}
	}
	
	public boolean isGoalState() {
		return this.is_goal_state;
	}
	
	public ArrayList<Action> getActionList() {
		return possible_actions;
	}
	
	public void setGoalState() {
		this.is_goal_state = true;
	}
	
	public Action getMaxQAction() {
		if (this.is_goal_state) {
			return this.possible_actions.get(0);
		}
		double maxVal = Double.MIN_VALUE;
		Action maxAction = possible_actions.get(0);
		for (int i = 0; i < possible_actions.size(); i++) {
			if (possible_actions.get(i).getQ() >= maxVal) {
				maxVal = possible_actions.get(i).getQ();
				maxAction = possible_actions.get(i);
			}
		}
		return maxAction;
	}
	
	public double getMaxQ() {
		if (possible_actions.size() == 0) { //need some essence of a goal state
			return 100;
		}
		double maxVal = Double.MIN_VALUE;
		for (int i = 0; i < possible_actions.size(); i++) {
			if (possible_actions.get(i).getQ() >= maxVal) {
				maxVal = possible_actions.get(i).getQ();
			}
		}
		return maxVal;
	}
	
	public Action randomAction() {
		if (possible_actions.size() == 0 ) {
			return null;
		}
		Random random = new Random();
		int index = random.nextInt(possible_actions.size());
		return possible_actions.get(index);
	}
	
	public int getPossibleActionCount() {
		return possible_actions.size();
	}
	
	public void clearQ() {
		for (int i = 0; i < possible_actions.size(); i++) {
			possible_actions.get(i).clearQ();
		}
	}
	
}

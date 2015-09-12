package theory;

import java.util.Set;

public class Constraint {
	Set<String> lhs;
	Set<String> rhs;
	boolean neg = false;
	
	public Constraint(Set<String> lhs, Set<String> rhs) {
		this.lhs = lhs;
		this.rhs = rhs;
	}
	
	public void negate() {
		this.neg = true;
	}
}

package theory;

import java.util.HashSet;
import java.util.Set;

import logic.Atom;
import logic.Formula;

public class Conditional {
	Formula lhs;
	Atom rhs;
	Set<Constraint> constraintsClauses;
	HashSet<String> vars;
	
	public Conditional(String str) throws Exception {
		if (!str.contains("=>") && !str.contains("->")) {
			throw new Exception("conditional => or -> missing");
		}
		
		// if the conditional contains "->", translate it to a regular conditional as follows:
		// A -> B becomes A & -B => false
		if (str.contains("->")) {
			String[] f = str.split("->");
			if (f.length == 1) {
				throw new Exception("Syntax error");
			}
			f[0] = f[0].trim();
			f[1] = f[1].trim();
			str = f[0] + " and " + (f[1].startsWith("-")?f[1].substring(1, f[1].length()):"-"+f[1]) + " => false";
			
			System.out.println("new conditional: " + str);
			
		}
		
		String[] form = str.split("=>");
		
		if (form.length == 1) {
			throw new Exception("Syntax error");
		}
		
		lhs = new Formula(form[0].trim());
		rhs = new Atom(form[1].trim());
		
		vars = lhs.getVars();
		if (!rhs.isBoolean()) {
			vars.add(rhs.getVar());
		}
						
		if (!Theory.vars.containsAll(vars)) {
			throw new Exception("Illegal variables used in conditional \"" + str + "\": first declare them!");
		}
	}
	
	public HashSet<String> getVars() {
		return vars;
	}
	
	public String getPositiveFormula() {
		if (lhs.isBoolean(false) || rhs.isBoolean(false)) {
			return "false";
		}
		if (lhs.isBoolean(true)) {
			return rhs.toString();
		}
		if (rhs.isBoolean(true)) {
			return lhs.toString();
		}
		
		return lhs.toString() + " and " + rhs.toString();
	}
	
	public String getExceptionFormula() {
		String l = this.lhs.toString();
		String r = this.rhs.toString();
		String negConcl = r.startsWith("-") ? r.substring(1, r.length()) : "-" + r;
		
		if (lhs.isBoolean(false) || rhs.isBoolean(true)) {
			return "false";
		}
		if (lhs.isBoolean(true)) {
			return negConcl;
		}
		if (rhs.isBoolean(false)) {
			return l;
		}
		
		return l + " and " + negConcl;
	}
	
	public String toString() {
		return lhs.toString() + " => " + rhs.toString();
	}
}

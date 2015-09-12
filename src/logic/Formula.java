package logic;

import java.util.HashSet;

public class Formula {
	// For now formulas are just conjunctions without parenthesis of the following form:
	// p and q and r and -s and ...
	
	HashSet<Atom> atoms;
	
	public Formula(String str) {
		atoms = new HashSet<Atom>();
		findAtoms(str);
		if (atoms.size() == 0) {
			atoms.add(new Atom(true));
		}
	}
	
	private void findAtoms(String str) {
		String[] form = str.split(" and ");
		
		String atom = form[0].trim();
		
		
		if (atom.equals("false")) {
			atoms.clear();
			atoms.add(new Atom(false));
			return;
		}
		
		if (!atom.equals("true")) {
			atoms.add(new Atom(form[0].trim()));
		}
		
		if (form.length > 1) {
			for (int i=0; i<form.length-1; i++) {
				findAtoms(form[i+1].trim());
			}
		}
	}
	
	public HashSet<String> getVars() {
		HashSet<String> ret = new HashSet<String>();
		
		for (Atom a : atoms) {
			if (!a.isBoolean()) {
				ret.add(a.getVar());
			}
		}
		
		return ret;
	}
	
	public String toString() {
		String ret = "";
		boolean first = true; 
		for (Atom a : atoms) {
			if (!first) ret += " and ";
			ret += a.toString();
			first = false;
		}
		return ret;
	}
	
	public boolean isBoolean(boolean b) {
		return ((atoms.size() == 1) && 
				((Atom) atoms.toArray()[0]).isBoolean(b));
	}
}

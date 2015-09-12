package theory;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class Theory {
	Set<Conditional> conditionals;
	static LinkedList<String> vars;
	LinkedHashMap<String, Integer> rankMap = null;
	public boolean SORT = true;
	
	public Theory() {
		conditionals = new HashSet<Conditional>();
		vars = new LinkedList<String>();
	}
	
	private void reinitRankMap() {
		rankMap = new LinkedHashMap<String, Integer>();
		
		for (int i=0; i<(int) Math.pow(2,vars.size()); i++) {
			rankMap.put(String.format("%"+vars.size()+"s", Integer.toBinaryString(i)).replace(' ', '0'), 0);
		}
	}
	
	public void addVars(String prop) {
		for (String p : prop.split(" ")) {
			vars.add(p);
		}
		
		reinitRankMap();
	}
	
	public void addCond(String conds) throws Exception {
		for (String cond : conds.split(",")) {
			Conditional c = new Conditional(cond.trim());
			conditionals.add(c);
			
			shiftRanking(c);
			
			checkAllConditionals();
		}
		
	}
	
	public boolean query(String cond) throws Exception {
				
		Conditional c = new Conditional(cond);
		boolean sat = satisfied(c);
				
		return sat;		
	}
	
	private void shiftRanking(Conditional c) {		
		// shift the exception worlds until the constraint is satisfied
		while (!satisfied(c)) {
			shiftOne(c);
		}
	}
	
	private void checkAllConditionals() {
		// check whether the other constraints are still satisfied
		boolean allSatisfied = false;
		
		while (!allSatisfied) {
			allSatisfied = true;
			for (Conditional c1 : conditionals) {
				if (!satisfied(c1)) {
					allSatisfied = false;
					shiftRanking(c1);
				}
			}
		}
	}
	
	private boolean satisfied(Conditional c) {
		getWorldsOf(c.getPositiveFormula());
		
		int minPositiveRanking = ranking(getWorldsOf(c.getPositiveFormula()));
		int minExceptionRanking = ranking(getWorldsOf(c.getExceptionFormula()));
		
		return minPositiveRanking < minExceptionRanking;
	}
	
	private int ranking(LinkedList<String> worlds) {
		int min = Integer.MAX_VALUE;
		
		for (String w : worlds) {
			if (rankMap.get(w) < min) {
				min = rankMap.get(w);
			}
		}
		
		return min;
	}
	
	private void shiftOne(Conditional c) {
		
		if (c.getPositiveFormula() == "false") {
			setWorldsTo(c.getExceptionFormula(), Integer.MAX_VALUE);
		}
		
		else {	
			shiftWorldsOf(c.getExceptionFormula(), 1);
		}
	}
	
	private void shiftWorldsOf(String form, int n) {
		for (String b : getWorldsOf(form)) {
			rankMap.put(b, rankMap.get(b)+n);
		}
	}
	
	private void setWorldsTo(String form, int n) {
		for (String b : getWorldsOf(form)) {
			rankMap.put(b, n);
		}
	}
	
	LinkedList<String> getWorldsOf(String form) {
		LinkedList<String> ret = new LinkedList<String>();
		
		LinkedHashMap<Integer, Integer> binaryValueMap;
		try {
			binaryValueMap = getBinaryValueMap(form);
		} catch (Exception e) {
			// we have a contradiction, return empty set of worlds
			//e.printStackTrace();
			return ret;
		}
				
		for (String b : rankMap.keySet()) {
			if (satisfiesConstraints(b, binaryValueMap)) {
				ret.add(b);
			}
		}
		
		return ret;
	}
	
	private LinkedHashMap<Integer, Integer> getBinaryValueMap(String form) throws Exception {
		LinkedHashMap<Integer, Integer> ret = new LinkedHashMap<Integer,Integer>();
		
		if (form == "false") {
			return ret;
		}
		
		for (String atom : form.split("and")) {
			atom = atom.trim();
			boolean negated = atom.startsWith("-");
			int value = (negated ? 0 : 1);
			String var = (negated ? atom.substring(1, atom.length()) : atom);
			int index = indexOf(var);
			
			Integer currentValue = ret.get(index);
			
			// look if we have a contradiction, if so, throw an exception
			if ((currentValue != null) && (Math.abs(ret.get(index)-value)==1)) {
				throw new Exception("not adding "+var+": input value ("+value+") contradicts with existing value ("+ret.get(index)+")");
			}
			else {
				ret.put(indexOf(var), value);
			}
		}
		
		return ret;
	}
	
	private int indexOf(String var) {
		for (int i=0; i<vars.size(); i++) {
			if (vars.get(i).equals(var)) {
				return i;
			}
		}
		
		return -1;
	}
	
	private boolean satisfiesConstraints(String b, LinkedHashMap<Integer, Integer> binaryValueMap) {
		boolean ret = true;
		
		for (Map.Entry<Integer, Integer> entry : binaryValueMap.entrySet()) {
		    int pos = entry.getKey();
		    int value = entry.getValue();
		    
		    int value2 = Character.getNumericValue(b.charAt(pos));
		    if (value != value2) {
		    	ret = false;
		    }
		}
		
		return ret;
	}
	
	public String rankingToString() {
		String ret = "";
		for (String var : vars) {
			ret +=  var + " ";
		}
		ret += "| rank\n";
		for (int i=0; i<vars.size(); i++) {
			ret += "-";
			for (int j=0; j<vars.get(i).length(); j++) {
				ret += "-";
			}
		}
		ret += "------\n";
		
		if (SORT) sortRanking();
		
		for (Map.Entry<String, Integer> entry : rankMap.entrySet()) {
			String key = entry.getKey();
			for (int i=0; i<key.length(); i++) {
				ret += key.charAt(i);
				
				for (int j=0; j<vars.get(i).length(); j++) {
					ret += " ";
				}
			}
			int val = entry.getValue();
			ret += "| " + (val==Integer.MAX_VALUE?"infinity":val) + "\n";
		}
		
		return ret;		
	}
	
	private void sortRanking() {
		List<Map.Entry<String, Integer>> entries = new ArrayList<Map.Entry<String, Integer>>(rankMap.entrySet());
		Collections.sort(entries, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> a, Map.Entry<String, Integer> b) {
				return a.getValue().compareTo(b.getValue());
			}
		});
		
		LinkedHashMap<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (Map.Entry<String, Integer> entry : entries) {
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		
		rankMap = sortedMap;
	}
			
	public String toString() {
		return "--- propositions:\n"
				+ vars + "\n\n"
				+ "--- conditionals:\n"
				+ conditionals + "\n";
	}
}

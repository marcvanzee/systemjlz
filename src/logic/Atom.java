package logic;

public class Atom {
	String var;
	boolean negated;
	
	public Atom(String str) {
		str = str.trim();
		if (str.charAt(0) == '-') {
			negated = true;
			str = str.substring(1, str.length());
		}
		
		var = str;
	}
	
	public Atom(boolean b) {
		var = (b ? "true" : "false");
	}
	
	public String getVar() {
		return var;
	}
	
	public String toString() {
		return (negated?"-":"")+var;
	}
	
	public boolean isBoolean() {
		return (var.equals("true") || var.equals("false"));
	}
	
	public boolean isBoolean(boolean b) {
		return ((b && var.equals("true") || !b && var.equals("false")));
	}
}

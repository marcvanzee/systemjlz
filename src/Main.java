import java.util.LinkedList;

import theory.Conditional;
import theory.Theory;

public class Main {

	Theory theory;
	
	public static void main(String arg[]) {
		(new Main()).simpleTest();
	}
	
	/**
	 * 
	 *  Here I do my simple tests
	 *  
	 */
	public void simpleTest() {
		
		try {
			theory = new Theory();
			
			String vars = "load sound loaded shoot dead fly";
			String conds = "load => sound, load => loaded, shoot and loaded => dead, sound => fly, fly => -dead";
			
			theory.addVars(vars);
			
			String query = "load and shoot => -dead";
						
			theory.addCond(conds);
			
			//System.out.println("\n"+theory.rankingToString());
			
			System.out.println(theory.query(query));
		
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * 
	 * Here I try to develop an action language, where the user enters the following information:
	 * - Fluents
	 * - Action specifications
	 * - Action occurrences
	 * - Initial state
	 * 
	 * Then this will be translated into a set of defaults of which we compute the values of the fluents.
	 * 
	 */
	public void actionLanguage() {
		theory = new Theory();
		
		/**
		 * SCENARIO SPECIFICATION
		 */
	
		// FLUENTS
		String fluents = "alive loaded";
		int MINTIME = 0;
		int MAXTIME = 5;
		
		// ACTIONS
		LinkedList<Action> actions = new LinkedList<Action>();
		actions.add(new Action("shoot", "alive and loaded", "-alive"));
		actions.add(new Action("reanimate", "-alive", "alive"));
		
		// ACTION OCCURRENCES
		LinkedList<Occurrence> occ = new LinkedList<Occurrence>();
		occ.add(new Occurrence("shoot", 1));
		occ.add(new Occurrence("reanimate", 2));
		
		// INITIAL STATE
		String initial = "alive and loaded";
		
		/**
		 * GENERATING DEFAULTS
		 */
		
		// first generate all variables from fluents and actions
		String vars = "";
		for (int i=MINTIME; i<=MAXTIME; i++) {
			for (String fluent : fluents.split(" ")) {
				vars += fluent + i + " ";
			}
			
			for (Action a : actions) {
				vars += a.name + i + " ";
			}
		}
		
		System.out.println(vars);
	}
	
	/**
	 * Yale Shooting Problem [Hanks and McDermott (1987) - "Nonmonotonic logics and temporal projection"]
	 * 
	 * - The Frame problem
	 * - The gun cannot magically unload, i.e. actions are independent of persistence
	 * 
	 * - I added an additional action that reanmiates the dead turkey in order to test how it works with two actions.
	 * 
	 * 
	 */
	public void ysp() {
		theory = new Theory();
				
		
		/**
		 * SCENARIO:
		 * 
		 * fluents: a=alive, l=loaded
		 * actions: s=shoot, r=reanimate
		 * 
		 * action specifications: 
		 *  - alive1 & loaded1 & shoot1 => -alive2 and -loaded2
		 *  - -alive2 & reanimate2 => alive3
		 *  
		 * initial state: alive1, loaded1
		 * 
		 * action occurrences: shoot1, reanimate2
		 * 
		 * DESIRED OUTCOME:
		 * 
		 * t|0  1  2  3
		 * -------------
		 * a|1  1  0  1
		 * l|1  1  0  0
		 * s|-  1  -  -
		 * r|-  -  1  -
		 */
		String fluents = "l0 l1 l2 l3 a0 a1 a2 a3";
		String actions = "s1 r2";
		String conds = "a1 and l1 and s1 and r2 => -a2, a1 and l1 and s1 and r2 => -l2, -a2 and s1 and r2 => a3," // actions
				+ "a0 and l0 and s1 and r2 => a1, a0 and l0 and s1 and r2 => l1, "
				+ "-a0 and l0 and s1 and r2 => -a1, -a0 and l0 and s1 and r2 => l1, "
				+ "a0 and -l0 and s1 and r2 => a1, a0 and -l0 and s1 and r2 => -l1,"
				+ "-a0 and -l0 and s1 and r2 => -a1, -a0 and -l0 and s1 and r2 => -l1,"
				
				+ "-a1 and l1 and s1 and r2 => -a2, -a1 and l1 and s1 and r2 => l2,"
				+ "a1 and -l1 and s1 and r2 => a2, a1 and -l1 and s1 and r2 => -l2,"
				+ "-a1 and -l1 and s1 and r2 => -a2, -a1 and -l1 and s1 and r2 => -l2,"
				
				+ "a2 and s1 and r2 => a3,"
				+ "l2 and s1 and r2 => l3,"
				+ "-l2 and s1 and r2 => -l3";
		
		String initial = "l0 and a0";
		String occ = "s1 and r2";
				
		try {
			theory.addVars(fluents + " " + actions);
			theory.addCond(conds);
			
			//System.out.println(theory.rankingToString());
			
			for (String fluent : fluents.split(" ")) {
				System.out.print(fluent + ": " + theory.query(initial + " and " + occ + "=>" + fluent));
				System.out.println("\t-" + fluent + ": " + theory.query(initial + " and " + occ + "=>" + "-" + fluent));
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Stanford Murder Mystery [Baker (1991) - "Nonmonotonic Reasoning in the Framework of Situation Calculus"]
	 * 
	 * - Postdiction
	 * 
	 */
	public void smm() {
		theory = new Theory();
				
		//String conds = "l1 => l2, a1 => a2, " // persistence
		//		+ "a1 and s1 and l1 => -a2," // actions
		//		+ "a0 and l0 and s1 => a1, a0 and l0 and s1 => l1"; // independence statements
	
		//String query = "a0 and s1 and l0 => l2";
		
		
		/**
		 * SCENARIO:
		 * 
		 * fluents: a=alive, l=loaded
		 * actions: s=shoot
		 * 
		 * action specifications: 
		 *  - alive0 & loaded0 & shoot0 => -alive1
		 *  
		 * initial state: alive0, -alive2
		 * 
		 * action occurrences: shoot0
		 * 
		 * DESIRED OUTCOME:
		 * 
		 * t|0  1  2  
		 * ---------
		 * a|1  0  0
		 * l|1  1  1
		 * s|1  -  -
		 */
		String fluents = "a0 a1 a2 l0 l1 l2 s0";
		String actions = "s0";
		String conds = "a0 and l0 and s0 => -a1," // actions
				
				+ "l0 and s0 => l1, -l0 and s0 => -l1,"
				+ "a0 and -l0 and s0 => a1, a0 and -l0 and s0 => -l0,"
				+ "-a0 and l0 and s0 => -a1, -a0 and l0 and s0 => l1,"
				+ "-a0 and -l0 and s0 => -a1, -a0 and -l0 and s0 => -l1,"
				
				+ "a1 and l1 and s0 => a2, a1 and l1 and s0 => l2,"
				+ "-a1 and l1 and s0 => -a2, -a1 and l1 and s0 => l2,"
				+ "a1 and -l1 and s0 => a2, a1 and -l1 and s0 => -l2,"
				+ "-a1 and -l1 and s0 => -a2, -a1 and -l1 and s0 => -l2";
		
		String initial = "a0 and -a2";
		String occ = "s0";
				
		try {
			theory.addVars(fluents + " " + actions);
			theory.addCond(conds);
			
			//System.out.println(theory.rankingToString());
			
			String lhs = initial + " and " + occ + "=>";
			
			for (String fluent : fluents.split(" ")) {
				boolean t = theory.query(lhs + fluent);
				boolean f = theory.query(lhs + "-" + fluent);
							
				System.out.println(fluent + ": " + (t ? t : (f ? !f : "?")));
			}
			
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * UNDER CONSTRUCTION
	 * Stanford Murder Mystery with Ramification [Baker (1991) - "Nonmonotonic Reasoning in the Framework of Situation Calculus"]
	 * 
	 * - Ramification: "Often, it is impractical to list explicitly all the consequences of an action. 
	 *                  Rather, some of these consequences will be ramifications; that is, they will be implied by domain constraints"
	 * 
	 */
	public void smmram() {
		theory = new Theory();
				
		/**
		 * SCENARIO:
		 * 
		 * fluents: a=alive, l=loaded, w=walking
		 * actions: aS=shoot, aL=load
		 * 
		 * action specifications: 
		 *  - load0 => loaded1
		 *  - alive1 & loaded1 & shoot1 => -alive2
		 *
		 * initial state: walking1, alive1, -loaded0
		 * 
		 * action occurrences: load0, shoot1
		 * 
		 * domain constraints: walking1 -> alive1
		 * 
		 * DESIRED OUTCOME:
		 * 
		 * t |0  1  2 
		 * ----------
		 * a |-  1  0
		 * l |0  1  -
		 * w |-  1  0
		 * aS|-  1  -
		 * aL|1  -  -
		 */
		String fluents = "a1 a2 l0 l1 w1 w2";
		String actions = "aS1 aL0";
		String conds = "aL0 => l1, a2 and l2 and aS2 => -a3, " // actions
				
				// persistence: t=0 => t=1
				// include all statements except the ones containing postcondition of the loading action in conclusion
				+ "a0 and l0 and w0 and aS2 and aL0 => a1, a0 and l0 and w0 and aS2 and aL0 => l1, a0 and l0 and w0 and aS2 and aL0 => w1,"
				+ "-a0 and l0 and w0 and aS2 and aL0 => -a1, -a0 and l0 and w0 and aS2 and aL0 => l1, -a0 and l0 and w0 and aS2 and aL0 => w1,"
				+ "a0 and -l0 and w0 and aS2 and aL0 => a1, a0 and -l0 and w1 and aS2 and aL0 => w1,"
				+ "a0 and l0 and -w0 and aS2 and aL0 => a1, a0 and l0 and -w0 and aS2 and aL0 => l1, a0 and l0 and -w0 and aS2 and aL0 => -w1,"
				+ "-a0 and -l0 and w0 and aS2 and aL0 => -a1, -a0 and -l0 and w0 and aS2 and aL0 => w1,"
				+ "-a0 and l0 and -w0 and aS2 and aL0 => -a1, -a0 and l0 and -w0 and aS2 and aL0 => l1, -a0 and l0 and -w0 and aS2 and aL0 => -w1,"
				+ "a0 and -l0 and -w0 and aS2 and aL0 => a1, a0 and -l0 and -w0 and aS2 and aL0 => -w1,"
				+ "-a0 and -l0 and -w0 and aS2 and aL0 => -a1, -a0 and -l0 and -w0 and aS2 and aL0 => -w1,"
				
				// t=1 => t=2
				// include all statements
				+ "a1 and l1 and w1 and aS2 and aL0 => a2, a1 and l1 and w1 and aS2 and aL0 => l2, a1 and l1 and w1 and aS2 and aL0 => w2,"
				+ "-a1 and l1 and w1 and aS2 and aL0 => -a2, -a1 and l1 and w1 and aS2 and aL0 => l2, -a1 and l1 and w1 and aS2 and aL0 => w2,"
				+ "a1 and -l1 and w1 and aS2 and aL0 => a2, a1 and -l1 and w1 and aS2 and aL0 => -l2, a1 and -l1 and w1 and aS2 and aL0 => w2,"
				+ "a1 and l1 and -w1 and aS2 and aL0 => a2, a1 and l1 and -w1 and aS2 and aL0 => l2, a1 and l1 and -w1 and aS2 and aL0 => -w2,"
				+ "-a1 and -l1 and w1 and aS2 and aL0 => -a2, -a1 and -l1 and w1 and aS2 and aL0 => -l2, -a1 and -l1 and w1 and aS2 and aL0 => w2,"
				+ "-a1 and l1 and -w1 and aS2 and aL0 => -a2, -a1 and l1 and -w1 and aS2 and aL0 => l2, -a1 and l1 and -w1 and aS2 and aL0 => -w2,"
				+ "a1 and -l1 and -w1 and aS2 and aL0 => a2, a1 and -l1 and -w1 and aS2 and aL0 => -l2, a1 and -l1 and -w1 and aS2 and aL0 => -w2,"
				+ "-a1 and -l1 and -w1 and aS2 and aL0 => -a2, -a1 and -l1 and -w1 and aS2 and aL0 => -l2, -a1 and -l1 and -w1 and aS2 and aL0 => -w2,"
				
				// t=2 => t=3
				// include all statements except ones containing postcondition of the shooting action in conclusion (a3)
				+ "a2 and l2 and w2 and aS2 and aL0 => l3, a2 and l2 and w2 and aS2 and aL0 => w3,"
				+ "-a2 and l2 and w2 and aS2 and aL0 => -a3, -a2 and l2 and w2 and aS2 and aL0 => l3, -a2 and l2 and w2 and aS2 and aL0 => w3,"
				+ "a2 and -l2 and w2 and aS2 and aL0 => -l3, a2 and -l2 and w2 and aS2 and aL0 => w3,"
				+ "a2 and l2 and -w2 and aS2 and aL0 => l3, a2 and l2 and -w2 and aS2 and aL0 => -w3,"
				+ "-a2 and -l2 and w2 and aS2 and aL0 => -a3, -a2 and -l2 and w2 and aS2 and aL0 => -l3, -a2 and -l2 and w2 and aS2 and aL0 => w3,"
				+ "-a2 and l2 and -w2 and aS2 and aL0 => -a3, -a2 and l2 and -w2 and aS2 and aL0 => l3, -a2 and l2 and -w2 and aS2 and aL0 => -w3,"
				+ "a2 and -l2 and -w2 and aS2 and aL0 => -l3, a2 and -l2 and -w2 and aS2 and aL0 => -w3,"
				+ "-a2 and -l2 and -w2 and aS2 and aL0 => -a3, -a2 and -l2 and -w2 and aS2 and aL0 => -l3, -a2 and -l2 and -w2 and aS2 and aL0 => -w3";
		
		String initial = "w0 and a0 and -l0";
		String occ = "aL0 and aS2";
				
		try {
			theory.addVars(fluents + " " + actions);
			theory.addCond(conds);
			
			System.out.println(theory.rankingToString());
			/*
			String lhs = initial + " and " + occ + "=>";
			
			for (String fluent : fluents.split(" ")) {
				boolean t = theory.query(lhs + fluent);
				boolean f = theory.query(lhs + "-" + fluent);
							
				System.out.println(fluent + ": " + (t ? t : (f ? !f : "?")));
			}
			*/
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

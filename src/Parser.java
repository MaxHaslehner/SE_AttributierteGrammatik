

public class Parser {
	public static final int _EOF = 0;
	public static final int _number = 1;
	public static final int _date = 2;
	public static final int _string = 3;
	public static final int maxT = 16;

	static final boolean _T = true;
	static final boolean _x = false;
	static final int minErrDist = 2;

	public Token t;    // last recognized token
	public Token la;   // lookahead token
	int errDist = minErrDist;
	
	public Scanner scanner;
	public Errors errors;

	

	public Parser(Scanner scanner) {
		this.scanner = scanner;
		errors = new Errors();
	}

	void SynErr (int n) {
		if (errDist >= minErrDist) errors.SynErr(la.line, la.col, n);
		errDist = 0;
	}

	public void SemErr (String msg) {
		if (errDist >= minErrDist) errors.SemErr(t.line, t.col, msg);
		errDist = 0;
	}
	
	void Get () {
		for (;;) {
			t = la;
			la = scanner.Scan();
			if (la.kind <= maxT) {
				++errDist;
				break;
			}

			la = t;
		}
	}
	
	void Expect (int n) {
		if (la.kind==n) Get(); else { SynErr(n); }
	}
	
	boolean StartOf (int s) {
		return set[s][la.kind];
	}
	
	void ExpectWeak (int n, int follow) {
		if (la.kind == n) Get();
		else {
			SynErr(n);
			while (!StartOf(follow)) Get();
		}
	}
	
	boolean WeakSeparator (int n, int syFol, int repFol) {
		int kind = la.kind;
		if (kind == n) { Get(); return true; }
		else if (StartOf(repFol)) return false;
		else {
			SynErr(n);
			while (!(set[syFol][kind] || set[repFol][kind] || set[0][kind])) {
				Get();
				kind = la.kind;
			}
			return StartOf(syFol);
		}
	}
	
	void Tasks() {
		Integer totalHours = 0; 
		String[] persons = new String[100]; // Array to track persons
		String[][] tasks = new String[100][100]; // Tasks for each person
		int personCount = 0;
		
		Expect(4);
		while (la.kind == 7) {
			totalHours = TaskEntry(persons, tasks, personCount);
			if (la.kind == 5) {
				Get();
			}
		}
		Expect(6);
		System.out.println("Gesamte Bearbeitungsdauer: " + totalHours + " Stunden.");
		System.out.println("Aufgaben nach Personen organisiert:");
		for (int i = 0; i < personCount; i++) {
		   System.out.print(persons[i] + ": ");
		   for (int j = 0; tasks[i][j] != null; j++) {
		       if (j > 0) System.out.print(", ");
		       System.out.print(tasks[i][j]);
		   }
		   System.out.println();
		}
		
	}

	Integer  TaskEntry(String[] persons, String[][] tasks, int personCount) {
		Integer  totalHours;
		Expect(7);
		Expect(8);
		Expect(3);
		String description = t.val; 
		Expect(9);
		Expect(10);
		Expect(2);
		String date = t.val; 
		String[] parts = date.split("\\.");
		int day = Integer.parseInt(parts[0]);
		int month = Integer.parseInt(parts[1]);
		if (month < 1 || month > 12 || day < 1 || day > 31) {
		   System.out.println("UngÃ¼ltiges Datum: " + date);
		}
		
		Expect(9);
		Expect(11);
		Expect(1);
		if (totalHours == null) totalHours = 0;
		totalHours += Integer.parseInt(t.val); 
		
		Expect(9);
		Expect(12);
		Expect(3);
		String lead = t.val; 
		boolean found = false;
		for (int i = 0; i < personCount; i++) {
		   if (persons[i].equals(lead)) {
		       for (int j = 0; j < 100; j++) {
		           if (tasks[i][j] == null) {
		               tasks[i][j] = description;
		               found = true;
		               break;
		           }
		       }
		   }
		}
		if (!found) {
		   persons[personCount] = lead;
		   tasks[personCount][0] = description;
		   personCount++;
		}
		
		Expect(9);
		Expect(13);
		Expect(3);
		String contributors = t.val; 
		String[] contributorList = contributors.split(", ");
		for (String contributor : contributorList) {
		   boolean foundContr = false;
		   for (int i = 0; i < personCount; i++) {
		       if (persons[i].equals(contributor)) {
		           for (int j = 0; j < 100; j++) {
		               if (tasks[i][j] == null) {
		                   tasks[i][j] = description;
		                   foundContr = true;
		                   break;
		               }
		           }
		       }
		   }
		   if (!foundContr) {
		       persons[personCount] = contributor;
		       tasks[personCount][0] = description;
		       personCount++;
		   }
		}
		
		Expect(9);
		Expect(14);
		if (la.kind == 3) {
			Get();
		}
		Expect(9);
		Expect(15);
		return totalHours;
	}



	public void Parse() {
		la = new Token();
		la.val = "";		
		Get();
		Tasks();
		Expect(0);

	}

	private static final boolean[][] set = {
		{_T,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x,_x,_x, _x,_x}

	};
} // end Parser


class Errors {
	public int count = 0;                                    // number of errors detected
	public java.io.PrintStream errorStream = System.out;     // error messages go to this stream
	public String errMsgFormat = "-- line {0} col {1}: {2}"; // 0=line, 1=column, 2=text
	
	protected void printMsg(int line, int column, String msg) {
		StringBuffer b = new StringBuffer(errMsgFormat);
		int pos = b.indexOf("{0}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, line); }
		pos = b.indexOf("{1}");
		if (pos >= 0) { b.delete(pos, pos+3); b.insert(pos, column); }
		pos = b.indexOf("{2}");
		if (pos >= 0) b.replace(pos, pos+3, msg);
		errorStream.println(b.toString());
	}
	
	public void SynErr (int line, int col, int n) {
		String s;
		switch (n) {
			case 0: s = "EOF expected"; break;
			case 1: s = "number expected"; break;
			case 2: s = "date expected"; break;
			case 3: s = "string expected"; break;
			case 4: s = "\"{\" expected"; break;
			case 5: s = "\",\" expected"; break;
			case 6: s = "\"}\" expected"; break;
			case 7: s = "\"(\" expected"; break;
			case 8: s = "\"Description:\" expected"; break;
			case 9: s = "\";\" expected"; break;
			case 10: s = "\"Date:\" expected"; break;
			case 11: s = "\"Hours-estimated:\" expected"; break;
			case 12: s = "\"Lead:\" expected"; break;
			case 13: s = "\"Contributors:\" expected"; break;
			case 14: s = "\"Subtasks:\" expected"; break;
			case 15: s = "\")\" expected"; break;
			case 16: s = "??? expected"; break;
			default: s = "error " + n; break;
		}
		printMsg(line, col, s);
		count++;
	}

	public void SemErr (int line, int col, String s) {	
		printMsg(line, col, s);
		count++;
	}
	
	public void SemErr (String s) {
		errorStream.println(s);
		count++;
	}
	
	public void Warning (int line, int col, String s) {	
		printMsg(line, col, s);
	}
	
	public void Warning (String s) {
		errorStream.println(s);
	}
} // Errors


class FatalError extends RuntimeException {
	public static final long serialVersionUID = 1L;
	public FatalError(String s) { super(s); }
}

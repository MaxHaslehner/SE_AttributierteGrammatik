import java.io.*;
import java.util.*;

public class Tasks {

	public static void main (String[] args) throws FileNotFoundException {
		String file = args[0];
		
		try {
			System.setOut(new PrintStream(new FileOutputStream("MyTasksStatistics.txt"),true,"UTF-8"));
		}
		catch(UnsupportedEncodingException e) {
			System.setOut(new PrintStream(new FileOutputStream("MyTasksStatistics.txt")));
		}
		
		Scanner scanner = new Scanner(file);
		Parser parser = new Parser(scanner);
		parser.Parse();
		System.out.println(parser.errors.count + " errors detected");

	}
}

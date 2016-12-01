import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import soot.Body;
import soot.Local;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.UnitBox;
import soot.Value;
import soot.ValueBox;
import soot.options.Options;
import soot.toolkits.graph.LoopNestTree;
import soot.util.Chain;

/**
 * @author Gursimran Singh
 * @rollno 2014041
 */

public class ClassAnalyzer {
	public static void main(String[] args) {
		if (args.length < 1) {
			System.err.println ("No arguments provided !!");
			System.err.println ("Usage: ClassAnalyzer java-class-to-analyze");
			return;
		}
		
		/*
		 * Create a SootClass from the java-class-to-analyze
		 */
		SootClass sootClass = Scene.v().loadClassAndSupport (args[0]);
		Scene.v().loadBasicClasses();
		Scene.v().loadNecessaryClasses();
		Scene.v().loadDynamicClasses();
		
		/* 
		 * Set Command line Options
		 */
		Options.v().setPhaseOption ("jb", "use-original-names:true");
		Options.v().set_output_format(Options.output_format_jimple);
		
		/*
		 * Getting Statistics about the class file
		 */
		// Local Fields
		Chain<SootField> sootFields = sootClass.getFields();
		printFields (sootFields);
		
		System.out.println();
		
		// Methods
		List<SootMethod> sootMethods = sootClass.getMethods();
		printMethodStats (sootMethods);
		
		/*
		 * Generate Jimple Output
		 */
		soot.Main.main(args);
	}
	
	static void printFields (Chain<SootField> sootFields) {
		System.out.println ("LOCAL FIELDS IN THE CLASS ARE:");
		
		Iterator<SootField> it = sootFields.iterator();
		while (it.hasNext()) {
			SootField field = it.next();
			System.out.print (field.getNumber() + ": ");
			System.out.print ("Field Name: " + field.getName() + ", ");
			System.out.print ("Field Type: " + field.getType());
			System.out.println();
		}
		System.out.println();
	}
	
	static void printMethodStats (List<SootMethod> sootMethods) {
		System.out.println ("INFORMATION ABOUT METHODS IN CLASS:");
		
		Iterator<SootMethod> it = sootMethods.iterator();
		while (it.hasNext()) {
			SootMethod method = it.next();
			System.out.println ("Method Number: " + method.getNumber());
			System.out.println ("============================================================");
			printBasicMethodInfo (method);
			printMethodInvocations (method);
			printMethodExceptions (method);
			printHasLoop (method);
			printHasMultiplicationOperator (method);
			System.out.println ("============================================================");
			System.out.println();
		}
		System.out.println();
	}
	
	static void printBasicMethodInfo (SootMethod method) {
		// Get Active Body
		Body activeBody = method.retrieveActiveBody();
		
		// Get all Parameters with Types
		ArrayList<String> paraList = new ArrayList<String>();
		List<?> paraTypeList = method.getParameterTypes();
		
		int paraCount = method.getParameterCount();
		for (int i=0; i < paraCount; i++) {
			paraList.add (activeBody.getParameterLocal(i).toString());
		}
		
		// Local Variables with Types
		Chain<Local> localChain = activeBody.getLocals();
		int localCount = localChain.size();
		Iterator<Local> it = localChain.iterator();
		
		
		// Print Information
		System.out.println ("Method Name: " + method.getName());
		System.out.println ("Method Type: " + method.getReturnType());
		
		System.out.print ("Parameters <N,T>: ");
		for (int i=0; i < paraCount; i++) {
			System.out.print ("<" + paraList.get(i) + ", " + paraTypeList.get(i).toString() + ">");
			if (i != paraCount -1) {
				System.out.print (", ");
			}
		}
		System.out.println ();
		
		System.out.print ("Local Variables <N,T>: ");
		int localCounter = 0;
		while (it.hasNext()) {
			Local localVar = it.next();
			
			if (localVar.getName().charAt(0) == '$') {
				localCounter++;
				continue;
			}
			
			System.out.print ("<" + localVar.getName() + ", " + localVar.getType() + ">");
			if (localCounter < localCount -1) {
				System.out.print (", ");
			}
			localCounter++;
		}
		System.out.println();
	}
	
	static void printMethodInvocations (SootMethod method) {
		// Get Active Body
		Body activeBody = method.retrieveActiveBody();
		
		// Invocations
		List<UnitBox> unitboxList = activeBody.getAllUnitBoxes();
		Iterator<UnitBox> it = unitboxList.iterator();
		int num = 0;
		
		System.out.println ("Method Invocations (Type, Function_prototype):");
		while (it.hasNext()) {
			UnitBox unitbox = it.next();
			Unit unit = unitbox.getUnit();
			String[] splitunit = unit.toString().split(" ");
			
			String func_proto = "";
			for (int i=1; i<splitunit.length; i++) {
				func_proto += splitunit[i];
				if (i != splitunit.length -1)
					func_proto += " ";
			}
			
			if (splitunit[0].compareTo ("specialinvoke") == 0) {
				System.out.println ("\t" + ++num + ". SPECIAL, " + func_proto);
			} else if (splitunit[0].compareTo ("virtualinvoke") == 0) {
				System.out.println ("\t" + ++num + ". VIRTUAL, " + func_proto);
			} else if (splitunit[0].compareTo ("staticinvoke") == 0) {
				System.out.println ("\t" + ++num + ". STATIC, " + func_proto);
			}
		}
	}
	
	static void printMethodExceptions (SootMethod method) {
		boolean excThrown = false;
		List<SootClass> excps = method.getExceptions();
		Iterator<SootClass> it = excps.iterator();
		
		if (excps.size() > 0) {
			excThrown = true;
		}
		
		System.out.println ("Exceptions Thrown: " + (excThrown?"YES":"NO"));
		while (it.hasNext()) {
			SootClass sc = it.next();
			System.out.println ("\t" + sc.toString());
		}
	}

	static void printHasLoop (SootMethod method) {
		// Get Active Body
		Body activeBody = method.retrieveActiveBody();
		
		boolean hasLoop = false;
		
		// Find Loops
		LoopNestTree loopNestTree = new LoopNestTree (activeBody);
		if (loopNestTree.size() > 0) {
			hasLoop = true;
		}
		System.out.println ("Has Loops: " + (hasLoop?"TRUE":"FALSE"));
	}

	static void printHasMultiplicationOperator (SootMethod method) {
		// Get Active Body
		Body activeBody = method.retrieveActiveBody();
		
		List<ValueBox> boxes = activeBody.getUseAndDefBoxes();
		Iterator<ValueBox> it = boxes.iterator();
		boolean containsMul = false;
		
		while (it.hasNext()) {
			ValueBox vbox = it.next();
			Value val = vbox.getValue();
			String valType = val.getType().toString();
			switch (valType) {
			case "byte":
			case "short":
			case "int":
			case "long":
			case "float":
			case "double":
				if (val.toString().contains("*")) {
					containsMul = true;
				}
			}
		}
		System.out.println ("Has Multiplication: " + (containsMul?"TRUE":"FALSE"));
	}
}

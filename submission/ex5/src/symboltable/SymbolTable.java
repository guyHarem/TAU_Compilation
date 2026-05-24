/***********/
/* PACKAGE */
/***********/
package symboltable;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.File;
import java.io.PrintWriter;
import temp.*;
import types.*;

/****************/
/* SYMBOL TABLE */
/****************/
public class SymbolTable
{
	public Temp currThis = null;
	private int hashArraySize = 13;
	
	/**********************************************/
	/* The actual symbol table data structure ... */
	/**********************************************/
	private SymbolTableEntry[] table = new SymbolTableEntry[hashArraySize];
	public int currentScopeLevel = 0;
	private SymbolTableEntry top;
	private int topIndex = 0;
	
	/**************************************************************/
	/* A very primitive hash function for exposition purposes ... */
	/**************************************************************/
	private int hash(String s)
	{
		if (s.charAt(0) == 'l') {return 1;}
		if (s.charAt(0) == 'm') {return 1;}
		if (s.charAt(0) == 'r') {return 3;}
		if (s.charAt(0) == 'i') {return 6;}
		if (s.charAt(0) == 'd') {return 6;}
		if (s.charAt(0) == 'k') {return 6;}
		if (s.charAt(0) == 'f') {return 6;}
		if (s.charAt(0) == 'S') {return 6;}
		return 12;
	}

	/****************************************************************************/
	/* Enter a variable, function, class type or array type to the symbol table */
	/****************************************************************************/
	public SymbolTableEntry enter(String name, Type t) {
        int hashValue = hash(name);
        SymbolTableEntry next = table[hashValue];
        
        // Use the current top and topIndex for stack-based scope management
        SymbolTableEntry e = new SymbolTableEntry(name, t, hashValue, next, top, topIndex++, currentScopeLevel);
        top = e;
        table[hashValue] = e;
        return e;
    }

	/***********************************************/
	/* Find the inner-most scope element with name */
	/***********************************************/
	public Type find(String name)
	{
		SymbolTableEntry e;

		for (e = table[hash(name)]; e != null; e = e.next)
		{
			if (name.equals(e.name))
			{
				return e.type;
			}
		}

		return null;
	}

	/***********************************************/
	/* Find name ONLY in the current scope         */
	/***********************************************/
	public Type findInCurrentScope(String name)
	{
		// Traverse from top down to the nearest SCOPE-BOUNDARY
		for (SymbolTableEntry e = top; e != null; e = e.prevtop)
		{
			if (e.name.equals("SCOPE-BOUNDARY"))
			{
				// Hit scope boundary - not found in current scope
				return null;
			}
			if (name.equals(e.name))
			{
				return e.type;
			}
		}
		return null;
	}

	/***********************************************/
	/* Find name in all nested scopes (not global) */
	/* Global scope = entries before any SCOPE-BOUNDARY */
	/***********************************************/
	public SymbolTableEntry findEntryExcludingGlobal(String name) {
        int totalBoundaries = 0;
        for (SymbolTableEntry e = top; e != null; e = e.prevtop) {
            if (e.name.equals("SCOPE-BOUNDARY")) totalBoundaries++;
        }
        if (totalBoundaries == 0) return null;

        int boundariesCrossed = 0;
        for (SymbolTableEntry e = top; e != null; e = e.prevtop) {
            if (e.name.equals("SCOPE-BOUNDARY")) {
                boundariesCrossed++;
                if (boundariesCrossed == totalBoundaries) return null;
                continue;
            }
            if (name.equals(e.name)) return e;
        }
        return null;
    }
	
	/***************************************************************************/
	/* begine scope = Enter the <SCOPE-BOUNDARY> element to the data structure */
	/***************************************************************************/
	public void beginScope() {
        currentScopeLevel++;
        enter("SCOPE-BOUNDARY", new TypeForScopeBoundaries("NONE"));
    }

	/********************************************************************************/
	/* end scope = Keep popping elements out of the data structure,                 */
	/* from most recent element entered, until a <NEW-SCOPE> element is encountered */
	/********************************************************************************/
	public void endScope()
	{
		/**************************************************************************/
		/* Pop elements from the symbol table stack until a SCOPE-BOUNDARY is hit */
		/**************************************************************************/
		while (top != null && !"SCOPE-BOUNDARY".equals(top.name))
		{
			table[top.index] = top.next;
			topIndex = topIndex -1;
			top = top.prevtop;
		}
		/**************************************/
		/* Pop the SCOPE-BOUNDARY sign itself */
		/**************************************/
		if (top != null) {
			table[top.index] = top.next;
			topIndex = topIndex -1;
			top = top.prevtop;
		}
		currentScopeLevel--;

		/*********************************************/
		/* Print the symbol table after every change */
		/*********************************************/
		printMe();
	}
	
	public static int n=0;
	
	public void printMe()
	{
		int i=0;
		int j=0;
		String dirname="./output/";
		String filename=String.format("SYMBOL_TABLE_%d_IN_GRAPHVIZ_DOT_FORMAT.txt",n++);

		try
		{
			/*******************************************/
			/* [1] Open Graphviz text file for writing */
			/*******************************************/
			new File(dirname).mkdirs();
			PrintWriter fileWriter = new PrintWriter(dirname+filename);

			/*********************************/
			/* [2] Write Graphviz dot prolog */
			/*********************************/
			fileWriter.print("digraph structs {\n");
			fileWriter.print("rankdir = LR\n");
			fileWriter.print("node [shape=record];\n");

			/*******************************/
			/* [3] Write Hash Table Itself */
			/*******************************/
			fileWriter.print("hashTable [label=\"");
			for (i=0;i<hashArraySize-1;i++) { fileWriter.format("<f%d>\n%d\n|",i,i); }
			fileWriter.format("<f%d>\n%d\n\"];\n",hashArraySize-1,hashArraySize-1);
		
			/****************************************************************************/
			/* [4] Loop over hash table array and print all linked lists per array cell */
			/****************************************************************************/
			for (i=0;i<hashArraySize;i++)
			{
				if (table[i] != null)
				{
					/*****************************************************/
					/* [4a] Print hash table array[i] -> entry(i,0) edge */
					/*****************************************************/
					fileWriter.format("hashTable:f%d -> node_%d_0:f0;\n",i,i);
				}
				j=0;
				for (SymbolTableEntry it = table[i]; it!=null; it=it.next)
				{
					/*******************************/
					/* [4b] Print entry(i,it) node */
					/*******************************/
					fileWriter.format("node_%d_%d ",i,j);
					fileWriter.format("[label=\"<f0>%s|<f1>%s|<f2>prevtop=%d|<f3>next\"];\n",
						it.name,
						it.type.name,
						it.prevtopIndex);

					if (it.next != null)
					{
						/***************************************************/
						/* [4c] Print entry(i,it) -> entry(i,it.next) edge */
						/***************************************************/
						fileWriter.format(
							"node_%d_%d -> node_%d_%d [style=invis,weight=10];\n",
							i,j,i,j+1);
						fileWriter.format(
							"node_%d_%d:f3 -> node_%d_%d:f0;\n",
							i,j,i,j+1);
					}
					j++;
				}
			}
			fileWriter.print("}\n");
			fileWriter.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}		
	}
	
	private static SymbolTable instance = null;

	/****************************************/
	/* Current function context for return  */
	/****************************************/
	private TypeFunction currentFunction = null;

	/****************************************/
	/* Current class context for members    */
	/****************************************/
	private TypeClass currentClass = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected SymbolTable() {}

	/****************************************/
	/* Function context for return checking */
	/****************************************/
	public void setCurrentFunction(TypeFunction f) { currentFunction = f; }
	public TypeFunction getCurrentFunction() { return currentFunction; }

	/****************************************/
	/* Class context for member registration */
	/****************************************/
	public void setCurrentClass(TypeClass c) { currentClass = c; }
	public TypeClass getCurrentClass() { return currentClass; }

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static SymbolTable getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new SymbolTable();

			/*****************************************/
			/* [1] Enter primitive types int, string */
			/*****************************************/
			instance.enter("int",   TypeInt.getInstance());
			instance.enter("string", TypeString.getInstance());

			/*************************************/
			/* [2] Enter void type               */
			/*************************************/
			instance.enter("void", TypeVoid.getInstance());

			/***************************************/
			/* [3] Enter library function PrintInt */
			/***************************************/
			instance.enter(
				"PrintInt",
				new TypeFunction(
					TypeVoid.getInstance(),
					"PrintInt",
					new TypeList(
						TypeInt.getInstance(),
						null)));

			/******************************************/
			/* [4] Enter library function PrintString */
			/******************************************/
			instance.enter(
				"PrintString",
				new TypeFunction(
					TypeVoid.getInstance(),
					"PrintString",
					new TypeList(
						TypeString.getInstance(),
						null)));

		}
		return instance;
	}

	public SymbolTableEntry findEntry(String name) {
        for (SymbolTableEntry e = table[hash(name)]; e != null; e = e.next) {
            if (name.equals(e.name)) return e;
        }
        return null;
    }
}

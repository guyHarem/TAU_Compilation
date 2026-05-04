/***********/
/* PACKAGE */
/***********/
package mips;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;
import temp.*;

public class MipsGenerator
{
	private static final int WORD_SIZE=4;
	/***********************/
	/* The file writer ... */
	/***********************/
	private PrintWriter fileWriter;
	
	public static String LABEL_STRLEN			= "label_strlen";
	public static String LABEL_STRCOPY			= "label_strcpy";
	public static String LABEL_STR_CONCAT		= "label_str_concat";

	public static String LABEL_ACCESS_VIOLATION	= "label_access_violation";
	public static String LABEL_DIV0				= "label_illegal_div_by_0";
	public static String LABEL_INV_PTR			= "label_invalid_ptr_deref";
	
	public static String STRING_ACCESS_VIOLATION	= "string_access_violation";
	public static String STRING_DIV0				= "string_illegal_div_by_0";
	public static String STRING_INV_PTR				= "string_invalid_ptr_dref";

	private String tempToString(Temp t) {
		return String.format("Temp_%s", t.getSerialNumber());
	}

	/***********************/
	/* The file writer ... */
	/***********************/
	public void finalizeFile()
	{
		// We do this in exit:
		// fileWriter.print("\tli $v0,10\n");
		// fileWriter.print("\tsyscall\n");
		fileWriter.close();
	}

	public void strLen(Temp dst, Temp str) {
		int d = dst.getSerialNumber();
		int s = str.getSerialNumber();
		String labelStart = "strlen_start_" + dst.getSerialNumber();
		String labelEnd = "strlen_end_" + dst.getSerialNumber();

		// dst = 0; t1 = src;
		fileWriter.format("\tli Temp_%d, 0\n", d);
		fileWriter.format("\tmove $t1, Temp_%d\n", s);

		// while (*t1 != 0)
		fileWriter.format("%s:\n", labelStart);
		fileWriter.format("\tlb $t0, 0($t1)\n");
		fileWriter.format("\tbeq $t0, $zero, %s\n", labelEnd);

		// dst++; t1++;
		fileWriter.format("\taddi Temp_%d, Temp_%d, 1\n", d, d);
		fileWriter.format("\taddi $t1, $t1, 1\n");
		fileWriter.format("\tj %s\n", labelStart);
		fileWriter.format("%s:\n", labelEnd);
	}

	// This also NULL terminates the string.
	public void strCopy(Temp dst, Temp src) {
		int d = dst.getSerialNumber();
		int s = src.getSerialNumber();
		String labelStart = "strcopy_start_" + d;
		String labelEnd = "strcopy_end_" + d;

		// t0 = s; t1 = d;
		fileWriter.format("\tmove $t0, Temp_%d\n", s);
		fileWriter.format("\tmove $t1, Temp_%d\n", d);

		// while (t0 != 0)
		fileWriter.format("%s:\n", labelStart);
		fileWriter.format("\tlb $t2, 0($t0)\n");
		fileWriter.format("\tbeq $t2, $zero, %s\n", labelEnd);
		
		// *(t1++) = *(t0++)
		fileWriter.format("\tsb $t2, 0($t1)\n");
		fileWriter.format("\taddi $t0, $t0, 1\n");
		fileWriter.format("\taddi $t1, $t1, 1\n");
		fileWriter.format("\tj %s\n", labelStart);
		fileWriter.format("%s:\n", labelEnd);
		
		// Null Terminate (*t1 = 0)
		fileWriter.format("\tsb $zero, 0($t1)\n");
	}
	
	public void printInt(Temp t)
	{
		int idx=t.getSerialNumber();
		// fileWriter.format("\taddi $a0,Temp_%d,0\n",idx);
		fileWriter.format("\tmove $a0,Temp_%d\n",idx);
		fileWriter.format("\tli $v0,1\n");
		fileWriter.format("\tsyscall\n");
		fileWriter.format("\tli $a0,32\n");
		fileWriter.format("\tli $v0,11\n");
		fileWriter.format("\tsyscall\n");
	}
	
	public void printString(Temp t)
	{
		fileWriter.format("\tmove $a0, Temp_%d\n", t.getSerialNumber());
		fileWriter.println("\tli $v0, 4");
		fileWriter.println("\tsyscall");
	}

//	public Temp addressLocalVar(int serialLocalVarNum)
//	{
//		Temp t  = TempFactory.getInstance().getFreshTemp();
//		int idx = t.getSerialNumber();
//
//		fileWriter.format("\taddi Temp_%d,$fp,%d\n",idx,-serialLocalVarNum*WORD_SIZE);
//
//		return t;
//	}

	public void allocate(String varName) {
		// Call this ONLY during a "Data Phase" at the start of your program
		fileWriter.format("global_%s: .word 0\n", varName); 
	}

	public void loadField(Temp dst, Temp objectBase, int offset) {
		// MIPS: lw $dst, offset($base)
        fileWriter.format("\tlw Temp_%d, %d(Temp_%d)\n", dst.getSerialNumber(), offset, objectBase.getSerialNumber());
    }

    public void storeField(Temp src, Temp objectBase, int offset) {
		// MIPS: sw $src, offset($base)
        fileWriter.format("\tsw Temp_%d, %d(Temp_%d)\n", src.getSerialNumber(), offset, objectBase.getSerialNumber());
    }

	public void loadGlobal(Temp dst, String varName)
	{
		int idxdst=dst.getSerialNumber();
		fileWriter.format("\tlw Temp_%d,global_%s\n",idxdst,varName);
	}

	public void storeGlobal(String varName, Temp src)
	{
		int idxsrc=src.getSerialNumber();
		fileWriter.format("\tsw Temp_%d, global_%s\n",idxsrc,varName);
	}

	public void storeLocal(Temp var, Temp val) {
		fileWriter.format("\tsw Temp_%d, Temp_%d\n", val.getSerialNumber(), var.getSerialNumber());
	}

	public void moveLocal(Temp dst, Temp src) {
		fileWriter.format("\tmove Temp_%d, Temp_%d\n", dst.getSerialNumber(), src.getSerialNumber());
	}

	/**
	 * Checks if a pointer is nil (0).
	 * If it is, jumps to the global handler.
	 */
	public void nilCheck(Temp ptr) {
		// If pointer == 0, jump to the handler
		fileWriter.format("\tbeq Temp_%d, $zero, %s\n", ptr.getSerialNumber(), MipsGenerator.LABEL_INV_PTR);
	}

	/**
	 * Checks if index is within [0, length).
	 */
	public void arrayBoundsCheck(Temp base, Temp index) {
		int b = base.getSerialNumber();
		int i = index.getSerialNumber();
		fileWriter.format("\tbltz Temp_%d, label_access_violation\n", i);		// i < 0?
		fileWriter.format("\tlw $t0, 0(Temp_%d)\n", b);							// i >= len? (*)
		fileWriter.format("\tbge Temp_%d, $t0, label_access_violation\n", i);	// If (*), jump to error.
	}

	// /**
	//  * Global error handlers printed once at the end of the file.
	//  */
	// public void addGlobalErrorHandlers() {
	// 	// Handler for Nil
	// 	fileWriter.format("%s:\n", MipsGenerator.LABEL_INV_PTR);
	// 	fileWriter.print("\tla $a0, string_invalid_ptr_dref\n");
	// 	fileWriter.print("\tli $v0, 4\n\tsyscall\n");
	// 	fileWriter.print("\tli $v0, 10\n\tsyscall\n"); // Exit

	// 	// Handler for Bounds
	// 	fileWriter.print("label_access_violation:\n");
	// 	fileWriter.print("\tla $a0, string_access_violation\n");
	// 	fileWriter.print("\tli $v0, 4\n\tsyscall\n");
	// 	fileWriter.print("\tli $v0, 10\n\tsyscall\n"); // Exit
	// }

	public void loadArray(Temp dst, Temp base, Temp index) {
		int d = dst.getSerialNumber();
		int b = base.getSerialNumber();
		int i = index.getSerialNumber();

		// Calculate offset: (index + 1) 
		// We add 1 because the length is at offset 0
		fileWriter.format("\tmove $t0, Temp_%d\n", i);
		fileWriter.format("\taddi $t0, $t0, 1\n");
		// fileWriter.format("\tsll $t0, $t0, 2\n"); // multiply by 4
		fileWriter.format("\tadd $t1, Temp_%d, $t0\n", b);
		fileWriter.format("\tlw Temp_%d, 0($t1)\n", d);
	}

	public void storeArray(Temp base, Temp index, Temp src) {
		int b = base.getSerialNumber();
		int i = index.getSerialNumber();
		int s = src.getSerialNumber();

		// Calculate offset: (index + 1) 
		// We add 1 because the length is at offset 0
		fileWriter.format("\tmove $t0, Temp_%d\n", i);
		fileWriter.format("\taddi $t0, $t0, 1\n");
		// fileWriter.format("\tsll $t0, $t0, 2\n"); // multiply by 4
		fileWriter.format("\tadd $t1, Temp_%d, $t0\n", b);
		fileWriter.format("\tsw Temp_%d, 0($t1)\n", s);
	}

	public void addStringLabel(String label, String value) {
		fileWriter.format("%s: .asciiz %s\n", label, value); // Value should already contain "" or '' around the string.
	}

	public void loadAddress(Temp dst, String label) {
		fileWriter.format("\tla Temp_%d, %s\n", dst.getSerialNumber(), label);
	}

	public void li(Temp t, int value)
	{
		int idx=t.getSerialNumber();
		fileWriter.format("\tli Temp_%d,%d\n",idx,value);
	}

	public void add(Temp dst, Temp oprnd1, Temp oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tadd Temp_%d,Temp_%d,Temp_%d\n",dstidx,i1,i2);
	}

	public void sub(Temp dst, Temp oprnd1, Temp oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tsub Temp_%d,Temp_%d,Temp_%d\n",dstidx,i1,i2);
	}

	public void mul(Temp dst, Temp oprnd1, Temp oprnd2)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		int dstidx=dst.getSerialNumber();

		fileWriter.format("\tmul Temp_%d,Temp_%d,Temp_%d\n",dstidx,i1,i2);
	}

	public void div(Temp dst, Temp oprnd1, Temp oprnd2)
	{
		// TODO: Implement (not simple).
		throw new RuntimeException("Not implemented");
	}

	public void label(String inlabel)
	{
		if (inlabel.equals("_start")) {
			fileWriter.format(".globl " + inlabel + "\n");
		}
		fileWriter.format("\n%s:\n",inlabel);
	}
	
	public void segmentLabel(String segmentName)
	{
		fileWriter.format("\n.%s\n", segmentName);
	}

	/**
	 * Mallocs memory for an array: (size + 1) * 4 bytes.
	 * Resulting address is returned in 'dst'.
	 */
	public void malloc(Temp dst, Temp size) {
		int d = dst.getSerialNumber();
		int s = size.getSerialNumber();

		// Calculate total bytes: (size + 1) << 2
		fileWriter.format("\tmove $a0, Temp_%d\n", s);
		fileWriter.format("\taddi $a0, $a0, 1\n");
		fileWriter.format("\tsll $a0, $a0, 2\n");

		// Allocate Heap Memory (sbrk)
		fileWriter.format("\tli $v0, 9\n");
		fileWriter.format("\tsyscall\n");

		// Move result from $v0 to our destination temp
		fileWriter.format("\tmove Temp_%d, $v0\n", d);
	}

	/**
	 * Stores the value at base.
	 */
	public void storeAt(Temp base, Temp value) {
		fileWriter.format("\tsw Temp_%d, 0(Temp_%d)\n", value.getSerialNumber(), base.getSerialNumber());
	}

	public void jump(String inlabel)
	{
		fileWriter.format("\tj %s\n",inlabel);
	}	

	public void blt(Temp oprnd1, Temp oprnd2, String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tblt Temp_%d,Temp_%d,%s\n",i1,i2,label);				
	}

	public void bge(Temp oprnd1, Temp oprnd2, String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbge Temp_%d,Temp_%d,%s\n",i1,i2,label);				
	}

	public void bne(Temp oprnd1, Temp oprnd2, String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbne Temp_%d,Temp_%d,%s\n",i1,i2,label);				
	}
	
	public void beq(Temp oprnd1, Temp oprnd2, String label)
	{
		int i1 =oprnd1.getSerialNumber();
		int i2 =oprnd2.getSerialNumber();
		
		fileWriter.format("\tbeq Temp_%d,Temp_%d,%s\n",i1,i2,label);				
	}

	public void beqz(Temp oprnd1, String label)
	{
		int i1 =oprnd1.getSerialNumber();
				
		fileWriter.format("\tbeq Temp_%d,$zero,%s\n",i1,label);				
	}

	public void allocateArray(Temp dst, Temp size) {
		int d = dst.getSerialNumber();
		int s = size.getSerialNumber();

		// Runtime Check: size must be > 0
		fileWriter.format("\tmove $a0, Temp_%d\n", s);
		fileWriter.format("\tblez $a0, %s\n", MipsGenerator.LABEL_ACCESS_VIOLATION);

		// Allocate (size + 1) * 4 bytes [the +1 is added for storing the array's length].
		fileWriter.format("\taddi $a0, $a0, 1\n");
		fileWriter.format("\tsll $a0, $a0, 2\n");
		fileWriter.format("\tli $v0, 9\n");
		fileWriter.format("\tsyscall\n");

		// Store the length at the start of the block and return the result.
		fileWriter.format("\tsw Temp_%d, 0($v0)\n", s);
		fileWriter.format("\tmove Temp_%d, $v0\n", d);
	}

	public void moveToReg(String targetReg, Temp src) {
		// Moves value from a temporary to a physical register (e.g., $a0)
		fileWriter.format("\tmove %s, Temp_%d\n", targetReg, src.getSerialNumber());
	}

	public void moveFromReg(String srcReg, Temp dst) {
		fileWriter.format("\tmove Temp_%d, %s\n", dst.getSerialNumber(), srcReg);
	}

	public void pushw(String s) {
        fileWriter.format("\tsubu $sp, $sp, %d\n", WORD_SIZE);
        fileWriter.format("\tsw %s, 0($sp)\n", s);
    }

    public void popw(String s) {
        fileWriter.format("\tlw %s, 0($sp)\n", s);
        fileWriter.format("\taddu $sp, $sp, %d\n", WORD_SIZE);
    }

	public void pushReg(String reg) {
		pushw(reg);
	}

	public void popReg(String reg) {
		popw(reg);
	}
	
	public void pushTemp(Temp t) {
		pushw(tempToString(t));
	}

	public void ret() {
		fileWriter.format("\tjr $ra\n");
	}

	public void jal(String label) {
		// Jump and Link to the function label
		fileWriter.format("\tjal %s\n", label);
	}

	public void exit() {
		fileWriter.println("\tli $v0, 10");
		fileWriter.println("\tsyscall");
	}
	
	/**************************************/
	/* USUAL SINGLETON IMPLEMENTATION ... */
	/**************************************/
	private static MipsGenerator instance = null;

	/*****************************/
	/* PREVENT INSTANTIATION ... */
	/*****************************/
	protected MipsGenerator() {}

	/******************************/
	/* GET SINGLETON INSTANCE ... */
	/******************************/
	public static MipsGenerator getInstance()
	{
		if (instance == null)
		{
			/*******************************/
			/* [0] The instance itself ... */
			/*******************************/
			instance = new MipsGenerator();

			try
			{
				/*********************************************************************************/
				/* [1] Open the MIPS text file and write data section with error message strings */
				/*********************************************************************************/
				String dirname="./output/";
				String filename=String.format("MIPS.txt");

				/***************************************/
				/* [2] Open MIPS text file for writing */
				/***************************************/
				instance.fileWriter = new PrintWriter(dirname + filename);
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		return instance;
	}
}

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

	public void dataSection() { fileWriter.format(".data\n"); }
	public void textSection() { fileWriter.format(".text\n"); }

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
		fileWriter.format("\tsw Temp_%d,global_%s\n",idxsrc,varName);
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
			fileWriter.format("\n.globl " + inlabel + "\n");
			textSection();
		}
		fileWriter.format("\n%s:\n",inlabel);

		// if (inlabel.equals("main"))
		// {
		// 	fileWriter.format(".text\n");
		// 	fileWriter.format("%s:\n",inlabel);
		// }
		// else
		// {
		// 	fileWriter.format("%s:\n",inlabel);
		// }
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

	// I think this needs to be here. I'm not completely sure - maybe it should be a library function?
	public void malloc(Temp dst, int size) {
		// Implement heap allocation for classes using syscall 9
	}

	public void allocateArray(Temp dst, Temp size) {
		// Implement array allocation (including length prefix)
		// Should also generate code for the "size > 0" runtime check
	}

	public void moveToReg(String targetReg, Temp src) {
		// Moves value from a temporary to a physical register (e.g., $a0)
		fileWriter.format("\tmove %s, Temp_%d\n", targetReg, src.getSerialNumber());
	}

	public void moveFromReg(String srcReg, Temp src) {
		// Moves value from a temporary to a physical register (e.g., $a0)
		fileWriter.format("\tmove Temp_%d, %s\n", src.getSerialNumber(), srcReg);
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

			/*****************************************************/
			/* [3] Print data section with error message strings */
			/*****************************************************/
			instance.fileWriter.print(".data\n");
			instance.fileWriter.print("string_access_violation: .asciiz \"Access Violation\"\n");
			instance.fileWriter.print("string_illegal_div_by_0: .asciiz \"Illegal Division By Zero\"\n");
			instance.fileWriter.print("string_invalid_ptr_dref: .asciiz \"Invalid Pointer Dereference\"\n");
		}
		return instance;
	}
}

/***********/
/* PACKAGE */
/***********/
package mips;

/*******************/
/* GENERAL IMPORTS */
/*******************/
import java.io.PrintWriter;

public class MipsGenerator
{
    private static final int WORD_SIZE=4;
    /***********************/
    /* The file writer ... */
    /***********************/
    private PrintWriter fileWriter;
    
    public static String LABEL_STRLEN           = "label_strlen";
    public static String LABEL_STRCOPY          = "label_strcpy";
    public static String LABEL_STR_CONCAT       = "label_str_concat";

    public static String LABEL_ACCESS_VIOLATION = "label_access_violation";
    public static String LABEL_DIV0             = "label_illegal_div_by_0";
    public static String LABEL_INV_PTR          = "label_invalid_ptr_deref";
    
    public static String STRING_ACCESS_VIOLATION    = "string_access_violation";
    public static String STRING_DIV0                = "string_illegal_div_by_0";
    public static String STRING_INV_PTR             = "string_invalid_ptr_dref";

    private String tempToString(String t) {
        return t;
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

    // NOTICE: Because of the hardcoded labels this must only be placed once in the code.
    public void strLen(String dst, String str) {
        String labelStart = "strlen_start";
        String labelEnd = "strlen_end";

        // dst = 0; t1 = src;
        fileWriter.format("\tli %s, 0\n", dst);
        fileWriter.format("\tmove $t1, %s\n", str);

        // while (*t1 != 0)
        fileWriter.format("%s:\n", labelStart);
        fileWriter.format("\tlb $t0, 0($t1)\n");
        fileWriter.format("\tbeq $t0, $zero, %s\n", labelEnd);

        // dst++; t1++;
        fileWriter.format("\taddi %s, %s, 1\n", dst, dst);
        fileWriter.format("\taddi $t1, $t1, 1\n");
        fileWriter.format("\tj %s\n", labelStart);
        fileWriter.format("%s:\n", labelEnd);
    }

    // NOTICE: Because of the hardcoded labels this must only be placed once in the code.
    // This also NULL terminates the string.
    public void strCopy(String dst, String src) {
        String labelStart = "strcopy_start";
        String labelEnd = "strcopy_end";

        // t0 = s; t1 = d;
        fileWriter.format("\tmove $t0, %s\n", src);
        fileWriter.format("\tmove $t1, %s\n", dst);

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
    
    public void printInt(String t)
    {
        // fileWriter.format("\taddi $a0,%s,0\n",t);
        fileWriter.format("\tmove $a0,%s\n",t);
        fileWriter.format("\tli $v0,1\n");
        fileWriter.format("\tsyscall\n");
        fileWriter.format("\tli $a0,32\n");
        fileWriter.format("\tli $v0,11\n");
        fileWriter.format("\tsyscall\n");
    }
    
    public void printString(String t)
    {
        fileWriter.format("\tmove $a0, %s\n", t);
        fileWriter.println("\tli $v0, 4");
        fileWriter.println("\tsyscall");
    }

    public void loadFromReg(String d, int offset, String srcReg) {
        fileWriter.format("\tlw %s, %d(%s)\n", d, offset, srcReg);
    }

    public void allocate(String varName) {
        // Call this ONLY during a "Data Phase" at the start of your program
        fileWriter.format("global_%s: .word 0\n", varName); 
    }

    public void loadField(String dst, String objectBase, int offset) {
        // MIPS: lw $dst, offset($base)
        fileWriter.format("\tlw %s, %d(%s)\n", dst, offset, objectBase);
    }

    public void storeField(String src, String objectBase, int offset) {
        // MIPS: sw $src, offset($base)
        fileWriter.format("\tsw %s, %d(%s)\n", src, offset, objectBase);
    }

    public void loadGlobal(String dst, String varName)
    {
        fileWriter.format("\tlw %s,global_%s\n",dst,varName);
    }

    public void storeGlobal(String varName, String src)
    {
        fileWriter.format("\tsw %s, global_%s\n",src,varName);
    }

    public void storeLocal(String var, String val) {
        fileWriter.format("\tsw %s, %s\n", val, var);
    }

    public void moveLocal(String dst, String src) {
        fileWriter.format("\tmove %s, %s\n", dst, src);
    }

    /**
     * Checks if a pointer is nil (0).
     * If it is, jumps to the global handler.
     */
    public void nilCheck(String ptr) {
        // If pointer == 0, jump to the handler
        fileWriter.format("\tbeq %s, $zero, %s\n", ptr, MipsGenerator.LABEL_INV_PTR);
    }

    /**
     * Checks if index is within [0, length).
     */
    public void arrayBoundsCheck(String base, String index) {
        fileWriter.format("\tbltz %s, label_access_violation\n", index);       // i < 0?
        fileWriter.format("\tlw $t0, 0(%s)\n", base);                         // i >= len? (*)
        fileWriter.format("\tbge %s, $t0, label_access_violation\n", index);   // If (*), jump to error.
    }

    public void loadArray(String dst, String base, String index) {
        // Calculate offset: (index + 1) * 4
        // We add 1 because the length is at offset 0
        fileWriter.format("\tmove $t0, %s\n", index);
        fileWriter.format("\taddi $t0, $t0, 1\n");
        fileWriter.format("\tsll $t0, $t0, 2\n"); // multiply by 4
        fileWriter.format("\tadd $t1, %s, $t0\n", base);
        fileWriter.format("\tlw %s, 0($t1)\n", dst);
    }

    public void storeArray(String base, String index, String src) {
        // Calculate offset: (index + 1) * 4
        // We add 1 because the length is at offset 0
        fileWriter.format("\tmove $t0, %s\n", index);
        fileWriter.format("\taddi $t0, $t0, 1\n");
        fileWriter.format("\tsll $t0, $t0, 2\n"); // multiply by 4
        fileWriter.format("\tadd $t1, %s, $t0\n", base);
        fileWriter.format("\tsw %s, 0($t1)\n", src);
    }

    public void addStringLabel(String label, String value) {
        fileWriter.format("%s: .asciiz %s\n", label, value); // Value should already contain "" or '' around the string.
    }

    public void loadAddress(String dst, String label) {
        fileWriter.format("\tla %s, %s\n", dst, label);
    }

    public void li(String t, int value)
    {
        fileWriter.format("\tli %s,%d\n",t,value);
    }

    public void add(String dst, String oprnd1, String oprnd2)
    {
        fileWriter.format("\tadd %s,%s,%s\n",dst,oprnd1,oprnd2);
    }

    public void sub(String dst, String oprnd1, String oprnd2)
    {
        fileWriter.format("\tsub %s,%s,%s\n",dst,oprnd1,oprnd2);
    }

    public void mul(String dst, String oprnd1, String oprnd2)
    {
        fileWriter.format("\tmul %s,%s,%s\n",dst,oprnd1,oprnd2);
    }

    public void div(String dst, String oprnd1, String oprnd2)
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
     * Mallocs memory.
     * Resulting address is returned in 'dst'.
     */
    public void malloc(String dst, String size) {
        // Allocate Heap Memory (sbrk)
        fileWriter.format("\tmove $a0, %s\n", size);
        fileWriter.format("\tli $v0, 9\n");
        fileWriter.format("\tsyscall\n");
        fileWriter.format("\tmove %s, $v0\n", dst); // Move result to destination
    }

    /**
     * Stores the value at base.
     */
    public void storeAt(String base, String value) {
        fileWriter.format("\tsw %s, 0(%s)\n", value, base);
    }

    public void jump(String inlabel)
    {
        fileWriter.format("\tj %s\n",inlabel);
    }   

    public void blt(String oprnd1, String oprnd2, String label)
    {
        fileWriter.format("\tblt %s,%s,%s\n",oprnd1,oprnd2,label);                
    }

    public void bge(String oprnd1, String oprnd2, String label)
    {
        fileWriter.format("\tbge %s,%s,%s\n",oprnd1,oprnd2,label);                
    }

    public void bne(String oprnd1, String oprnd2, String label)
    {
        fileWriter.format("\tbne %s,%s,%s\n",oprnd1,oprnd2,label);                
    }
    
    public void beq(String oprnd1, String oprnd2, String label)
    {
        fileWriter.format("\tbeq %s,%s,%s\n",oprnd1,oprnd2,label);                
    }

    public void beqz(String oprnd1, String label)
    {
        fileWriter.format("\tbeq %s,$zero,%s\n",oprnd1,label);             
    }

    public void allocateArray(String dst, String size) {
        // Runtime Check: size must be > 0
        fileWriter.format("\tmove $a0, %s\n", size);
        fileWriter.format("\tblez $a0, %s\n", MipsGenerator.LABEL_ACCESS_VIOLATION);

        // Allocate (size + 1) * 4 bytes [the +1 is added for storing the array's length].
        fileWriter.format("\taddi $a0, $a0, 1\n");
        fileWriter.format("\tsll $a0, $a0, 2\n");
        fileWriter.format("\tli $v0, 9\n");
        fileWriter.format("\tsyscall\n");

        // Store the length at the start of the block and return the result.
        fileWriter.format("\tsw %s, 0($v0)\n", size);
        fileWriter.format("\tmove %s, $v0\n", dst);
    }

    public void moveToReg(String targetReg, String src) {
        // Moves value from a temporary to a physical register (e.g., $a0)
        fileWriter.format("\tmove %s, %s\n", targetReg, src);
    }

    public void moveFromReg(String srcReg, String dst) {
        fileWriter.format("\tmove %s, %s\n", dst, srcReg);
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
    
    public void pushTemp(String t) {
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

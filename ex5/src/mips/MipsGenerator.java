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

    public static String outputPath = null;

    /***********************/
    /* The file writer ... */
    /***********************/
    private PrintWriter fileWriter;
    
    public static String LABEL_STRLEN           = "label_strlen";
    public static String LABEL_STRCOPY          = "label_strcpy";
    public static String LABEL_STR_CONCAT       = "label_str_concat";
    public static String LABEL_STR_EQ           = "label_str_eq";
    public static String LABEL_PRINT_INT        = "label_print_int";
    public static String LABEL_PRINT_STRING     = "label_print_string";
    public static String LABEL_MALLOC           = "label_malloc";
    public static String LABEL_ALLOC_ARRAY      = "label_alloc_array";

    public static String LABEL_ACCESS_VIOLATION = "label_access_violation";
    public static String LABEL_DIV0             = "label_illegal_div_by_0";
    public static String LABEL_INV_PTR          = "label_invalid_ptr_deref";
    
    public static String STRING_ACCESS_VIOLATION    = "string_access_violation";
    public static String STRING_DIV0                = "string_illegal_div_by_0";
    public static String STRING_INV_PTR             = "string_invalid_ptr_dref";

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

        fileWriter.format("\tmove $s1, %s\n", str); // Load string addr into iterator
        fileWriter.format("\tli %s, 0\n", dst);     // Initialize counter to 0

        fileWriter.format("%s:\n", labelStart);
        fileWriter.format("\tlb $s0, 0($s1)\n");
        fileWriter.format("\tbeq $s0, $zero, %s\n", labelEnd);

        fileWriter.format("\taddi %s, %s, 1\n", dst, dst);
        fileWriter.format("\taddi $s1, $s1, 1\n");
        fileWriter.format("\tj %s\n", labelStart);
        fileWriter.format("%s:\n", labelEnd);
    }

    // NOTICE: Because of the hardcoded labels this must only be placed once in the code.
    // This also NULL terminates the string.
    public void strCopy(String dst, String src) {        String labelStart = "strcopy_start";
        String labelEnd = "strcopy_end";

        // t0 = s; t1 = d;
        fileWriter.format("\tmove $s0, %s\n", src);
        fileWriter.format("\tmove $s1, %s\n", dst);

        // while (t0 != 0)
        fileWriter.format("%s:\n", labelStart);
        fileWriter.format("\tlb $s2, 0($s0)\n");
        fileWriter.format("\tbeq $s2, $zero, %s\n", labelEnd);
        
        // *(t1++) = *(t0++)
        fileWriter.format("\tsb $s2, 0($s1)\n");
        fileWriter.format("\taddi $s0, $s0, 1\n");
        fileWriter.format("\taddi $s1, $s1, 1\n");
        fileWriter.format("\tj %s\n", labelStart);
        fileWriter.format("%s:\n", labelEnd);
        
        // Null Terminate (*t1 = 0)
        fileWriter.format("\tsb $zero, 0($s1)\n");
    }

    // Byte-by-byte contents-equality. dst <- 1 if both strings have identical
    // contents (including null terminator), else 0. Uses $s0..$s2 as scratch.
    public void strEq(String dst, String s1, String s2) {
        String loop  = "streq_loop";
        String diff  = "streq_diff";
        String match = "streq_match";

        fileWriter.format("\tmove $s0, %s\n", s1);
        fileWriter.format("\tmove $s1, %s\n", s2);

        fileWriter.format("%s:\n", loop);
        fileWriter.format("\tlb $s2, 0($s0)\n");
        fileWriter.format("\tlb %s, 0($s1)\n", dst);
        fileWriter.format("\tbne $s2, %s, %s\n", dst, diff);
        fileWriter.format("\tbeq $s2, $zero, %s\n", match);
        fileWriter.format("\taddi $s0, $s0, 1\n");
        fileWriter.format("\taddi $s1, $s1, 1\n");
        fileWriter.format("\tj %s\n", loop);

        fileWriter.format("%s:\n", diff);
        fileWriter.format("\tli %s, 0\n", dst);
        fileWriter.format("\tj streq_done\n");

        fileWriter.format("%s:\n", match);
        fileWriter.format("\tli %s, 1\n", dst);
        fileWriter.format("streq_done:\n");
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

    public void storeAtReg(String src, int offset, String dstReg) {
        fileWriter.format("\tsw %s, %d(%s)\n", src, offset, dstReg);
    }

    public void allocate(String varName) {
        // Call this ONLY during a "Data Phase" at the start of your program
        fileWriter.format("global_%s: .word 0\n", varName);
    }

    /**
     * Emit a vtable entry into the .data segment.
     */
    public void emitVtable(String className, java.util.List<String> methodLabels) {
        fileWriter.format("vtable_%s:", className);
        if (methodLabels == null || methodLabels.isEmpty()) {
            fileWriter.format(" .word 0\n");
            return;
        }
        StringBuilder sb = new StringBuilder(" .word ");
        for (int i = 0; i < methodLabels.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(methodLabels.get(i));
        }
        sb.append("\n");
        fileWriter.print(sb.toString());
    }

    /**
     * `new ClassName`: malloc + write the vtable pointer at offset 0.
     */
    public void newObject(String dst, String className, int instanceSize) {
        fileWriter.format("\tli $a0, %d\n", instanceSize);
        fileWriter.format("\tjal %s\n", LABEL_MALLOC);
        fileWriter.format("\tmove %s, $v0\n", dst);
        fileWriter.format("\tla $s0, vtable_%s\n", className);
        fileWriter.format("\tsw $s0, 0(%s)\n", dst);
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
        fileWriter.format("\tlw $s0, 0(%s)\n", base);                         // i >= len? (*)
        fileWriter.format("\tbge %s, $s0, label_access_violation\n", index);   // If (*), jump to error.
    }

    /**
     * Array allocation length must be > 0.
     */
    public void bleAccessViolation(String len) {
        fileWriter.format("\tblez %s, %s\n", len, LABEL_ACCESS_VIOLATION);
    }

    public void loadArray(String dst, String base, String index) {
        // Calculate offset: (index + 1) * 4
        // We add 1 because the length is at offset 0
        fileWriter.format("\tmove $s0, %s\n", index);
        fileWriter.format("\taddi $s0, $s0, 1\n");
        fileWriter.format("\tsll $s0, $s0, 2\n"); // multiply by 4
        fileWriter.format("\tadd $s1, %s, $s0\n", base);
        fileWriter.format("\tlw %s, 0($s1)\n", dst);
    }

    public void storeArray(String base, String index, String src) {
        // Calculate offset: (index + 1) * 4
        // We add 1 because the length is at offset 0
        fileWriter.format("\tmove $s0, %s\n", index);
        fileWriter.format("\taddi $s0, $s0, 1\n");
        fileWriter.format("\tsll $s0, $s0, 2\n"); // multiply by 4
        fileWriter.format("\tadd $s1, %s, $s0\n", base);
        fileWriter.format("\tsw %s, 0($s1)\n", src);
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

    /**
     * Saturation clamp to L's signed range [-32768, 32767].
     * Uses $s0/$s1 as scratch.
     */
    public void clamp(String dst) {
        String hi = "clamp_hi_" + clampCounter;
        String lo = "clamp_lo_" + clampCounter;
        clampCounter++;
        fileWriter.format("\tli $s0, 32767\n");
        fileWriter.format("\tslt $s1, $s0, %s\n", dst);
        fileWriter.format("\tbeq $s1, $zero, %s\n", hi);
        fileWriter.format("\tmove %s, $s0\n", dst);
        fileWriter.format("%s:\n", hi);
        fileWriter.format("\tli $s0, -32768\n");
        fileWriter.format("\tslt $s1, %s, $s0\n", dst);
        fileWriter.format("\tbeq $s1, $zero, %s\n", lo);
        fileWriter.format("\tmove %s, $s0\n", dst);
        fileWriter.format("%s:\n", lo);
    }
    private int clampCounter = 0;

    public void div(String dst, String oprnd1, String oprnd2)
    {
        // Runtime check: divide-by-zero -> "Illegal Division By Zero" + exit.
        fileWriter.format("\tbeq %s, $zero, %s\n", oprnd2, LABEL_DIV0);
        fileWriter.format("\tdiv %s,%s\n", oprnd1, oprnd2);
        fileWriter.format("\tmflo %s\n", dst);
        clamp(dst);
    }

    public void label(String inlabel)
    {
        if (inlabel.equals("main")) {
            fileWriter.format(".globl " + inlabel + "\n");
        }
        fileWriter.format("\n%s:\n",inlabel);
    }
    
    public void segmentLabel(String segmentName)
    {
        fileWriter.format("\n.%s\n", segmentName);
    }

    /**
     * Mallocs memory and zero-fills it.
     * Resulting address is returned in 'dst'.
     *
     * Spec §2.5: reads of uninitialized class/array reference fields must trigger
     * Invalid Pointer Dereference, so heap memory must come back zeroed (0 == nil
     * for refs; 0 is the natural default for ints).
     */
    /**
     * Mallocs memory and zero-fills it.
     * Resulting address is returned in 'dst'.
     *
     * Heap memory comes back zeroed so reference fields default to nil.
     */
    public void malloc(String dst, String size) {
        // Round size up to a word multiple before sbrk so the zero-fill terminates cleanly.
        fileWriter.format("\tmove $a0, %s\n", size);
        fileWriter.format("\taddiu $a0, $a0, 3\n");
        fileWriter.format("\tsrl $a0, $a0, 2\n");
        fileWriter.format("\tsll $a0, $a0, 2\n");
        // Save the rounded byte count somewhere stable across the syscall.
        fileWriter.format("\tmove $s2, $a0\n");
        fileWriter.format("\tli $v0, 9\n");
        fileWriter.format("\tsyscall\n");
        fileWriter.format("\tmove %s, $v0\n", dst);

        // Zero-fill [base, base+rounded_size).
        String labelStart = "malloc_zero_start_" + (mallocZeroCounter);
        String labelEnd   = "malloc_zero_end_"   + (mallocZeroCounter);
        mallocZeroCounter++;
        fileWriter.format("\tmove $s0, %s\n", dst);
        fileWriter.format("\tadd $s1, $s0, $s2\n");
        fileWriter.format("%s:\n", labelStart);
        fileWriter.format("\tbeq $s0, $s1, %s\n", labelEnd);
        fileWriter.format("\tsw $zero, 0($s0)\n");
        fileWriter.format("\taddiu $s0, $s0, 4\n");
        fileWriter.format("\tj %s\n", labelStart);
        fileWriter.format("%s:\n", labelEnd);
    }
    private int mallocZeroCounter = 0;

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

        // Allocate (size + 1) * 4 bytes [the +1 is for storing the array's length].
        fileWriter.format("\taddi $a0, $a0, 1\n");
        fileWriter.format("\tsll $a0, $a0, 2\n");
        fileWriter.format("\tli $v0, 9\n");
        fileWriter.format("\tsyscall\n");

        // Store the length at the start of the block and return the result.
        fileWriter.format("\tsw %s, 0($v0)\n", size);
        fileWriter.format("\tmove %s, $v0\n", dst);

        // Zero the data slots so reference elements default to nil.
        String labelStart = "alloc_array_zero_start_" + arrayZeroCounter;
        String labelEnd   = "alloc_array_zero_end_"   + arrayZeroCounter;
        arrayZeroCounter++;
        fileWriter.format("\tadd $s0, %s, 4\n", dst);     // first element
        fileWriter.format("\tadd $s1, $s0, $a0\n");        // end (= base+(size+1)*4)
        fileWriter.format("\tsubu $s1, $s1, 4\n");
        fileWriter.format("%s:\n", labelStart);
        fileWriter.format("\tbeq $s0, $s1, %s\n", labelEnd);
        fileWriter.format("\tsw $zero, 0($s0)\n");
        fileWriter.format("\taddiu $s0, $s0, 4\n");
        fileWriter.format("\tj %s\n", labelStart);
        fileWriter.format("%s:\n", labelEnd);
    }
    private int arrayZeroCounter = 0;

    public void moveToReg(String targetReg, String src) {
        // Moves value from a temporary to a physical register (e.g., $a0)
        fileWriter.format("\tmove %s, %s\n", targetReg, src);
    }

    public void moveFromReg(String srcReg, String dst) {
        fileWriter.format("\tmove %s, %s\n", dst, srcReg);
    }

    public void addToSp(int amount) {
        // Signed 16-bit immediate; works both for shrinking and extending the frame.
        fileWriter.format("\taddiu $sp, $sp, %d\n", amount);
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

    public void ret() {
        fileWriter.format("\tjr $ra\n");
    }

    /**
     * Standard MIPS prologue. After this, $fp points to the saved $fp slot;
     * locals are at negative offsets, args at positive.
     */
    public void prologue(int totalFrame, int paramCount) {
        fileWriter.format("\taddiu $sp, $sp, -%d\n", totalFrame);
        fileWriter.format("\tsw $ra, %d($sp)\n", totalFrame - 4);
        fileWriter.format("\tsw $fp, %d($sp)\n", totalFrame - 8);
        fileWriter.format("\taddiu $fp, $sp, %d\n", totalFrame - 8);
        // Spill $a0..$a3 (up to paramCount) to the shadow space.
        int n = Math.min(paramCount, 4);
        for (int i = 0; i < n; i++) {
            fileWriter.format("\tsw $a%d, %d($fp)\n", i, 8 + i * 4);
        }
    }

    /**
     * Standard MIPS epilogue: restore $sp via $fp, then $fp and $ra.
     */
    public void epilogue() {
        fileWriter.format("\tmove $sp, $fp\n");
        fileWriter.format("\tlw $ra, 4($sp)\n");
        fileWriter.format("\tlw $fp, 0($sp)\n");
        fileWriter.format("\taddiu $sp, $sp, 8\n");
        fileWriter.format("\tjr $ra\n");
    }

    public void jal(String label) {
        // Jump and Link to the function label
        fileWriter.format("\tjal %s\n", label);
    }

    /**
     * Indirect call (used for virtual dispatch via vtable).
     */
    public void jalr(String reg) {
        fileWriter.format("\tjalr %s\n", reg);
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
                String path = outputPath;
                if (path == null) {
                    String dirname = "./output/";
                    new java.io.File(dirname).mkdirs();
                    path = dirname + "MIPS.txt";
                }
                instance.fileWriter = new PrintWriter(path);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return instance;
    }

    /**
     * Called when register allocation fails. Replaces whatever was written
     * to the output file with the single line "Register Allocation Failed"
     * and exits.
     */
    public static void failAllocation()
    {
        try {
            if (instance != null && instance.fileWriter != null) {
                instance.fileWriter.close();
            }
            String path = outputPath;
            if (path == null) {
                String dirname = "./output/";
                new java.io.File(dirname).mkdirs();
                path = dirname + "MIPS.txt";
            }
            PrintWriter w = new PrintWriter(path);
            w.println("Register Allocation Failed");
            w.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.exit(0);
    }
}

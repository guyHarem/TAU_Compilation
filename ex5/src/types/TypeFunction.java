package types;

public class TypeFunction extends Type
{
	/***********************************/
	/* The return type of the function */
	/***********************************/
	public Type returnType;

	/*************************/
	/* types of input params */
	/*************************/
	public TypeList params;

	/****************************************************/
	/* Stack-frame bookkeeping. Frame layout (high→low): */
	/*   args 4+ pushed by caller    +24, +28, ...       */
	/*   shadow for $a3              +20                 */
	/*   shadow for $a2              +16                 */
	/*   shadow for $a1              +12                 */
	/*   shadow for $a0              +8                  */
	/*   saved $ra                   +4                  */
	/*   saved $fp                    0  ← $fp           */
	/*   local 0                     -4                  */
	/*   local 1                     -8                  */
	/*   ...                                             */
	/****************************************************/
	public int paramCount = 0;     // total parameters (incl. implicit `this`)
	public int paramSlotCount = 0; // params allocated so far
	public int localSlotCount = 0; // body-locals allocated so far
	public int frameSize = 0;      // = localSlotCount * 4, settled in AstFuncDec.irMe
	public boolean processingParams = false;

	public int allocateParamSlot() {
		int off = 8 + paramSlotCount * 4; // $fp + 8 + N*4
		paramSlotCount++;
		return off;
	}

	public int allocateLocalSlot() {
		localSlotCount++;
		return -localSlotCount * 4;       // $fp - 4, -8, -12, ...
	}

	/****************/
	/* CTROR(S) ... */
	/****************/
	public TypeFunction(Type returnType, String name, TypeList params)
	{
		this.name = name;
		this.returnType = returnType;
		this.params = params;
	}

	@Override
	public boolean isFunction() { return true; }
}

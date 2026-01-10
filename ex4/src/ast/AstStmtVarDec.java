package ast;

import temp.*;
import types.*;

public class AstStmtVarDec extends AstStmt {
    public AstVarDec dec;

    public AstStmtVarDec(AstVarDec var, int lineNum) {
        serialNumber = AstNodeSerialNumber.getFresh();
        this.dec = var;
        this.line = lineNum;
    }

    @Override
    public void printMe() {
        if (dec != null) dec.printMe();
        AstGraphviz.getInstance().logNode(serialNumber, "var dec stmt");
        if (dec != null) AstGraphviz.getInstance().logEdge(serialNumber, dec.serialNumber);
    }

    @Override
    public Type semantMe() {
        if (dec != null) {
            return dec.semantMe();
        }
        return null;
    }

    @Override
	public Temp irMe()
	{
		if (dec != null) return dec.irMe();
		return null;
	}
}

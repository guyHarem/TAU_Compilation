package ir;

public class IrCommandList {
    public IrCommand head;
    public IrCommandList tail;

    public IrCommandList(IrCommand head, IrCommandList tail) {
        this.head = head;
        this.tail = tail;
    }

    public void mipsMe() {
        if (head != null) head.mipsMe(); // Call mipsMe on the command 
        if (tail != null) tail.mipsMe(); // Recurse on the rest of the list
    }
}

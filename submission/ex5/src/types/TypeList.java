package types;

public class TypeList extends Type
{
	/****************/
	/* DATA MEMBERS */
	/****************/
	public Type head;
	public TypeList tail;

	/******************/
	/* CONSTRUCTOR(S) */
	/******************/
	public TypeList(Type head, TypeList tail)
	{
		this.head = head;
		this.tail = tail;
	}

	public int size() {
		int count = 0;
		for (TypeList it = this; it != null; it = it.tail) {
			count++;
		}
		return count;
	}
}

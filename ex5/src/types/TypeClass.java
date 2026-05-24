package types;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class TypeClass extends Type
{
	/*********************************************************************/
	/* If this class does not extend a father class this should be null  */
	/*********************************************************************/
	public TypeClass father;

	/**************************************************/
	/* Gather up all data members in one place        */
	/* Note that data members coming from the AST are */
	/* packed together with the class methods         */
	/**************************************************/
	public TypeList dataMembers;

	/*************************************************/
	/* Map of member names to their types for lookup */
	/*************************************************/
	public Map<String, Type> memberMap = new HashMap<>();
    private Map<String, Integer> fieldOffsets = new HashMap<>();

    /*************************************************/
    /* Vtable layout: per-class method slot index +  */
    /* the class-effective label for that slot.      */
    /* Inherited methods reuse the parent's index;   */
    /* overrides keep the index but replace the      */
    /* label with this class's mangled label.        */
    /*************************************************/
    public Map<String, Integer> methodIndex = new LinkedHashMap<>();
    public Map<String, String>  methodLabel = new LinkedHashMap<>();

    /*************************************************/
    /* Per-instance constant initializers for class  */
    /* fields. Used by AstNewExp to emit             */
    /* IrCommandStoreField at construction time.     */
    /*************************************************/
    public static class FieldInit {
        public int offset;
        public Integer intValue;        // non-null => int literal
        public String  stringLabel;     // non-null => string literal label
        // nil => both null; field is already 0-initialised by malloc.
    }
    public java.util.List<FieldInit> fieldInits = new java.util.ArrayList<>();

    /**
     * All field initializers up to this class — parent's first.
     */
    public java.util.List<FieldInit> getAllFieldInits() {
        java.util.List<FieldInit> all = new java.util.ArrayList<>();
        if (father != null) all.addAll(father.getAllFieldInits());
        all.addAll(fieldInits);
        return all;
    }

	/****************/
	/* CTROR(S) ... */
	/****************/
	public TypeClass(TypeClass father, String name, TypeList dataMembers)
	{
		this.name = name;
		this.father = father;
		this.dataMembers = dataMembers;
        // Inherit parent vtable layout — overrides will shadow but slot indexes are stable.
        if (father != null) {
            this.methodIndex.putAll(father.methodIndex);
            this.methodLabel.putAll(father.methodLabel);
        }
	}

	/**
     * Returns the total number of fields in the hierarchy up to this class.
     */
    public int getFieldCount() {
        int count = fieldOffsets.size();
        if (father != null) {
            count += father.getFieldCount();
        }
        return count;
    }

	/**
     * Registers a field and assigns it a unique offset based on inheritance.
     * Object layout: word 0 = vtable pointer; fields start at offset 4.
     */
    public void addOffset(String fieldName, Type fieldType) {
        int index = (father != null) ? father.getFieldCount() : 0;
        index += fieldOffsets.size();
        fieldOffsets.put(fieldName, 4 /* vtable header */ + index * 4);
    }

    /**
     * Recursively finds the offset of a field.
     */
    public int getFieldOffset(String name) {
        if (fieldOffsets.containsKey(name)) {
            return fieldOffsets.get(name);
        }
        return (father != null) ? father.getFieldOffset(name) : -1;
    }

    /**
     * Total instance byte size: header + all fields.
     */
    public int getInstanceSize() {
        return 4 + getFieldCount() * 4;
    }

    /**
     * Look up the slot index of a method (recurse into parent).
     */
    public int getMethodIndex(String memberName) {
        if (methodIndex.containsKey(memberName)) return methodIndex.get(memberName);
        return (father != null) ? father.getMethodIndex(memberName) : -1;
    }

    /**
     * Look up the most-derived effective label for a method (used both for
     * direct calls inside class scope and for vtable construction).
     */
    public String getMethodLabel(String memberName) {
        if (methodLabel.containsKey(memberName)) return methodLabel.get(memberName);
        return (father != null) ? father.getMethodLabel(memberName) : null;
    }

    /**
     * Vtable entries in slot order.
     */
    public List<String> getVtableLabels() {
        // Build a slot-sized array.
        int n = 0;
        for (Integer slot : methodIndex.values()) if (slot != null && slot + 1 > n) n = slot + 1;
        List<String> v = new ArrayList<>();
        for (int i = 0; i < n; i++) v.add(null);
        for (Map.Entry<String, Integer> e : methodIndex.entrySet()) {
            v.set(e.getValue(), methodLabel.get(e.getKey()));
        }
        return v;
    }

	/**
	 * Add a member (field or method) to this class
	 */
	public void addMember(String memberName, Type memberType)
	{
		if (!memberType.isFunction()) this.addOffset(memberName, memberType);
		memberMap.put(memberName, memberType);
	}

	/**
	 * Find a member in this class or any of its superclasses.
	 * Returns null if not found.
	 */
	public Type findMember(String memberName)
	{
		// First check this class
		Type t = memberMap.get(memberName);
		if (t != null) {
			return t;
		}

		// Then check superclass chain
		if (father != null) {
			return father.findMember(memberName);
		}

		return null;
	}

	/**
	 * Check if this class is a descendant of the given ancestor class
	 */
	public boolean isDescendantOf(TypeClass ancestor)
	{
		if (this == ancestor) {
			return true;
		}
		if (father != null) {
			return father.isDescendantOf(ancestor);
		}
		return false;
	}

	@Override
	public boolean isClass() { return true; }
}

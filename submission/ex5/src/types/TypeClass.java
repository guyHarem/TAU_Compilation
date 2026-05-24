package types;

import java.util.HashMap;
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

	/****************/
	/* CTROR(S) ... */
	/****************/
	public TypeClass(TypeClass father, String name, TypeList dataMembers)
	{
		this.name = name;
		this.father = father;
		this.dataMembers = dataMembers;
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
     */
    public void addOffset(String fieldName, Type fieldType) {
        int offset = (father != null) ? father.getFieldCount() : 0;
        offset += fieldOffsets.size(); 
        fieldOffsets.put(fieldName, offset * 4 /* For now we assume DWORD sizes for all fields */);
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

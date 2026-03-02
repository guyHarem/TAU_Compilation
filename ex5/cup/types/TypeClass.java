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
	 * Add a member (field or method) to this class
	 */
	public void addMember(String memberName, Type memberType)
	{
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

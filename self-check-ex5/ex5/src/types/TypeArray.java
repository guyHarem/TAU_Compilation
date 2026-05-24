package types;

/**
 * Represents an array type.
 * Each array declaration creates a unique TypeArray instance.
 * Arrays of the same element type but different names are NOT interchangeable.
 */
public class TypeArray extends Type
{
	/**
	 * The type of elements in this array
	 */
	public Type elementType;

	/**
	 * Constructor
	 * @param name The name of this array type (from the array declaration)
	 * @param elementType The type of elements stored in this array
	 */
	public TypeArray(String name, Type elementType)
	{
		this.name = name;
		this.elementType = elementType;
	}

	@Override
	public boolean isArray() { return true; }
}

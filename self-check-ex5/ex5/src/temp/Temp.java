/***********/
/* PACKAGE */
/***********/
package temp;

/*******************/
/* GENERAL IMPORTS */
/*******************/

/*******************/
/* PROJECT IMPORTS */
/*******************/

public class Temp
{
	private int serial=0;
	
	public Temp(int serial)
	{
		this.serial = serial;
	}
	
	public int getSerialNumber()
	{
		return serial;
	}

	@Override
    public String toString()
    {
        return "t" + serial;
    }

	@Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Temp)) return false;
        return serial == ((Temp) obj).serial;
    }

	@Override
    public int hashCode() {
        return Integer.hashCode(serial);
    }
}

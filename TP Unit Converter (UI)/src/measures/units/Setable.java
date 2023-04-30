package measures.units;

import javax.naming.OperationNotSupportedException;

/**
 * An interface defining the possibility to set value (or not).
 * @apiNote Some {@link Unit}s may allow to set values whereas other may not :
 * For instance, symbolic units would not allow setting values.
 * @author davidroussel
 * @param <E> The type of value that can be set
 */
public interface Setable<E>
{
	/**
	 * Setable value accessor
	 * @return true if values can be set with {@link #setValue(Object)}, false
	 * otherwise
	 * @see #setValue(Object)
	 */
	public abstract boolean isSetable();

	/**
	 * Value setter
	 * @param value the value to set
	 * @throws OperationNotSupportedException if setting values is not allowed
	 * in this class.
	 * @see #isSetable()
	 */
	public abstract void setValue(E value) throws OperationNotSupportedException;
}

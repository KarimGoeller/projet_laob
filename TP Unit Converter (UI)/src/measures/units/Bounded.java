package measures.units;

/**
 * Interface for all units having bounds (min & max values).
 * @author davidroussel
 * @param <E> the type of values of the bounds
 */
public interface Bounded<E extends Comparable<E>>
{
	/**
	 * Accessor to lower bound
	 * @return the lower bound
	 */
	public abstract E getMin();

	/**
	 * Accessor to upper bound
	 * @return the upper bound
	 */
	public abstract E getMax();

	/**
	 * Indicator of cyclic values:
	 * All values above maximum value shall be renormalized between [min..max].
	 * @return true if values are cyclic
	 */
	public abstract boolean isCyclic();

	/**
	 * Normalize provided value inside [min..max]
	 * @param value the value to normalize
	 * @return the normalized value
	 */
	public abstract E normalize(E value);
}

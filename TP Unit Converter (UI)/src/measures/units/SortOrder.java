package measures.units;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Sorting order to use during comparison between Units
 */
public enum SortOrder
{
	/**
	 * Ascending Name ({@link Unit#description} and {@link Unit#symbol})
	 * sorting
	 */
	NAME_ASCENDING,
	/**
	 * Descending Name ({@link Unit#description} and {@link Unit#symbol})
	 * sorting
	 */
	NAME_DESCENDING,
	/**
	 * Ascending Factor sorting (for SI derived units)
	 */
	FACTOR_ASCENDING,
	/**
	 * Descending Factor sorting (for SI derived units)
	 */
	FACTOR_DESCENDING;

	/**
	 * String representation
	 * @return a String representation of this enum
	 */
	@Override
	public String toString()
	{
		switch (this)
		{
			case NAME_ASCENDING:
				return new String("Name ascending");
			case NAME_DESCENDING:
				return new String("Name descending");
			case FACTOR_ASCENDING:
				return new String("Factor ascending");
			case FACTOR_DESCENDING:
				return new String("Factor descending");
		}

		throw new AssertionError(getClass().getSimpleName()
		    + ".toString() unknown assertion: " + this);
	}

	/**
	 * Creates a collection of all possible sorting values.
	 * Can be used to fill a {@link javafx.scene.control.ComboBox}'s content
	 * @return a collection of all possible sorting policies
	 * @see application.Controller#initialize(java.net.URL, java.util.ResourceBundle)
	 */
	public static Collection<SortOrder> all()
	{
		Collection<SortOrder> list = new ArrayList<>();
		list.add(NAME_ASCENDING);
		list.add(NAME_DESCENDING);
		list.add(FACTOR_ASCENDING);
		list.add(FACTOR_DESCENDING);
		return list;
	}
}
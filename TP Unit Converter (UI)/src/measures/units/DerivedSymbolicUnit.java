package measures.units;

import java.text.ParseException;
import java.util.NoSuchElementException;

import measures.MeasureType;

/**
 * Special case of Derived Unit from {@link DerivedNumericUnit}.
 * Symbolic units provide formated values as symbols rather than numbers.
 * However Symbolic Units still have internal values just as
 * {@link DerivedNumericUnit}.
 * e.g. : Beaufort wind speed scale, Cardinal directions (North, East, ...)
 * @author davidroussel
 */
public class DerivedSymbolicUnit extends DerivedNumericUnit
{
	/**
	 * Symbols associated to each values pairs.
	 */
	protected String[] symbols;

	/**
	 * Valued constructor
	 * @param type The type of measures of this unit
	 * @param description the description of this unit
	 * @param symbol the symbol used with this unit
	 * @param values values associated to each symbol. First falue is also min
	 * and last value is max.
	 * @param symbols symbols associated to values. N symbols require N+1 values
	 * in order to have 1 values interval per symbol.
	 * @param cyclic flag indicating values in this unit are cyclic (meaning
	 * values over max or lower min shall be normalised between [min..max].
	 * Otherwise, setting a value below minimum acceptable value will result in
	 * setting the minimum value and settting a value above maximum value will
	 * result in setting the maximum value.
	 * @param power the power to apply on values from SI unit to convert to this
	 * unit values
	 * @param factor the factor to apply on values from SI unit to convert to
	 * this unit values
	 * @param offset the offset to apply on values from SI unit to convert to
	 * this unit values
	 * @param order The order in which factor and offset should be applied
	 * @throws NullPointerException if any of the provided arguments are null
	 * @throws ParseException if the format string cannot be parsed.
	 * @throws IllegalArgumentException if power or factor are too close to 0
	 * (let's say within 10^-6).
	 */
	public DerivedSymbolicUnit(MeasureType type,
	                           String description,
	                           String symbol,
	                           String[] symbols,
	                           boolean cyclic,
	                           double power,
	                           double factor,
	                           double offset,
	                           OperationOrder order)
	    throws NullPointerException,
	    ParseException,
	    IllegalArgumentException
	{
		super(type,
		      description,
		      symbol,
		      "",	// Empty format causes null formatter
		      0.0,	// min is index of first symbol
		      (double)(symbols.length - 1), // max is index of last symbol
		      cyclic,
		      false,	// Symbolic units are NOT setable
		      power,
		      factor,
		      offset,
		      order);
		this.symbols = symbols;
	}

	/**
	 * Computes the symbol to return according to internal value
	 * {@link Unit#value}
	 * @return the symbol in {@link #symbols} corresponding to the internal
	 * value's interval in {@link #values} or null if there is no such symbol.
	 * @throws IllegalStateException if the internal value is not in the rangle
	 * of {@link #values}
	 * @throws IllegalStateException if the internal value is out of bounds of
	 * the internal {@link #symbols} array.
	 * @throws NoSuchElementException if there is no value to format
	 */
	@Override
	public String formatValue() throws IllegalStateException
	{
		/*
		 * The other way of processing here is to convert the internal
		 * value in an index among #symbols
		 */
		int iValue = (int)Math.round(value.get());
		if ((iValue < 0) || (iValue >= symbols.length))
		{
			throw new IllegalStateException("out of symbols bounds internal value "
			    + iValue + " out of [0 ... " + (symbols.length - 1) + "]");
		}

		return symbols[iValue];
	}
}

package measures.units;

/**
 * Operation order to apply when converting from SI unit to other unit,
 * knowning that power (iff != 1) is always applied first
 * <ul>
 * 	<li>NO_CHANGE : no operation required : value = (SI value)^power</li>
 * 	<li>FACTOR_ONLY : product only : value = {@link DerivedNumericUnit#factor} * (SI value)^power</li>
 * 	<li>OFFSET_ONLY : addition only : value = (SI value)^power + {@link DerivedNumericUnit#offset}</li>
 * 	<li>FACTOR_FIRST : factor followed by offset : value = ((SI value)^power * {@link DerivedNumericUnit#factor}) + {@link DerivedNumericUnit#offset}</li>
 * 	<li>OFFSET_FIRST : offset followed by factor : value = ((SI value)^power + {@link DerivedNumericUnit#offset}) * {@link DerivedNumericUnit#factor}</li>
 * </ul>
 * Also provides numerical methods to compute values to and from SI unit
 * @see #fromSI(double, double, double, double)
 * @see #toSI(double, double, double, double)
 */
public enum OperationOrder
{
	/**
	 * If the derived unit has the same values as SI unit.
	 * e.g. milli-bars compared to hecto-Pascals for instance.
	 */
	NO_CHANGE,
	/**
	 * When the derived unif differs only by a factor from base SI unit.
	 * e.g. Km/h compared to m/s for instance.
	 */
	FACTOR_ONLY,
	/**
	 * When the derived unit differs only by an offset from base SI unit.
	 * e.g. °C compared to °K for instance.
	 */
	OFFSET_ONLY,
	/**
	 * When the derived unit's values can be converted to base SI units
	 * by applying factor first then adding offset.
	 * e.g. °C compared to °F : °F = (°C * (9/5)) + 32
	 */
	FACTOR_AND_OFFSET,
	/**
	 * When this unit's value can be converted to base SI unit value by
	 * adding offset first then multiplying by factor.
	 */
	OFFSET_AND_FACTOR;

	/**
	 * Reverts value to SI unit using factor, offset and operation order
	 * @param value the value to revert to SI unit
	 * @param factor the factor to apply (when applicable)
	 * @param offset the offset to apply (when applicable)
	 * @param power the power to apply (when applicable)
	 * @return the provided value converted to SI unit value
	 * @throws AssertionError if unknown enum value is used
	 */
	public double toSI(double value,
	                   double factor,
	                   double offset,
	                   double power)
	    throws AssertionError
	{
		double powValue = value;
		if (power != 1.0)
		{
			powValue = Math.pow(value, power);
		}

		switch (this)
		{
			case NO_CHANGE:
				return powValue;
			case FACTOR_ONLY:
				return powValue * factor;
			case OFFSET_ONLY:
				return powValue + offset;
			case FACTOR_AND_OFFSET:
				return (powValue * factor) + offset;
			case OFFSET_AND_FACTOR:
				return (powValue + offset) * factor;
		}
		throw new AssertionError("unknown op : " + this);
	}

	/**
	 * Convert value from SI unit using factor offset and operation order
	 * @param value the value to convert from SI unit
	 * @param factor the factor to apply (when applicable)
	 * @param offset the offset to apply (when applicable)
	 * @param power the power to apply (when applicable)
	 * @return the provided value converted from SI unit value
	 * @throws AssertionError if unknown enum value is used
	 */
	public double fromSI(double value,
	                     double factor,
	                     double offset,
	                     double power)
	        throws AssertionError
	{
		double returnValue;
		switch (this)
		{
			case NO_CHANGE:
				returnValue = value;
				break;
			case FACTOR_ONLY:
				returnValue = value / factor;
				break;
			case OFFSET_ONLY:
				returnValue =  value - offset;
				break;
			case FACTOR_AND_OFFSET:
				returnValue =  (value - offset) / factor;
				break;
			case OFFSET_AND_FACTOR:
				returnValue = (value / factor) - offset;
				break;
			default:
				throw new AssertionError("unknown op : " + this);
		}
		if (power != 1.0)
		{
			returnValue = Math.pow(returnValue, 1.0 / power);
		}
		return returnValue;
	}

	/**
	 * String representation of this enum
	 * @return a new String representing this enum
	 */
	@Override
	public String toString() throws AssertionError
	{
		switch(this)
		{
			case NO_CHANGE		: return new String("No Change");
			case FACTOR_ONLY	: return new String("Factor Only");
			case OFFSET_ONLY	: return new String("Offset Only");
			case FACTOR_AND_OFFSET	: return new String("Factor First");
			case OFFSET_AND_FACTOR	: return new String("Offset First");
			default:
				break;
		}
		throw new AssertionError("OperationOrder::toString : unknown op : " + this);
	}
}
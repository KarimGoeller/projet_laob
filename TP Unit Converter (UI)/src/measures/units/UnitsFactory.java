package measures.units;

import java.text.ParseException;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.logging.Level;
import java.util.logging.Logger;

import logger.LoggerFactory;
import measures.MeasureType;
import utils.FlyweightFactory;

/**
 * Factory to build {@link Unit}s {@link Set}s
 * @author davidroussel
 */
public class UnitsFactory
{
	/**
	 * The factoy providing {@link Unit}s
	 */
	static private FlyweightFactory<Unit<Double>> unitFactory =
		new FlyweightFactory<Unit<Double>>();

	/**
	 * Logger from {@link #unitFactory}
	 */
	static private Logger logger = LoggerFactory
	    .getParentLogger(UnitsFactory.class,
	                     unitFactory.getLogger(),
	                     (unitFactory .getLogger() == null ?
	                      Level.INFO : null)); // null level to inherit parent logger's level

	/**
	 * Hash type, description and symbol just as {@link Unit#hashCode()} would
	 * in order to provide a hashing value used as a key in {@link #unitFactory}
	 * @param type the class of the unit
	 * @param measures the type of Measures measured by values in a particular unit
	 * @param description The description of a unit
	 * @param symbol the symbol associated with this unit
	 * @return a hash value equivalent to the desired {@link Unit#hashCode()}
	 */
	private static int hash(Class<? extends Unit<?>> type,
	                        MeasureType measures,
	                        String description,
	                        String symbol)
	{
		final int prime = 31;
		int result = 1;
		result = (prime * result) + type.hashCode();
		result = (prime * result) + measures.hashCode();
		result = (prime * result) + description.hashCode();
		result = (prime * result) + symbol.hashCode();
		return result;
	}

	/**
	 * Factory method retreiving a {@link BoundedBaseNumericUnit} based on
	 * provided measure type, description, symbol, bounds an cyclic flag
	 * @param type the type of measure of the requested {@link Unit}
	 * @param description the description of the requested {@link Unit}
	 * @param symbol the symbol of the requested {@link Unit}
	 * @return The requested {@link Unit} from {@link #unitFactory} or null of
	 * the requested {@link Unit} can't be retrieved
	 * @throws NullPointerException if type or description are null. Although
	 * symbol argument is allowed to be null to represent an empty symbol
	 * @throws ParseException if the provided format can't be parsed
	 */
	public static Unit<Double> getBoundedBaseNumericUnit(MeasureType type,
	                                                     String description,
	                                                     String symbol)
		    throws NullPointerException,
	    ParseException
	{
		return getBoundedBaseNumericUnit(type,
		                                 description,
		                                 symbol,
		                                 BaseNumericUnit.DefaultFormat);
	}

	/**
	 * Factory method retreiving a {@link BoundedBaseNumericUnit} based on
	 * provided measure type, description, symbol, bounds an cyclic flag
	 * @param type the type of measure of the requested {@link Unit}
	 * @param description the description of the requested {@link Unit}
	 * @param symbol the symbol of the requested {@link Unit}
	 * @param format the numeric format to apply when formatting values of the
	 * requested {@link Unit}
	 * @return The requested {@link Unit} from {@link #unitFactory} or null of
	 * the requested {@link Unit} can't be retrieved
	 * @throws NullPointerException if type or description are null. Although
	 * symbol argument is allowed to be null to represent an empty symbol
	 * @throws ParseException if the provided format can't be parsed
	 */
	public static Unit<Double> getBoundedBaseNumericUnit(MeasureType type,
	                                                     String description,
	                                                     String symbol,
	                                                     String format)
	    throws NullPointerException,
	    ParseException
	{
		return getBoundedBaseNumericUnit(type,
		                                 description,
		                                 symbol,
		                                 format,
		                                 0.0,
		                                 Double.POSITIVE_INFINITY,
		                                 false);
	}

	/**
	 * Factory method retreiving a {@link BoundedBaseNumericUnit} based on
	 * provided measure type, description, symbol, bounds an cyclic flag
	 * @param type the type of measure of the requested {@link Unit}
	 * @param description the description of the requested {@link Unit}
	 * @param symbol the symbol of the requested {@link Unit}
	 * @param format the numeric format to apply when formatting values of the
	 * requested {@link Unit}
	 * @param min min value of the requested {@link Unit}
	 * @param max max value of the requested {@link Unit}
	 * @param cyclic cyclic flag of the requested {@link Unit}
	 * @return The requested {@link Unit} from {@link #unitFactory} or null of
	 * the requested {@link Unit} can't be retrieved
	 * @throws NullPointerException if type or description are null. Although
	 * symbol argument is allowed to be null to represent an empty symbol
	 * @throws ParseException if the provided format can't be parsed
	 * @throws IllegalStateException if a newly created Unit's hashCode
	 * does not match the hash value provided by
	 * {@link #hash(MeasureType, String, String)}
	 * @see #hash(MeasureType, String, String)
	 */
	public static Unit<Double> getBoundedBaseNumericUnit(MeasureType type,
	                                                     String description,
	                                                     String symbol,
	                                                     String format,
	                                                     double min,
	                                                     double max,
	                                                     boolean cyclic)
	    throws NullPointerException,
	    ParseException
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(description);
		Objects.requireNonNull(format);

		int hash = hash(BoundedBaseNumericUnit.class, type, description, symbol);
		Unit<Double> unit = unitFactory.get(hash);

		if (unit == null)
		{
			Unit<Double> tempUnit = new BoundedBaseNumericUnit(type,
			                                                   description,
			                                                   symbol,
			                                                   format,
			                                                   min,
			                                                   max,
			                                                   cyclic);
			int tempHash = tempUnit.hashCode();
			if (hash != tempHash)
			{
				throw new IllegalStateException("Unexpected hash code "
				    + tempHash + ", expected " + hash);
			}
			unitFactory.put(hash, tempUnit);
			unit = unitFactory.get(hash);;
		}

		logger.info("Built unit: " + unit);
		return unit;
	}

	/**
	 * Factory method retreiving a {@link DerivedNumericUnit} based on
	 * provided measure type, description, symbol, bounds, cyclic flag as well as
	 * conversion coefs such as pwoer, factor, offset and operation order.
	 * @param type the type of measure of the requested {@link Unit}
	 * @param description the description of the requested {@link Unit}
	 * @param symbol the symbol of the requested {@link Unit}
	 * @param format the numeric format to apply when formatting values of the
	 * requested {@link Unit}
	 * @param min min value of the requested {@link Unit}
	 * @param max max value of the requested {@link Unit}
	 * @param cyclic cyclic flag of the requested {@link Unit}
	 * @param power the power to apply on values from SI unit to convert to this
	 * {@link Unit} values
	 * @param factor the factor to apply on values from SI unit to convert to
	 * this {@link Unit} values
	 * @param offset the offset to apply on values from SI unit to convert to
	 * this {@link Unit} values
	 * @param order The order in which factor and offset should be applied
	 * @return The requested {@link Unit} from {@link #unitFactory} or null of
	 * the requested {@link Unit} can't be retrieved
	 * @throws NullPointerException if type or description are null. Although
	 * symbol argument is allowed to be null to represent an empty symbol
	 * @throws ParseException if the provided format can't be parsed
	 * @throws IllegalStateException if a newly created Unit's hashCode
	 * does not match the hash value provided by
	 * {@link #hash(MeasureType, String, String)}
	 * @see #hash(MeasureType, String, String)
	 */
	public static Unit<Double> getDerivedNumericUnit(MeasureType type,
	                                                 String description,
	                                                 String symbol,
	                                                 String format,
	                                                 double min,
	                                                 double max,
	                                                 boolean cyclic,
	                                                 double power,
	                                                 double factor,
	                                                 double offset,
	                                                 OperationOrder order)
	    throws NullPointerException,
	    ParseException
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(description);
		Objects.requireNonNull(format);

		int hash = hash(DerivedNumericUnit.class, type, description, symbol);
		Unit<Double> unit = unitFactory.get(hash);

		if (unit == null)
		{
			Unit<Double> tempUnit = new DerivedNumericUnit(type,
			                                               description,
			                                               symbol,
			                                               format,
			                                               min,
			                                               max,
			                                               cyclic,
			                                               power,
			                                               factor,
			                                               offset,
			                                               order);
			int tempHash = tempUnit.hashCode();
			if (hash != tempHash)
			{
				throw new IllegalStateException("Unexpected hash code "
				    + tempHash + ", expected " + hash);
			}
			unitFactory.put(hash, tempUnit);
			unit = unitFactory.get(hash);
		}

		logger.info("Built unit: " + unit);
		return unit;
	}

	/**
	 * Factory method retreiving a {@link DerivedNumericUnit} based on
	 * provided measure type, description, symbol, lower bound, as well as
	 * conversion coefs such as pwoer, factor, offset and operation order.
	 * @param type the type of measure of the requested {@link Unit}
	 * @param description the description of the requested {@link Unit}
	 * @param symbol the symbol of the requested {@link Unit}
	 * @param format the numeric format to apply when formatting values of the
	 * requested {@link Unit}
	 * @param min min value of the requested {@link Unit}
	 * @param power the power to apply on values from SI unit to convert to this
	 * {@link Unit} values
	 * @param factor the factor to apply on values from SI unit to convert to
	 * this {@link Unit} values
	 * @param offset the offset to apply on values from SI unit to convert to
	 * this {@link Unit} values
	 * @param order The order in which factor and offset should be applied
	 * @return The requested {@link Unit} from {@link #unitFactory} or null of
	 * the requested {@link Unit} can't be retrieved
	 * @throws NullPointerException if type or description are null. Although
	 * symbol argument is allowed to be null to represent an empty symbol
	 * @throws ParseException if the provided format can't be parsed
	 * @throws IllegalStateException if a newly created Unit's hashCode
	 * does not match the hash value provided by
	 * {@link #hash(MeasureType, String, String)}
	 * @see #hash(MeasureType, String, String)
	 */
	public static Unit<Double> getDerivedNumericUnit(MeasureType type,
	                                                 String description,
	                                                 String symbol,
	                                                 String format,
	                                                 double min,
	                                                 double power,
	                                                 double factor,
	                                                 double offset,
	                                                 OperationOrder order)
	    throws NullPointerException,
	    ParseException
	{
		return getDerivedNumericUnit(type,
		                             description,
		                             symbol,
		                             format,
		                             min,
		                             Double.POSITIVE_INFINITY,
		                             false,
		                             power,
		                             factor,
		                             offset,
		                             order);
	}

	/**
	 * Factory method retreiving a {@link DerivedNumericUnit} based on
	 * provided measure type, description, symbol as well as
	 * conversion coefs such as pwoer, factor, offset and operation order.
	 * @param type the type of measure of the requested {@link Unit}
	 * @param description the description of the requested {@link Unit}
	 * @param symbol the symbol of the requested {@link Unit}
	 * @param format the numeric format to apply when formatting values of the
	 * requested {@link Unit}
	 * @param min min value of the requested {@link Unit}
	 * @param power the power to apply on values from SI unit to convert to this
	 * {@link Unit} values
	 * @param factor the factor to apply on values from SI unit to convert to
	 * this {@link Unit} values
	 * @param offset the offset to apply on values from SI unit to convert to
	 * this {@link Unit} values
	 * @param order The order in which factor and offset should be applied
	 * @return The requested {@link Unit} from {@link #unitFactory} or null of
	 * the requested {@link Unit} can't be retrieved
	 * @throws NullPointerException if type or description are null. Although
	 * symbol argument is allowed to be null to represent an empty symbol
	 * @throws ParseException if the provided format can't be parsed
	 * @throws IllegalStateException if a newly created Unit's hashCode
	 * does not match the hash value provided by
	 * {@link #hash(MeasureType, String, String)}
	 * @see #hash(MeasureType, String, String)
	 */
	public static Unit<Double> getDerivedNumericUnit(MeasureType type,
	                                                 String description,
	                                                 String symbol,
	                                                 String format,
	                                                 double power,
	                                                 double factor,
	                                                 double offset,
	                                                 OperationOrder order)
	    throws NullPointerException,
	    ParseException
	{
		return getDerivedNumericUnit(type,
		                             description,
		                             symbol,
		                             format,
		                             0.0,
		                             Double.POSITIVE_INFINITY,
		                             false,
		                             power,
		                             factor,
		                             offset,
		                             order);
	}

	/**
	 * Factory method retreiving a {@link BaseSymbolicUnit} on
	 * provided measure type, description, symbol, values and symbols associated
	 * to the provided values
	 * @param type the type of measure of the requested {@link Unit}
	 * @param description the description of the requested {@link Unit}
	 * @param symbol the symbol of the requested {@link Unit}
	 * @param values values ranges associated to each symbol of the requested
	 * {@link Unit}
	 * @param symbols symbols associated to values of the requested {@link Unit}
	 * @param cyclic cyclic flag of the requested {@link Unit}
	 * @return The requested {@link Unit} from {@link #unitFactory} or null of
	 * the requested {@link Unit} can't be retrieved
	 * @throws NullPointerException if type or description are null. Although
	 * symbol argument is allowed to be null to represent an empty symbol
	 * @throws ParseException if the provided format can't be parsed
	 * @throws IllegalStateException if a newly created Unit's hashCode
	 * does not match the hash value provided by
	 * {@link #hash(MeasureType, String, String)}
	 * @see #hash(MeasureType, String, String)
	 */
	public static Unit<Double> getBaseSymbolicUnit(MeasureType type,
	                                               String description,
	                                               String symbol,
	                                               double[] values,
	                                               String[] symbols,
	                                               boolean cyclic)
	    throws NullPointerException,
	    ParseException
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(description);
		Objects.requireNonNull(values);
		Objects.requireNonNull(symbols);

		int hash = hash(BaseSymbolicUnit.class, type, description, symbol);
		Unit<Double> unit = unitFactory.get(hash);

		if (unit == null)
		{
			Unit<Double> tempUnit = new BaseSymbolicUnit(type,
			                                             description,
			                                             symbol,
			                                             values,
			                                             symbols,
			                                             cyclic);
			int tempHash = tempUnit.hashCode();
			if (hash != tempHash)
			{
				throw new IllegalStateException("Unexpected hash code "
				    + tempHash + ", expected " + hash);
			}
			unitFactory.put(hash, tempUnit);
			unit = unitFactory.get(hash);
		}

		logger.info("Built unit: " + unit);
		return unit;
	}

	/**
	 * Factory method retreiving a {@link BaseSymbolicUnit} on
	 * provided measure type, description, symbol, values and symbols associated
	 * to the provided values
	 * @param type the type of measure of the requested {@link Unit}
	 * @param description the description of the requested {@link Unit}
	 * @param symbol the symbol of the requested {@link Unit}
	 * @param values values ranges associated to each symbol of the requested
	 * {@link Unit}
	 * @param symbols symbols associated to values of the requested {@link Unit}
	 * @param cyclic cyclic flag of the requested {@link Unit}
	 * @param power the power to apply to convert from SI value
	 * @param factor the factor to apply to convert from SI value
	 * @param offset the offset to apply to convert from SI value
	 * @param order the operation order to convert from SI value
	 * @return The requested {@link Unit} from {@link #unitFactory} or null of
	 * the requested {@link Unit} can't be retrieved
	 * @throws NullPointerException if type or description are null. Although
	 * symbol argument is allowed to be null to represent an empty symbol
	 * @throws ParseException if the provided format can't be parsed
	 * @throws IllegalStateException if a newly created Unit's hashCode
	 * does not match the hash value provided by
	 * {@link #hash(MeasureType, String, String)}
	 * @see #hash(MeasureType, String, String)
	 */
	public static Unit<Double> getDerivedSymbolicUnit(MeasureType type,
	                                                  String description,
	                                                  String symbol,
	                                                  String[] symbols,
	                                                  boolean cyclic,
	                                                  double power,
	                                                  double factor,
	                                                  double offset,
	                                                  OperationOrder order)
	    throws NullPointerException,
	    ParseException
	{
		Objects.requireNonNull(type);
		Objects.requireNonNull(description);
		Objects.requireNonNull(symbols);
		Objects.requireNonNull(order);

		int hash = hash(DerivedSymbolicUnit.class, type, description, symbol);
		Unit<Double> unit = unitFactory.get(hash);

		if (unit == null)
		{
			Unit<Double> tempUnit = new DerivedSymbolicUnit(type,
			                                                description,
			                                                symbol,
			                                                symbols,
			                                                cyclic,
			                                                power,
			                                                factor,
			                                                offset,
			                                                order);
			int tempHash = tempUnit.hashCode();
			if (hash != tempHash)
			{
				throw new IllegalStateException("Unexpected hash code "
				    + tempHash + ", expected " + hash);
			}
			unitFactory.put(hash, tempUnit);
			unit = unitFactory.get(hash);
		}

		logger.info("Built unit: " + unit);
		return unit;
	}

	/**
	 * Factory method retreiving a {@link DecomposedUnit} on
	 * provided unit to decompose, description, symbols, separators and coefs
	 * @param unit The unit to decompose
	 * @param description the description of the requested {@link Unit}
	 * @param symbols The symbol string containing symbols separated by separator.
	 * @param separator the separator to print between decomposed values and
	 * symbols.
	 * @param coefs The coefficients to apply at each step of the decomposition
	 * @return The requested {@link Unit} from {@link #unitFactory} or null of
	 * the requested {@link Unit} can't be retrieved
	 * @throws NullPointerException if any of the provided arguments is null
	 * @throws ParseException if the provided format can't be parsed
	 * @throws IllegalStateException if a newly created Unit's hashCode
	 * does not match the hash value provided by
	 * {@link #hash(MeasureType, String, String)}
	 * @see #hash(MeasureType, String, String)
	 */
	public static Unit<Double> getDecomposedUnit(Unit<Double> unit,
	                                             String description,
	                                             String symbols,
	                                             String separator,
	                                             Double[] coefs)
	    throws NullPointerException,
	    ParseException
	{
		Objects.requireNonNull(unit);
		Objects.requireNonNull(description);
		Objects.requireNonNull(symbols);
		Objects.requireNonNull(separator);
		Objects.requireNonNull(coefs);

		int hash = hash(DecomposedUnit.class, unit.getType(), description, symbols);
		Unit<Double> factoryUnit = unitFactory.get(hash);

		if (factoryUnit == null)
		{
			Unit<Double> tempUnit = new DecomposedUnit(unit,
			                                           description,
			                                           symbols,
			                                           separator,
			                                           coefs);
			int tempHash = tempUnit.hashCode();
			if (hash != tempHash)
			{
				throw new IllegalStateException("Unexpected hash code "
				    + tempHash + ", expected " + hash);
			}
			unitFactory.put(hash, tempUnit);
			factoryUnit = unitFactory.get(hash);
		}

		logger.info("Built unit: " + unit);
		return factoryUnit;
	}

	/**
	 * Factory method to provide a {@link Unit} {@link Set} according to the
	 * provided {@link MeasureType}
	 * @param type the type of measures the returned {@link Unit}s will measure
	 * @return A Set of {@link Unit}s measuring the provided {@link MeasureType}
	 * @throws ParseException if some units format can't be parsed
	 * @throws NullPointerException if some units arguments are null
	 */
	public static Set<Unit<Double>> getUnits(MeasureType type)
		throws NullPointerException,
		ParseException
	{
		logger.info("Building Unit set for " + type);

		Set<Unit<Double>> set = new TreeSet<>();

		switch (type)
		{
			case LENGTH:
				set.add(getBoundedBaseNumericUnit(MeasureType.LENGTH,
				                                  "Mètres",
				                                  "m",
				                                  "7.3"));
				set.add(getDerivedNumericUnit(MeasureType.LENGTH,
				                              "Centimètres",
				                              "cm",
				                              "8.2",
				                              1.0,
				                              0.01,
				                              0.0,
				                              OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.LENGTH,
						"Millimètres",
						"mm",
						"8.2",
						1.0,
						0.001,
						0.0,
						OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.LENGTH,
						"Pouce",
						"in",
						"8.2",
						1.0,
						0.0254,
						0.0,
						OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.LENGTH,
						"Pied",
						"ft",
						"8.2",
						1.0,
						0.3048,
						0.0,
						OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.LENGTH,
						"Yard",
						"yd",
						"8.2",
						1.0,
						0.9144,
						0.0,
						OperationOrder.FACTOR_ONLY));

				break;
			case AREA:
				set.add(getBoundedBaseNumericUnit(MeasureType.AREA,
				                                  "Mètres carré",
				                                  "m²",
				                                  "9.4"));
				set.add(getDerivedNumericUnit(MeasureType.AREA,
				                              "Centimètres carré",
				                              "cm²",
				                              "7.1",
				                              1.0,
				                              0.0001,
				                              0.0,
				                              OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.AREA,
						"Hectar",
						"ha",
						"7.1",
						1.0,
						10000.0,
						0.0,
						OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.AREA,
						"Square Inch",
						"in²",
						"7.1",
						1.0,
						0.00064516,
						0.0,
						OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.AREA,
						"Square feet",
						"ft²",
						"7.1",
						1.0,
						0.09290304,
						0.0,
						OperationOrder.FACTOR_ONLY));
				break;
			case VOLUME:
				set.add(getBoundedBaseNumericUnit(MeasureType.VOLUME,
				                                  "Mètres cube",
				                                  "m³",
				                                  "12.6"));
				set.add(getDerivedNumericUnit(MeasureType.VOLUME,
				                              "Litres",
				                              "l",
				                              "10.3",
				                              1.0,
				                              0.001,
				                              0.0,
				                              OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.VOLUME,
						"Gallon",
						"gal",
						"10.3",
						1.0,
						0.00454609,
						0.0,
						OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.VOLUME,
						"US Gallon",
						"USgal",
						"10.3",
						1.0,
						0.003785411784,
						0.0,
						OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.VOLUME,
						"Centimètres cube",
						"cm^3",
						"10.3",
						1.0,
						0.000001,
						0.0,
						OperationOrder.FACTOR_ONLY));
				break;
			case WEIGHT:
				set.add(getBoundedBaseNumericUnit(MeasureType.WEIGHT,
				                                  "Kilogrammes",
				                                  "kg",
				                                  "12.6"));
				set.add(getDerivedNumericUnit(MeasureType.WEIGHT,
				                              "Grammes",
				                              "g",
				                              "11.3",
				                              1.0,
				                              0.001,
				                              0.0,
				                              OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.WEIGHT,
						"MilliGrammes",
						"mg",
						"11.3",
						1.0,
						0.000001,
						0.0,
						OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.WEIGHT,
						"Livre",
						"lb",
						"11.3",
						1.0,
						0.45359237,
						0.0,
						OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.WEIGHT,
						"Tonne",
						"T",
						"11.3",
						1.0,
						1000.0,
						0.0,
						OperationOrder.FACTOR_ONLY));
				break;
			case SPEED:
				set.add(getBoundedBaseNumericUnit(MeasureType.SPEED,
				                                  "Mètres / seconde",
				                                  "m/s"));
 				set.add(getDerivedNumericUnit(MeasureType.SPEED,
				                              "Kilomètres / heure",
				                              "km/h",
				                              "5.1",
				                              1.0,
				                              1.0/3.6,
				                              0.0,
				                              OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.SPEED,
						"Miles / heure",
						"mph",
						"5.1",
						1.0,
						0.44704,
						0.0,
						OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.SPEED,
						"Miles / minutes",
						"mpm",
						"5.1",
						1.0,
						0.007450666667 ,
						0.0,
						OperationOrder.FACTOR_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.SPEED,
						"Noeud",
						"kn",
						"5.1",
						1.0,
						0.514444,
						0.0,
						OperationOrder.FACTOR_ONLY));

				break;
			case PRESSURE:
				set.add(getBoundedBaseNumericUnit(MeasureType.PRESSURE,
				                                  "Pascals",
				                                  "pa"));
				set.add(getDerivedNumericUnit(MeasureType.PRESSURE,
				                              "Hectopascals",
				                              "hPa",
				                              "8.2",
				                              1.0,
				                              100.0,
				                              0.0,
				                              OperationOrder.FACTOR_ONLY));
				break;
			case DIRECTION:
				set.add(getBoundedBaseNumericUnit(MeasureType.DIRECTION,
				                                  "Radians",
				                                  "",
				                                  "9.5"));
				set.add(getDerivedNumericUnit(MeasureType.DIRECTION,
				                              "Degrés",
				                              "°",
				                              "4.1",
				                              0.0,
				                              360.0,
				                              true,
				                              1.0,
				                              Math.PI / 180.0,
				                              0.0,
				                              OperationOrder.FACTOR_ONLY));
				break;
			case TEMPERATURE:
				set.add(getBoundedBaseNumericUnit(MeasureType.TEMPERATURE,
				                                  "Kelvins",
				                                  "°K"));
				set.add(getDerivedNumericUnit(MeasureType.TEMPERATURE,
				                              "Degrés Celsius",
				                              "°C",
				                              "+4.1",
				                              -273.15,
				                              Double.POSITIVE_INFINITY,
				                              false,
				                              1.0,
				                              1.0,
				                              273.15,
				                              OperationOrder.OFFSET_ONLY));
				set.add(getDerivedNumericUnit(MeasureType.TEMPERATURE,
						"Degrés Fahrenheit",
						"°F",
						"+4.1",
						-459.67,
						Double.POSITIVE_INFINITY,
						false,
						1.0,
						5.0/9.0,
						459.67 ,
						OperationOrder.OFFSET_AND_FACTOR));
				break;
			case TIME:
			{
				/*
				 * We need to keep track of "seconds" so we can decompose it later
				 */
				Unit<Double> seconds = getBoundedBaseNumericUnit(MeasureType.TIME,
				                                                 "Secondes",
				                                                 "s");
				set.add(seconds); // keep track of "seconds" so it can be decomposed
				set.add(getDerivedNumericUnit(MeasureType.TIME,
				                              "Minutes",
				                              "m",
				                              "8.2",
				                              1.0,
				                              60.0,
				                              0.0,
				                              OperationOrder.FACTOR_ONLY));
				break;
			}
			default:
				break;
		}
		return set;
	}
}

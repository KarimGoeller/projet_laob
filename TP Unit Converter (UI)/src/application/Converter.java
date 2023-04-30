package application;

import java.text.ParseException;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import logger.LoggerFactory;
import measures.MeasureType;
import measures.units.Setable;
import measures.units.SortOrder;
import measures.units.Unit;
import measures.units.UnitsFactory;

/**
 * Converter represents the main Data Model class.
 * It consists in
 * <ul>
 * 	<li>
 * 	<li>A List of source Units to convert from, with a currently selected source unit.</li>
 * 	<li>A List of destination Units to convert to, with a currently selected destination unit.</li>
 * </ul>
 * @author davidroussel
 */
public class Converter
{
	/**
	 * Logger to show debug message or only log them in a file
	 */
	private Logger logger = null;

	/**
	 * Property holding the current type of measure determining source and destination units
	 * @implNote this property can evt be bound to a
	 * {@link javafx.scene.control.ComboBox#valueProperty()} so that changes in
	 * the bounded combobox can be reflected in this property.
	 * @see UnitsFactory#getUnits(ObjectProperty)
	 */
	private ObjectProperty<MeasureType> measureType;

	/**
	 * Observable List of source {@link Unit}s.
	 * Such an Observable list can be set as the content of a
	 * {@link javafx.scene.control.ComboBox} in a UI.
	 * Source units are all {@link Setable} in order for a value to be set in
	 * one of the source units and converted to a destination unit.
	 * If Units are obtained from {@link UnitsFactory#getUnits(ObjectProperty)}
	 * these shall be filtered to keep only {@link Setable} {@link Unit}s.
	 * @see UnitsFactory#getUnits(ObjectProperty)
	 */
	private ObservableList<Unit<Double>> sourceUnits;

	/**
	 * Property holding the currently selected source unit.
	 * This is the {@link Unit} we'll be converting values from.
	 * @implNote holded {@link Unit} shall be part of {@link #sourceUnits}
	 * @implNote holded  {@link Unit} shall be {@link Setable} in order for
	 * a source value to be set without throwing an {@link UnsupportedOperationException}
	 * @implNote this property can evt be bound to a
	 * {@link javafx.scene.control.ComboBox#valueProperty()} so that changes in
	 * the bounded combobox can be reflected in this property.
	 */
	private static ObjectProperty<Unit<Double>> sourceUnit;

	/**
	 * The property holding the order to apply when sorting source units
	 * @implNote this property can evt be bound to a
	 * {@link javafx.scene.control.ComboBox#valueProperty()} so that changes in
	 * the bounded combobox can be reflected in this property.
	 * @see SortOrder
	 */
	private ObjectProperty<SortOrder> sourceSortOrder;

	/**
	 * Observable list of destination {@link Unit}s
	 * Such an Observable list can be set as the content of a
	 * {@link javafx.scene.control.ComboBox} in a UI.
	 * @see UnitsFactory#getUnits(ObjectProperty)
	 */
	private ObservableList<Unit<Double>> destinationUnits;

	/**
	 * Property holding the currently selected destination unit.
	 * This is the {@link Unit} we'll be converting values to.
	 * @implNote holded {@link Unit} shall be part of {@link #destinationUnits}
	 * @implNote this property can evt be bound to a
	 * {@link javafx.scene.control.ComboBox#valueProperty()} so that changes in
	 * the bounded combobox can be reflected in this property.
	 */
	private ObjectProperty<Unit<Double>> destinationUnit;

	/**
	 * Property holding the order to apply when sorting destination units
	 * @implNote this property can evt be bound to a
	 * {@link javafx.scene.control.ComboBox#valueProperty()} so that changes in
	 * the bounded combobox can be reflected in this property.
	 * @see SortOrder
	 */
	private ObjectProperty<SortOrder> destinationSortOrder;

	/**
	 * Property indicating whether {@link #sourceUnit} and {@link #destinationUnit}
	 * are exchangeable : Whenever {@link #destinationUnit} is also
	 * {@link Setable}, then source and destination are NOT unexchangeable.
	 * This property needs to be updated each time {@link #destinationUnit} is
	 * changed.
	 * @implNote this property can be bound to the {@link Node#disableProperty()}
	 * of the exchange button in UI.
	 * @see #setSourceUnit(Unit)
	 * @see #setDestinationUnit(Unit)
	 */
	private BooleanProperty unexchangeableUnits;

	/**
	 * The text property containing the text to parse to obtain
	 * source unit value
	 * @implNote This property can be bound to a
	 * {@link javafx.scene.control.TextField#textProperty()} in order for
	 * changes in UI to be reflected in this model
	 */
	private StringProperty inputText;

	/**
	 * The text property containing the text obtained after value conversion
	 * from {@link #sourceUnit} to {@link #destinationUnit}
	 * @implNote this property shall be bound to a
	 * {@link javafx.scene.control.Label} in order for changes in this model to
	 * be reflected in UI.
	 */
	private StringProperty outputText;

	/**
	 * Constructor from a single type of measure
	 * @param type the type of units to use
	 * @throws ParseException if one of the constructed units can't parse its format string
	 * @see SimpleObjectProperty
	 * @see SimpleListProperty
	 * @see SimpleStringProperty
	 * @see #applyMeasureType()
	 * @see #applyUnitListOrder(List, SortOrder)
	 */
	public Converter(MeasureType type) throws ParseException
	{
		logger = LoggerFactory.getParentLogger(getClass(), null, Level.INFO);

		measureType = new SimpleObjectProperty<MeasureType>(type);

		sourceUnits = FXCollections.<Unit<Double>>observableArrayList();
		sourceUnit = new SimpleObjectProperty<Unit<Double>>();
		sourceSortOrder = new SimpleObjectProperty<SortOrder>(SortOrder.FACTOR_ASCENDING);

		destinationUnits = FXCollections.<Unit<Double>>observableArrayList();
		destinationUnit = new SimpleObjectProperty<Unit<Double>>();
		destinationSortOrder = new SimpleObjectProperty<SortOrder>(SortOrder.FACTOR_ASCENDING);

		unexchangeableUnits = new SimpleBooleanProperty(false);

		inputText = new SimpleStringProperty();
		outputText = new SimpleStringProperty();

		applyMeasureType();
		applyUnitListOrder(sourceUnits, sourceSortOrder.get());
		applyUnitListOrder(destinationUnits, destinationSortOrder.get());
		selectFirstUnits();
	}

	/**
	 * Convenience method to set source and destination units.
	 * Source unit is set to be the first available unit.
	 * Destination unit is set to be the first unit != source unit (iff possible)
	 */
	protected void selectFirstUnits()
	{
		/*
		 * Set source unit as 1st unit of #sourceUnits
		 */
		Unit<Double> source = null;
		if (!sourceUnits.isEmpty())
		{
			source = sourceUnits.get(0);
			sourceUnit.set(source);
		}

		/*
		 * Set destination unit as first unit of #destinationUnits != from
		 * sourceUnit.
		 * If possible, otherwise set it to source unit.
		 */
		if (!destinationUnits.isEmpty())
		{
			for (Unit<Double> unit : destinationUnits)
			{
				if (!unit.equals(source))
				{
					destinationUnit.set(unit);
					break;
				}
			}
			if (destinationUnit.get() == null)
			{
				destinationUnit.set(source);
			}
		}
	}

	/**
	 * Accessor to the measures type property
	 * @return the measures type property
	 */
	public final ObjectProperty<MeasureType> measureTypeProperty()
	{
		return measureType;
	}

	/**
	 * Current measures type accessor
	 * @return the current measures type
	 */
	public MeasureType getMeasureType()
	{
		return measureType.get();
	}

	/**
	 * Current measures type setter.
	 * Sets a new type in {@link #measureType}, then clears {@link #sourceUnits} and {@link #destinationUnits}
	 * @param type the type to set
	 * @throws ParseException if one of the created units can't parse its format
	 * @see #applyMeasureType()
	 */
	public void setMeasureType(MeasureType type) throws ParseException
	{
		/*
		 * The property is changed only if it is not already bound.
		 * Otherwise it has already been changed by the binding
		 */
		if (!measureType.isBound())
		{
			measureType.set(type);
		}
		applyMeasureType();
	}

	/**
	 * Apply the chosen {@link #measureType} by clearing {@link #sourceUnits} and
	 * {@link #destinationUnits} and recreating it.
	 * Units obtained with {@link UnitsFactory#getUnits(ObjectProperty)} are set into
	 * {@link #sourceUnits} (only if setable) and {@link #destinationUnits}.
	 * Also reapply ordering on both lists.
	 * @throws ParseException if one of the created units can't parse its format
	 * @see #clear()
	 * @see UnitsFactory#getUnits(ObjectProperty)
	 * @see #applyUnitListOrder(List, SortOrder)
	 */
	public void applyMeasureType() throws ParseException
	{

		/*
		 * TODO applyMeasureType
		 * 	- clear()
		 * 	- clears #sourceUnits
		 * 	- clears #destinationUnits
		 * 	- then use UnitsFactory.getUnits(...) to get units corresponding to measureType's value
		 * 	- add these units to #sourceUnits (only if they are setable)
		 * 	- add these units to #destinationUnits
		 * 	- reapply #sourceSortOrder's value to sort #sourceUnits
		 * 	- reapply #destinationSortOrder's value to sort #destinationUnits
		 * 	- selectFirstUnits
		 * Caution : Clearing units list might trigger comboboxes changes
		 * onAction... in UI where the currently selected elt in these comboboxes
		 * might be null since there is no more elements.
		 */

		clear();
		sourceUnits.clear();
		destinationUnits.clear();
		Set<Unit<Double>> unit=UnitsFactory.getUnits(measureType.get());
		unit.forEach(x -> {
			if(x.isSetable()) {
				sourceUnits.add(x);
			}
			destinationUnits.add(x);
		});
		applyUnitListOrder(sourceUnits,sourceSortOrder.get());
		applyUnitListOrder(destinationUnits,destinationSortOrder.get());
		selectFirstUnits();
	}

	/**
	 * Accessor to source units
	 * @return the source units
	 */
	public ObservableList<Unit<Double>> getSourceUnits()
	{
		return sourceUnits;
	}

	/**
	 * Souce Unit property accessor
	 * @return the source unit property
	 * @implNote This property will typically be bound to a
	 * {@link javafx.scene.control.ComboBox#valueProperty()} so that changes in
	 * the combobox will be reflected in this source unit property.
	 */
	public final ObjectProperty<Unit<Double>> sourceUnitProperty()
	{
		return sourceUnit;
	}

	/**
	 * Accessor to the currently selected source unit
	 * @return the currently selected source unit
	 */
	public static final Unit<Double> getSourceUnit()
	{
		return sourceUnit.get();
	}

	/**
	 * Source Unit setter
	 * @param sourceUnit the new source unit to set
	 * @throws IllegalArgumentException if the provided unit is not already part
	 * of {@link #sourceUnits}
	 * @implSpec if {@link #sourceUnit} is not bound, then the provided
	 * sourceUnit is set (otherwise it has already been set through properties
	 * binding).
	 * @see #applySourceUnit()
	 */
	public final void setSourceUnit(final Unit<Double> sourceUnit)
		throws IllegalArgumentException
	{
		if (!sourceUnits.contains(sourceUnit))
		{
			throw new IllegalArgumentException("Unexpected provided unit: "
			    + sourceUnit);
		}
		if (!this.sourceUnit.isBound())
		{
			this.sourceUnit.set(sourceUnit);
		}
		applySourceUnit();
	}

	/**
	 * Apply actions associated to {@link #sourceUnit} change.
	 * if {@link #inputText} is not empty then it shall be parsed to
	 * obtain source unit's value and conversion to destination unit shall be
	 * performed.
	 * @see #sourceUnit
	 * @see #inputText
	 * @see #applyInputText()
	 * @see #convert()
	 */
	public void applySourceUnit()
	{
		Unit<Double> source = sourceUnit.get();
		String input = inputText.get();
		if (input != null)
		{
			if (!input.isEmpty())
			{
				boolean parsed = applyInputText();
				if (parsed)
				{
					/*
					 * TODO applySourceUnit()
					 * 	- apply source formatted value to #inputText
					 * 	- then try to convert
					 * 		- logger.severe(...) if conversion is not possible
					 */
					inputText.setValue(input);
					convert();
					logger.severe("pas possible de convertire");
					inputText.set("Not ready yet");
				}
			}
		}
	}

	/**
	 * Accessor to the source units ordering property
	 * @return the source units ordering property
	 */
	public final ObjectProperty<SortOrder> sourceOrderProperty()
	{
		return sourceSortOrder;
	}

	/**
	 * Accessor to the current source units sorting order
	 * @return the current source units sorting order
	 */
	public SortOrder getSourceSortOrder()
	{
		return sourceSortOrder.get();
	}

	/**
	 * Source units order setter
	 * @param order the new source units order to set
	 * @implSpec Shall trigger a new sort operation on {@link #sourceUnits}
	 * @see #applySourceSortOrder()
	 */
	public void setSourceSortOrder(SortOrder order)
	{
		/*
		 * Caution sourceSortOrder can only be "set" iff not already bound
		 */
		if (!sourceSortOrder.isBound())
		{
			sourceSortOrder.set(order);
		}
		applySourceSortOrder();
	}

	/**
	 * Apply actions related to {@link #sourceSortOrder} changes:
	 * {@link #sourceUnits} shall be re-sorted according to
	 * {@link #sourceSortOrder}
	 * @see #applyUnitListOrder(List, SortOrder)
	 */
	public void applySourceSortOrder()
	{
		SortOrder order = sourceSortOrder.get();
		if (order != null)
		{
			applyUnitListOrder(sourceUnits, order);
		}
	}

	/**
	 * Sort the provided {@link Unit} list with provided {@link SortOrder}
	 * @param units the {@link Unit} {@link List} to sort
	 * @param order the order to use when sorting the {@link Unit} {@link List}
	 * @see Unit#setOrder(SortOrder)
	 * @see List#sort(java.util.Comparator)
	 * @see Unit#compareTo(Unit)
	 */
	private void applyUnitListOrder(List<Unit<Double>> units, SortOrder order)
	{
		Unit.setOrder(order);
		if (!units.isEmpty())
		{
			units.sort(Unit::compareTo); // Unit::compareTo is a method reference
		}
	}

	/**
	 * Accessor to destination units
	 * @return destination units
	 */
	public ObservableList<Unit<Double>> getdestinationUnits()
	{
		return destinationUnits;
	}

	/**
	 * Accessor to the destination unit property
	 * @return the destination unit property
	 * @implNote This property will typically be bound to a
	 * {@link javafx.scene.control.ComboBox#valueProperty()} so that changes in
	 * the combobox will be reflected in this destination unit property.
	 */
	public final ObjectProperty<Unit<Double>> destinationUnitProperty()
	{
		return destinationUnit;
	}

	/**
	 * Accessor to the currently selected destination unit
	 * @return the currently selected destination unit
	 */
	public final Unit<Double> getDestinationUnit()
	{
		return destinationUnit.get();
	}

	/**
	 * Destination Unit setter
	 * @param destinationUnit the new destination unit to set
	 * @throws IllegalArgumentException if the provided unit is not part of
	 * {@link #destinationUnits}
	 * @implSpec if {@link #destinationUnit} is not already bound it has been
	 * set to the provided destination unit, otherwise it has already been
	 * changed through properties binding.
	 * @see #applyDestinationUnit()
	 */
	public final void setDestinationUnit(final Unit<Double> destinationUnit)
		throws IllegalArgumentException
	{
		if (!destinationUnits.contains(destinationUnit))
		{
			throw new IllegalArgumentException("Unexpected provided unit: " + destinationUnit);
		}
		if (!this.destinationUnit.isBound())
		{
			this.destinationUnit.set(destinationUnit);
		}

		applyDestinationUnit();
	}

	/**
	 * Apply actions asociated to {@link #destinationUnit} changes.
	 * If {@link #sourceUnit} has a value, then conversion is performed.
	 * Update {@link #unexchangeableUnits} according to the
	 * chosen {@link #destinationUnit}
	 */
	public void applyDestinationUnit()
	{
		Unit<Double> destination = destinationUnit.get();
		if (destination != null)
		{
			unexchangeableUnits.set(!(destination.isSetable()));
//			logger.info("Unexchangeable property set to " + unexchangeableUnits.get());
		}

		Unit<Double> source = sourceUnit.get();
		if (source != null)
		{
			if (source.hasValue())
			{
				convert();
			}
		}
	}

	/**
	 * Accessor to the destination units ordering property
	 * @return the destination units ordering property
	 */
	public final ObjectProperty<SortOrder> destinationOrderProperty()
	{
		return destinationSortOrder;
	}

	/**
	 * Acessor to the current destination units sorting order
	 * @return the current destination units sorting order
	 */
	public SortOrder getDestinationSortOrder()
	{
		return destinationSortOrder.get();
	}

	/**
	 * Destination units order setter
	 * @param order the new destination units order to set
	 * @implSpec Shall trigger a new sort operation on {@link #destinationUnits}
	 * @see #applyUnitListOrder(List, SortOrder)
	 */
	public void setDestinationSortOrder(SortOrder order)
	{
		if (!destinationSortOrder.isBound())
		{
			destinationSortOrder.set(order);
		}
		applyDestinationSortOrder();
	}

	/**
	 * Apply actions related to {@link #destinationSortOrder} changes:
	 * {@link #destinationUnits} list shall be re-sorted according to
	 * {@link #destinationSortOrder} value.
	 * @see #applyUnitListOrder(List, SortOrder)
	 */
	public void applyDestinationSortOrder()
	{
		SortOrder order = destinationSortOrder.get();
		if (order != null)
		{
			applyUnitListOrder(destinationUnits, order);
		}
	}

	/**
	 * Accessor to the boolean property indicating source and destination units
	 * can't be exchanged
	 * @return the {@link #unexchangeableUnits} property
	 */
	public BooleanProperty unexchangeableUnitsProperty()
	{
		return unexchangeableUnits;
	}

	/**
	 * Accessor to text property containing input value to parse
	 * @return the text property
	 */
	public StringProperty inputTextProperty()
	{
		return inputText;
	}

	/**
	 * Accessor to text property content
	 * @return the content of the text porperty
	 */
	public String getInputText()
	{
		return inputText.get();
	}

	/**
	 * Set text in input text property (iff not already bound)
	 * @param text the text to set
	 */
	public void setInputText(String text)
	{
		if (!inputText.isBound())
		{
			inputText.set(text);
		}
		else
		{
			logger.severe("can't set text on already bound input text property");
		}
		applyInputText();
	}

	/**
	 * Apply actions related to {@link #inputText} changes:
	 * Parse {@link #inputText} to retrieve value for {@link #sourceUnit}
	 * @return true if {@link #inputText} text has been parsed and value set into
	 * {@link #sourceUnit}
	 * @see #parseInputText(String)
	 */
	public boolean applyInputText()
	{
		String text = inputText.get();
		if (text != null)
		{
			return parseInputText(text);
		}
		else
		{
			logger.warning("Input text null content");
		}
		return false;
	}

	/**
	 * Parse text to obtain double value to store in {@link #sourceUnit}
	 * Once value has been set into {@link #sourceUnit}, {@link #inputText} is
	 * re-formated once again with {@link #sourceUnit}'s value.
	 * @param text the text to parse
	 * @return true if the provided text has been parsed and value set into
	 * {@link #sourceUnit}
	 */
	protected boolean parseInputText(String text)
	{
		if (text.isEmpty())
		{
			logger.severe("Empty text to parse");
			return false;
		}
		double value = 0.0;
		try
		{
			value = Double.parseDouble(text);
		}
		catch (NumberFormatException e)
		{
			logger.severe("Unable to parse \"" + text + "\" to double :" + e.getLocalizedMessage());
			if (!inputText.isBound())
			{
				inputText.set(null);
			}
			return false;
		}
		Unit<Double> source = sourceUnit.get();
		if (source != null)
		{
			source.setValue(value);
			if (!inputText.isBound())
			{
				inputText.set(source.formatValue());
			}
			return true;
		}
		return false;
	}

	/**
	 * Convert source unit value into destination unit value
	 * @throws IllegalStateException if this conversion can't be performed. If
	 * {@link #sourceUnit} is null or doesn't have any value
	 * @throws IllegalStateException if {@link #sourceUnit} is null,
	 * if {@link #sourceUnit} has no values
	 * if {@link #destinationUnit} is null
	 */
	public void convert() throws IllegalStateException
	{
		/*
		 * TODO convert()
		 * 	- Get source unit (throw IllegalStateException if null)
		 * 	- Check source has a value (throw IllegalStateException no available value)
		 * 	- Get destination unit (throw IllegalStateException if null)
		 * 	- convert value from source to destination
		 * 	- format #outputText with formatted value from destination
		 */
		Unit<Double> source = sourceUnit.get();
		if(source == null){
			throw new IllegalStateException("null source unit");
		}
		if(!source.hasValue()){
			throw new IllegalStateException("no available value");
		}
		Unit<Double> destination = destinationUnit.get();
		if (destination == null)
		{
			throw new IllegalStateException("null destination unit");
		}
		destination.convertValueFrom(source);
		outputText.set(destination.formatValue());
	}

	/**
	 * Accessor to the property holding the output text
	 * @return the output text property
	 */
	public StringProperty outputTextProperty()
	{
		return outputText;
	}

	/**
	 * Accessor to the output text
	 * @return the output text
	 */
	public String getOutputText()
	{
		return outputText.get();
	}

	/**
	 * Switch {@link #sourceUnit} and {@link #destinationUnit} if possible
	 * @throws NullPointerException if either source or destination units are null
	 */
	public void switchUnits()
	{
		/*
		 * TODO switchUnits()
		 * 	- if source is null throw NullPointerException
		 * 	- if destination is null throw NullPointerException
		 * 	- according to #unexchangeableUnits's value
		 * 		- set #sourceUnit as destination
		 * 		- set #destinationUnit as source
		 * 		- logger.warning if not possible
		 */
		Unit<Double> source = sourceUnit.get();
		if(source == null){
			throw new NullPointerException("source is null");
		}
		Unit<Double> destination = destinationUnit.get();
		if(destination == null){
			throw new NullPointerException("destination is null");
		}
		if(!unexchangeableUnits.get()){
			sourceUnit.set(destination);
			destinationUnit.set(source);
		}
		else {
			logger.warning("source and destination are not exchangeable");
		}
	}

	/**
	 * Clears {@link #inputTextProperty()}, {@link #outputTextProperty()}
	 * as well as {@link #sourceUnit} and {@link #destinationUnits} values
	 */
	public void clear()
	{
		/*
		 * TODO clear()
		 * 	- clear inputText (iff not bound)
		 * 	- clear sourceUnit's value iff is has a value
		 * 	- clear outputText (iff not bound)
		 * 	- clear destinationUnit's value iff it has a value
		 */
		Unit<Double> source = sourceUnit.get();
		if(!inputText.isBound()){
			inputText.set("");
		}
		if(source!=null && source.hasValue()){
			source.clearValue();
		}
		Unit<Double> destination = destinationUnit.get();
		if(!outputText.isBound()){
			outputText.set("");
		}
		if(destination!=null && destination.hasValue()){
			destination.clearValue();
		}
	}

	/**
	 * String representation
	 * @return a new string representing this converter
	 */
	@Override
	public String toString()
	{
		StringBuilder builder = new StringBuilder();
		builder.append("Source Units:\n");
		for (Unit<Double> unit : sourceUnits)
		{
			builder.append("[");
			builder.append(unit.toString());
			builder.append("]\n");
		}
		Unit<Double> source = sourceUnit.get();
		builder.append("source: ");
		if (source != null)
		{
			builder.append(source.toString());
		}

		builder.append("\nDestination Units:\n");
		for (Unit<Double> unit : destinationUnits)
		{
			builder.append("[");
			builder.append(unit.toString());
			builder.append("]\n");
		}

		Unit<Double> destination = destinationUnit.get();
		builder.append("destination: ");
		if (destination != null)
		{
			builder.append(destination);
		}
		return builder.toString();
	}
}

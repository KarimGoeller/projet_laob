/**
 *
 */
package application.cells;

import measures.MeasureType;

/**
 * Custom Cell to display {@link MeasureType}s in a {@link javafx.scene.control.ComboBox}
 * @author davidroussel
 * Examples:
 * {@code myComboBox.setButtonCell(new MesuresCell());}
 * {@code myComboBox.setCellFactory(combobox -> new MesuresCell());}
 */
public class MeasuresCell extends CustomCell<MeasureType>
{
	/**
	 * Default constructor
	 * Loads FXML file MeasuresCell.fxml to layout the cell and binds controller
	 * @implSpec The controller specified in MeasuresCell.fxml is a
	 * {@link MeasuresCellController}
	 */
	public MeasuresCell()
	{
		super("MeasuresCell.fxml");
	}
}

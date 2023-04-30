package application.cells;


import javafx.scene.image.Image;
import measures.units.SortOrder;
import utils.IconFactory;

public class SortOrderCellController extends AbstractCustomCellController<SortOrder>{

    private static Image nameAscendingIcon = IconFactory.getIcon("alphabetical_sorting");
    private static Image nameDescendingIcon = IconFactory.getIcon("alphabetical_sorting_2");
    private static Image factorAscendingIcon = IconFactory.getIcon("generic_sorting_2");
    private static Image factorDescendingIcon = IconFactory.getIcon("generic_sorting");

    public static Image getIconFromSort(SortOrder type)
    {
        switch(type)
        {
            case NAME_ASCENDING:
                return nameAscendingIcon;
            case NAME_DESCENDING:
                return nameDescendingIcon;
            case FACTOR_ASCENDING:
                return factorAscendingIcon;
            case FACTOR_DESCENDING:
                return factorDescendingIcon;
            default:
                throw new AssertionError("Unknown SortOrder: " + type);
        }
    }

    @Override
    public void setIcon(SortOrder value) {
        setImage(getIconFromSort(value));
    }
}

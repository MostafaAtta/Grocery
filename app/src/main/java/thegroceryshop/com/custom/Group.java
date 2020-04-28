package thegroceryshop.com.custom;

import com.thoughtbot.expandablerecyclerview.models.ExpandableGroup;
import java.util.List;

public class Group extends ExpandableGroup<Child> {

  private int iconResId;

  public Group(String title, List<Child> items, int iconResId) {
    super(title, items);
    this.iconResId = iconResId;
  }

  public int getIconResId() {
    return iconResId;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof Group)) return false;

    Group genre = (Group) o;

    return getIconResId() == genre.getIconResId();

  }

  @Override
  public int hashCode() {
    return getIconResId();
  }
}


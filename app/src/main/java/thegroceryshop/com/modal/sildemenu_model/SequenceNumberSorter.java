package thegroceryshop.com.modal.sildemenu_model;

import java.util.Comparator;

public class SequenceNumberSorter implements Comparator<NestedCategoryItem> {
        @Override
        public int compare(NestedCategoryItem o1, NestedCategoryItem o2) {
            if(Integer.parseInt(o1.getSequenceNumber()) > Integer.parseInt(o2.getSequenceNumber())){
                return 1;
            }else if(Integer.parseInt(o1.getSequenceNumber()) == Integer.parseInt(o2.getSequenceNumber())){
                return 0;
            }else{
                return -1;
            }
        }
    }
package thegroceryshop.com.dialog;

import android.content.Context;
import android.content.DialogInterface;
import androidx.appcompat.app.AlertDialog;
import android.widget.ArrayAdapter;

import java.util.ArrayList;
import java.util.List;

import thegroceryshop.com.R;
import thegroceryshop.com.adapter.MultiSelectDialogAdapter;
import thegroceryshop.com.adapter.SelectDialogAdapter;
import thegroceryshop.com.modal.Brand;

public class SelectItemUtil {

    /**
     * Show a dialog to select from multiple options.
     *
     * @param context              ui context
     * @param title                dialog title
     * @param list                 list of items to be shown
     * @param itemSelectedListener listener
     */

    public static void showMultiSelectDialog(Context context, CharSequence title, final List<KeyValue> list, final ItemSelectedListener itemSelectedListener) {

        // make adapter
        List<String> displayValues = new ArrayList<>();
        for (KeyValue keyValue : list) {
            displayValues.add(keyValue.getKey().toString());
        }



        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, displayValues);

        // listener for selection
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(itemSelectedListener != null){
                    itemSelectedListener.onItemSelected((list.get(i)).getKey().toString(), list.get(i).getValue().toString());
                }
                dialogInterface.dismiss();
            }
        };

        // show alert dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setAdapter(adapter, listener);
        builder.show();
    }

    /**
     * Show a dialog to select from multiple options.
     *
     * @param context              ui context
     * @param title                dialog title
     * @param list                 list of items to be shown
     * @param itemSelectedListener listener
     */

    public static void showDialogWithKeys(Context context, CharSequence title, final List<KeyValue> list, final ItemSelectedListener itemSelectedListener) {

        // make adapter
        List<String> displayValues = new ArrayList<>();
        for (KeyValue keyValue : list) {
            displayValues.add(keyValue.getValue().toString());
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, displayValues);

        // listener for selection
        final DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(itemSelectedListener != null){
                    itemSelectedListener.onItemSelected((list.get(i)).getKey().toString(), list.get(i).getValue().toString());
                }
                dialogInterface.dismiss();
            }
        };

        // show alert dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setAdapter(adapter, listener);
        builder.show();
    }

    public static ArrayList<Brand> cloneList(ArrayList<Brand> list) {
        if(list != null && list.size() >0) {
            ArrayList<Brand> clone = new ArrayList<>(list.size());
            for (KeyValuePair item : list) {
                try {
                    clone.add((Brand) item.clone());
                } catch (CloneNotSupportedException e) {
                    e.printStackTrace();
                    return null;
                }
            }
            return clone;
        }
        return null;
    }

    public static void showMultiSelectDialog(Context context, CharSequence title, final ArrayList<Brand> list, final ObjectSelectedListener objectSelectedListener) {

        final ArrayList<Brand> prevlist = cloneList(list);

        // make adapter
        final MultiSelectDialogAdapter multiSelectDialogAdapter = new MultiSelectDialogAdapter(context, list);
        multiSelectDialogAdapter.setOnItemSelectedListener(new MultiSelectDialogAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {
                list.get(position).setSelected(!list.get(position).isSelected());
                multiSelectDialogAdapter.notifyDataSetChanged();
            }
        });

        // show alert dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setAdapter(multiSelectDialogAdapter, null);
        builder.setPositiveButton(context.getString(R.string.apply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(objectSelectedListener != null){
                    objectSelectedListener.onObjectSelected(null, null, null);
                }
            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(prevlist != null){
                    list.clear();
                    list.addAll(prevlist);
                }
            }
        });


        builder.show();
    }

    public static void showSelectDialog(Context context, CharSequence title, final ArrayList<Brand> list, final ObjectSelectedListener objectSelectedListener) {

        final ArrayList<Brand> prevlist = cloneList(list);

        // make adapter
        final SelectDialogAdapter selectDialogAdapter = new SelectDialogAdapter(context, list);
        selectDialogAdapter.setOnItemSelectedListener(new SelectDialogAdapter.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int position) {

                for(int i=0; i<list.size(); i++){
                    if(i == position){
                        list.get(i).setSelected(true);
                    }else{
                        list.get(i).setSelected(false);
                    }
                }
                selectDialogAdapter.notifyDataSetChanged();
            }
        });

        // show alert dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setAdapter(selectDialogAdapter, null);
        builder.setPositiveButton(context.getString(R.string.apply), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(objectSelectedListener != null){
                    objectSelectedListener.onObjectSelected(null, null, null);
                }
            }
        });
        builder.setNegativeButton(context.getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                if(prevlist != null){
                    list.clear();
                    list.addAll(prevlist);
                }
            }
        });


        builder.show();
    }

    /**
     * Listener for item selection
     */
    public interface ItemSelectedListener {
        void onItemSelected(String key, String value);
    }

    /**
     * Listener for item selection
     */
    public interface ObjectSelectedListener {
        void onObjectSelected(String key, Object object, String diaplayValue);
    }

    /**
     * Interface for returning kay and value of an item
     *
     * @param <T>
     */
    public interface KeyValue<T> {

        /**
         * Method to return item's key
         *
         * @return key
         */
        T getKey();

        /**
         * Method to return item's value
         *
         * @return value
         */
        T getValue();
    }

    /**
     * Interface for returning kay and value of an item
     *
     * @param <T>
     */
    public interface KeyValuePair<T> extends Cloneable {

        /**
         * Method to return item's key
         *
         * @return key
         */
        String getKey();

        /**
         * Method to return item's value
         *
         * @return value
         */
        T getValue();

        /**
         * Method to return item's display value
         *
         * @return value
         */
        String getString();

        boolean isSelected();

        void setSelected(boolean isSelcted);

        Object clone() throws CloneNotSupportedException;
    }
}
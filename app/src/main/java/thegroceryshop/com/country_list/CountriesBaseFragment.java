package thegroceryshop.com.country_list;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;
import android.util.SparseArray;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;

import com.google.i18n.phonenumbers.PhoneNumberUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.TreeSet;

import thegroceryshop.com.R;
import thegroceryshop.com.application.OnlineMartApplication;
import thegroceryshop.com.country_list.model.CountryPhoneCode;


/*
 * Created by umeshk on 12/12/2016.
 */

public abstract class CountriesBaseFragment extends Fragment {

    private ArrayList<CountryPhoneCode> list_countries = new ArrayList<>();
    private ArrayList<CountryPhoneCode> saved_list_countries = new ArrayList<>();
    protected static final TreeSet<String> CANADA_CODES = new TreeSet<String>();

    static {
        CANADA_CODES.add("204");
        CANADA_CODES.add("236");
        CANADA_CODES.add("249");
        CANADA_CODES.add("250");
        CANADA_CODES.add("289");
        CANADA_CODES.add("306");
        CANADA_CODES.add("343");
        CANADA_CODES.add("365");
        CANADA_CODES.add("387");
        CANADA_CODES.add("403");
        CANADA_CODES.add("416");
        CANADA_CODES.add("418");
        CANADA_CODES.add("431");
        CANADA_CODES.add("437");
        CANADA_CODES.add("438");
        CANADA_CODES.add("450");
        CANADA_CODES.add("506");
        CANADA_CODES.add("514");
        CANADA_CODES.add("519");
        CANADA_CODES.add("548");
        CANADA_CODES.add("579");
        CANADA_CODES.add("581");
        CANADA_CODES.add("587");
        CANADA_CODES.add("604");
        CANADA_CODES.add("613");
        CANADA_CODES.add("639");
        CANADA_CODES.add("647");
        CANADA_CODES.add("672");
        CANADA_CODES.add("705");
        CANADA_CODES.add("709");
        CANADA_CODES.add("742");
        CANADA_CODES.add("778");
        CANADA_CODES.add("780");
        CANADA_CODES.add("782");
        CANADA_CODES.add("807");
        CANADA_CODES.add("819");
        CANADA_CODES.add("825");
        CANADA_CODES.add("867");
        CANADA_CODES.add("873");
        CANADA_CODES.add("902");
        CANADA_CODES.add("905");
    }

    protected SparseArray<ArrayList<CountryPhoneCode>> mCountriesMap = new SparseArray<ArrayList<CountryPhoneCode>>();

    protected PhoneNumberUtil mPhoneNumberUtil = PhoneNumberUtil.getInstance();
    protected RecyclerView recyl_countries_list;

    protected String mLastEnteredPhone;
    protected EditText edt_search;
    protected CountryAdapter countriesAdapter;

    public static String result_country = "result_country";

    protected CountryAdapter.OnItemSelectedListener mOnItemSelectedListener = new CountryAdapter.OnItemSelectedListener() {
        @Override
        public void onItemSelected(int position) {

            if(list_countries != null && list_countries.size() > 0){
                CountryPhoneCode c = list_countries.get(position);
                if (mLastEnteredPhone != null && mLastEnteredPhone.startsWith(c.getCountryCodeStr())) {
                    return;
                }

                Intent resultIntent = new Intent();
                resultIntent.putExtra(result_country, c);
                getActivity().setResult(Activity.RESULT_OK, resultIntent);
                getActivity().finish();
            }

        }
    };

    protected OnSearchTextChangedListener mOnSearchTextChangedListener = new OnSearchTextChangedListener() {
        @Override
        public void onTextChanged(String text) {

                mLastEnteredPhone = text;

                if(text != null && text.length() > 0){
                    if(list_countries != null){
                        list_countries.clear();
                        for(int i=0; i<saved_list_countries.size(); i++){
                            CountryPhoneCode country = saved_list_countries.get(i);
                            if(country.getName().toLowerCase().startsWith(text.toLowerCase())
                                    || country.getCountryCodeStr().toLowerCase().contains(text.toLowerCase())){
                                list_countries.add(country);
                            }
                        }
                        countriesAdapter.notifyDataSetChanged();
                    }
                }else {
                    list_countries.clear();
                    list_countries.addAll(saved_list_countries);
                    countriesAdapter.notifyDataSetChanged();
                }
        }
    };

    protected void initUI(View rootView) {
        recyl_countries_list = rootView.findViewById(R.id.countries_recyl_list);
        recyl_countries_list.setLayoutManager(new LinearLayoutManager(getActivity()));

        countriesAdapter = new CountryAdapter(getActivity(), list_countries, R.layout.lyt_country_adapter);
        recyl_countries_list.setAdapter(countriesAdapter);

        countriesAdapter.setOnItemSelectedListener(mOnItemSelectedListener);

        edt_search = rootView.findViewById(R.id.countries_edt_search);
        edt_search.addTextChangedListener(new CustomPhoneNumberFormattingTextWatcher(mOnSearchTextChangedListener));
        edt_search.setImeOptions(EditorInfo.IME_ACTION_NONE);

    }

    protected void initCodes(Context context) {
        new AsyncPhoneInitTask(context, edt_search.getText().toString()).execute();
    }

    protected class AsyncPhoneInitTask extends AsyncTask<Void, Void, ArrayList<CountryPhoneCode>> {

        private Context mContext;
        private String search_string;

        public AsyncPhoneInitTask(Context context, String search_string) {
            mContext = context;
            this.search_string = search_string;
        }

        @Override
        protected ArrayList<CountryPhoneCode> doInBackground(Void... params) {
            ArrayList<CountryPhoneCode> data = new ArrayList<CountryPhoneCode>(233);
            BufferedReader reader = null;
            try {

                if(OnlineMartApplication.mLocalStore.getAppLangId().equalsIgnoreCase("1")){
                    reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries.dat"), "UTF-8"));
                }else{
                    reader = new BufferedReader(new InputStreamReader(mContext.getApplicationContext().getAssets().open("countries_arabic.dat"), "UTF-8"));
                }

                // do reading, usually loop until end of file reading
                String line;
                int i = 0;
                while ((line = reader.readLine()) != null) {
                    //process line
                    CountryPhoneCode c = new CountryPhoneCode(mContext, line, i);
                    data.add(c);
                    ArrayList<CountryPhoneCode> list = mCountriesMap.get(c.getCountryCode());
                    if (list == null) {
                        list = new ArrayList<CountryPhoneCode>();
                        mCountriesMap.put(c.getCountryCode(), list);
                    }
                    list.add(c);
                    i++;
                }
            } catch (IOException e) {
                //log the exception
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        //log the exception
                    }
                }
            }
            if (!TextUtils.isEmpty(search_string)) {
                return data;
            }
            String countryRegion = PhoneUtils.getCountryRegionFromPhone(mContext);
            int code = mPhoneNumberUtil.getCountryCodeForRegion(countryRegion);
            ArrayList<CountryPhoneCode> list = mCountriesMap.get(code);
            if (list != null) {
                for (CountryPhoneCode c : list) {
                    if (c.getPriority() == 0) {
                        break;
                    }
                }
            }
            return data;
        }

        @Override
        protected void onPostExecute(ArrayList<CountryPhoneCode> data) {
            list_countries.addAll(data);
            saved_list_countries.addAll(list_countries);
            countriesAdapter.notifyDataSetChanged();
        }
    }


}

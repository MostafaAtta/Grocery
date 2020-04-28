package thegroceryshop.com.country_list.model;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.Locale;

/**
 * Created by rohitg on 12/9/2016.
 */

public class CountryPhoneCode implements Parcelable{

    private String mName;
    private String mCountryISO;
    private int mCountryCode;
    private String mCountryCodeStr;
    private int mPriority;
    private int mResId;
    private int mNum;

    public CountryPhoneCode(Context context, String str, int num) {
        String[] data = str.split(",");
        mNum = num;
        mName = data[0];
        mCountryISO = data[1];
        mCountryCode = Integer.parseInt(data[2]);
        mCountryCodeStr = "+" + data[2];
        if (data.length > 3) {
            mPriority = Integer.parseInt(data[3]);
        }
        String fileName = String.format(Locale.ENGLISH, "f%03d", num);
        mResId = context.getApplicationContext().getResources().getIdentifier(fileName, "drawable", context.getApplicationContext().getPackageName());
    }

    protected CountryPhoneCode(Parcel in) {
        mName = in.readString();
        mCountryISO = in.readString();
        mCountryCode = in.readInt();
        mCountryCodeStr = in.readString();
        mPriority = in.readInt();
        mResId = in.readInt();
        mNum = in.readInt();
    }

    public static final Creator<CountryPhoneCode> CREATOR = new Creator<CountryPhoneCode>() {
        @Override
        public CountryPhoneCode createFromParcel(Parcel in) {
            return new CountryPhoneCode(in);
        }

        @Override
        public CountryPhoneCode[] newArray(int size) {
            return new CountryPhoneCode[size];
        }
    };

    public String getName() {
        return mName;
    }

    public String getCountryISO() {
        return mCountryISO;
    }

    public int getCountryCode() {
        return mCountryCode;
    }

    public String getCountryCodeStr() {
        return mCountryCodeStr;
    }

    public int getPriority() {
        return mPriority;
    }

    public int getResId() {
        return mResId;
    }

    public int getNum() {
        return mNum;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(mName);
        parcel.writeString(mCountryISO);
        parcel.writeInt(mCountryCode);
        parcel.writeString(mCountryCodeStr);
        parcel.writeInt(mPriority);
        parcel.writeInt(mResId);
        parcel.writeInt(mNum);
    }
}

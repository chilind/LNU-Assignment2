package ch223av.dv606.assignment2.MyCountriesCalendar;

import android.os.Parcel;
import android.os.Parcelable;

public class Visit implements Parcelable{
    private int mYear;
    private String mCountry;


    protected Visit(Parcel in) {
        mYear = in.readInt();
        mCountry = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mYear);
        dest.writeString(mCountry);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Visit> CREATOR = new Creator<Visit>() {
        @Override
        public Visit createFromParcel(Parcel in) {
            return new Visit(in);
        }

        @Override
        public Visit[] newArray(int size) {
            return new Visit[size];
        }
    };


    public String getCountry() {
        return mCountry;
    }

    public void setCountry(String country) {
        mCountry = country;
    }

    public int getYear() {
        return mYear;
    }

    public void setYear(int year) {
        mYear = year;
    }
}

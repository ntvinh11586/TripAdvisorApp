package DataPack;

/**
 * Created by vinh on 26/04/2016.
 */
public class LocationItem {
    public String mName;
    public String mAddress;
    public String mWebsite;
    public String mDescription;
    public String mPhone;
    public float mX, mY;

    public LocationItem(String name,
                        String address,
                        String website,
                        String description,
                        String phone,
                        float x, float y) {
        mName = name;
        mAddress = address;
        mWebsite = website;
        mDescription = description;
        mPhone = phone;
        mX = x; mY = y;
    }
}

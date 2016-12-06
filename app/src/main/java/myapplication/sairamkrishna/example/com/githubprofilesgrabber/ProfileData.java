package myapplication.sairamkrishna.example.com.githubprofilesgrabber;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by pc hp on 28.11.2016.
 */
public class ProfileData implements Parcelable{

    private String loginName;
    private String avatarSource;
    private String htmlUrl;




    public ProfileData(String loginName, String avatarSource,String htmlUrl)
    {
        this.loginName = loginName;
        this.avatarSource = avatarSource;
        this.htmlUrl = htmlUrl;
    }

    public ProfileData(Parcel input)
    {
        loginName = input.readString();
        avatarSource = input.readString();
        htmlUrl = input.readString();

    }


    public String getAvatarSource() {
        return avatarSource;
    }

    public void setAvatarSource(String avatarSource) {
        this.avatarSource = avatarSource;
    }

    public String getLoginName() {
        return loginName;
    }

    public void setLoginName(String loginName) {
        this.loginName = loginName;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public void setHtmlUrl(String htmlUrl) {
        this.htmlUrl = htmlUrl;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(loginName);
        dest.writeString(avatarSource);
        dest.writeString(htmlUrl);
    }

    public static final Parcelable.Creator<ProfileData> CREATOR
            = new Parcelable.Creator<ProfileData>() {
        public ProfileData createFromParcel(Parcel in) {
            return new ProfileData(in);
        }

        public ProfileData[] newArray(int size) {
            return new ProfileData[size];
        }
    };

}

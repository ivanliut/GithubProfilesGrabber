package myapplication.sairamkrishna.example.com.githubprofilesgrabber;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by pc hp on 28.11.2016.
 */
public class ProfileAdapter extends BaseAdapter implements ListAdapter{

    private List<ProfileData> profiles;
    private Context context;

    public ProfileAdapter(Context context,List<ProfileData> profiles) {
        this.profiles = profiles;
        this.context = context;
    }

    @Override
    public int getCount() {
        return profiles.size();
    }

    @Override
    public Object getItem(int position) {
        return profiles.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.list_profile_item, null);

        ProfileData profileData = profiles.get(position);
        String login = profileData.getLoginName();
        String avatar = profileData.getAvatarSource();
        String htmlUrl = profileData.getHtmlUrl();

        TextView textView = (TextView) view.findViewById(R.id.list_profile_login);
        TextView textViewHtmlUrl = (TextView) view.findViewById(R.id.list_profile_htmlUrl);


        textView.setText(login);
        textViewHtmlUrl.setText(htmlUrl);

        ImageView imageView = (ImageView) view.findViewById(R.id.imageView);
        Picasso.with(context).load(avatar).into(imageView);


        return view;
    }
}

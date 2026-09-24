package thanhdnh.ueh.edu.article_app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class UserProfileAdapter extends BaseAdapter {
  private ArrayList<UserProfile> userList;
  private Context context;

  public UserProfileAdapter(ArrayList<UserProfile> userList, Context context) {
    this.userList = userList;
    this.context = context;
  }

  @Override
  public int getCount() {
    return userList != null ? userList.size() : 0;
  }

  @Override
  public Object getItem(int position) {
    return userList.get(position);
  }

  @Override
  public long getItemId(int position) {
    return userList.get(position).getId();
  }

  @Override
  public View getView(int position, View convertView, ViewGroup parent) {
    android.util.Log.d("DEBUG_ADAPTER", "Đang vẽ item vị trí: " + position);

    final MyView dataitem;
    LayoutInflater inflater = LayoutInflater.from(context);

    if (convertView == null) {
      dataitem = new MyView();
      convertView = inflater.inflate(R.layout.user_disp_tpl, parent, false);
      dataitem.iv_photo = convertView.findViewById(R.id.imv_photo);
      dataitem.tv_caption = convertView.findViewById(R.id.tv_title);
      convertView.setTag(dataitem);
    } else {
      dataitem = (MyView) convertView.getTag();
    }

    UserProfile user = userList.get(position);

    if (user != null) {
      dataitem.tv_caption.setText(user.getUsername());

      // Kiểm tra url avatar
      if (user.getAvatar_url() != null && !user.getAvatar_url().trim().isEmpty()) {
        Picasso.get()
                .load(user.getAvatar_url())
                .placeholder(android.R.drawable.sym_def_app_icon) // Hiện icon mặc định nếu đang tải
                .error(android.R.drawable.ic_delete)             // Hiện icon nếu ảnh lỗi
                .into(dataitem.iv_photo);
      }
    }

    return convertView;
  }

  private static class MyView {
    ImageView iv_photo;
    TextView tv_caption;
  }
}
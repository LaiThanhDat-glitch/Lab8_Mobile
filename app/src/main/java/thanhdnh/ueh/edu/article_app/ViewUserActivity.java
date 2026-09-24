package thanhdnh.ueh.edu.article_app;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.squareup.picasso.Picasso;

public class ViewUserActivity extends AppCompatActivity {
  ImageView iv_avatar;
  TextView tv_username, tv_detail_description;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_view_user);

    // 1. Hiển thị thanh ActionBar và kích hoạt nút Back mũi tên
    if (getSupportActionBar() != null) {
      getSupportActionBar().setDisplayHomeAsUpEnabled(true);
      getSupportActionBar().setTitle("User Detail");
    }

    iv_avatar = findViewById(R.id.iv_detail);
    tv_username = findViewById(R.id.tv_detail_title);
    tv_detail_description = findViewById(R.id.tv_detail_description);

    int id = (int) getIntent().getLongExtra("id", 0);
    UserProfile user = UserData.getUserFromId(id);

    if (user != null) {
      if (user.getAvatar_url() != null && !user.getAvatar_url().isEmpty()) {
        Picasso.get()
                .load(user.getAvatar_url())
                .resize(400, 500)
                .centerCrop()
                .into(iv_avatar);
      }

      tv_username.setText(user.getUsername());

      String detailInfo = "Email: " + user.getEmail() + "\n\n"
              + "Description: " + user.getDesc() + "\n\n"
              + "Hobby: " + user.getHobby();
      tv_detail_description.setText(detailInfo);
    }
  }

  // 2. Bắt sự kiện click vào mũi tên Back trên thanh ActionBar
  @Override
  public boolean onOptionsItemSelected(@NonNull MenuItem item) {
    if (item.getItemId() == android.R.id.home) {
      finish(); // Đóng Activity hiện tại để quay về MainActivity
      return true;
    }
    return super.onOptionsItemSelected(item);
  }
}
package thanhdnh.ueh.edu.article_app;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
  public GridView gridview;

  private final AdapterView.OnItemClickListener onitemclick = new AdapterView.OnItemClickListener() {
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
      // Chuyển sang màn hình chi tiết ViewUserActivity (hoặc ViewProfileActivity tùy tên class bạn đặt ở bước trước)
      Intent intent = new Intent(MainActivity.this, ViewUserActivity.class);
      intent.putExtra("id", gridview.getAdapter().getItemId(position));
      startActivity(intent);
    }
  };

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    if (getSupportActionBar() != null) {
      getSupportActionBar().hide();
    }

    gridview = findViewById(R.id.gridview);

    if (gridview == null) {
      android.util.Log.e("DEBUG_APP", "LỖI: Không tìm thấy R.id.gridview trong activity_main.xml!");
      return;
    }

    gridview.setOnItemClickListener(onitemclick);

    // Lưu ý: Thay đường link URL nếu bạn có link json chứa UserProfile riêng
    String url = "https://gist.githubusercontent.com/LaiThanhDat-glitch/9b8d68879b5da365460a754bc3720f0e/raw/c67e2f3c6de5c74409eb4d30ee68110c3f876fcd/users.json";
    new UserData(this, gridview).loadData(url, this);
  }
}
package thanhdnh.ueh.edu.article_app;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.GridView;
import android.widget.Toast;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

public class UserData {
  public static UserList data;
  private Context context;
  private GridView gridview;

  public UserData(Context context, GridView gridview) {
    this.context = context;
    this.gridview = gridview;
  }

  // Tìm User theo id
  public static UserProfile getUserFromId(int id) {
    if (data == null || data.getUsers() == null) return null;
    for (UserProfile user : data.getUsers()) {
      if (user.getId() == id) {
        return user;
      }
    }
    return null;
  }

  // Tải dữ liệu JSON dùng DownloadWithProgress
  public void loadData(String url, Activity activity) {
    Handler mainHandler = new Handler(Looper.getMainLooper());

    DownloadWithProgress.download(url, context.getCacheDir(), mainHandler, new DownloadWithProgress.DownloadCallback() {
      @Override
      public void onProgress(int progress) {
        // Cập nhật tiến trình nếu có ProgressBar
      }

      @Override
      public void onSuccess(File file) {
        String jsonContent = readText(file);
        Log.d("USER_DATA", "JSON nhận được: " + jsonContent);

        Gson gson = new Gson();
        data = gson.fromJson(jsonContent, UserList.class);

// Thêm dòng log này để kiểm tra:
        Log.d("DEBUG_COUNT", "data: " + data + ", users: " + (data != null ? data.getUsers() : "null"));

        activity.runOnUiThread(() -> {
          if (data != null && data.getUsers() != null && !data.getUsers().isEmpty()) {
            Log.d("DEBUG_COUNT", "Số lượng user parse được: " + data.getUsers().size());
            UserProfileAdapter adapter = new UserProfileAdapter(data.getUsers(), activity);
            gridview.setAdapter(adapter);
          } else {
            Log.e("DEBUG_COUNT", "data.getUsers() BỊ NULL HOẶC RỖNG!");
          }
        });
      }

      @Override
      public void onError(Exception e) {
        Log.e("USER_DATA", "Lỗi tải file: " + e.getMessage());
        activity.runOnUiThread(() ->
                Toast.makeText(activity, "Lỗi kết nối mạng khi tải dữ liệu!", Toast.LENGTH_SHORT).show()
        );
      }
    });
  }

  public String readText(File file) {
    if (file == null || !file.exists()) return "";
    StringBuilder buffer = new StringBuilder();
    try (InputStream stream = new FileInputStream(file);
         BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
      String line;
      while ((line = reader.readLine()) != null) {
        buffer.append(line).append("\n");
      }
      return buffer.toString();
    } catch (Exception e) {
      e.printStackTrace();
    }
    return buffer.toString();
  }
}
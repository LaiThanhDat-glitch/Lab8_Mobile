package thanhdnh.ueh.edu.article_app;

import android.net.Uri;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.ProgressBar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;

public class DownloadWithProgress {
  public static String cached_file_path = "";

  public interface DownloadCallback {
    void onProgress(int progress);
    void onSuccess(File file);
    void onError(Exception e);
  }

  // Phương thức tải file có báo tiến trình (dùng chung cho cả JSON hoặc file bất kỳ)
  public static void download(String inputUrl, File storageDir, Handler mainHandler, DownloadCallback callback) {
    OkHttpClient client = new OkHttpClient();
    Request request = new Request.Builder().url(inputUrl).build();

    client.newCall(request).enqueue(new Callback() {
      @Override
      public void onFailure(Call call, IOException e) {
        if (mainHandler != null && callback != null) {
          mainHandler.post(() -> callback.onError(e));
        }
      }

      @Override
      public void onResponse(Call call, Response response) {
        if (!response.isSuccessful()) {
          if (mainHandler != null && callback != null) {
            mainHandler.post(() -> callback.onError(new IOException("Tải thất bại: HTTP " + response.code())));
          }
          return;
        }

        ResponseBody body = response.body();
        if (body == null) {
          if (mainHandler != null && callback != null) {
            mainHandler.post(() -> callback.onError(new IOException("Response body rỗng")));
          }
          return;
        }

        try {
          long totalBytes = body.contentLength();
          InputStream inputStream = body.byteStream();
          String contentType = response.header("Content-Type", "");
          String extension = getExtensionFromMimeType(contentType);

          File targetFile = File.createTempFile("download_", extension, storageDir);

          try (OutputStream outputStream = new FileOutputStream(targetFile)) {
            byte[] buffer = new byte[2048];
            long downloadedBytes = 0;
            int bytesRead;

            while ((bytesRead = inputStream.read(buffer)) != -1) {
              outputStream.write(buffer, 0, bytesRead);
              downloadedBytes += bytesRead;
              if (totalBytes > 0) {
                int progress = (int) ((downloadedBytes * 100) / totalBytes);
                if (mainHandler != null && callback != null) {
                  mainHandler.post(() -> callback.onProgress(progress));
                }
              }
            }
            outputStream.flush();
          }

          if (mainHandler != null && callback != null) {
            mainHandler.post(() -> callback.onSuccess(targetFile));
          }
        } catch (Exception e) {
          if (mainHandler != null && callback != null) {
            mainHandler.post(() -> callback.onError(e));
          }
        }
      }
    });
  }

  // Tải ảnh trực tiếp có hiển thị ProgressBar và đưa lên ImageView
  public static void downloadImageWithProgress(String inputUrl, Handler mainHandler, File storageDir, ProgressBar progressBar, ImageView imageView) {
    if (progressBar != null) {
      mainHandler.post(() -> {
        progressBar.setProgress(0);
        progressBar.setVisibility(ProgressBar.VISIBLE);
      });
    }

    download(inputUrl, storageDir, mainHandler, new DownloadCallback() {
      @Override
      public void onProgress(int progress) {
        if (progressBar != null) {
          progressBar.setProgress(progress);
        }
      }

      @Override
      public void onSuccess(File file) {
        cached_file_path = file.getAbsolutePath();
        if (imageView != null) {
          imageView.setImageURI(Uri.fromFile(file));
        }
        if (progressBar != null) {
          progressBar.setVisibility(ProgressBar.GONE);
        }
      }

      @Override
      public void onError(Exception e) {
        if (progressBar != null) {
          progressBar.setVisibility(ProgressBar.GONE);
        }
      }
    });
  }

  private static String getExtensionFromMimeType(String mimeType) {
    Map<String, String> mimeMap = new HashMap<>();
    mimeMap.put("image/jpeg", ".jpg");
    mimeMap.put("image/png", ".png");
    mimeMap.put("application/json", ".json");
    if (mimeType == null) return "";
    for (String key : mimeMap.keySet()) {
      if (mimeType.contains(key)) {
        return mimeMap.get(key);
      }
    }
    return ".tmp";
  }
}

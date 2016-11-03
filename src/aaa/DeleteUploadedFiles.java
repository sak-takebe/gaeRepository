package aaa;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.storage.Storage;
import com.google.api.services.storage.StorageScopes;

@SuppressWarnings("serial")
public class DeleteUploadedFiles extends HttpServlet {

	private static Properties properties;
	private static Storage storage;
	private static String BUCKET_NAME = "smple_bucket";
	private static String APPLICATION_NAME_PROPERTY = "application.name";

	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		String token = req.getParameter("parameter1");
		String fileNames = req.getParameter("parameter2");
		String responseJson = "";

		res.setContentType("application/json;charset=UTF-8");
		PrintWriter out = res.getWriter();

		// ユーザ認証
		String dir = Util.getUserid(token);

		if (!dir.isEmpty()) {
			// GCS ファイル削除処理
			boolean deleted;
			try {
				for (int i = 0; i < 1; i++) {
					deleteFile(BUCKET_NAME, fileNames);
				}
				deleted = true; // 削除成功
			} catch (Exception e) {
				deleted = false; // 削除失敗
				e.printStackTrace();
			}

			// TODO
			// DBからファイル情報削除する処理

			// トークン生成
			token = UUID.randomUUID().toString();
			if (Util.updateToken(dir, token)) {
				// 戻り値設定
				responseJson = "{\"message\":\"success\", \"token\":\"" + token
						+ "\", \"deleted\":\"" + String.valueOf(deleted)
						+ "\"}";
			} else {
				responseJson = "{\"message\":\"トークン更新失敗\"}";
			}
		} else {
			// ユーザ認証失敗
			responseJson = "{\"message\":\"fail\"}";
		}
		out.write(responseJson);
	}

	/**
	 * Deletes a file within a bucket
	 *
	 * @param bucketName
	 *            Name of bucket that contains the file
	 * @param fileName
	 *            The file to delete
	 * @throws Exception
	 */
	private static void deleteFile(String bucketName, String fileName)
			throws Exception {

		Storage storage = getStorage();

		storage.objects().delete(bucketName, fileName).execute();
	}

	private static Storage getStorage() throws Exception {

		if (storage == null) {

			HttpTransport httpTransport = new NetHttpTransport();
			JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();

			List<String> scopes = new ArrayList<String>();
			scopes.add(StorageScopes.CLOUD_PLATFORM);
			scopes.add(StorageScopes.DEVSTORAGE_FULL_CONTROL);
			scopes.add(StorageScopes.DEVSTORAGE_READ_WRITE);

			GoogleCredential credential = GoogleCredential
					.getApplicationDefault();
			if (credential.createScopedRequired()) {
				credential = credential.createScoped(scopes);
			}
			storage = new Storage.Builder(httpTransport, jsonFactory, null)
					.setHttpRequestInitializer(credential)
					.setApplicationName(
							getProperties().getProperty(
									APPLICATION_NAME_PROPERTY)).build();
		}
		return storage;
	}

	private static Properties getProperties() throws Exception {

		if (properties == null) {
			properties = new Properties();
			InputStream stream = DeleteUploadedFiles.class
					.getResourceAsStream("/resources/cloudstorage.properties");
			try {
				properties.load(stream);
			} catch (IOException e) {
				throw new RuntimeException(
						"cloudstorage.properties must be present in classpath",
						e);
			} finally {
				stream.close();
			}
		}
		return properties;
	}
}

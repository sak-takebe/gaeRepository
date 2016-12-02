package main.java;

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
        String fileNames[] = req.getParameterValues("parameter2[]");
        String responseJson = "";

        res.setContentType("application/json;charset=UTF-8");
        PrintWriter out = res.getWriter();

        // ユーザ認証
        String directory = Util.getUserid(token);

        if (!directory.isEmpty()) {

            boolean deleted = false;
            try {
                if (fileNames.length != 0) {
                    // 削除処理
                    deleteFile(BUCKET_NAME, directory, fileNames);
                    // TODO exceptionが発生しなかった場合にはtrueになる。例外ハンドリングは要検討
                    deleted = true; // 削除成功
                }
            } catch (Exception e) {
                deleted = false; // 削除失敗
                e.printStackTrace();
            }
            // トークン生成
            token = UUID.randomUUID().toString();
            if (Util.updateToken(directory, token)) {
                // トークン更新成功 ⇒ 画面に「新トークン」と「ファイル削除結果」を返却
                responseJson = "{\"message\":\"success\", \"token\":\"" + token
                        + "\", \"deleted\":\"" + String.valueOf(deleted)
                        + "\"}";
            } else {
                // トークン更新失敗
                responseJson = "{\"message\":\"token update fail\"}";
            }
        } else {
            // ユーザ認証失敗
            responseJson = "{\"message\":\"auth fail\"}";
        }
        out.write(responseJson);
    }

    /**
     * Deletes a file within a bucket
     *
     * @param bucketName
     *            Name of bucket that contains the file
     * @param directory
     *            Name of directory that contains the file
     * @param fileNames
     *            The file to delete
     * @throws Exception
     */
    private static void deleteFile(String bucketName, String directory, String[] fileNames) throws Exception {

        Storage storage = getStorage();

        for (String fileName : fileNames) {
            // GCSからファイルを削除する処理(IOException)
            String filePath = directory + "/" + fileName;
            storage.objects().delete(bucketName, filePath).execute();
            // DBからファイル情報を削除する処理
            Util.deleteFileInfo(fileName);
        }
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

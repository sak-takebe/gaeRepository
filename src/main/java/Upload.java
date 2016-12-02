package main.java;

import java.io.IOException;
import java.io.PrintWriter;
import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.tools.cloudstorage.GcsFileOptions;
import com.google.appengine.tools.cloudstorage.GcsFilename;
import com.google.appengine.tools.cloudstorage.GcsOutputChannel;
import com.google.appengine.tools.cloudstorage.GcsService;
import com.google.appengine.tools.cloudstorage.GcsServiceFactory;

@SuppressWarnings({ "serial" })
public class Upload extends HttpServlet {

    private BlobstoreService blobstoreService = BlobstoreServiceFactory
            .getBlobstoreService();
    private GcsService gcsService = GcsServiceFactory.createGcsService();

    private static final String BUCKET_NAME = "smple_bucket";
    private static final String DATE_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String PUBKIC_READ = "public-read";

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        // アップロードボタン押下時にajaxでチェックしているが、
        // 改ざんに備えてtokenからユーザIDが取得できなかった場合には空回りさせる。
        String token = req.getParameter("token");
        String dir = Util.getUserid(token);

        // トークン生成
        token = UUID.randomUUID().toString();

        if (!dir.isEmpty() && Util.updateToken(dir, token)) {

            res.setCharacterEncoding("UTF-8");

            // [blobstore]
            Map<String, List<BlobKey>> blobs = blobstoreService.getUploads(req);
            List<List<BlobKey>> blobKeysList = new ArrayList<>();

            // [Google Cloud Storage]
            BlobInfoFactory factory = new BlobInfoFactory();
            List<BlobInfo> blobInfoList = new ArrayList<>();
            String[] filenameArray;

            List<GcsFilename> gcsFileNames = new ArrayList<>();
            List<GcsOutputChannel> gcsWriteChannels = new ArrayList<>();
            GcsFileOptions options = new GcsFileOptions.Builder()
                    .mimeType("text/tab-separated-values").acl(PUBKIC_READ)
                    .build();

            if (blobs != null) {
                for (int i = 0; i < blobs.size(); i++) {
                    blobKeysList.add(blobs.get("file" + "[" + i + "]"));
                    blobInfoList.add(factory.loadBlobInfo(blobKeysList.get(i)
                            .get(0)));
                    filenameArray = spritComma(blobInfoList.get(i)
                            .getFilename());
                    gcsFileNames.add(new GcsFilename(BUCKET_NAME + "/" + dir,
                            filenameArray[0] + "_"
                                    + UUID.randomUUID().toString() + "."
                                    + filenameArray[1]));
                }
            }

            if (gcsFileNames != null) {
                String[] fileIdArray;
                for (int i = 0; i < gcsFileNames.size(); i++) {
                    // [書き込み処理]
                    gcsWriteChannels.add(gcsService.createOrReplace(
                            gcsFileNames.get(i), options));
                    gcsWriteChannels.get(i).write(
                            ByteBuffer.wrap(blobstoreService.fetchData(
                                    blobKeysList.get(i).get(0), 0, 1015800)));
                    gcsWriteChannels.get(i).close();

                    // [アップロードファイル管理]
                    // ・ファイルID
                    fileIdArray = spritComma(gcsFileNames.get(i)
                            .getObjectName());
                    String fairuId = fileIdArray[0].substring(fileIdArray[0]
                            .length() - 36);
                    // ・ファイル名
                    String fairuMei = gcsFileNames.get(i).getObjectName();
                    // ・ファイルサイズ
                    long fairuSize = factory.loadBlobInfo(
                            blobKeysList.get(i).get(0)).getSize();
                    // ・ファイル作成日
                    Date creation = factory.loadBlobInfo(
                            blobKeysList.get(i).get(0)).getCreation();
                    String fairuSakuseibi = new SimpleDateFormat(DATE_PATTERN)
                            .format(creation);
                    // ・コンテキストタイプ
                    String fairuContentType = factory.loadBlobInfo(
                            blobKeysList.get(i).get(0)).getContentType();

                    // オートインクリメントインデックス取得後、データストア登録処理
                    for (int idx = 1;; idx++) {
                        Key key = KeyFactory.createKey("FairuJouhou", idx);
                        ArrayList<String> propertyName = new ArrayList<String>();
                        ArrayList<String> value = new ArrayList<String>();
                        propertyName.add("fairuId");
                        value.add(fairuId);
                        propertyName.add("fairuMei");
                        value.add(fairuMei);
                        propertyName.add("fairuSize");
                        value.add(String.valueOf(fairuSize));
                        propertyName.add("fairuSakuseibi");
                        value.add(fairuSakuseibi);
                        propertyName.add("fairuContentType");
                        value.add(fairuContentType);
                        propertyName.add("directory");
                        value.add(dir);
                        boolean indexExisted = Util.indexExisted(key,
                                propertyName, value);
                        if (!indexExisted) {
                            // ユニークな値を特定し登録処理完了のため
                            break;
                        }
                    }
                }
            }

            // 戻り値設定
            String responseJson = "{\"token\":\"" + token + "\"}";
            res.setContentType("application/json;charset=UTF-8");
            PrintWriter out = res.getWriter();
            out.write(responseJson);
        }
    }

    private String[] spritComma(String beforeFilename) {

        // ファイル名
        String name = "";
        // 拡張子格納変数
        String ext;
        // ファイル名分割
        String[] filename_ar = beforeFilename.split("\\.");
        // ファイル名格納
        int j;
        for (j = 0; j < filename_ar.length - 1; j++) {
            name += filename_ar[j];
        }
        // 拡張子格納
        ext = filename_ar[j];

        return new String[] { name, ext };
    }
}

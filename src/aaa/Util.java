package aaa;

import java.util.ArrayList;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;
import com.google.appengine.api.datastore.Transaction;
import com.google.common.collect.ComputationException;

public class Util {

    private static DatastoreService datastoreService = DatastoreServiceFactory
            .getDatastoreService();

    /**
     *
     * インデックスが存在しているかどうかを判定してDB登録する.
     *
     * 存在している場合：true 存在していない場合：false(EntityNotFoundException catchしてDB登録登録)
     *
     */
    public static boolean indexExisted(Key key, ArrayList<String> propertyName,
            ArrayList<String> value) {

        Transaction tx = datastoreService.beginTransaction();

        try {
            datastoreService.get(tx, key);
            tx.rollback();
            // 既にエントリが存在
            return true;
        } catch (EntityNotFoundException e) {
            // まだエントリが存在しなかったのでput
            Entity entity = new Entity(key);
            try {
                for (int i = 0; i < propertyName.size(); i++) {
                    // propertyName, value
                    entity.setProperty(propertyName.get(i), value.get(i));
                }
                // 登録
                // datastoreService.put(entity);
                datastoreService.put(tx, entity);
                tx.commit();
                // ユニークな値が確保できた
                return false;
            } catch (ComputationException e2) {
                // エントリをgetしてからcommitまでの間に割り込まれた場合は例外
                if (tx.isActive()) {
                    tx.rollback();
                }
                return true;
            }
        }
    }

    /**
     *
     * ファイル情報削除.
     * @param fileName ファイル名
     *
     */
    public static void deleteFileInfo(String fileName) {

        Query query = new Query("FairuJouhou");

        query.setFilter(FilterOperator.EQUAL.of("fairuMei", fileName));
        PreparedQuery pQuery = datastoreService.prepare(query);
        for (Entity entity : pQuery.asIterable()) {
            // レコード削除
            // TODO トランザクションを使用する。 delete(Transaction txn, Key... keys);
            datastoreService.delete(entity.getKey());
        }
    }

    /**
     *
     * ユーザ認証.
     *
     */
    public static String getUserid(String token) {

        String db_userid = "";

        Query query = new Query("UserInfo");
        query.setFilter(FilterOperator.EQUAL.of("token", token));
        PreparedQuery pQuery = datastoreService.prepare(query);

        for (Entity entity : pQuery.asIterable()) {
            // ユーザIDを取得
            db_userid = entity.getProperty("userid").toString();
        }

        if (!db_userid.isEmpty()) {
            return db_userid;
        } else {
            return "";
        }
    }

    /**
     *
     * ログイン処理.
     *
     */
    public static String[] getLoginInfo(String userid, String password) {

        String db_userid = "";
        String db_userpassword = "";

        Query query = new Query("UserInfo");
        query.setFilter(FilterOperator.EQUAL.of("userid", userid));
        // query.setFilter(FilterOperator.EQUAL.of("userpassword", password));
        // TODO 複数条件 ログインIDも指定する。
        // ユーザを検索
        PreparedQuery pQuery = datastoreService.prepare(query);

        for (Entity entity : pQuery.asIterable()) {
            db_userid = entity.getProperty("userid").toString();
            db_userpassword = entity.getProperty("userpassword").toString();
        }

        if (!db_userid.isEmpty()) {
            return new String[] { db_userid, db_userpassword };
        } else {
            return null;
        }
    }

    /**
     *
     * トークン更新処理.
     *
     */
    public static boolean updateToken(String userid, String token) {

        Query query = new Query("UserInfo");
        query.setFilter(FilterOperator.EQUAL.of("userid", userid));

        // ユーザを検索
        PreparedQuery pQuery = datastoreService.prepare(query);

        for (Entity entity : pQuery.asIterable()) {
            // ユーザのトークンを更新
            entity.setProperty("token", token);
            datastoreService.put(entity);
            return true;
        }
        return false;
    }
}

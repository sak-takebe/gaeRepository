package aaa;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;

@SuppressWarnings("serial")
public class RegistUserInfo extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		// 子画面からのパラメータを取得
		String userid = req.getParameter("parameter1");
		String userpassword = req.getParameter("parameter2");

		// ユーザ登録処理
		ArrayList<String> propertyName = new ArrayList<String>();
		ArrayList<String> value = new ArrayList<String>();
		// userid
		propertyName.add("userid");
		value.add(userid);
		// password
		propertyName.add("userpassword");
		value.add(userpassword);
		// token
		propertyName.add("token");
		String token = UUID.randomUUID().toString();
		value.add(token);
		// createDate
		propertyName.add("createDate");
		value.add(new Date().toString());
		// updateDate
		propertyName.add("updateDate");
		value.add(new Date().toString());

		for (int idx = 1;; idx++) {
			Key key = KeyFactory.createKey("UserInfo", idx);
			boolean indexExisted = Util.indexExisted(key, propertyName, value);
			if (!indexExisted) {
				// ユニークな値を特定し登録処理完了のため
				break;
			}
		}

		// 画面からのユーザIDをそのまま返却
		String id = userid;

		// 戻り値設定
		String responseJson = "{\"message\":\"success\", \"userid\":\""+ id + "\", \"token\":\""+ token + "\"}";
		// String responseJson = "{\"return\":\"fail\"}";

		resp.setContentType("application/json;charset=UTF-8");

		PrintWriter out = resp.getWriter();
		out.write(responseJson);
	}


}

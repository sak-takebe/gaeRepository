package aaa;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings("serial")
public class Login extends HttpServlet {

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		// 子画面からのパラメータを取得
		String userid = req.getParameter("parameter1");
		String userpassword = req.getParameter("parameter2");

		String token = "";
		String responseJson = "";

		// ログイン処理
		String[] result = Util.getLoginInfo(userid, userpassword);

		if (result != null) {

			userid = result[0];

			// トークン生成
			token = UUID.randomUUID().toString();

			// トークン更新処理
			if (Util.updateToken(userid, token)) {
				// 戻り値設定
				responseJson = "{\"message\":\"success\", \"userid\":\""
						+ userid + "\", \"token\":\"" + token + "\"}";
			} else {
				//
				responseJson = "{\"message\":\"failTokenUpdate\"}";
			}
		} else {
			responseJson = "{\"message\":\"failLogin\"}";
		}

		resp.setContentType("application/json;charset=UTF-8");
		PrintWriter out = resp.getWriter();
		out.write(responseJson);
	}
}

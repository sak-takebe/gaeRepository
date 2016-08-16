package aaa;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings({ "serial" })
public class CreateUserid extends HttpServlet {

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse res)
			throws IOException, ServletException {

		// ユーザIDの採番処理を行うサーブレット
		String parameter1 = req.getParameter("");
		String parameter2 = req.getParameter("");

		String responseJson = "{\"userid\":\"" + UUID.randomUUID().toString() + "\"}";

		res.setContentType("application/json;charset=UTF-8");

		PrintWriter out = res.getWriter();
		out.write(responseJson);

	}
}
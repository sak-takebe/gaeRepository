package main.java;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@SuppressWarnings({ "serial" })
public class IsExistUser extends HttpServlet {

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse res)
            throws IOException, ServletException {

        // tokenからユーザIDを取得する処理
        String token = req.getParameter("parameter1");
        String userdir = "";

        userdir = Util.getUserid(token);

        String message = "";
        if (!userdir.isEmpty()) {
            message = "success";
        } else {
            message = "fail";
        }
        String responseJson = "{\"message\":\"" + message + "\"}";
        res.setContentType("application/json;charset=UTF-8");

        PrintWriter out = res.getWriter();
        out.write(responseJson);
    }
}

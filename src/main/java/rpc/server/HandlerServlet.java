package rpc.server;

import rpc.message.CallContent;
import rpc.message.HttpCallContent;
import rpc.message.RetContent;

import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class HandlerServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        ServletInputStream in = req.getInputStream();
        ObjectInputStream bin = new ObjectInputStream(in);
        try {
            CallContent callContent = (CallContent) bin.readObject();
            Object res = MethodCall.call(callContent);

            ServletOutputStream out = resp.getOutputStream();
            ObjectOutputStream oout = new ObjectOutputStream(out);
            RetContent retContent = new RetContent();
            retContent.setRes(res);
            if(callContent instanceof HttpCallContent) {
                HttpCallContent httpCallContent = (HttpCallContent) callContent;
                retContent.setState(httpCallContent.getState());
            }
            oout.writeObject(retContent);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}

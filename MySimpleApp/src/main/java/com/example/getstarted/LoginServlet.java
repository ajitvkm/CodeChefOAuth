package com.example.getstarted;
import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.getstarted.CodeChefAPIInfo;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.math.BigInteger;
import java.security.SecureRandom;

@SuppressWarnings("serial")
@MultipartConfig
@WebServlet(name = "login", urlPatterns = {"/login"})
public class LoginServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String state = new BigInteger(130, new SecureRandom()).toString(32);
		req.getSession().setAttribute("state", state);
		String clientId = getServletContext().getInitParameter("codechef.clientId");
		String redirectURI = getServletContext().getInitParameter("codechef.callback");
		CodeChefAPIInfo.LOGGER.info("clientId = " + clientId);
		CodeChefAPIInfo.LOGGER.info("redirectURI = " + redirectURI);
		String authorizationURL = CodeChefAPIInfo.getAuthorizationURL(clientId, redirectURI);
		authorizationURL = authorizationURL + "&state=" + state;
		CodeChefAPIInfo.LOGGER.info("Sending authorization URL = " + authorizationURL);
		resp.sendRedirect(authorizationURL);
		/*
		   URL urlObj = new URL(authorizationURL);
		   HttpURLConnection con = (HttpURLConnection)urlObj.openConnection();
		   con.setRequestMethod("GET");
		   int responseCode = con.getResponseCode();
		   if(responseCode == HttpURLConnection.HTTP_OK) {
		   } else {
		   System.out.println("GET failed!!");
		   }
		*/
	}
}

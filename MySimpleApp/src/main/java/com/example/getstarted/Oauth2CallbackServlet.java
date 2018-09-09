/*
 * Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.getstarted;
import com.example.getstarted.CodeChefAPIInfo;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.logging.Level;

@WebServlet(name = "oauth2callback", value = "/oauth2callback")
@SuppressWarnings("serial")
public class Oauth2CallbackServlet extends HttpServlet {

	private String getJsonTokenString(String clientId, String clientSecret, String redirectURI, String authorizationCode) throws JSONException {
		CodeChefAPIInfo.LOGGER.info("Getting access_token from code chef");
		CodeChefAPIInfo.LOGGER.info("clientId = " + clientId);
		CodeChefAPIInfo.LOGGER.info("clientSecret = " + clientSecret);
		CodeChefAPIInfo.LOGGER.info("redirectURI = " + redirectURI);
		String tokenURL = CodeChefAPIInfo.getTokenURL(authorizationCode, clientId,
												  	  clientSecret, "htpp://localhost:8080/tokencallback");
		CodeChefAPIInfo.LOGGER.info("Sending token URL = " + tokenURL);
		HttpURLConnection conn = null;
		BufferedReader reader = null;
		StringBuilder strBuf = new StringBuilder();
		try {
		URL url = new URL(tokenURL);
		conn = (HttpURLConnection)url.openConnection();
		conn.setRequestMethod("POST");
		conn.setRequestProperty("content-Type", "application/json");
		if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
			throw new RuntimeException("HTTP GET Request Failed with Error code : "
					+ conn.getResponseCode());
		}

		reader = new BufferedReader(new InputStreamReader(conn.getInputStream(),"utf-8"));
		String output = null;
		while ((output = reader.readLine()) != null)
			strBuf.append(output);
		} catch(MalformedURLException e) {
            e.printStackTrace();
        } catch(IOException e){
            e.printStackTrace();
        } finally {
            if(reader!=null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if(conn!=null)
                conn.disconnect();
        }
		return strBuf.toString();
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
		if(req.getSession().getAttribute("state") == null
		   || !req.getParameter("state").equals((String) req.getSession().getAttribute("state"))) {
			resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			resp.sendRedirect("/login");
			return;
		}

		CodeChefAPIInfo.LOGGER.log(Level.SEVERE, "req = ", req);
		req.getSession().removeAttribute("state");
		String authorizationCode = req.getParameter("code");
		CodeChefAPIInfo.LOGGER.info("authorizationCode = " + authorizationCode);
		if((req.getSession().getAttribute("access_token") == null ||
			req.getSession().getAttribute("refresh_token") == null ||
			req.getSession().getAttribute("scope") == null) &&
			req.getSession().getAttribute("getToken") == null) {
			req.getSession().setAttribute("getToken", 1);
			String clientId = getServletContext().getInitParameter("codechef.clientId");
			req.getSession().removeAttribute("getToken");
			String clientSecret = getServletContext().getInitParameter("codechef.clientSecret");
			String redirectURI = getServletContext().getInitParameter("codechef.callback");
			String jsonString = "";
			try {
			CodeChefAPIInfo.LOGGER.info("Calling getJsonTokenString!");
			jsonString = getJsonTokenString(clientId, clientSecret, redirectURI, authorizationCode);
			JSONObject jsonObj = new JSONObject(jsonString);
			String access_token = jsonObj.getString("access_token");
			String refresh_token = jsonObj.getString("refresh_token");
			String scope = jsonObj.getString("scope");
			CodeChefAPIInfo.LOGGER.info("access_token = " + access_token);
			CodeChefAPIInfo.LOGGER.info("refresh_token = " + refresh_token);
			CodeChefAPIInfo.LOGGER.info("scope = " + scope);

			req.getSession().setAttribute("access_token", access_token);
			req.getSession().setAttribute("refresh_token", refresh_token);
			req.getSession().setAttribute("scope", scope);
			} catch(JSONException e) {
				throw new IOException();
			}
		}
		CodeChefAPIInfo.LOGGER.info("Redirecting to contest page!");
		resp.sendRedirect("/contest");
	}
}
// [END example]

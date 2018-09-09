/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.getstarted;
import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

import com.example.getstarted.CodeChefAPIInfo;

@SuppressWarnings("serial")
@MultipartConfig
@WebServlet(name = "contest", urlPatterns = {"", "/contest"}, loadOnStartup = 1)
public class ContestServlet extends HttpServlet {

	@Override
	public void init() throws ServletException {
	}

	@Override
	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		req.setAttribute("action", "Get");          // Part of the Header in form.jsp
		req.setAttribute("destination", "contest");  // The urlPattern to invoke (this Servlet)
		req.setAttribute("page", "form");           // Tells base.jsp to include form.jsp
		req.getRequestDispatcher("/base.jsp").forward(req, resp);
	}

	@Override
	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String contestCode = req.getParameter("title");
		String contestURL = CodeChefAPIInfo.getContestURL(contestCode);
		CodeChefAPIInfo.LOGGER.info("Inquiring about contest " + contestCode);
		CodeChefAPIInfo.LOGGER.info("Contest URL = " + contestURL);
		StringBuilder strBuf = new StringBuilder();
		HttpURLConnection conn=null;
		BufferedReader reader=null;
		try {
			URL url = new URL(contestURL);
			conn = (HttpURLConnection)url.openConnection();
			conn.setRequestMethod("GET");
			conn.setRequestProperty("content-Type", "application/json");
			String authorizationString = "Bearer " + req.getSession().getAttribute("access_token");
			conn.setRequestProperty("Authorization", authorizationString);

			if (conn.getResponseCode() != HttpURLConnection.HTTP_OK) {
				throw new RuntimeException("HTTP GET Request Failed with Error code : "
						+ conn.getResponseCode());
			}

			//Read the content from the defined connection
			//Using IO Stream with Buffer raise highly the efficiency of IO
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
		CodeChefAPIInfo.LOGGER.info("JSON Response from code chef: " + strBuf.toString());
		req.setAttribute("contest_details", strBuf.toString());
		req.getRequestDispatcher("/base.jsp").forward(req, resp);
	}
}

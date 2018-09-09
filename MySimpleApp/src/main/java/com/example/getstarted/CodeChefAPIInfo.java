package com.example.getstarted;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CodeChefAPIInfo {
	public static final String CODECHEF_API_BASE_URL = "https://api.codechef.com";

	public static String getAuthorizationURL(String clientId, String redirectURI) {
		String url = CODECHEF_API_BASE_URL + "/oauth/authorize?response_type=code&client_id=" +
					 clientId + "&redirect_uri=" + redirectURI;
		return url;
	}

	public static String getTokenURL(String code, String clientId, String clientSecret, String redirectURI) {
		String url = CODECHEF_API_BASE_URL + "/oauth/token?grant_type=authorization_code&client_id=" + clientId + "&client_secret=" + clientSecret + "&redirect_uri=" + redirectURI + "&code=" + code;
		return url;
	}

	public static String getContestURL(String contestCode) {
		String url = CODECHEF_API_BASE_URL + "/contest/" + contestCode;
		return url;
	}

	public static final Logger LOGGER = Logger.getLogger(CodeChefAPIInfo.class.getName());
};

package com.sinosoft.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TokenHelper {

	private static Log LOG = LogFactory.getLog(TokenHelper.class);

	public static String TOKEN_KEY = "springmvc.token";

	public static String TOKEN_SESSION_KEY = "token";

	/**
	 * 验证token
	 * @param request
	 * @return
	 */
	public static boolean validToken(HttpServletRequest request) {
		HttpSession session = request.getSession();
		synchronized (session) {
			String token = getTokenValue(request);
			if (token == null) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("no token found in " + request.getRequestURI());
				}
				return true;
			}
			List<String> tokens = getTokens(session);
			if (tokens.contains(token)) {
				removeToken(session, token);
				return true;
			} else {
				return false;
			}
		}
	}

	/**
	 * 新增服务器token
	 * @param session
	 * @param token
	 */
	public static void addToken(HttpSession session, String token) {
		@SuppressWarnings("unchecked")
		ArrayList<String> tokens = (ArrayList<String>) session.getAttribute(TOKEN_SESSION_KEY);
		if (tokens == null) {
			tokens = new ArrayList<String>();
		} else if (tokens.size() > 100) {
			tokens = (ArrayList<String>) tokens.subList(0, 100);
		}
		tokens.add(token);
		session.setAttribute(TOKEN_SESSION_KEY, tokens);
	}

	/**
	 * 生成和添加token
	 * @param session
	 * @return
	 */
	public static String generateAndAddToken(HttpSession session) {
		String token = generateToken();
		addToken(session, token);
		return token;
	}

	/**
	 * 生成token
	 * 
	 * @return
	 */
	public static String generateToken() {
		return UUID.randomUUID().toString().replace("-", "");
	}

	/**
	 * 获取token
	 * 
	 * @param request
	 * @return
	 */
	private static String getTokenValue(HttpServletRequest request) {
		return request.getParameter(TOKEN_KEY);
	}

	/**
	 * 移除token
	 * 
	 * @param token
	 */
	private static void removeToken(HttpSession session, String token) {
		@SuppressWarnings("unchecked")
		ArrayList<String> tokens = (ArrayList<String>) session.getAttribute(TOKEN_SESSION_KEY);
		tokens.remove(token);
		if (tokens != null && !tokens.isEmpty()) {
			session.setAttribute(TOKEN_SESSION_KEY, tokens);
		}
	}

	private static List<String> getTokens(HttpSession session) {
		@SuppressWarnings("unchecked")
		ArrayList<String> tokens = (ArrayList<String>) session.getAttribute(TOKEN_SESSION_KEY);
		if (tokens == null) {
			tokens = new ArrayList<String>();
			session.setAttribute(TOKEN_SESSION_KEY, tokens);
		}
		return tokens;
	}
}

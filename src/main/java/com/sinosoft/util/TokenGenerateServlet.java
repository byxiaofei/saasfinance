package com.sinosoft.util;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


public class TokenGenerateServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2260406951592429225L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String token = TokenHelper.generateAndAddToken(req.getSession());
		resp.getWriter().write(token);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		this.doGet(req, resp);
	}
}

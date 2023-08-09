package org.upsmf.grievance.config;

import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Serializable;

@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint, Serializable {
	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void commence(javax.servlet.http.HttpServletRequest httpServletRequest,
			HttpServletResponse httpServletResponse, AuthenticationException e) throws IOException, ServletException {
		/*	ServletContext ctx = httpServletRequest.getServletContext();
			if (!(Boolean) ctx.getAttribute("WhiteList")) {
			httpServletResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
		}*/

	}

}
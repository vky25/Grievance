package org.upsmf.grievance.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	/*@Autowired
	private UserService userService;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;*/

	@Value("${urls.whitelist}")
	private String whitelistUrls;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws IOException, ServletException {

		
		/*List<String> whitelistUrlList = Arrays.asList(whitelistUrls.split(","));

		ServletContext ctx = req.getServletContext();
		Boolean whiteListed;
		Boolean authorized = Boolean.FALSE;
		String username = null;
		String authToken = null;
		UserDetails userDetails = null;
		Map<String, Object> userInfoObectMap = new HashMap<>();

		if (whitelistUrlList.contains(req.getRequestURI())) {
			whiteListed = Boolean.TRUE;
		} else {
			whiteListed = Boolean.FALSE;
			String header = req.getHeader(HEADER_STRING);
			if (StringUtils.isBlank(header)) {
				res.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
				res.getWriter()
						.write(ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.UNAUTHORIZED_ACCESS));
				res.setContentType("application/json");
				res.getWriter().flush();
				return;
			}
			if (header.startsWith(TOKEN_PREFIX)) {
				authToken = header.replace(TOKEN_PREFIX, "");
				username = getUserName(username, authToken);
			} else {
				logger.warn("couldn't find bearer string, will ignore the header");
			}
			if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
				userInfoObectMap = userService.getUserInfoObjects(username);
				userDetails = (UserDetails) userInfoObectMap.get("UserDetails");
				if (jwtTokenUtil.validateToken(authToken, userDetails)) {
					UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, Arrays.asList(new SimpleGrantedAuthority("ROLE_ADMIN")));
					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
					logger.info("authenticated user " + username + ", setting security context");
					SecurityContextHolder.getContext().setAuthentication(authentication);
				}
			}

			
			Boolean userTokenAvailable = userService.findUserByToken(authToken);
			authorized = checkForAuthorization(req, ctx, authorized, userDetails, userInfoObectMap, userTokenAvailable);
		}

		if (!authorized && !whiteListed) {
			res.setStatus(HttpServletResponse.SC_FORBIDDEN);
			res.getWriter()
					.write(ResponseGenerator.failureResponse(ResponseMessages.ErrorMessages.INVALID_ACCESS_ROLE));
			res.setContentType("application/json");
			res.getWriter().flush();
			return;
		}*/

		chain.doFilter(req, res);
	}

	/*public String getUserName(String username, String authToken) {
		try {
			username = jwtTokenUtil.getUsernameFromToken(authToken);
		} catch (IllegalArgumentException e) {
			logger.error("an error occured during getting username from token", e);
		} catch (ExpiredJwtException e) {
			logger.warn("the token is expired and not valid anymore", e);
		} catch (SignatureException e) {
			logger.error("Authentication Failed. Username or Password not valid.");
		}
		return username;
	}

	public Boolean checkForAuthorization(HttpServletRequest req, ServletContext ctx, Boolean authorized,
			UserDetails userDetails, Map<String, Object> userInfoObectMap, Boolean userTokenAvailable) {
		if (userTokenAvailable) {
			try {
				if (userDetails != null) {
					User user = (User) userInfoObectMap.get("User");
					req.setAttribute("UserInfo", user);
					ctx.setAttribute("UserInfo", user);
					List<Long> roleIds = MasterDataManager.getRoleIdsForUserId(user.getId());
					for (Long roleId : roleIds) {
						List<String> actionUrlList = MasterDataManager.getActionUrlsForRoleId(roleId);
						if (actionUrlList.contains(req.getRequestURI())) {
							ctx.setAttribute("Authorized", Boolean.TRUE);
							authorized = Boolean.TRUE;
							break;
						}
					}
				}
			} catch (UsernameNotFoundException e) {
				e.getMessage();
			}
		}
		return authorized;
	}*/
}
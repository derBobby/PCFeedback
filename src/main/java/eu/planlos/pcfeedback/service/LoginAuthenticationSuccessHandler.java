package eu.planlos.pcfeedback.service;

//@Component
//public class LoginAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {
// 
//	private static final Logger logger = LoggerFactory.getLogger(LoginAuthenticationSuccessHandler.class);	
//
//	// is this necessary?
//	public static final String REDIRECT_URL_SESSION_ATTRIBUTE_NAME = "REDIRECT_URL";
//	
//	@Override
//	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
//			Authentication authentication) throws IOException, ServletException {
//		
//		logger.debug("Successfull login for: \"" + authentication.getName() + "\"");
//
//		User user = (User) authentication.getPrincipal();
//		String loginName = user.getUsername();
//		
//		boolean isAdmin = false;
//		if(authentication.getAuthorities().contains(new SimpleGrantedAuthority(ApplicationRole.ROLE_ADMIN))) {
//			logger.debug("User is administrator");
//			isAdmin = true;
//		}
//		request.getSession().setAttribute(SessionAttribute.ISADMIN, isAdmin);
//		request.getSession().setAttribute(SessionAttribute.LOGINNAME, loginName);
//		
//		super.onAuthenticationSuccess(request, response, authentication);
//	}
//
//}

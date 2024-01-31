package it.zero11.xroads.webservices;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.oltu.oauth2.client.OAuthClient;
import org.apache.oltu.oauth2.client.URLConnectionClient;
import org.apache.oltu.oauth2.client.request.OAuthClientRequest;
import org.apache.oltu.oauth2.client.response.OAuthAuthzResponse;
import org.apache.oltu.oauth2.client.response.OAuthJSONAccessTokenResponse;
import org.apache.oltu.oauth2.common.exception.OAuthProblemException;
import org.apache.oltu.oauth2.common.exception.OAuthSystemException;
import org.apache.oltu.oauth2.common.message.types.GrantType;

import com.vaadin.flow.server.WrappedHttpSession;

import it.zero11.xroads.ui.utils.SessionUtils;
import it.zero11.xroads.utils.modules.core.XRoadsCoreModule;
import it.zero11.xroads.utils.modules.core.dao.ParamDao;
import it.zero11.xroads.utils.modules.core.model.ParamType;

@Path("oauth/")
public class OAuthWebservice extends HttpServlet{	

	private static final long serialVersionUID = 1L;
	@GET
	@Path("redirect")
	public Response redirect(@Context HttpServletRequest httpServletRequest) throws IOException, ServletException, URISyntaxException {
		try {
			OAuthAuthzResponse oauthAuthzResponse = OAuthAuthzResponse.oauthCodeAuthzResponse(httpServletRequest);

			OAuthClientRequest oauthTokenRequest;
			oauthTokenRequest = OAuthClientRequest
					.tokenLocation(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.OAUTH_CLIENT_TOKEN_URL)) 			
					.setGrantType(GrantType.AUTHORIZATION_CODE)
					.setClientId(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.OAUTH_CLIENT_ID)) 			
					.setClientSecret(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.OAUTH_CLIENT_SECRET)) 	
					.setRedirectURI(getOAuthRedirectURL())
					.setCode(oauthAuthzResponse.getCode())
					.buildBodyMessage();

			OAuthClient oAuthClient = new OAuthClient(new URLConnectionClient());

			OAuthJSONAccessTokenResponse oAuthResponse = oAuthClient.accessToken(oauthTokenRequest);

			System.out.println(oAuthResponse);
			String roles = oAuthResponse.getParam("roles");


			if (roles != null && roles.contains("admin")){
				SessionUtils.putUserInSession(new WrappedHttpSession(httpServletRequest.getSession()), ((Object)oAuthResponse.getAccessToken()));	
				return Response.seeOther(new URI(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.BASE_URL))).build();
			}else{			
				return Response.status(Status.BAD_REQUEST).build();
			}
		} catch (OAuthSystemException | OAuthProblemException e) {
			e.printStackTrace();
			return Response.status(Status.BAD_REQUEST).build();
		}
	}

	public static String getOAuthRedirectURL(){
		try{
			OAuthClientRequest request = OAuthClientRequest
					.authorizationLocation(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.OAUTH_CLIENT_AUTH_URL))
					.setClientId(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.OAUTH_CLIENT_ID))
					.setRedirectURI(ParamDao.getInstance().getParameter(XRoadsCoreModule.INSTANCE, ParamType.BASE_URL) + "/rest/oauth/redirect")
					.setResponseType("code")
					.buildQueryMessage();

			return request.getLocationUri();
		}catch(OAuthSystemException e){
			return null;
		}
	}

}

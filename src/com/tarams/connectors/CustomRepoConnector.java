package com.tarams.connectors;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.manifoldcf.agents.interfaces.ServiceInterruption;
import org.apache.manifoldcf.agents.system.Logging;
import org.apache.manifoldcf.core.interfaces.ConfigParams;
import org.apache.manifoldcf.core.interfaces.IHTTPOutput;
import org.apache.manifoldcf.core.interfaces.IPostParameters;
import org.apache.manifoldcf.core.interfaces.IThreadContext;
import org.apache.manifoldcf.core.interfaces.ManifoldCFException;
import org.apache.manifoldcf.crawler.connectors.BaseRepositoryConnector;
import org.apache.manifoldcf.examples.docs4u.D4UException;
import org.apache.manifoldcf.examples.docs4u.D4UFactory;
import org.apache.manifoldcf.examples.docs4u.Docs4UAPI;

public class CustomRepoConnector extends BaseRepositoryConnector {

	protected final static String PARAMETER_REPOSITORY_ROOT = "rootdirectory"; 
	protected final static long SESSION_EXPIRATION_MILLISECONDS = 300000L; 
	protected String rootDirectory = null;  
	protected Docs4UAPI session = null; 
	protected long sessionExpiration = -1L;
	//	Save activity
	protected final static String ACTIVITY_SAVE = "save";
	//	Delete activity 
	protected final static String ACTIVITY_DELETE = "delete";

	public CustomRepoConnector() {
		super();
	}


	/** Return the list of activities that this connector supports (i.e. writes into the log).
	 *@return the list.
	 */
	@Override
	public String[] getActivitiesList() {
		return new String[]{ACTIVITY_SAVE,ACTIVITY_DELETE};
	}

	/** Test the connection and Returns a string describing the connection integrity.
	 *@return the connection's status as a displayable string.
	 */
	@Override
	public String check() throws ManifoldCFException {
		try {
			// Get or establish the session
			Docs4UAPI currentSession = getSession();
			// Check session integrity
			try {
				currentSession.sanityCheck();
			}
			catch (D4UException e) {
				Logging.ingest.warn("Docs4U: Error checking repository: "+e.getMessage(),e);
				return "Error: "+e.getMessage();
			}
			// If it passed, return "everything ok" message
			return super.check();
		}
		catch (ServiceInterruption e) {
			return "Transient error: "+e.getMessage();
		}
	}


	@Override
	public void connect(ConfigParams configParams) {
		super.connect(configParams);
		rootDirectory = configParams.getParameter(PARAMETER_REPOSITORY_ROOT);
	}

	@Override
	public void disconnect()throws ManifoldCFException {
		expireSession();
		rootDirectory = null;
		super.disconnect();
	}

	@Override
	public void outputConfigurationHeader(IThreadContext threadContext, IHTTPOutput out, Locale locale,
			ConfigParams parameters, List<String> tabsArray) throws ManifoldCFException, IOException {
		tabsArray.add("Repository");
		super.outputConfigurationHeader(threadContext, out, locale, parameters, tabsArray);
	}

	@Override
	public void outputConfigurationBody(IThreadContext threadContext, IHTTPOutput out, Locale locale,
			ConfigParams parameters, String tabName) throws ManifoldCFException, IOException {
		if(tabName.equals("Repository")) {
			out.println("RootDirectory: <input type=\"text\" name=\"rootdirectory\"/>");
		}
		super.outputConfigurationBody(threadContext, out, locale, parameters, tabName);
	}



	/** This method is periodically called for all connectors that are connected but not
	 * in active use.
	 */
	@Override
	public void poll() throws ManifoldCFException {
		if (session != null) {
			if (System.currentTimeMillis() >= sessionExpiration)
				expireSession();
		}
	}

	@Override
	public String processConfigurationPost(IThreadContext threadContext, IPostParameters variableContext,
			ConfigParams parameters) throws ManifoldCFException {
		String repositoryRoot = variableContext.getParameter(PARAMETER_REPOSITORY_ROOT);
		System.out.println("repositoryRoot::"+"\t"+repositoryRoot);
		if (repositoryRoot != null) {
			parameters.setParameter(PARAMETER_REPOSITORY_ROOT,repositoryRoot);
		}
		return null;
	}

	@Override
	public void viewConfiguration(IThreadContext threadContext, IHTTPOutput out, Locale locale, ConfigParams parameters)
			throws ManifoldCFException, IOException {
		out.print("RootDirectory:"+"\t"+parameters.getParameter(PARAMETER_REPOSITORY_ROOT));
		super.viewConfiguration(threadContext, out, locale, parameters);
	}

	/** Get the current session, or create one if not valid.
	 */
	protected Docs4UAPI getSession() throws ManifoldCFException, ServiceInterruption {
		if (session == null) {
			//	       We need to establish a new session
			try {
				session = D4UFactory.makeAPI(rootDirectory); 
			}
			catch (D4UException e){
				Logging.ingest.warn("Docs4U: Session setup error: "+e.getMessage(),e);
				throw new ManifoldCFException("Session setup error: "+e.getMessage(),e);
			}
		}
		sessionExpiration = System.currentTimeMillis() + SESSION_EXPIRATION_MILLISECONDS;
		return session;
	}

	protected void expireSession() {
		session = null;
		sessionExpiration = -1L;
	}

}

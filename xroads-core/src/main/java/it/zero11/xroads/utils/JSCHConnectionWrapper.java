package it.zero11.xroads.utils;

import java.io.File;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

public class JSCHConnectionWrapper implements AutoCloseable {
	private Session session;
	private ChannelSftp sftpChannel = null;
	private final String password;

	public JSCHConnectionWrapper(String host, String username, String password, int port) throws JSchException{
		this.password = password;
		
		final JSch jsch = new JSch();
		int retry = 3;
		do{
			session = jsch.getSession(username, host, port);
			session.setConfig("StrictHostKeyChecking", "no");
			session.setPassword(password);
			jsch.setKnownHosts("~/.ssh/known_hosts");
			if (new File("~/.ssh/id_rsa").exists()) {
				jsch.addIdentity("~/.ssh/id_rsa");
			}
			try {
				session.connect(60000);
				return;
			}catch(JSchException e){
				if ("Auth fail".equals(e.getMessage())){
					retry--;
				}else{
					throw new JSchException(e.getMessage(), e.getCause());
				}
			}
		}while(retry > 0);
		
		throw new JSchException("Auth Fail after 3 retries.");
	}

	public synchronized ChannelSftp getSFTPChannel() throws JSchException {
		if (sftpChannel != null)
			return sftpChannel;
		Channel channel = session.openChannel("sftp");
		channel.connect(60000);
		sftpChannel = (ChannelSftp) channel;
		return sftpChannel;
	}

	public Session getSession() {
		return session;
	}

	public void disconnect() {
		if (sftpChannel != null)
			sftpChannel.disconnect();
		session.disconnect();
	}

	String getPassword() {
		return password;
	}

	@Override
	public void close() throws Exception {
		disconnect();
	}

}

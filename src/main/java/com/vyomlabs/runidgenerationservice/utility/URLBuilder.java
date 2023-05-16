package com.vyomlabs.runidgenerationservice.utility;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vyomlabs.runidgenerationservice.config.ControlMConfig;

public class URLBuilder {

	Logger logger = LoggerFactory.getLogger(URLBuilder.class);

	FileInputStream fileInputStream = null;
	FileOutputStream fileOutputStream = null;
	FileUtility fileUtility = new FileUtility();
	File file;
	ControlMConfig config = null;
	public String serverName;
	public String port;

	public URLBuilder() {
		try {
			file = new File(Path.of("").toAbsolutePath().toString() + "\\" + "ServerDetails.txt");
			boolean result = file.createNewFile();
			if (result) {
				logger.info("File Created : " + file.getName() + ", at path :" + file.getAbsolutePath());
				logger.info("Please update your server name and port details in it...");
				storeControlMProperties();
			} else {
				logger.info("File already exists : " + file.getName() + " \n at path:" + file.getAbsolutePath());
				config = getControlMProperties();
			}
		} catch (IOException e) {
			logger.info("Exception caught in URLBuilder constructor:" + e.getMessage());
		}

	}

	@Override
	public String toString() {
		return "URLBuilder [serverName=" + serverName + ", port=" + port + "]";
	}

	public ControlMConfig getControlMProperties() throws FileNotFoundException, IOException {
		logger.trace("Inside getControlMProperties method..");
		ControlMConfig controlMConfig = new ControlMConfig();
		fileInputStream = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(fileInputStream);
		controlMConfig.setSessionLoginApi(properties.getProperty("session.login.api"));
		controlMConfig.setOrderApi(properties.getProperty("order.api"));
		controlMConfig.setJobStatusApi(properties.getProperty("job.status.api"));
		controlMConfig.setSLAStatusApi("/automation-api/run/services/sla");
		controlMConfig.setRunJobsApi("/automation-api/run/jobs/status?jobname=SLA_ControlM_");
		controlMConfig.setServerName(properties.getProperty("server.name"));
		controlMConfig.setPort(Integer.parseInt(properties.getProperty("port")));
		fileInputStream.close();
		return controlMConfig;
	}

	private void storeControlMProperties() throws IOException {
		logger.trace("Inside storeControlMProperties method..");
		fileInputStream = new FileInputStream(file);
		Properties properties = new Properties();
		properties.load(fileInputStream);
		fileInputStream.close();
		fileOutputStream = new FileOutputStream(file);
		properties.setProperty("session.login.api", "/automation-api/session/login");
		properties.setProperty("order.api", "/automation-api/run/order");
		properties.setProperty("job.status.api", "/automation-api/run/status/");
		properties.setProperty("server.name", "HOST_NAME");
		properties.setProperty("port", "8443");
		properties.store(fileOutputStream, null);
		fileOutputStream.close();
	}

	public String buildLoginURL() throws FileNotFoundException, IOException {
		StringBuffer sb = new StringBuffer();
		logger.trace("Inside buildLoginURL method");
		sb.append("https://");
		sb.append(config.getServerName()).append(":").append(config.getPort()).append(config.getSessionLoginApi());
		logger.trace("Login URL:" + sb.toString());
		return sb.toString();
	}

	public String buildRunOrderURL() throws FileNotFoundException, IOException {
		StringBuffer sb = new StringBuffer();
		logger.trace("Inside buildRunOrderURL method");
		sb.append("https://");
		sb.append(config.getServerName()).append(":").append(config.getPort()).append(config.getOrderApi());
		logger.trace("Run Order URL:" + sb.toString());
		return sb.toString();
	}

	public String buildJobStatusURL() throws FileNotFoundException, IOException {
		StringBuffer sb = new StringBuffer();
		logger.trace("Inside buildJobStatusURL method");
		sb.append("https://");
		sb.append(config.getServerName()).append(":").append(config.getPort()).append(config.getJobStatusApi());
		logger.trace("Job Status URL:" + sb.toString());
		return sb.toString();
	}

	public String buildSLAServicesURL() {
		StringBuffer sb = new StringBuffer();
		logger.trace("Inside buildSLAServicesURL method");
		sb.append("https://");
		sb.append(config.getServerName()).append(":").append(config.getPort()).append(config.getSLAStatusApi());
		logger.trace("SLA Services URL:" + sb.toString());
		return sb.toString();
	}

	public String buildRunJobsURL(String folderName) {
		StringBuffer sb = new StringBuffer();
		logger.trace("Inside buildRunJobsURL() method");
		sb.append("https://");
		sb.append(config.getServerName()).append(":").append(config.getPort()).append(config.getRunJobsApi())
				.append(folderName);
		logger.trace("Run Jobs URL:" + sb.toString());
		return sb.toString();
	}

}

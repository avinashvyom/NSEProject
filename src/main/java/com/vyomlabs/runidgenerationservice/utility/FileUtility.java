package com.vyomlabs.runidgenerationservice.utility;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileUtility {

	Logger logger = LoggerFactory.getLogger(FileUtility.class);

	File createFile(String fileName) throws IOException {
		File file;
		logger.trace("Inside createFile method...");
		file = new File(Path.of("").toAbsolutePath().toString() + "\\" + fileName);
		boolean result = file.createNewFile();
		if (result) {
			logger.info("File Created : " + fileName + ", at path :" + file.getAbsolutePath());
			return file;
		} else {
			logger.info("File already exists : " + fileName + "\n at path:" + file.getAbsolutePath());
			return file;
		}

	}

}

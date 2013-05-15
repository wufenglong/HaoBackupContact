package com.hao.contact.backup.model;

import java.io.File;
import java.io.FilenameFilter;

import android.os.Environment;

public class FileNameSelector implements FilenameFilter {
	String extension = ".";
	public final static String FILE_DIR_NAME = "/HaoBackup";
	public final static String ROOT_PATH = Environment
			.getExternalStorageDirectory() + FILE_DIR_NAME;

	public FileNameSelector(String fileExtensionNoDot) {
		extension += fileExtensionNoDot;
	}

	public boolean accept(File dir, String name) {
		return name.endsWith(extension);
	}
}

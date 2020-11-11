package org.blkj.utils.base;
//用来过滤文件
import java.io.File;
import java.io.FilenameFilter;

public class FileManagerFilter implements FilenameFilter {
	private String name;

	private String extension;

	public FileManagerFilter(String name, String extension) {
		this.name = name;
		this.extension = extension;

	}

	public boolean accept(File dir, String filename) {
		boolean fileOK = true;
		if (name == "*"&&extension=="*") {
			return fileOK = true;

		}
		if (name != null) {
			// 不大解理"&="的运行过程,
			fileOK &= filename.startsWith(name);

		}
		if (extension != null) {
			fileOK &= filename.endsWith('.' + extension);
		}

		return fileOK;
	}

}

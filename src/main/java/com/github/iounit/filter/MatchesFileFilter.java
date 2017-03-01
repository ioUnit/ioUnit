package com.github.iounit.filter;

import java.io.File;
import java.util.regex.Pattern;

public class MatchesFileFilter extends VisibleFileFilter {

	Pattern pattern;


	Pattern excludePattern;

	public MatchesFileFilter(String matches, String exclude) {
		super();
		this.pattern = Pattern.compile(matches == null || matches.trim().isEmpty() ? ".*" : matches);
		this.excludePattern = exclude == null ? null : Pattern.compile(exclude);
	}

	@Override
	public boolean accept(File file) {
		return super.accept(file)
				&& (pattern.matcher(file.getName()).matches() || pattern.matcher(file.getPath()).matches())
				&& !(excludePattern.matcher(file.getName()).matches()
						|| excludePattern.matcher(file.getPath()).matches());
	}
	
	public Pattern getPattern() {
		return pattern;
	}

	public Pattern getExcludePattern() {
		return excludePattern;
	}

}
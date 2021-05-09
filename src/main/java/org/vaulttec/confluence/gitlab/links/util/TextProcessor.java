/*
 * GitLab Links for Confluence
 * Copyright (c) 2021 Torsten Juergeleit
 * mailto:torsten AT vaulttec DOT org
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.vaulttec.confluence.gitlab.links.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextProcessor {

	private static final Pattern LINE_PATTERN = Pattern.compile("(^).*($)", Pattern.MULTILINE);

	private final String text;
	private final List<Line> lines;

	public TextProcessor(String text) {
		this.text = text;
		this.lines = new ArrayList<>();

		// Find all lines in given text
		Matcher lineMatcher = LINE_PATTERN.matcher(text);
		while (lineMatcher.find()) {
			int start = lineMatcher.start(1);
			int end = lineMatcher.start(2) >= 0 ? lineMatcher.start(2) : text.length();
			this.lines.add(new Line(start, end));
		}
	}

	public int getLineCount() {
		return lines.size();
	}

	public String getText() {
		return text;
	}

	public String getText(int startLineNo) {
		if (startLineNo > 0 && startLineNo <= getLineCount()) {
			int start = lines.get(startLineNo - 1).start;
			return text.substring(start);
		}
		return null;
	}

	public String getText(int startLineNo, int endLineNo) {
		if (startLineNo > 0 && endLineNo >= startLineNo && endLineNo <= getLineCount()) {
			int start = lines.get(startLineNo - 1).start;
			int end = lines.get(endLineNo - 1).end;
			return text.substring(start, end);
		}
		return null;
	}

	private class Line {
		private final int start;
		private final int end;

		public Line(int start, int end) {
			this.start = start;
			this.end = end;
		}
	}
}

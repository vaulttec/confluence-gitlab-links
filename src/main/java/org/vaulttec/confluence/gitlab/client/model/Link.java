/*
 * GitLab Link for Confluence
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
package org.vaulttec.confluence.gitlab.client.model;

public class Link {
	private final String serverUrl;
	private final String url;
	private final Type type;

	public Link(String serverUrl, String url) {
		this.serverUrl = serverUrl;
		this.url = url;
		this.type = determineType();
	}

	public String getUrl() {
		return url;
	}

	public Type getType() {
		return type;
	}

	public String getGroup() {
		if (type != Type.INVALID) {
			String context = getContext();
			if (type == Type.GROUP) {
				return context;
			}
			return context.substring(0, context.indexOf("/"));
		}
		return null;
	}

	public String getProject() {
		if (type != Type.INVALID && type != Type.GROUP) {
			String context = getContext();
			int delimiter = context.indexOf("/-/");
			return delimiter >= 0 ? context.substring(0, delimiter) : context;
		}
		return null;
	}

	public String getName() {
		if (type != Type.INVALID) {
			String context = getContext();
			switch (type) {
			case GROUP:
				return context;
			case PROJECT:
				return context.substring(context.indexOf("/") + 1);
			case BRANCH:
				return context.substring(context.indexOf("/-/tree/") + 8);
			case FOLDER: {
				String subContext = context.substring(context.indexOf("/-/tree/") + 8);
				return subContext.substring(subContext.indexOf("/") + 1);
			}
			case FILE: {
				String subContext = context.substring(context.indexOf("/-/blob/") + 8);
				return subContext.substring(subContext.indexOf("/") + 1);
			}
			case COMMIT:
				return context.substring(context.indexOf("/-/commit/") + 10);
			case ISSUE:
				return context.substring(context.indexOf("/-/issues/") + 10);
			case MERGE_REQUEST:
				return context.substring(context.indexOf("/-/merge_requests/") + 18);
			case TAG:
				return context.substring(context.indexOf("/-/tags/") + 8);
			case RELEASE:
				return context.substring(context.indexOf("/-/releases/") + 12);
			default:
				break;
			}
		}
		return null;
	}

	public String getFileName() {
		if (type == Type.FILE) {
			String[] nameSegments = getName().split("/");
			return nameSegments[nameSegments.length - 1];
		}
		return null;
	}

	public String getBranch() {
		if (type == Type.BRANCH || type == Type.FOLDER || type == Type.FILE) {
			String context = getContext();
			switch (type) {
			case BRANCH:
				return context.substring(context.indexOf("/-/tree/") + 8);
			case FOLDER: {
				String subContext = context.substring(context.indexOf("/-/tree/") + 8);
				return subContext.substring(0, subContext.indexOf("/"));
			}
			case FILE: {
				String subContext = context.substring(context.indexOf("/-/blob/") + 8);
				return subContext.substring(0, subContext.indexOf("/"));
			}
			default:
				break;
			}
		}
		return null;
	}

	@Override
	public String toString() {
		return "Link [type=" + type + (type != Type.INVALID ? ", context=" + getContext() + ", name=" + getName() : "") + "]";
	}

	private Type determineType() {
		if (url != null && url.startsWith(serverUrl)) {
			String context = getContext();
			if (!context.contains("/")) {
				return Type.GROUP;
			}
			if (!context.contains("/-/")) {
				return Type.PROJECT;
			}
			if (context.contains("/-/tree/")) {
				String name = context.substring(context.indexOf("/-/tree/") + 8);
				if (name.contains("/")) {
					return Type.FOLDER;
				}
				return Type.BRANCH;
			}
			if (context.contains("/-/blob/")) {
				return Type.FILE;
			}
			if (context.contains("/-/commit/")) {
				return Type.COMMIT;
			}
			if (context.contains("/-/issues/")) {
				return Type.ISSUE;
			}
			if (context.contains("/-/merge_requests/")) {
				return Type.MERGE_REQUEST;
			}
			if (context.contains("/-/tags/")) {
				return Type.TAG;
			}
			if (context.contains("/-/releases/")) {
				return Type.RELEASE;
			}
		}
		return Type.INVALID;
	}

	private String getContext() {
		return url.substring(serverUrl.length() + 1);
	}

	public enum Type {
		INVALID, GROUP, PROJECT, BRANCH, FOLDER, FILE, COMMIT, ISSUE, MERGE_REQUEST, TAG, RELEASE
	}
}

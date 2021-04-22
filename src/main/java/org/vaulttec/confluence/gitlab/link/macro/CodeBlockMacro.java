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
package org.vaulttec.confluence.gitlab.link.macro;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.vaulttec.confluence.gitlab.client.GitLabClient;
import org.vaulttec.confluence.gitlab.client.model.Link;
import org.vaulttec.confluence.gitlab.client.model.Link.Type;
import org.vaulttec.confluence.gitlab.link.util.NumberProcessor;
import org.vaulttec.confluence.gitlab.link.util.TextProcessor;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;

public class CodeBlockMacro implements Macro {

	private final UserManager userManager;
	private final GitLabClient gitlabClient;

	public CodeBlockMacro(UserManager userManager, GitLabClient gitlabClient) {
		this.userManager = userManager;
		this.gitlabClient = gitlabClient;
	}

	@Override
	public String execute(Map<String, String> parameters, String bodyContent, ConversionContext conversionContext)
			throws MacroExecutionException {
		System.out.println(parameters);
		String url = parameters.get("url");
		String language = parameters.getOrDefault("language", "java");
		Boolean gutter = Boolean.valueOf(parameters.getOrDefault("gutter", "false"));
		String firstLineText = parameters.get("firstLine");
		String lastLineText = parameters.get("lastLine");
		String theme = parameters.getOrDefault("theme", "Confluence");

		Map<String, Object> context = MacroUtils.defaultVelocityContext();
		context.put("language", language);
		context.put("gutter", gutter);
		context.put("firstLine", firstLineText);
		context.put("lastLine", lastLineText);
		context.put("theme", theme);

		// First check URL
		if (StringUtils.isNotEmpty(url)) {
			Link link = gitlabClient.getLink(url);
			context.put("link", link);
			if (link.getType() == Type.FILE) {

				// Get file content as currrent authenticated Confluence user
				UserProfile userProfile = userManager.getRemoteUser();
				String file = gitlabClient.getRawFile(link.getProject(), link.getName(), link.getBranch(),
						userProfile.getUsername());
				if (file != null) {
					TextProcessor textProc = new TextProcessor(file);

					// Now check line numbers
					int firstLine;
					if (StringUtils.isEmpty(firstLineText)) {
						firstLine = 1;
					} else {
						NumberProcessor firstLineProc = new NumberProcessor(firstLineText);
						firstLine = firstLineProc.getInt();
					}
					if (firstLine >= 1 && firstLine <= textProc.getLineCount()) {
						int lastLine;
						if (StringUtils.isEmpty(lastLineText)) {
							lastLine = textProc.getLineCount();
						} else {
							NumberProcessor lastLineProc = new NumberProcessor(lastLineText);
							lastLine = lastLineProc.getInt();
						}
						if (lastLine >= 1 && lastLine >= firstLine && lastLine <= textProc.getLineCount()) {
							context.put("file", textProc.getText(firstLine, lastLine));
						} else {
							context.put("error", "org.vaulttec.confluence-gitlab-link.code-block.macro.error.invalid_last_line");
						}
					} else {
						context.put("error", "org.vaulttec.confluence-gitlab-link.code-block.macro.error.invalid_first_line");
					}
				} else {
					context.put("error", "org.vaulttec.confluence-gitlab-link.code-block.macro.error.not_accessible");
				}
			} else {
				context.put("error", "org.vaulttec.confluence-gitlab-link.code-block.macro.error.invalid_url");
			}
		} else {
			context.put("error", "org.vaulttec.confluence-gitlab-link.code-block.macro.error.no_url");
		}
		return VelocityUtils.getRenderedTemplate("templates/code-block-macro.vm", context);
	}

	@Override
	public BodyType getBodyType() {
		return BodyType.NONE;
	}

	@Override
	public OutputType getOutputType() {
		return OutputType.BLOCK;
	}
}

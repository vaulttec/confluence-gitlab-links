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
import org.vaulttec.confluence.gitlab.client.model.Issue;
import org.vaulttec.confluence.gitlab.client.model.Link;
import org.vaulttec.confluence.gitlab.client.model.Link.Type;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;

public class IssueMacro implements Macro {

	private final UserManager userManager;
	private final GitLabClient gitlabClient;

	public IssueMacro(UserManager userManager, GitLabClient gitlabClient) {
		this.userManager = userManager;
		this.gitlabClient = gitlabClient;
	}

	@Override
	public String execute(Map<String, String> parameters, String bodyContent, ConversionContext conversionContext)
			throws MacroExecutionException {
		String url = parameters.get("url");
		Map<String, Object> context = MacroUtils.defaultVelocityContext();

		// First check URL
		if (StringUtils.isNotEmpty(url)) {
			Link link = gitlabClient.getLink(url);
			if (link.getType() == Type.ISSUE && StringUtils.isNotEmpty(link.getName())) {
				context.put("link", link);

				// Get issue details as current authenticated Confluence user
				UserProfile userProfile = userManager.getRemoteUser();
				if (userProfile != null) {
					Issue issue = gitlabClient.getIssue(link.getGroupAndProject(), link.getName(), userProfile.getUsername());
					if (issue != null) {
						context.put("issue", issue);
					} else {
						context.put("error", "org.vaulttec.confluence-gitlab-link.project.macro.error.not_accessible");
					}
				} else {
					context.put("error", "org.vaulttec.confluence-gitlab-link.project.macro.error.not_accessible");
				}
			} else {
				context.put("error", "org.vaulttec.confluence-gitlab-link.project.macro.error.invalid_url");
			}
		} else {
			context.put("error", "org.vaulttec.confluence-gitlab-link.project.macro.error.no_url");
		}
		return VelocityUtils.getRenderedTemplate("templates/issue-macro.vm", context);
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

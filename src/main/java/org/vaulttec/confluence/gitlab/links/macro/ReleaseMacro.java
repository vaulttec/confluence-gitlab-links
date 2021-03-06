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
package org.vaulttec.confluence.gitlab.links.macro;

import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.vaulttec.confluence.gitlab.links.client.GitLabClient;
import org.vaulttec.confluence.gitlab.links.client.model.Link;
import org.vaulttec.confluence.gitlab.links.client.model.Link.Type;
import org.vaulttec.confluence.gitlab.links.client.model.Release;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.renderer.radeox.macros.MacroUtils;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.sal.api.user.UserProfile;

public class ReleaseMacro implements Macro {

	private final UserManager userManager;
	private final GitLabClient gitlabClient;

	public ReleaseMacro(UserManager userManager, GitLabClient gitlabClient) {
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
			if (link.getType() == Type.RELEASE && StringUtils.isNotEmpty(link.getName())) {
				context.put("link", link);

				// Only access GitLab API if API key is provided
				if (gitlabClient.hasApiKey()) {

					// Get release details as current authenticated Confluence user
					UserProfile userProfile = userManager.getRemoteUser();
					if (userProfile != null) {
						Release release = gitlabClient.getRelease(link.getGroupAndProject(), link.getName(),
								userProfile.getUsername());
						if (release != null) {
							context.put("release", release);
						} else {
							context.put("error",
									"org.vaulttec.confluence-gitlab-links.release.macro.error.not_accessible");
						}
					}
				} else {
					context.put("error", "org.vaulttec.confluence-gitlab-links.release.macro.error.not_accessible");
				}
			} else {
				context.put("error", "org.vaulttec.confluence-gitlab-links.release.macro.error.invalid_url");
			}
		} else {
			context.put("error", "org.vaulttec.confluence-gitlab-links.release.macro.error.no_url");
		}
		return VelocityUtils.getRenderedTemplate("templates/release-macro.vm", context);
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

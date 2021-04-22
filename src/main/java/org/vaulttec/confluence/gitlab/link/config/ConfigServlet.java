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
package org.vaulttec.confluence.gitlab.link.config;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaulttec.confluence.gitlab.client.GitLabClient;
import org.vaulttec.confluence.gitlab.client.model.Version;
import org.vaulttec.confluence.gitlab.link.Constants;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.user.UserKey;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

public class ConfigServlet extends HttpServlet {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigServlet.class);

	private static final long serialVersionUID = 1L;
	private static final String CONTENT_TYPE = "text/html;charset=utf-8";
	private static final String TEMPLATE = "templates/config.vm";

	private final UserManager userManager;
	private final LoginUriProvider loginUriProvider;
	private final TemplateRenderer renderer;
	private final ConfigStore configStore;
	private final GitLabClient gitlabClient;

	public ConfigServlet(UserManager userManager, LoginUriProvider loginUriProvider, TemplateRenderer renderer,
			ConfigStore configStore, GitLabClient gitlabClient) {
		this.userManager = userManager;
		this.loginUriProvider = loginUriProvider;
		this.renderer = renderer;
		this.configStore = configStore;
		this.gitlabClient = gitlabClient;
	}

	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {
		UserKey userkey = userManager.getRemoteUserKey(request);
		if (userkey == null || !userManager.isSystemAdmin(userkey)) {
			redirectToLogin(request, response);
			return;
		}

		Map<String, Object> context = new HashMap<>();
		context.put(Constants.KEY_SERVER_URL, configStore.getServerUrl());
		context.put(Constants.KEY_API_KEY, configStore.getApiKey());
		context.put(Constants.KEY_DEFAULT_THEME, configStore.getDefaultTheme());

		response.setContentType(CONTENT_TYPE);
		renderer.render(TEMPLATE, context, response.getWriter());
	}

	private void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
		response.sendRedirect(loginUriProvider.getLoginUri(getUri(request)).toASCIIString());
	}

	private URI getUri(HttpServletRequest request) {
		StringBuffer builder = request.getRequestURL();
		if (request.getQueryString() != null) {
			builder.append("?");
			builder.append(request.getQueryString());
		}
		return URI.create(builder.toString());
	}

	@Override
	public void doPost(final HttpServletRequest request, final HttpServletResponse response) throws IOException {
		Map<String, Object> context = new HashMap<>();
		context.put("success", "success");

		String url = StringUtils.trimToEmpty(request.getParameter(Constants.KEY_SERVER_URL));
		context.put(Constants.KEY_SERVER_URL, url);
		if (StringUtils.isNotEmpty(url) && (url.startsWith("https://") || url.startsWith("http://"))) {
			configStore.setServerUrl(url);
		} else {
			context.put(Constants.KEY_SERVER_URL + "Error", "error");
			context.remove("success");
		}

		String key = StringUtils.trimToEmpty(request.getParameter(Constants.KEY_API_KEY));
		context.put(Constants.KEY_API_KEY, key);
		if (StringUtils.isNotEmpty(key)) {
			configStore.setApiKey(key);
		} else {
			context.put(Constants.KEY_API_KEY + "Error", "error");
			context.remove("success");
		}

		// Validate url and API key
		if (context.containsKey("success")) {
			LOG.debug("Checking GitLab Server at {}", url);
			Version version = gitlabClient.getVersion();
			if (version != null) {
				LOG.info("GitLab Server {} version {} (revision {})", url, version.getVersion(), version.getRevision());
			} else {
				context.remove("success");
				context.put("error", "error");
			}
		}

		String defaultTheme = request.getParameter(Constants.KEY_DEFAULT_THEME);
		configStore.setDefaultTheme(defaultTheme);
		context.put(Constants.KEY_DEFAULT_THEME, defaultTheme);

		response.setContentType(CONTENT_TYPE);
		renderer.render(TEMPLATE, context, response.getWriter());
	}
}

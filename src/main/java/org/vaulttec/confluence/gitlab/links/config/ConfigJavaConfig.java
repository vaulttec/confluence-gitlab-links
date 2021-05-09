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
package org.vaulttec.confluence.gitlab.links.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.vaulttec.confluence.gitlab.links.client.GitLabClient;

import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

@Configuration
public class ConfigJavaConfig {

	@Bean
	public ConfigStore configStore(PluginSettingsFactory pluginSettingsFactory) {
		return new ConfigStoreImpl(pluginSettingsFactory);
	}

	@Bean
	public ConfigServlet configServlet(UserManager userManager, LoginUriProvider loginUriProvider,
			TemplateRenderer renderer, ConfigStore configStore, GitLabClient gitlabClient) {
		return new ConfigServlet(userManager, loginUriProvider, renderer, configStore, gitlabClient);
	}
}

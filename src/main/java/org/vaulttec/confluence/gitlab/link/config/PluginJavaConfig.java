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

import static com.atlassian.plugins.osgi.javaconfig.OsgiServices.importOsgiService;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import com.atlassian.plugins.osgi.javaconfig.configs.beans.ModuleFactoryBean;
import com.atlassian.plugins.osgi.javaconfig.configs.beans.PluginAccessorBean;
import com.atlassian.sal.api.ApplicationProperties;
import com.atlassian.sal.api.auth.LoginUriProvider;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.templaterenderer.TemplateRenderer;

@Configuration
@Import({ ModuleFactoryBean.class, PluginAccessorBean.class, })
public class PluginJavaConfig {

	// Imports services from OSGi
	@Bean
	public ApplicationProperties applicationProperties() {
		return importOsgiService(ApplicationProperties.class);
	}

	@Bean
	public UserManager userManager() {
		return importOsgiService(UserManager.class);
	}

	@Bean
	public LoginUriProvider loginUriProvider() {
		return importOsgiService(LoginUriProvider.class);
	}

	@Bean
	public TemplateRenderer renderer() {
		return importOsgiService(TemplateRenderer.class);
	}

	@Bean
	public PluginSettingsFactory settingsFactory() {
		return importOsgiService(PluginSettingsFactory.class);
	}

	@Bean
	public RequestFactory<?> requestFactory() {
		return importOsgiService(RequestFactory.class);
	}
}
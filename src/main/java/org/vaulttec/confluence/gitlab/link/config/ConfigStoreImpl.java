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

import org.vaulttec.confluence.gitlab.link.Constants;

import com.atlassian.sal.api.pluginsettings.PluginSettingsFactory;

public class ConfigStoreImpl implements ConfigStore {
	private static final String KEY_SERVER_URL = Constants.PLUGIN_STORAGE_KEY + "." + Constants.KEY_SERVER_URL;
	private static final String KEY_API_KEY = Constants.PLUGIN_STORAGE_KEY + "." + Constants.KEY_API_KEY;
	private static final String KEY_DEFAULT_THEME = Constants.PLUGIN_STORAGE_KEY + "." + Constants.KEY_DEFAULT_THEME;

	private PluginSettingsFactory pluginSettingsFactory;

	public ConfigStoreImpl(PluginSettingsFactory pluginSettingsFactory) {
		this.pluginSettingsFactory = pluginSettingsFactory;
	}

	@Override
	public String getServerUrl() {
		return (String) pluginSettingsFactory.createGlobalSettings().get(KEY_SERVER_URL);
	}

	@Override
	public void setServerUrl(String url) {
		// Remove trailing slashes
		while (url.endsWith("/")) {
			url = url.substring(0, url.length() - 1);
		}
		pluginSettingsFactory.createGlobalSettings().put(KEY_SERVER_URL, url);
	}

	@Override
	public String getApiUrl() {
		return getServerUrl() + "/api/v4";
	}

	@Override
	public String getApiKey() {
		return (String) pluginSettingsFactory.createGlobalSettings().get(KEY_API_KEY);
	}

	@Override
	public void setApiKey(String key) {
		pluginSettingsFactory.createGlobalSettings().put(KEY_API_KEY, key);
	}

	@Override
	public String getDefaultTheme() {
		return (String) pluginSettingsFactory.createGlobalSettings().get(KEY_DEFAULT_THEME);
	}

	@Override
	public void setDefaultTheme(String theme) {
		pluginSettingsFactory.createGlobalSettings().put(KEY_DEFAULT_THEME, theme);
	}
}

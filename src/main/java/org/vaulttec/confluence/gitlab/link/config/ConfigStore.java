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

/**
 * Interface for storing Plugin Configuration. Implementations can decide if
 * they should be backed by a persistent store or not.
 */
public interface ConfigStore {
	String getServerUrl();
	void setServerUrl(String url);

	String getApiUrl();

	String getApiKey();
	void setApiKey(String key);

	String getDefaultTheme();
	void setDefaultTheme(String theme);
}

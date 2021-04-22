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
package org.vaulttec.confluence.gitlab.link.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaulttec.confluence.gitlab.link.config.ConfigStore;

import com.atlassian.plugins.rest.common.security.AnonymousAllowed;

/**
 * REST API for accessing current config settings.
 * <p>
 * This API is used e.g in "autoconvert-gitlab-link.js".
 */
@Path("/config")
public class ConfigResource {

	private static final Logger LOG = LoggerFactory.getLogger(ConfigResource.class);

	private ConfigStore configStore;

	public ConfigResource(ConfigStore configStore) {
		this.configStore = configStore;
	}

	@GET
	@Produces(MediaType.APPLICATION_JSON)
	@AnonymousAllowed
	public Response getConfig() {
		String serverUrl = configStore.getServerUrl();
		Config config = new Config(serverUrl, serverUrl.split("://")[1], configStore.getDefaultTheme());
		LOG.debug("Sending plugin config: {}", config);
		return Response.ok().entity(config).build();
	}

	@XmlRootElement
	@XmlAccessorType(XmlAccessType.FIELD)
	public static final class Config {
		@XmlElement
		private final String serverUrl;

		@XmlElement
		private final String serverHost;

		@XmlElement
		private final String defaultTheme;

		public Config(String serverUrl, String serverHost, String defaultTheme) {
			this.serverUrl = serverUrl;
			this.serverHost = serverHost;
			this.defaultTheme = defaultTheme;
		}

		public String getServerUrl() {
			return this.serverUrl;
		}

		public String getServerHost() {
			return this.serverHost;
		}

		public String getDefaultTheme() {
			return defaultTheme;
		}

		@Override
		public String toString() {
			return "Config [serverUrl=" + serverUrl + ", serverHost=" + serverHost + ", defaultTheme=" + defaultTheme
					+ "]";
		}
	}
}

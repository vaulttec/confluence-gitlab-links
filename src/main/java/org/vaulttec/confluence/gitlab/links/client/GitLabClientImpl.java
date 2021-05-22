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
package org.vaulttec.confluence.gitlab.links.client;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import javax.ws.rs.core.HttpHeaders;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.vaulttec.confluence.gitlab.links.client.model.Issue;
import org.vaulttec.confluence.gitlab.links.client.model.Link;
import org.vaulttec.confluence.gitlab.links.client.model.MergeRequest;
import org.vaulttec.confluence.gitlab.links.client.model.Milestone;
import org.vaulttec.confluence.gitlab.links.client.model.Release;
import org.vaulttec.confluence.gitlab.links.client.model.Version;
import org.vaulttec.confluence.gitlab.links.config.ConfigStore;

import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.Request.MethodType;
import com.atlassian.sal.api.net.RequestFactory;
import com.atlassian.sal.api.net.Response;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.sal.api.net.ResponseStatusException;
import com.atlassian.sal.api.net.ReturningResponseHandler;

public class GitLabClientImpl implements GitLabClient {
	private static final Logger LOG = LoggerFactory.getLogger(GitLabClientImpl.class);
	private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper()
			.configure(DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES, false);

	private static final TypeReference<List<Milestone>> RESPONSE_TYPE_MILESTONES = new TypeReference<List<Milestone>>() {
	};

	private final RequestFactory<?> requestFactory;
	private final ConfigStore configStore;

	public GitLabClientImpl(RequestFactory<?> requestFactory, ConfigStore configStore) {
		this.requestFactory = requestFactory;
		this.configStore = configStore;
	}

	@Override
	public String getServerUrl() {
		return configStore.getServerUrl();
	}

	@Override
	public boolean hasApiKey() {
		return StringUtils.isNotEmpty(configStore.getApiKey());
	}

	@Override
	public Link getLink(String url) {
		return new Link(configStore.getServerUrl(), url);
	}

	@Override
	public Version getVersion() {
		LOG.debug("Get version of GitLab instance");
		return get("/version?sudo=ghost", Version.class);
	}

	@Override
	public Issue getIssue(String projectId, String issueId, String username) {
		LOG.debug("Get details of issue '{}' in project '{}' for user '{}'", issueId, projectId, username);
		return get("/projects/" + encode(projectId) + "/issues/" + issueId + "?sudo=" + username, Issue.class);
	}

	@Override
	public MergeRequest getMergeRequest(String projectId, String mergeRequestId, String username) {
		LOG.debug("Get details of merge request '{}' in project '{}' for user '{}'", mergeRequestId, projectId,
				username);
		return get("/projects/" + encode(projectId) + "/merge_requests/" + mergeRequestId + "?sudo=" + username,
				MergeRequest.class);
	}

	@Override
	public Milestone getMilestone(String projectId, String milestoneId, String username, boolean isInGroup) {
		LOG.debug("Get details of milestone '{}' in {} '{}' for user '{}'", milestoneId,
				(isInGroup ? "group" : "project"), projectId, username);
		List<Milestone> milestones = getList((isInGroup ? "/groups/" : "/projects/") + encode(projectId)
				+ "/milestones?iids[]=" + milestoneId + "&sudo=" + username, RESPONSE_TYPE_MILESTONES);
		return milestones != null && !milestones.isEmpty() ? milestones.get(0) : null;
	}

	@Override
	public Release getRelease(String projectId, String tagName, String username) {
		LOG.debug("Get details of release for tag '{}' in project '{}' for user '{}'", tagName, projectId, username);
		return get("/projects/" + encode(projectId) + "/releases/" + tagName + "?sudo=" + username, Release.class);
	}

	@Override
	public String getRawFile(String projectId, String filePath, String ref, String username) {
		LOG.debug("Get raw file '{}' ({}) in project '{}' for user '{}'", filePath, ref, projectId, username);
		return get("/projects/" + encode(projectId) + "/repository/files/" + encode(filePath) + "/raw?ref=" + ref
				+ "&sudo=" + username);
	}

	protected <R> List<R> getList(String apiEndPoint, TypeReference<List<R>> typeReference) {
		Request<?, ?> request = createRequest(Request.MethodType.GET, apiEndPoint);
		try {
			String response = request.execute();
			JsonParser jsonParser = OBJECT_MAPPER.getJsonFactory().createJsonParser(response);
			List<R> result = jsonParser.<List<R>>readValueAs(typeReference);
			return result;
		} catch (ResponseException | IOException e) {
			logException(Request.MethodType.GET, null, apiEndPoint, e);
		}
		return null;
	}

	protected <R> R get(String apiEndPoint, Class<R> resultClass) {
		Request<?, ?> request = createRequest(Request.MethodType.GET, apiEndPoint);
		try {
			return request.executeAndReturn(new ReturningResponseHandler<Response, R>() {

				@Override
				public R handle(Response response) throws ResponseException {
					if (response.isSuccessful()) {
						return response.getEntity(resultClass);
					}
					logErrorResponse(Request.MethodType.GET, null, apiEndPoint, response);
					return null;
				}
			});
		} catch (ResponseException e) {
			logException(Request.MethodType.GET, null, apiEndPoint, e);
		}
		return null;
	}

	protected String get(String apiEndPoint) {
		Request<?, ?> request = createRequest(Request.MethodType.GET, apiEndPoint);
		try {
			return request.execute();
		} catch (ResponseException e) {
			logException(Request.MethodType.GET, null, apiEndPoint, e);
		}
		return null;
	}

	private Request<?, ?> createRequest(Request.MethodType type, String apiEndPoint) {
		Request<?, ?> request = requestFactory.createRequest(type, configStore.getApiUrl() + apiEndPoint);
		request.setHeader(HttpHeaders.AUTHORIZATION, "Bearer " + configStore.getApiKey());
		return request;
	}

	private void logException(Request.MethodType type, Map<String, String> uriVariables, String url, Exception e) {
		if (e instanceof ResponseStatusException) {
			Response response = ((ResponseStatusException) e).getResponse();
			if (response.getStatusCode() == 404) {
				LOG.debug("Resource '{}' not accessible", url);
			} else {
				LOG.error("API call {} '{}' {} failed with status {}: {}", type.name(), url,
						uriVariables != null ? uriVariables : "", response.getStatusText(), e.getMessage());
			}
		} else if (e instanceof ResponseException) {
			LOG.error("API call {} '{}' {} failed: {}", type.name(), url, uriVariables != null ? uriVariables : "",
					e.getMessage());
		} else {
			LOG.error("API call {} '{}' {} failed", type.name(), url, uriVariables != null ? uriVariables : "", e);
		}
	}

	private void logErrorResponse(MethodType type, Map<String, String> uriVariables, String url, Response response) {
		if (response.getStatusCode() == 404) {
			LOG.debug("Resource '{}' not accessible", url);
		} else {
			LOG.error("API call {} '{}' {} failed with status {}", type.name(), url,
					uriVariables != null ? uriVariables : "", response.getStatusText());
		}
	}

	private String encode(String text) {
		try {
			text = URLEncoder.encode(text, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			// Ignore
		}
		return text;
	}
}

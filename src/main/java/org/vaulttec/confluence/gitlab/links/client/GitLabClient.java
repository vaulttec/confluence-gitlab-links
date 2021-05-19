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

import org.vaulttec.confluence.gitlab.links.client.model.Issue;
import org.vaulttec.confluence.gitlab.links.client.model.Link;
import org.vaulttec.confluence.gitlab.links.client.model.MergeRequest;
import org.vaulttec.confluence.gitlab.links.client.model.Milestone;
import org.vaulttec.confluence.gitlab.links.client.model.Release;
import org.vaulttec.confluence.gitlab.links.client.model.Version;

public interface GitLabClient {
	String getServerUrl();

	Link getLink(String url);

	Version getVersion();

	Issue getIssue(String projectId, String issueId, String username);

	MergeRequest getMergeRequest(String projectId, String mergeRequestId, String username);

	Milestone getMilestone(String projectId, String milestoneId, String username, boolean isInGroup);

	Release getRelease(String projectId, String tagName, String username);

	String getRawFile(String projectId, String filePath, String ref, String username);
}

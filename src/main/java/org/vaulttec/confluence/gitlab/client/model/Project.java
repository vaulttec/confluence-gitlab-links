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
package org.vaulttec.confluence.gitlab.client.model;

import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
import org.codehaus.jackson.annotate.JsonProperty;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown = true)
public class Project {
	private Integer id;
	@JsonProperty("name_with_namespace")
	private String nameWithNamespace;
	@JsonProperty("web_url")
	private String webUrl;
	@JsonProperty("avatar_url")
	private String avatarUrl;
	@JsonProperty("star_count")
	private Integer starCount;
	@JsonProperty("forks_count")
	private Integer forksCount;
	@JsonProperty("tag_list")
	private List<String> tags;

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getNameWithNamespace() {
		return nameWithNamespace;
	}

	public void setNameWithNamespace(String nameWithNamespace) {
		this.nameWithNamespace = nameWithNamespace;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public Integer getStarCount() {
		return starCount;
	}

	public void setStarCount(Integer starCount) {
		this.starCount = starCount;
	}

	public Integer getForksCount() {
		return forksCount;
	}

	public void setForksCount(Integer forksCount) {
		this.forksCount = forksCount;
	}

	public List<String> getTags() {
		return tags;
	}

	public void setTags(List<String> tags) {
		this.tags = tags;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", nameWithNamespace=" + nameWithNamespace + "]";
	}

}

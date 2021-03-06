/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.nifi.registry.client.impl;

import org.apache.commons.lang3.StringUtils;
import org.apache.nifi.registry.client.ExtensionRepoClient;
import org.apache.nifi.registry.client.NiFiRegistryException;
import org.apache.nifi.registry.extension.repo.ExtensionRepoArtifact;
import org.apache.nifi.registry.extension.repo.ExtensionRepoBucket;
import org.apache.nifi.registry.extension.repo.ExtensionRepoGroup;
import org.apache.nifi.registry.extension.repo.ExtensionRepoVersion;
import org.apache.nifi.registry.extension.repo.ExtensionRepoVersionSummary;

import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class JerseyExtensionRepoClient extends AbstractJerseyClient implements ExtensionRepoClient {

    private WebTarget extensionRepoTarget;

    public JerseyExtensionRepoClient(final WebTarget baseTarget) {
        this(baseTarget, Collections.emptyMap());
    }

    public JerseyExtensionRepoClient(final WebTarget baseTarget, final Map<String, String> headers) {
        super(headers);
        this.extensionRepoTarget = baseTarget.path("extensions/repo");
    }

    @Override
    public List<ExtensionRepoBucket> getBuckets() throws IOException, NiFiRegistryException {
        return executeAction("Error retrieving buckets for extension repo", () -> {
           final ExtensionRepoBucket[] repoBuckets = getRequestBuilder(extensionRepoTarget).get(ExtensionRepoBucket[].class);
           return  repoBuckets == null ? Collections.emptyList() : Arrays.asList(repoBuckets);
        });
    }

    @Override
    public List<ExtensionRepoGroup> getGroups(final String bucketName) throws IOException, NiFiRegistryException {
        if (StringUtils.isBlank(bucketName)) {
            throw new IllegalArgumentException("Bucket name cannot be null or blank");
        }

        return executeAction("Error retrieving groups for extension repo", () -> {
            final WebTarget target = extensionRepoTarget
                    .path("{bucketName}")
                    .resolveTemplate("bucketName", bucketName);

            final ExtensionRepoGroup[] repoGroups = getRequestBuilder(target).get(ExtensionRepoGroup[].class);
            return  repoGroups == null ? Collections.emptyList() : Arrays.asList(repoGroups);
        });
    }

    @Override
    public List<ExtensionRepoArtifact> getArtifacts(final String bucketName, final String groupId)
            throws IOException, NiFiRegistryException {

        if (StringUtils.isBlank(bucketName)) {
            throw new IllegalArgumentException("Bucket name cannot be null or blank");
        }

        if (StringUtils.isBlank(groupId)) {
            throw new IllegalArgumentException("Group id cannot be null or blank");
        }

        return executeAction("Error retrieving artifacts for extension repo", () -> {
            final WebTarget target = extensionRepoTarget
                    .path("{bucketName}/{groupId}")
                    .resolveTemplate("bucketName", bucketName)
                    .resolveTemplate("groupId", groupId);

            final ExtensionRepoArtifact[] repoArtifacts = getRequestBuilder(target).get(ExtensionRepoArtifact[].class);
            return  repoArtifacts == null ? Collections.emptyList() : Arrays.asList(repoArtifacts);
        });
    }

    @Override
    public List<ExtensionRepoVersionSummary> getVersions(final String bucketName, final String groupId, final String artifactId)
            throws IOException, NiFiRegistryException {

        if (StringUtils.isBlank(bucketName)) {
            throw new IllegalArgumentException("Bucket name cannot be null or blank");
        }

        if (StringUtils.isBlank(groupId)) {
            throw new IllegalArgumentException("Group id cannot be null or blank");
        }

        if (StringUtils.isBlank(artifactId)) {
            throw new IllegalArgumentException("Artifact id cannot be null or blank");
        }

        return executeAction("Error retrieving versions for extension repo", () -> {
            final WebTarget target = extensionRepoTarget
                    .path("{bucketName}/{groupId}/{artifactId}")
                    .resolveTemplate("bucketName", bucketName)
                    .resolveTemplate("groupId", groupId)
                    .resolveTemplate("artifactId", artifactId);

            final ExtensionRepoVersionSummary[] repoVersions = getRequestBuilder(target).get(ExtensionRepoVersionSummary[].class);
            return  repoVersions == null ? Collections.emptyList() : Arrays.asList(repoVersions);
        });
    }

    @Override
    public ExtensionRepoVersion getVersion(final String bucketName, final String groupId, final String artifactId, final String version)
            throws IOException, NiFiRegistryException {

        validate(bucketName, groupId, artifactId, version);

        return executeAction("Error retrieving versions for extension repo", () -> {
            final WebTarget target = extensionRepoTarget
                    .path("{bucketName}/{groupId}/{artifactId}/{version}")
                    .resolveTemplate("bucketName", bucketName)
                    .resolveTemplate("groupId", groupId)
                    .resolveTemplate("artifactId", artifactId)
                    .resolveTemplate("version", version);

            return getRequestBuilder(target).get(ExtensionRepoVersion.class);
        });
    }

    @Override
    public InputStream getVersionContent(final String bucketName, final String groupId, final String artifactId, final String version)
            throws IOException, NiFiRegistryException {

        validate(bucketName, groupId, artifactId, version);

        return executeAction("Error retrieving version content for extension repo", () -> {
            final WebTarget target = extensionRepoTarget
                    .path("{bucketName}/{groupId}/{artifactId}/{version}/content")
                    .resolveTemplate("bucketName", bucketName)
                    .resolveTemplate("groupId", groupId)
                    .resolveTemplate("artifactId", artifactId)
                    .resolveTemplate("version", version);

            return getRequestBuilder(target)
                    .accept(MediaType.APPLICATION_OCTET_STREAM_TYPE)
                    .get()
                    .readEntity(InputStream.class);
        });
    }

    @Override
    public String getVersionSha256(final String bucketName, final String groupId, final String artifactId, final String version)
            throws IOException, NiFiRegistryException {

        validate(bucketName, groupId, artifactId, version);

        return executeAction("Error retrieving version content for extension repo", () -> {
            final WebTarget target = extensionRepoTarget
                    .path("{bucketName}/{groupId}/{artifactId}/{version}/sha256")
                    .resolveTemplate("bucketName", bucketName)
                    .resolveTemplate("groupId", groupId)
                    .resolveTemplate("artifactId", artifactId)
                    .resolveTemplate("version", version);

            return getRequestBuilder(target).accept(MediaType.TEXT_PLAIN_TYPE).get(String.class);
        });
    }

    private void validate(String bucketName, String groupId, String artifactId, String version) {
        if (StringUtils.isBlank(bucketName)) {
            throw new IllegalArgumentException("Bucket name cannot be null or blank");
        }

        if (StringUtils.isBlank(groupId)) {
            throw new IllegalArgumentException("Group id cannot be null or blank");
        }

        if (StringUtils.isBlank(artifactId)) {
            throw new IllegalArgumentException("Artifact id cannot be null or blank");
        }

        if (StringUtils.isBlank(version)) {
            throw new IllegalArgumentException("Version cannot be null or blank");
        }
    }
}

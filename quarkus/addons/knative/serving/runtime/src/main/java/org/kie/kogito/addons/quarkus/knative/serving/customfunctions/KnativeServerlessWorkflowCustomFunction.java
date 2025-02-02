/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.addons.quarkus.knative.serving.customfunctions;

import java.net.URI;
import java.util.Map;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.kogito.process.workitem.WorkItemExecutionException;

import com.fasterxml.jackson.databind.JsonNode;

import static org.kie.kogito.addons.quarkus.knative.serving.customfunctions.KnativeWorkItemHandler.OPERATION_PROPERTY_NAME;

/**
 * Implementation of a Serverless Workflow custom function to invoke Knative services.
 * 
 * @see <a href="https://github.com/serverlessworkflow/specification/blob/main/specification.md#defining-custom-function-types">Serverless Workflow specification - Defining custom function types</a>
 */
@ApplicationScoped
final class KnativeServerlessWorkflowCustomFunction {

    static final String CLOUD_EVENT_PROPERTY_NAME = "asCloudEvent";

    static final String PATH_PROPERTY_NAME = "path";

    private final KnativeServiceRegistry knativeServiceRegistry;

    private final KnativeServiceRequestClientResolver knativeServiceRequestClientResolver;

    @Inject
    KnativeServerlessWorkflowCustomFunction(KnativeServiceRegistry knativeServiceRegistry,
            KnativeServiceRequestClientResolver knativeServiceRequestClientResolver) {
        this.knativeServiceRegistry = knativeServiceRegistry;
        this.knativeServiceRequestClientResolver = knativeServiceRequestClientResolver;

    }

    JsonNode execute(String processInstanceId, Map<String, Object> metadata, Map<String, Object> arguments) {
        URI serviceAddress = getServiceAddress((String) metadata.get(OPERATION_PROPERTY_NAME));
        String path = metadata.getOrDefault(PATH_PROPERTY_NAME, "/").toString();

        return knativeServiceRequestClientResolver.resolve(metadata).execute(
                processInstanceId,
                serviceAddress,
                path,
                arguments);
    }

    private URI getServiceAddress(String knativeServiceName) {
        return knativeServiceRegistry.getServiceAddress(knativeServiceName)
                .orElseThrow(() -> new WorkItemExecutionException("The Knative service '" + knativeServiceName
                        + "' could not be found."));
    }
}

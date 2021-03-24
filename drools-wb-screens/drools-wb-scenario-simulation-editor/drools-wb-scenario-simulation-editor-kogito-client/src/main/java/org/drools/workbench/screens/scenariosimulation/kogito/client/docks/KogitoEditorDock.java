/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.workbench.screens.scenariosimulation.kogito.client.docks;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.kie.workbench.common.widgets.client.docks.AuthoringEditorDock;
import org.uberfire.client.workbench.docks.UberfireDock;
import org.uberfire.client.workbench.docks.UberfireDocks;
import org.uberfire.mvp.PlaceRequest;

@ApplicationScoped
public class KogitoEditorDock implements AuthoringEditorDock {

    protected UberfireDocks uberfireDocks;
    protected String authoringPerspectiveIdentifier = null;
    protected UberfireDock[] activeDocks;

    @Inject
    public KogitoEditorDock(final UberfireDocks uberfireDocks) {
        this.uberfireDocks = uberfireDocks;
    }

    @PostConstruct
    public void initialize() {
    }

    @Override
    public boolean isSetup() {
        return authoringPerspectiveIdentifier != null;
    }

    @Override
    public void setup(String authoringPerspectiveIdentifier, PlaceRequest defaultPlaceRequest) {
        this.authoringPerspectiveIdentifier = authoringPerspectiveIdentifier;
    }

    @Override
    public void show() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void expandAuthoringDock(UberfireDock dockToOpen) {
    }

}

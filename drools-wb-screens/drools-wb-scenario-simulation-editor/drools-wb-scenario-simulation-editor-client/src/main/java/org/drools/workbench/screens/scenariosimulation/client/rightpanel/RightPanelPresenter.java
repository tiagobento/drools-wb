/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.drools.workbench.screens.scenariosimulation.client.rightpanel;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.enterprise.context.Dependent;
import javax.inject.Inject;

import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.user.client.ui.Widget;
import org.drools.workbench.screens.scenariosimulation.client.events.SetInstanceHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.events.SetPropertyHeaderEvent;
import org.drools.workbench.screens.scenariosimulation.client.resources.i18n.ScenarioSimulationEditorConstants;
import org.drools.workbench.screens.scenariosimulation.model.typedescriptor.FactModelTree;
import org.uberfire.client.annotations.DefaultPosition;
import org.uberfire.client.annotations.WorkbenchPartTitle;
import org.uberfire.client.annotations.WorkbenchPartView;
import org.uberfire.client.annotations.WorkbenchScreen;
import org.uberfire.workbench.model.CompassPosition;
import org.uberfire.workbench.model.Position;

import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter.DEFAULT_PREFERRED_WIDHT;
import static org.drools.workbench.screens.scenariosimulation.client.rightpanel.RightPanelPresenter.IDENTIFIER;

@Dependent
@WorkbenchScreen(identifier = IDENTIFIER, preferredWidth = DEFAULT_PREFERRED_WIDHT)
public class RightPanelPresenter implements RightPanelView.Presenter {

    public static final int DEFAULT_PREFERRED_WIDHT = 300;

    public static final String IDENTIFIER = "org.drools.scenariosimulation.RightPanel";

    private RightPanelView view;

    private ListGroupItemPresenter listGroupItemPresenter;

    protected Map<String, FactModelTree> dataObjectFieldsMap = new TreeMap<>();

    protected Map<String, FactModelTree> simpleJavaTypeFieldsMap = new TreeMap<>();

    protected Map<String, FactModelTree> instanceFieldsMap = new TreeMap<>();

    protected Map<String, FactModelTree> simpleJavaInstanceFieldsMap = new TreeMap<>();

    protected Map<String, FactModelTree> hiddenFieldsMap;

    protected EventBus eventBus;

    protected boolean editingColumnEnabled = false;

    protected ListGroupItemView selectedListGroupItemView;
    protected FieldItemView selectedFieldItemView;

    public RightPanelPresenter() {
        //Zero argument constructor for CDI
    }

    @Inject
    public RightPanelPresenter(RightPanelView view, ListGroupItemPresenter listGroupItemPresenter) {
        this.view = view;
        this.listGroupItemPresenter = listGroupItemPresenter;
        this.listGroupItemPresenter.init(this);
    }

    @PostConstruct
    public void setup() {
        view.init(this);
        view.disableEditorTab();
    }

    @DefaultPosition
    public Position getDefaultPosition() {
        return CompassPosition.EAST;
    }

    @WorkbenchPartTitle
    public String getTitle() {
        return ScenarioSimulationEditorConstants.INSTANCE.testTools();
    }

    @WorkbenchPartView
    public Widget asWidget() {
        return view.asWidget();
    }

    @Override
    public void onClearSearch() {
        view.clearInputSearch();
        view.hideClearButton();
        onSearchedEvent("");
    }

    @Override
    public void onClearNameField() {
        view.clearNameField();
    }

    @Override
    public void onClearStatus() {
        onClearSearch();
        onClearNameField();
    }

    @Override
    public void clearDataObjectList() {
        view.getDataObjectListContainer().removeAllChildren();
    }

    @Override
    public void clearSimpleJavaTypeList() {
        view.getSimpleJavaTypeListContainer().removeAllChildren();
    }

    @Override
    public void clearInstanceList() {
        view.getInstanceListContainer().removeAllChildren();
    }

    @Override
    public void clearSimpleJavaInstanceFieldList() {
        view.getSimpleJavaInstanceListContainer().removeAllChildren();
    }

    @Override
    public Optional<FactModelTree> getFactModelTreeFromFactTypeMap(String factName) {
        return Optional.ofNullable(dataObjectFieldsMap.get(factName));
    }

    @Override
    public Optional<FactModelTree> getFactModelTreeFromSimpleJavaTypeMap(String factName) {
        return Optional.ofNullable(simpleJavaTypeFieldsMap.get(factName));
    }

    @Override
    public Optional<FactModelTree> getFactModelTreeFromInstanceMap(String factName) {
        return Optional.ofNullable(instanceFieldsMap.get(factName));
    }

    @Override
    public Optional<FactModelTree> getFactModelTreeFromSimpleJavaInstanceMap(String factName) {
        return Optional.ofNullable(simpleJavaInstanceFieldsMap.get(factName));
    }

    @Override
    public FactModelTree getFactModelTreeFromHiddenMap(String factName) {
        return hiddenFieldsMap.get(factName);
    }

    @Override
    public void setDataObjectFieldsMap(SortedMap<String, FactModelTree> dataObjectFieldsMap) {
        clearDataObjectList();
        this.dataObjectFieldsMap = dataObjectFieldsMap;
        this.dataObjectFieldsMap.forEach(this::addDataObjectListGroupItemView);
    }

    @Override
    public void setSimpleJavaTypeFieldsMap(SortedMap<String, FactModelTree> simpleJavaTypeFieldsMap) {
        clearSimpleJavaTypeList();
        this.simpleJavaTypeFieldsMap = simpleJavaTypeFieldsMap;
        this.simpleJavaTypeFieldsMap.forEach(this::addSimpleJavaTypeListGroupItemView);
    }

    @Override
    public void setHiddenFieldsMap(SortedMap<String, FactModelTree> hiddenFieldsMap) {
        this.hiddenFieldsMap = hiddenFieldsMap;
    }

    @Override
    public void setInstanceFieldsMap(SortedMap<String, FactModelTree> instanceFieldsMap) {
        clearInstanceList();
        this.instanceFieldsMap = instanceFieldsMap;
        this.instanceFieldsMap.forEach(this::addInstanceListGroupItemView);
    }

    @Override
    public void setSimpleJavaInstanceFieldsMap(SortedMap<String, FactModelTree> simpleJavaInstanceFieldsMap) {
        clearSimpleJavaInstanceFieldList();
        this.simpleJavaInstanceFieldsMap = simpleJavaInstanceFieldsMap;
        this.simpleJavaInstanceFieldsMap.forEach(this::addSimpleJavaInstanceListGroupItemView);
    }

    @Override
    public void onShowClearButton() {
        view.showClearButton();
    }

    @Override
    public void setEventBus(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Override
    public void onSearchedEvent(String search) {
        clearLists();
        dataObjectFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(search.toLowerCase()))
                .forEach(filteredEntry -> addDataObjectListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        simpleJavaTypeFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(search.toLowerCase()))
                .forEach(filteredEntry -> addSimpleJavaTypeListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        instanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(search.toLowerCase()))
                .forEach(filteredEntry -> addInstanceListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        simpleJavaInstanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().toLowerCase().contains(search.toLowerCase()))
                .forEach(filteredEntry -> addSimpleJavaInstanceListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
    }

    @Override
    public void onPerfectMatchSearchedEvent(String search, boolean notEqualsSearch) {
        clearLists();
        dataObjectFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> filterTerm(entry.getKey(), search, notEqualsSearch))
                .forEach(filteredEntry -> addDataObjectListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        simpleJavaTypeFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> filterTerm(entry.getKey(), search, notEqualsSearch))
                .forEach(filteredEntry -> addSimpleJavaTypeListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        instanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> filterTerm(entry.getKey(), search, notEqualsSearch))
                .forEach(filteredEntry -> addInstanceListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
        simpleJavaInstanceFieldsMap
                .entrySet()
                .stream()
                .filter(entry -> filterTerm(entry.getKey(), search, notEqualsSearch))
                .forEach(filteredEntry -> addSimpleJavaInstanceListGroupItemView(filteredEntry.getKey(), filteredEntry.getValue()));
    }

    @Override
    public void addDataObjectListGroupItemView(String factName, FactModelTree factModelTree) {
        DivElement toAdd = listGroupItemPresenter.getDivElement(factName, factModelTree);
        view.getDataObjectListContainer().appendChild(toAdd);
    }

    @Override
    public void addSimpleJavaTypeListGroupItemView(String factName, FactModelTree factModelTree) {
        DivElement toAdd = listGroupItemPresenter.getDivElement(factName, factModelTree);
        view.getSimpleJavaTypeListContainer().appendChild(toAdd);
    }

    @Override
    public void addInstanceListGroupItemView(String instanceName, FactModelTree factModelTree) {
        DivElement toAdd = listGroupItemPresenter.getDivElement(instanceName, factModelTree);
        view.getInstanceListContainer().appendChild(toAdd);
    }

    @Override
    public void addSimpleJavaInstanceListGroupItemView(String instanceName, FactModelTree factModelTree) {
        DivElement toAdd = listGroupItemPresenter.getDivElement(instanceName, factModelTree);
        view.getSimpleJavaInstanceListContainer().appendChild(toAdd);
    }

    @Override
    public void onEnableEditorTab() {
        onDisableEditorTab();
        listGroupItemPresenter.enable();
        editingColumnEnabled = true;
        view.enableEditorTab();
    }

    @Override
    public void onEnableEditorTab(String factName, String propertyName, boolean notEqualsSearch) {
        onDisableEditorTab();
        onPerfectMatchSearchedEvent(factName, notEqualsSearch);
        listGroupItemPresenter.enable(factName);
        editingColumnEnabled = true;
        view.enableEditorTab();
        if (propertyName != null && !notEqualsSearch) {
            listGroupItemPresenter.selectProperty(factName, propertyName);
        }
    }

    @Override
    public void onDisableEditorTab() {
        onSearchedEvent("");
        listGroupItemPresenter.disable();
        editingColumnEnabled = false;
        view.disableEditorTab();
        selectedFieldItemView = null;
        selectedListGroupItemView = null;
    }

    @Override
    public void setSelectedElement(ListGroupItemView selected) {
        selectedListGroupItemView = selected;
        selectedFieldItemView = null;
        view.enableAddButton();
    }

    @Override
    public void setSelectedElement(FieldItemView selected) {
        selectedFieldItemView = selected;
        selectedListGroupItemView = null;
        view.enableAddButton();
    }

    @Override
    public void onModifyColumn() {
        if (editingColumnEnabled) {
            if (selectedListGroupItemView != null) {
                String className = selectedListGroupItemView.getActualClassName();
                getFullPackage(className).ifPresent(fullPackage -> eventBus.fireEvent(new SetInstanceHeaderEvent(fullPackage, className)));
            } else if (selectedFieldItemView != null) {
                String baseClass = selectedFieldItemView.getFullPath().split("\\.")[0];
                String value = isSimple(baseClass) ?
                        selectedFieldItemView.getFullPath() :
                        selectedFieldItemView.getFullPath() + "." + selectedFieldItemView.getFieldName();
                getFullPackage(baseClass).ifPresent(fullPackage -> eventBus.fireEvent(new SetPropertyHeaderEvent(fullPackage, value, selectedFieldItemView.getClassName())));
            }
        }
    }

    protected Optional<String> getFullPackage(String className) {
        return getFactModelTreeFromMaps(className).map(FactModelTree::getFullPackage);
    }

    protected Optional<FactModelTree> getFactModelTreeFromMaps(String key) {
        return Optional.ofNullable(getFactModelTreeFromFactTypeMap(key)
                                           .orElseGet(() -> getFactModelTreeFromSimpleJavaTypeMap(key)
                                                   .orElseGet(() -> getFactModelTreeFromInstanceMap(key)
                                                           .orElseGet(() -> getFactModelTreeFromSimpleJavaInstanceMap(key).orElse(null)))));
    }

    protected boolean isSimple(String key) {
        return Optional.ofNullable(getFactModelTreeFromSimpleJavaTypeMap(key))
                .orElseGet(() -> getFactModelTreeFromSimpleJavaInstanceMap(key))
                .isPresent();
    }

    protected void clearLists() {
        clearDataObjectList();
        clearSimpleJavaTypeList();
        clearInstanceList();
        clearSimpleJavaInstanceFieldList();
    }

    protected boolean filterTerm(String key, String search, boolean notEqualsSearch) {
        List<String> terms = Arrays.asList(search.split(";"));
        if (notEqualsSearch) {
            return !terms.contains(key);
        } else {
            return terms.contains(key);
        }
    }
}

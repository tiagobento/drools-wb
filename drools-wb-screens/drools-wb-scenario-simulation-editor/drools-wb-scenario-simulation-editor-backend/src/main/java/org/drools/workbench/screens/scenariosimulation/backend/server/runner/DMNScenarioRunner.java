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

package org.drools.workbench.screens.scenariosimulation.backend.server.runner;

import java.util.Map;

import org.drools.workbench.screens.scenariosimulation.backend.server.expression.DMNFeelExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.model.Scenario;
import org.drools.workbench.screens.scenariosimulation.model.Simulation;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.kie.api.runtime.KieContainer;

public class DMNScenarioRunner extends AbstractScenarioRunner {

    public DMNScenarioRunner(KieContainer kieContainer, Simulation simulation) {
        this(kieContainer, simulation, null);
    }

    public DMNScenarioRunner(KieContainer kieContainer, Simulation simulation, String fileName) {
        this(kieContainer, simulation.getSimulationDescriptor(), toScenarioMap(simulation), fileName);
    }

    public DMNScenarioRunner(KieContainer kieContainer, SimulationDescriptor simulationDescriptor, Map<Integer, Scenario> scenarios, String fileName) {
        super(kieContainer, simulationDescriptor, scenarios, fileName, DMNFeelExpressionEvaluator::new);
    }

    @Override
    protected AbstractRunnerHelper newRunnerHelper(SimulationDescriptor simulationDescriptor) {
        return new DMNScenarioRunnerHelper(simulationDescriptor);
    }
}

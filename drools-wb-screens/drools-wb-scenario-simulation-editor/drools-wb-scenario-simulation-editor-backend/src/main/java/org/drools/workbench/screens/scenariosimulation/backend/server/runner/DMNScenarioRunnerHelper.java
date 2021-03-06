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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.workbench.screens.scenariosimulation.backend.server.expression.ExpressionEvaluator;
import org.drools.workbench.screens.scenariosimulation.backend.server.fluent.DMNScenarioExecutableBuilder;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ResultWrapper;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioExpect;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioGiven;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioResult;
import org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ScenarioRunnerData;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionElement;
import org.drools.workbench.screens.scenariosimulation.model.ExpressionIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactIdentifier;
import org.drools.workbench.screens.scenariosimulation.model.FactMapping;
import org.drools.workbench.screens.scenariosimulation.model.FactMappingValue;
import org.drools.workbench.screens.scenariosimulation.model.ScenarioSimulationModel;
import org.drools.workbench.screens.scenariosimulation.model.SimulationDescriptor;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.RequestContext;
import org.kie.dmn.api.core.DMNDecisionResult;
import org.kie.dmn.api.core.DMNResult;

import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ResultWrapper.createErrorResult;
import static org.drools.workbench.screens.scenariosimulation.backend.server.runner.model.ResultWrapper.createResult;
import static org.kie.dmn.api.core.DMNDecisionResult.DecisionEvaluationStatus.SUCCEEDED;

public class DMNScenarioRunnerHelper extends AbstractRunnerHelper {

    private final SimulationDescriptor simulationDescriptor;

    public DMNScenarioRunnerHelper(SimulationDescriptor simulationDescriptor) {
        this.simulationDescriptor = simulationDescriptor;
    }

    @Override
    public RequestContext executeScenario(KieContainer kieContainer,
                                          ScenarioRunnerData scenarioRunnerData,
                                          ExpressionEvaluator expressionEvaluator,
                                          SimulationDescriptor simulationDescriptor) {
        if (!ScenarioSimulationModel.Type.DMN.equals(simulationDescriptor.getType())) {
            throw new ScenarioException("Impossible to run a not-DMN simulation with DMN runner");
        }
        DMNScenarioExecutableBuilder executableBuilder = DMNScenarioExecutableBuilder.createBuilder(kieContainer);
        executableBuilder.setActiveModel(simulationDescriptor.getDmnFilePath());
        for (ScenarioGiven input : scenarioRunnerData.getGivens()) {
            executableBuilder.setValue(input.getFactIdentifier().getName(), input.getValue());
        }

        return executableBuilder.run();
    }

    @Override
    public void verifyConditions(SimulationDescriptor simulationDescriptor,
                                 ScenarioRunnerData scenarioRunnerData,
                                 ExpressionEvaluator expressionEvaluator,
                                 RequestContext requestContext) {
        DMNResult dmnResult = requestContext.getOutput(DMNScenarioExecutableBuilder.DMN_RESULT);

        for (ScenarioExpect output : scenarioRunnerData.getExpects()) {
            FactIdentifier factIdentifier = output.getFactIdentifier();
            String decisionName = factIdentifier.getName();
            DMNDecisionResult decisionResult = dmnResult.getDecisionResultByName(decisionName);
            if (decisionResult == null) {
                throw new ScenarioException("DMN execution has not generated a decision result with name " + decisionName);
            }

            for (FactMappingValue expectedResult : output.getExpectedResult()) {
                ExpressionIdentifier expressionIdentifier = expectedResult.getExpressionIdentifier();

                FactMapping factMapping = simulationDescriptor.getFactMapping(factIdentifier, expressionIdentifier)
                        .orElseThrow(() -> new IllegalStateException("Wrong expression, this should not happen"));

                ScenarioResult scenarioResult = fillResult(expectedResult, factIdentifier, () -> getSingleFactValueResult(factMapping, expectedResult, decisionResult, expressionEvaluator));

                scenarioRunnerData.addResult(scenarioResult);
            }
        }
    }

    @SuppressWarnings("unchecked")
    protected ResultWrapper getSingleFactValueResult(FactMapping factMapping,
                                                     FactMappingValue expectedResult,
                                                     DMNDecisionResult decisionResult,
                                                     ExpressionEvaluator expressionEvaluator) {
        Object resultRaw = decisionResult.getResult();

        if (!SUCCEEDED.equals(decisionResult.getEvaluationStatus())) {
            return createErrorResult();
        }

        for (ExpressionElement expressionElement : factMapping.getExpressionElementsWithoutClass()) {
            if (!(resultRaw instanceof Map)) {
                throw new ScenarioException("Wrong resultRaw structure because it is not a complex type as expected");
            }
            Map<String, Object> result = (Map<String, Object>) resultRaw;
            resultRaw = result.get(expressionElement.getStep());
        }

        Class<?> resultClass = resultRaw != null ? resultRaw.getClass() : null;

        try {
            return expressionEvaluator.evaluate(expectedResult.getRawValue(), resultRaw, resultClass) ?
                    createResult(resultRaw) :
                    createErrorResult();
        } catch (Exception e) {
            expectedResult.setError(true);
            throw new ScenarioException(e.getMessage(), e);
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object createObject(String className, Map<List<String>, Object> params, ClassLoader classLoader) {
        Map<String, Object> toReturn = new HashMap<>();
        for (Map.Entry<List<String>, Object> listObjectEntry : params.entrySet()) {

            List<String> allSteps = listObjectEntry.getKey();
            List<String> steps = allSteps.subList(0, allSteps.size() - 1);
            String lastStep = allSteps.get(allSteps.size() - 1);

            Map<String, Object> targetMap = toReturn;
            for (String step : steps) {
                targetMap = (Map<String, Object>) targetMap.computeIfAbsent(step, k -> new HashMap<>());
            }
            targetMap.put(lastStep, listObjectEntry.getValue());
        }
        return toReturn;
    }
}

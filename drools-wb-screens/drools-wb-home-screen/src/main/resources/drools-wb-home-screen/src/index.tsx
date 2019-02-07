import * as AppFormer from "appformer-js";
import * as HomeApi from "kie-wb-common-home-api";
import { DroolsWbHomeScreenProvider } from "./DroolsWbHomeScreenProvider";

AppFormer.register(new HomeApi.HomeScreenAppFormerComponent(new DroolsWbHomeScreenProvider()));

import { render } from "solid-js/web";
import { lazy } from "solid-js";

const WizardNewProject = lazy(() => import("./components/wizards/new_project/main"));

export { WizardNewProject }

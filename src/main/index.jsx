import { render } from 'solid-js/web';
import 'solid-devtools';
import '@unocss/reset/tailwind.css';
import 'virtual:uno.css';
import { ui_root } from './App.cljs';
//import { ui_root } from './root2.cljs';

// Log to the console
console.log('start');

function UiRoot() {
  return (
    <div>ava2222a222222222211</div>
  );
}


// Select the root element
const rootElement = document.getElementById('root');

// Clear the root element
if (rootElement) {
  rootElement.innerHTML = '';
}

render(ui_root, document.getElementById("root"));
render(UiRoot, document.getElementById("root"));


// Render the main component
//render(app.root, document.getElementById('root'));

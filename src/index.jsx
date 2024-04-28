/* @refresh reload */
import { render } from 'solid-js/web';
import "solid-devtools";
import "virtual:windi.css";
import "virtual:windi-devtools";
import Root from './App';
import start from "./start";

start();
render(() => <Root />, document.getElementById('root'));

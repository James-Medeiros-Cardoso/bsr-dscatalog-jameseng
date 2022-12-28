import React from 'react';
import ReactDOM from 'react-dom';
import '@popperjs/core';
import 'bootstrap/js/dist/collapse';
//import 'bootstrap/dist/css/bootstrap.css'; // APAGADO
import App from './App';

ReactDOM.render(
  <React.StrictMode>
    <App />
  </React.StrictMode>,
  document.getElementById('root')
);
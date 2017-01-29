import './index.css'

import React from 'react';
import {render} from 'react-dom';

import { Provider } from 'react-redux';
import { createStore, applyMiddleware, combineReducers } from 'redux';
import { Router, Route, IndexRoute, browserHistory } from 'react-router';
import { syncHistoryWithStore, routerMiddleware} from 'react-router-redux';

import App from './App';
import reducers from './reducers/reducers';
import ConsolePage from './components/ConsolePage';
import FeedPage from './components/FeedPage';

const middleware = routerMiddleware(browserHistory);
const store = createStore(
    reducers,
  window.devToolsExtension && window.devToolsExtension(),
  applyMiddleware(middleware)
);

// Create an enhanced history that syncs navigation events with the store
const history = syncHistoryWithStore(browserHistory, store);

render(
  <Provider store={store}>
    <Router history={history}>
      <Route path="/" component={App}>
          <IndexRoute component={ConsolePage} />
          <Route path={FeedPage.route} component={FeedPage} />
      </Route>
    </Router>
  </Provider>,
  document.querySelector('#app')
);

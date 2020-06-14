import React from "react";
import { Redirect, Route, Switch } from "react-router-dom";

import { isLoggedIn } from "../actions/Utils";

import Dashboard from "../views/Dashboard";
import Drivers from "../views/Drivers";
import Delivery from "../views/Delivery";
import Statistics from "../views/Statistics";
import Login from "../views/Login";
import Registration from "../views/Registration";

const PrivateRoute = ({ Component, path, loggedIn }) => (
  <Route
    path={path}
    render={(props) => {
      return loggedIn() ? <Component {...props} /> : <Redirect to="/login" />;
    }}
  />
);

const Body = () => (
  <Switch>
    <Route exact path="/login" component={Login} />
    <Route exact path="/registration" component={Registration} />
    <PrivateRoute
      exact
      path="/dashboard"
      Component={Dashboard}
      loggedIn={isLoggedIn}
    />
    <PrivateRoute
      exact
      path="/drivers"
      Component={Drivers}
      loggedIn={isLoggedIn}
    />
    <PrivateRoute
      exact
      path="/delivery"
      Component={Delivery}
      loggedIn={isLoggedIn}
    />
    <PrivateRoute
      exact
      path="/statistics"
      Component={Statistics}
      loggedIn={isLoggedIn}
    />
    <Redirect to="/dashboard" />
  </Switch>
);

export default Body;

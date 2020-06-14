import React, { Fragment } from "react";
import "./App.scss";
import "./assets/black-dashboard/scss/black-dashboard-react.scss";
import "./assets/black-dashboard/css/nucleo-icons.css";

import MyNavbar from "./common/MyNavbar";
import MyRoutes from "./routes/MyRoutes";
import MySnackar from "./common/MySnackbar";

function App() {
  return (
    <Fragment>
      <MyNavbar />
      <MyRoutes />
      <MySnackar />
    </Fragment>
  );
}

export default App;

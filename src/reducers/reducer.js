import { combineReducers } from "redux";

import userReducer from "./userReducer";
import deliveryReducer from "./deliveryReducer";
import snackbarReducer from "./snackbarReducer";

const reducer = combineReducers({
  user: userReducer,
  delivery: deliveryReducer,
  snackbar: snackbarReducer,
});

export default reducer;

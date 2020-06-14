const initialUserState = {
  username: "",
  driversCount: 0,
  drivers: [],
};

const userReducer = (state = initialUserState, action) => {
  switch (action.type) {
    case "LOGIN_SUCCESS": {
      return {
        ...state,
        username: action.payload.username,
      };
    }

    case "DRIVERS_DATA": {
      return {
        ...state,
        driversCount: action.payload.driversCount,
        drivers: action.payload.drivers,
      };
    }

    default: {
      return state;
    }
  }
};

export default userReducer;

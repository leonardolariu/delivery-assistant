import {
  API_ENDPOINT,
  POST,
  AUTHORIZED_GET,
  AUTHORIZED_POST,
  AUTHORIZED_DELETE_WITHOUT_BODY,
} from "./Utils";

export const login = (user) => {
  return (dispatch) => {
    return POST(`${API_ENDPOINT}/auth/signin`, user)
      .then((response) => {
        localStorage.setItem("jwt", response.data.token);
        localStorage.setItem("role", response.data.roles[0]);

        dispatch({
          type: "LOGIN_SUCCESS",
          payload: {
            username: response.data.username,
            message: "Connected to DeliveryAssistant. Get productive!",
            type: "success",
            icon: "tim-icons icon-check-2",
            autoDismiss: 7,
          },
        });
      })
      .catch((error) =>
        dispatch({
          type: "LOGIN_ERROR",
          payload: {
            message: "Invalid credentials!",
            type: "danger",
            icon: "tim-icons icon-alert-circle-exc",
            autoDismiss: 7,
          },
        })
      );
  };
};

export const register = (user) => {
  return (dispatch) => {
    return POST(`${API_ENDPOINT}/auth/signup`, user)
      .then((response) => {
        let options = {
          message: "Account created successfully.",
          type: "success",
          icon: "tim-icons icon-check-2",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      })
      .catch((error) => {
        let options = {
          message: error.response.data.message,
          type: "danger",
          icon: "tim-icons icon-alert-circle-exc",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      });
  };
};

export const getDriversData = () => {
  return (dispatch) => {
    return AUTHORIZED_GET(`${API_ENDPOINT}/drivers`).then((response) => {
      dispatch({
        type: "DRIVERS_DATA",
        payload: response.data,
      });
    });
  };
};

export const getDailyDeliveryData = () => {
  return (dispatch) => {
    return AUTHORIZED_GET(`${API_ENDPOINT}/delivery/daily`).then((response) => {
      dispatch({
        type: "DAILY_DELIVERY_DATA",
        payload: response.data,
      });
    });
  };
};

export const getDeliveryDataForLast30Days = () => {
  return (dispatch) => {
    return AUTHORIZED_GET(`${API_ENDPOINT}/delivery/last-30-days`).then(
      (response) => {
        dispatch({
          type: "DELIVERY_DATA_LAST_30_DAYS",
          payload: response.data,
        });
      }
    );
  };
};

export const addDriver = (driver) => {
  return (dispatch) => {
    return AUTHORIZED_POST(`${API_ENDPOINT}/drivers`, driver)
      .then((response) => {
        let options = {
          message: "Driver added to the team successfully.",
          type: "success",
          icon: "tim-icons icon-check-2",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      })
      .catch((error) => {
        let options = {
          message: error.response.data.message,
          type: "danger",
          icon: "tim-icons icon-alert-circle-exc",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      });
  };
};

export const removeDriver = (id) => {
  return (dispatch) => {
    return AUTHORIZED_DELETE_WITHOUT_BODY(`${API_ENDPOINT}/drivers/${id}`)
      .then((response) => {
        let options = {
          message: response.data.message,
          type: "success",
          icon: "tim-icons icon-check-2",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      })
      .catch((error) => {
        let options = {
          message: error.response.data.message,
          type: "danger",
          icon: "tim-icons icon-alert-circle-exc",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      });
  };
};

export const uploadCSV = (formData) => {
  return (dispatch) => {
    return AUTHORIZED_POST(
      `${API_ENDPOINT}/delivery/daily/upload-csv`,
      formData
    )
      .then((response) => {
        let options = {
          message: response.data.message,
          type: "success",
          icon: "tim-icons icon-check-2",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      })
      .catch((error) => {
        let options = {
          message: error.response.data.message,
          type: "danger",
          icon: "tim-icons icon-alert-circle-exc",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      });
  };
};

export const getEstimatedDriversCount = () => {
  return (dispatch) => {
    AUTHORIZED_GET(`${API_ENDPOINT}/delivery/daily/estimate-drivers-count`)
      .then((response) => {
        dispatch({
          type: "ESTIMATION_SUCCESS",
          payload: {
            estimatedDriversCount: response.data.estimatedDriversCount,
            message: "Estimation provided.",
            type: "success",
            icon: "tim-icons icon-check-2",
            autoDismiss: 7,
          },
        });
      })
      .catch((error) =>
        dispatch({
          type: "ESTIMATION_ERROR",
          payload: {
            message: error.response.data.message,
            type: "danger",
            icon: "tim-icons icon-alert-circle-exc",
            autoDismiss: 7,
          },
        })
      );
  };
};

export const processRoutes = (driversCount) => {
  return (dispatch) => {
    return AUTHORIZED_POST(
      `${API_ENDPOINT}/delivery/daily/process-routes/${driversCount}`,
      null
    )
      .then((response) => {
        let options = {
          message: response.data.message,
          type: "success",
          icon: "tim-icons icon-check-2",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      })
      .catch((error) => {
        let options = {
          message: error.response.data.message,
          type: "danger",
          icon: "tim-icons icon-alert-circle-exc",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      });
  };
};

export const informDrivers = (driversToInform) => {
  return (dispatch) => {
    return AUTHORIZED_POST(
      `${API_ENDPOINT}/delivery/daily/inform-drivers/`,
      driversToInform
    )
      .then((response) => {
        let options = {
          message: response.data.message,
          type: "success",
          icon: "tim-icons icon-check-2",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      })
      .catch((error) => {
        let options = {
          message: error.response.data.message,
          type: "danger",
          icon: "tim-icons icon-alert-circle-exc",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      });
  };
};

export const resetDailyDelivery = () => {
  return (dispatch) => {
    return AUTHORIZED_POST(`${API_ENDPOINT}/delivery/daily/reset`, null)
      .then((response) => {
        let options = {
          message: response.data.message,
          type: "success",
          icon: "tim-icons icon-check-2",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      })
      .catch((error) => {
        let options = {
          message: error.response.data.message,
          type: "danger",
          icon: "tim-icons icon-alert-circle-exc",
          autoDismiss: 7,
        };

        dispatch(setNotification(options));
      });
  };
};

export const setNotification = (options) => {
  return {
    type: "SET_NOTIFICATION",
    payload: {
      options: options,
    },
  };
};

export const resetNotificationTrigger = () => {
  return {
    type: "RESET_TRIGGER",
  };
};

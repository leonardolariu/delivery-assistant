const initialDeliveryState = {
  daily: {
    date: "",
    status: "",
    estimatedDriversCount: 0,
    actualDriversCount: 0,
    packagesCount: 0,
    minimumDistanceToCover: 0, // in km
    mapCenter: { lat: 0, lng: 0 },
    routes: [],
  },
  statistics: [],
};

const deliveryReducer = (state = initialDeliveryState, action) => {
  switch (action.type) {
    case "DAILY_DELIVERY_DATA": {
      return {
        ...state,
        daily: {
          date: action.payload.date,
          status: action.payload.status,
          estimatedDriversCount: action.payload.estimatedDriversCount,
          actualDriversCount: action.payload.actualDriversCount,
          packagesCount: action.payload.packagesCount,
          minimumDistanceToCover: action.payload.minimumDistanceToCover,
          mapCenter: {
            lat: action.payload.mapCenter.xcoordinate,
            lng: action.payload.mapCenter.ycoordinate,
          },
          routes: action.payload.routes,
        },
      };
    }

    case "DELIVERY_DATA_LAST_30_DAYS": {
      return {
        ...state,
        statistics: action.payload.deliveries,
      };
    }

    case "ESTIMATION_SUCCESS": {
      return {
        ...state,
        daily: {
          ...state.daily,
          estimatedDriversCount: action.payload.estimatedDriversCount,
        },
      };
    }

    default: {
      return state;
    }
  }
};

export default deliveryReducer;

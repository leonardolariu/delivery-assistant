import React, { Fragment } from "react";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";
import {
  setNotification,
  getDriversData,
  getDailyDeliveryData,
  uploadCSV,
  resetDailyDelivery,
  getEstimatedDriversCount,
  processRoutes,
  informDrivers,
} from "../actions/Actions";

import {
  Button,
  Card,
  CardHeader,
  CardBody,
  CardTitle,
  Row,
  Col,
  Input,
  Modal,
  ModalBody,
  ModalFooter,
  FormGroup,
  Label,
} from "reactstrap";

import csvFormat from "../assets/other/csv-format.png";
import Loader from "react-loader-spinner";
import GoogleMapReact from "google-map-react";
import Marker from "./containers/Marker";

class Delivery extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      selectedFile: null,
      csvModal: false,
      resetModal: false,
      selectBeforeProcess: "",
      loading: false,
      loadingText: "",
      selectAfterProcess: "all",
      zoom: 11,
      driversToInform: [],
    };

    this.toggleCSVModal = this.toggleCSVModal.bind(this);
    this.toggleResetModal = this.toggleResetModal.bind(this);
  }

  hasDrivers = () => {
    return this.props.user.driversCount !== 0;
  };

  hasStartedDailyDelivery = () => {
    return this.props.delivery.daily.status !== "NOT_STARTED";
  };

  hasCompletedDailyDelivery = () => {
    return this.props.delivery.daily.status === "COMPLETED";
  };

  canUpload = () => {
    return !this.hasStartedDailyDelivery() && this.state.selectedFile !== null;
  };

  canEstimate = () => {
    return (
      this.hasStartedDailyDelivery() &&
      this.props.delivery.daily.estimatedDriversCount === 0
    );
  };

  canProcessRoutes = () => {
    return this.props.delivery.daily.status === "FILE_UPLOADED";
  };

  canCheckRoutes = () => {
    return this.props.delivery.daily.status === "ROUTES_PROCESSED";
  };

  canInformDrivers = () => {
    var actualDriversCount = this.props.delivery.daily.actualDriversCount;
    return (
      actualDriversCount !== 0 &&
      this.state.driversToInform.length === actualDriversCount
    );
  };

  getOptions = (message) => {
    return {
      message: message,
      type: "primary",
      icon: "tim-icons icon-bell-55",
      autoDismiss: 10,
    };
  };

  componentDidMount() {
    this.props.getDriversData().then(() => {
      if (!this.hasDrivers()) {
        let options = this.getOptions(
          "You need to form a team before delivering any goods."
        );
        this.props.setNotification(options);
      }
    });

    this.props.getDailyDeliveryData().then(() => {
      if (this.hasCompletedDailyDelivery()) {
        let options = this.getOptions(
          "You have already completed your daily delivery. Come back tomorrow."
        );
        this.props.setNotification(options);
      }
    });
  }

  getStatusView = (status) => {
    return this.props.delivery.daily.status === status ? (
      <div>
        <i className="tim-icons icon-delivery-fast text-primary" />{" "}
        <i>{status}</i>
      </div>
    ) : (
      <div className="status-inactive">
        <i>{status}</i>
      </div>
    );
  };

  // On file select (from the pop up)
  onFileChange = (event) => {
    this.setState({ selectedFile: event.target.files[0] });
  };

  // On file upload (click the upload button)
  onFileUpload = () => {
    // Create an object of formData
    const formData = new FormData();
    formData.append("file", this.state.selectedFile);

    this.props.uploadCSV(formData).then(() => {
      this.setState({
        selectedFile: null,
      });

      this.props.getDailyDeliveryData();
    });
  };

  estimate = () => {
    this.props.getEstimatedDriversCount();
  };

  onChange = (fieldName, value) => {
    this.setState({
      [fieldName]: value,
    });
  };

  onChangeSelectBeforeProcess = (event) =>
    this.onChange("selectBeforeProcess", event.target.value);

  onChangeSelectAfterProcess = (event) =>
    this.onChange("selectAfterProcess", event.target.value);

  onChangeDriverCheckBox = (event) => {
    const driverEmail = event.target.value;

    if (!this.state.driversToInform.some((item) => driverEmail === item)) {
      this.setState({
        driversToInform: [...this.state.driversToInform, driverEmail],
      });
    } else {
      var arr = [...this.state.driversToInform]; // make a separate copy of the array
      var index = arr.indexOf(driverEmail);
      if (index !== -1) {
        arr.splice(index, 1);
        this.setState({
          driversToInform: arr,
        });
      }
    }
  };

  getSelectOptions = () => {
    let actualDriversCount = this.props.delivery.daily.actualDriversCount;
    return actualDriversCount === 0 ? (
      [...Array(this.props.user.driversCount).keys()].map((i) => (
        <option value={i + 1}>{i + 1}</option>
      ))
    ) : (
      <option value={actualDriversCount}>{actualDriversCount}</option>
    );
  };

  getSelectRouteOptions = () => {
    return (
      <Fragment>
        <option value={"all"}>all</option>
        {[...Array(this.props.delivery.daily.actualDriversCount).keys()].map(
          (i) => (
            <option value={i + 1}>{i + 1}</option>
          )
        )}
      </Fragment>
    );
  };

  getDriversCheckBoxes = () => {
    return this.canCheckRoutes()
      ? this.props.user.drivers.map((driver) => (
          <FormGroup check>
            <Label check>
              <Input
                type="checkbox"
                value={driver.email}
                onChange={this.onChangeDriverCheckBox}
              />{" "}
              {driver.name}
              <span className="form-check-sign">
                <span className="check"></span>
              </span>
            </Label>
          </FormGroup>
        ))
      : this.props.user.drivers.map((driver) => (
          <FormGroup check disabled>
            <Label check>
              <Input type="checkbox" disabled />
              {driver.name}
              <span className="form-check-sign">
                <span className="check"></span>
              </span>
            </Label>
          </FormGroup>
        ));
  };

  processRoutes = () => {
    this.setState({
      loading: true,
      loadingText: "Grab a coffee! This might take a while...",
    });
    this.props.processRoutes(this.state.selectBeforeProcess).then(() => {
      this.props.getDailyDeliveryData().then(() => {
        this.setState({ loading: false });
      });
    });
  };

  hashCode(str) {
    var hash = 0;
    for (var i = 0; i < str.length; i++) {
      hash = str.charCodeAt(i) + ((hash << 5) - hash);
    }
    return hash;
  }

  intToRGB(i) {
    var c = (i & 0x00ffffff).toString(16).toUpperCase();

    return "00000".substring(0, 6 - c.length) + c;
  }

  putRouteMarker = (routeAA) => {
    const color = this.intToRGB(
      this.hashCode(routeAA.packages[0].recipientPhoneNumber)
    );

    return routeAA.packages.map((aPackage) => (
      <Marker
        lat={aPackage.xcoordinate}
        lng={aPackage.ycoordinate}
        color={"#" + color}
        order={aPackage.order}
      />
    ));
  };

  putMarkers = () => {
    if (this.state.selectAfterProcess === "all") {
      return this.props.delivery.daily.routes.map((routeAA) => {
        return this.putRouteMarker(routeAA);
      });
    } else {
      const routeIndex = this.state.selectAfterProcess - 1;
      const route = this.props.delivery.daily.routes[routeIndex];
      return this.putRouteMarker(route);
    }
  };

  informDrivers = () => {
    this.setState({
      loading: true,
      loadingText: "Your daily delivery will be soon COMPLETED...",
    });
    this.props.informDrivers(this.state.driversToInform).then(() => {
      this.setState({ loading: false });
      this.props.getDailyDeliveryData();
    });
  };

  toggleCSVModal() {
    this.setState({
      csvModal: !this.state.csvModal,
    });
  }

  toggleResetModal() {
    this.setState({
      resetModal: !this.state.resetModal,
    });
  }

  resetProgress = () => {
    this.toggleResetModal();

    this.props.resetDailyDelivery().then(() => {
      this.props.getDailyDeliveryData();
    });
  };

  render() {
    return (
      <div className="content">
        {}
        <Modal
          isOpen={this.state.csvModal}
          toggle={this.toggleCSVModal}
          style={{ marginTop: "0px" }}
        >
          <div className="modal-header">
            <h5 className="modal-title">CSV Format</h5>
            <button
              type="button"
              className="close"
              data-dismiss="modal"
              aria-hidden="true"
              onClick={this.toggleCSVModal}
            >
              <i className="tim-icons icon-simple-remove" />
            </button>
          </div>
          <ModalBody>
            <img src={csvFormat} alt="csv-format"></img>
          </ModalBody>
        </Modal>

        <Modal
          isOpen={this.state.resetModal}
          toggle={this.toggleResetModal}
          style={{ marginTop: "48px" }}
        >
          <div className="modal-header">
            <h5 className="modal-title">Reset</h5>
            <button
              type="button"
              className="close"
              data-dismiss="modal"
              aria-hidden="true"
              onClick={this.toggleResetModal}
            >
              <i className="tim-icons icon-simple-remove" />
            </button>
          </div>
          <ModalBody>
            <p>Are you sure you want to reset daily delivery progress?</p>
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={this.toggleResetModal}>
              Close
            </Button>
            <Button color="primary" onClick={() => this.resetProgress()}>
              Yes
            </Button>
          </ModalFooter>
        </Modal>

        <Modal isOpen={this.state.loading} className="loading-modal">
          <ModalBody>
            <div>
              <Loader
                type="ThreeDots"
                color="#5e72e4"
                height="100"
                width="100"
              />
            </div>
            <h2>{this.state.loadingText}</h2>
          </ModalBody>
        </Modal>

        {this.hasDrivers() && !this.hasCompletedDailyDelivery() && (
          <Row>
            <Col lg="8">
              <Card className="card-chart">
                <CardHeader>
                  <CardTitle tag="h3">
                    <i>
                      Upload CSV
                      <span style={{ color: "red" }}>*</span>
                    </i>
                  </CardTitle>
                </CardHeader>
                <CardBody className="file-upload-holder">
                  <div className="file-upload-div">
                    Please check the{" "}
                    <a href="#" onClick={() => this.toggleCSVModal()}>
                      format
                    </a>
                    {!this.hasStartedDailyDelivery() ? (
                      <Input type="file" onChange={this.onFileChange} />
                    ) : (
                      <Input type="file" disabled />
                    )}
                  </div>
                  <div className="file-upload-div">
                    {this.canUpload() ? (
                      <Button color="primary" onClick={this.onFileUpload}>
                        Upload
                      </Button>
                    ) : (
                      <Button color="primary" disabled>
                        Upload
                      </Button>
                    )}
                  </div>
                </CardBody>
              </Card>

              <Card className="card-chart">
                <CardHeader>
                  <CardTitle tag="h3">
                    <i>Get Estimation (optional)</i>
                  </CardTitle>
                </CardHeader>
                <CardBody className="file-upload-holder">
                  <div className="custom-div">
                    {this.props.delivery.daily.estimatedDriversCount === 0 ? (
                      <div className="custom-div-text">
                        We will analyze your data and try to provide the best
                        number of delivery clusters.
                      </div>
                    ) : (
                      <div className="custom-div-text">
                        We analyzed your data and identified{" "}
                        <span className="highlighted-text-primary">
                          {this.props.delivery.daily.estimatedDriversCount}
                        </span>{" "}
                        delivery zones.
                      </div>
                    )}
                  </div>
                  <div className="custom-div">
                    {this.canEstimate() ? (
                      <Button color="primary" onClick={() => this.estimate()}>
                        Estimate
                      </Button>
                    ) : (
                      <Button color="primary" disabled>
                        Estimate
                      </Button>
                    )}
                  </div>
                </CardBody>
              </Card>

              <Card className="card-chart">
                <CardHeader>
                  <CardTitle tag="h3">
                    <i>
                      Process Routes
                      <span style={{ color: "red" }}>*</span>
                    </i>
                  </CardTitle>
                </CardHeader>
                <CardBody className="file-upload-holder">
                  <div className="file-upload-div">
                    Select the number of drivers you wish to assign to this
                    delivery.
                    {this.canProcessRoutes() ? (
                      <Input
                        type="select"
                        value={this.state.selectBeforeProcess}
                        onChange={this.onChangeSelectBeforeProcess}
                      >
                        {this.getSelectOptions()}
                      </Input>
                    ) : (
                      <Input type="select" disabled>
                        {this.getSelectOptions()}
                      </Input>
                    )}
                  </div>
                  <div className="file-upload-div">
                    {this.canProcessRoutes() &&
                    this.state.selectBeforeProcess !== "" ? (
                      <Button
                        color="primary"
                        onClick={() => this.processRoutes()}
                      >
                        Process
                      </Button>
                    ) : (
                      <Button color="primary" disabled>
                        Process
                      </Button>
                    )}
                  </div>
                </CardBody>
              </Card>

              <Card className="card-chart">
                <CardHeader>
                  <CardTitle tag="h3">
                    <i>Check Routes (optional)</i>
                  </CardTitle>
                </CardHeader>
                <CardBody className="file-upload-holder">
                  <div className="file-upload-div">
                    Select the route you wish to check.
                    {this.canCheckRoutes() ? (
                      <Input
                        type="select"
                        value={this.state.selectAfterProcess}
                        onChange={this.onChangeSelectAfterProcess}
                      >
                        {this.getSelectRouteOptions()}
                      </Input>
                    ) : (
                      <Input type="select" disabled>
                        {this.getSelectRouteOptions()}
                      </Input>
                    )}
                  </div>
                  <div className="file-upload-div"></div>
                </CardBody>
                {this.canCheckRoutes() ? (
                  <div style={{ height: "78.5vh", width: "100%" }}>
                    <GoogleMapReact
                      bootstrapURLKeys={{
                        key: "AIzaSyDKX3KCgzQxFycTsUUWnJCASsh48eyMziQ",
                      }}
                      defaultCenter={this.props.delivery.daily.mapCenter}
                      defaultZoom={this.state.zoom}
                    >
                      {this.putMarkers()}
                    </GoogleMapReact>
                  </div>
                ) : null}
              </Card>

              <Card className="card-chart">
                <CardHeader>
                  <CardTitle tag="h3">
                    <i>
                      Inform Drivers
                      <span style={{ color: "red" }}>*</span>
                    </i>
                  </CardTitle>
                </CardHeader>
                <CardBody>
                  <div className="inform-drivers-holder">
                    {this.getDriversCheckBoxes()}
                  </div>

                  <div className="inform-button-holder">
                    {this.canInformDrivers() ? (
                      <Button
                        color="primary"
                        onClick={() => this.informDrivers()}
                      >
                        Inform
                      </Button>
                    ) : (
                      <Button color="primary" disabled>
                        Inform
                      </Button>
                    )}
                  </div>
                </CardBody>
              </Card>
            </Col>

            <Col lg="4">
              <div className="sticky-card-holder">
                <Card className="card-chart">
                  <CardHeader>
                    <h5 className="card-category">Delivery Status</h5>
                    <CardTitle tag="h3">
                      {this.getStatusView("NOT_STARTED")}
                      {this.getStatusView("FILE_UPLOADED")}
                      {this.getStatusView("ROUTES_PROCESSED")}
                      {this.getStatusView("COMPLETED")}
                    </CardTitle>
                  </CardHeader>
                  <CardBody>
                    {this.hasStartedDailyDelivery() ? (
                      <Button
                        color="danger"
                        style={{ float: "right" }}
                        onClick={() => this.toggleResetModal()}
                      >
                        Reset Progress
                      </Button>
                    ) : (
                      <Button
                        color="danger"
                        style={{ float: "right" }}
                        disabled
                      >
                        Reset Progress
                      </Button>
                    )}
                  </CardBody>
                </Card>
              </div>
            </Col>
          </Row>
        )}
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    user: state.user,
    delivery: state.delivery,
    snackbar: state.snackbar,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    ...bindActionCreators(
      {
        setNotification,
        getDriversData,
        getDailyDeliveryData,
        uploadCSV,
        resetDailyDelivery,
        getEstimatedDriversCount,
        processRoutes,
        informDrivers,
      },
      dispatch
    ),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Delivery);

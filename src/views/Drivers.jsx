import React, { Fragment } from "react";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";
import {
  getDriversData,
  getDailyDeliveryData,
  addDriver,
  removeDriver,
} from "../actions/Actions";

import {
  Button,
  Card,
  CardHeader,
  CardBody,
  CardTitle,
  Row,
  Col,
  Table,
  FormGroup,
  Label,
  Input,
  Modal,
  ModalBody,
  ModalFooter,
} from "reactstrap";

class Drivers extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      name: "",
      email: "",
      removeModal: false,
      driverToRemoveId: null,
      driverToRemoveName: "",
    };

    this.togglRemoveModal = this.toggleRemoveModal.bind(this);
  }

  componentDidMount() {
    this.props.getDriversData();
    this.props.getDailyDeliveryData();
  }

  onChange = (fieldName, value) => {
    this.setState({
      [fieldName]: value,
    });
  };

  onChangeName = (event) => this.onChange("name", event.target.value);
  onChangeEmail = (event) => this.onChange("email", event.target.value);

  isFormValid = () => {
    if (this.state.name === "" || this.state.email === "") return false;
    return true;
  };

  submitDriverData = () => {
    this.props
      .addDriver({
        name: this.state.name,
        email: this.state.email,
      })
      .then(() => {
        this.props.getDriversData();
        if (
          this.props.snackbar.options.message ===
          "Driver added to the team successfully."
        ) {
          this.setState({ name: "", email: "" });
        }
      });
  };

  hasStartedDailyDelivery = () => {
    return this.props.delivery.daily.status !== "NOT_STARTED";
  };

  hasCompletedDailyDelivery = () => {
    return this.props.delivery.daily.status === "COMPLETED";
  };

  areActionsAvailable = () => {
    return !this.hasStartedDailyDelivery() || this.hasCompletedDailyDelivery();
  };

  toggleRemoveModal(driverId, driverName) {
    this.setState({
      removeModal: !this.state.removeModal,
      driverToRemoveId: driverId,
      driverToRemoveName: driverName,
    });
  }

  removeDriver = (driverId) => {
    this.toggleRemoveModal(null, "");

    this.props.removeDriver(driverId).then(() => {
      this.props.getDriversData();
    });
  };

  populateTable = () => {
    return this.props.user.drivers.map((driver) => (
      <tr>
        <td className="custom-td">{driver.name}</td>
        <td className="custom-td">{driver.email}</td>
        <td className="custom-td">{driver.since}</td>
        <td className="custom-td text-center">
          {this.areActionsAvailable() ? (
            <Button
              color="danger"
              onClick={() => this.toggleRemoveModal(driver.id, driver.name)}
            >
              Remove
            </Button>
          ) : (
            <Button color="danger" disabled>
              Remove
            </Button>
          )}
        </td>
      </tr>
    ));
  };

  render() {
    return (
      <Fragment>
        <Modal
          isOpen={this.state.removeModal}
          toggle={() => this.toggleRemoveModal(null, "")}
          style={{ marginTop: "48px" }}
        >
          <div className="modal-header">
            <h5 className="modal-title" id="exampleModalLabel">
              Remove
            </h5>
            <button
              type="button"
              className="close"
              data-dismiss="modal"
              aria-hidden="true"
              onClick={() => this.toggleRemoveModal(null, "")}
            >
              <i className="tim-icons icon-simple-remove" />
            </button>
          </div>
          <ModalBody>
            <p>
              Are you sure you want to remove{" "}
              <span className="highlighted-text-primary">
                {this.state.driverToRemoveName}{" "}
              </span>
              from your team?
            </p>
          </ModalBody>
          <ModalFooter>
            <Button
              color="secondary"
              onClick={() => this.toggleRemoveModal(null, "")}
            >
              Close
            </Button>
            <Button
              color="primary"
              onClick={() => this.removeDriver(this.state.driverToRemoveId)}
            >
              Yes
            </Button>
          </ModalFooter>
        </Modal>

        <Row>
          <Col lg="8">
            <Card>
              <CardHeader>
                <CardTitle tag="h4">Drivers</CardTitle>
              </CardHeader>
              <CardBody>
                <Table className="tablesorter">
                  <thead className="text-primary">
                    <tr>
                      <th>Name</th>
                      <th>Email</th>
                      <th>Since</th>
                      <th className="text-center">Action</th>
                    </tr>
                  </thead>
                  <tbody>{this.populateTable()}</tbody>
                </Table>
              </CardBody>
            </Card>
          </Col>

          <Col lg="4">
            <div className="sticky-card-holder">
              <Card className="add-driver-card">
                <CardBody>
                  <form>
                    <FormGroup>
                      <Label for="Name">
                        <span>Name</span>
                      </Label>
                      <Input
                        type="username"
                        placeholder="Enter name"
                        value={this.state.name}
                        onChange={this.onChangeName}
                      />
                    </FormGroup>
                    <FormGroup>
                      <Label for="Email">
                        <span>Email</span>
                      </Label>
                      <Input
                        type="email"
                        placeholder="Enter email"
                        value={this.state.email}
                        onChange={this.onChangeEmail}
                      />
                    </FormGroup>
                    {this.areActionsAvailable() ? (
                      <Button
                        color="primary"
                        style={{ float: "right" }}
                        onClick={() => this.submitDriverData()}
                      >
                        Add Driver
                      </Button>
                    ) : (
                      <Button
                        color="primary"
                        style={{ float: "right" }}
                        disabled
                      >
                        Add Driver
                      </Button>
                    )}
                  </form>
                </CardBody>
              </Card>
            </div>
          </Col>
        </Row>
      </Fragment>
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
      { getDriversData, getDailyDeliveryData, addDriver, removeDriver },
      dispatch
    ),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Drivers);

import React from "react";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";
import { getDriversData, getDailyDeliveryData } from "../actions/Actions";

import {
  Button,
  Card,
  CardHeader,
  CardBody,
  CardTitle,
  Row,
  Col,
} from "reactstrap";

class Dashboard extends React.Component {
  componentDidMount() {
    this.props.getDriversData();
    this.props.getDailyDeliveryData();
  }

  goTo = (page) => {
    this.props.history.push(page);
  };

  render() {
    return (
      <div className="content">
        <Row>
          <Col lg="4">
            <Card className="card-chart">
              <CardHeader>
                <h5 className="card-category">Your Team</h5>
                <CardTitle tag="h3">
                  <i className="tim-icons icon-single-02 text-primary" />
                  <i>{this.props.user.driversCount} drivers</i>
                </CardTitle>
              </CardHeader>
              <CardBody>
                <Button
                  color="primary"
                  style={{ float: "right" }}
                  onClick={() => this.goTo("/drivers")}
                >
                  Edit
                </Button>
              </CardBody>
            </Card>
          </Col>
          <Col lg="4">
            <Card className="card-chart">
              <CardHeader>
                <h5 className="card-category">Daily Delivery</h5>
                <CardTitle tag="h3">
                  <i className="tim-icons icon-delivery-fast text-primary" />{" "}
                  <i>{this.props.delivery.daily.status}</i>
                </CardTitle>
              </CardHeader>
              <CardBody>
                <Button
                  color="primary"
                  style={{ float: "right" }}
                  onClick={() => this.goTo("/delivery")}
                >
                  Proceed
                </Button>
              </CardBody>
            </Card>
          </Col>
          <Col lg="4">
            <Card className="card-chart">
              <CardHeader>
                <h5 className="card-category">Statistics</h5>
                <CardTitle tag="h3">
                  <i className="tim-icons icon-sound-wave text-primary" />{" "}
                  &fnof;<i>(business, time)</i>
                </CardTitle>
              </CardHeader>
              <CardBody>
                <Button
                  color="primary"
                  style={{ float: "right" }}
                  onClick={() => this.goTo("/statistics")}
                >
                  Analyze
                </Button>
              </CardBody>
            </Card>
          </Col>
        </Row>
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
    ...bindActionCreators({ getDriversData, getDailyDeliveryData }, dispatch),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Dashboard);

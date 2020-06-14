import React, { Fragment } from "react";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";
import {
  getDeliveryDataForLast30Days,
  setNotification,
} from "../actions/Actions";

import { Line } from "react-chartjs-2";
import { Card, CardHeader, CardBody, CardTitle } from "reactstrap";

const options1 = {
  maintainAspectRatio: false,
  legend: {
    display: false,
  },
  tooltips: {
    backgroundColor: "#f5f5f5",
    titleFontColor: "#333",
    bodyFontColor: "#666",
    bodySpacing: 4,
    xPadding: 12,
    mode: "nearest",
    intersect: 0,
    position: "nearest",
  },
  responsive: true,
  scales: {
    yAxes: [
      {
        barPercentage: 1.6,
        gridLines: {
          drawBorder: false,
          color: "rgba(29,140,248,0.0)",
          zeroLineColor: "transparent",
        },
        ticks: {
          suggestedMin: 2,
          suggestedMax: 10,
          padding: 5,
          fontColor: "#9a9a9a",
        },
      },
    ],
    xAxes: [
      {
        barPercentage: 1.6,
        gridLines: {
          drawBorder: false,
          color: "rgba(29,140,248,0.1)",
          zeroLineColor: "transparent",
        },
        ticks: {
          padding: 20,
          fontColor: "#9a9a9a",
        },
      },
    ],
  },
};

const options2 = {
  maintainAspectRatio: false,
  legend: {
    display: false,
  },
  tooltips: {
    backgroundColor: "#f5f5f5",
    titleFontColor: "#333",
    bodyFontColor: "#666",
    bodySpacing: 4,
    xPadding: 12,
    mode: "nearest",
    intersect: 0,
    position: "nearest",
  },
  responsive: true,
  scales: {
    yAxes: [
      {
        barPercentage: 1.6,
        gridLines: {
          drawBorder: false,
          color: "rgba(29,140,248,0.0)",
          zeroLineColor: "transparent",
        },
        ticks: {
          suggestedMin: 60,
          suggestedMax: 125,
          padding: 20,
          fontColor: "#9a9a9a",
        },
      },
    ],
    xAxes: [
      {
        barPercentage: 1.6,
        gridLines: {
          drawBorder: false,
          color: "rgba(29,140,248,0.1)",
          zeroLineColor: "transparent",
        },
        ticks: {
          padding: 20,
          fontColor: "#9a9a9a",
        },
      },
    ],
  },
};

class Statistics extends React.Component {
  componentDidMount() {
    this.props.getDeliveryDataForLast30Days().then(() => {
      if (this.props.delivery.statistics.length === 0) {
        let options = this.getOptions(
          "You have no COMPLETED deliveries in the last 30 days."
        );
        this.props.setNotification(options);
      }
    });
  }

  getOptions = (message) => {
    return {
      message: message,
      type: "primary",
      icon: "tim-icons icon-bell-55",
      autoDismiss: 10,
    };
  };

  data1 = (canvas) => {
    var ctx = canvas.getContext("2d");

    let gradientStroke = ctx.createLinearGradient(0, 230, 0, 50);
    gradientStroke.addColorStop(1, "rgba(29,140,248,0.2)");
    gradientStroke.addColorStop(0.4, "rgba(29,140,248,0.0)");
    gradientStroke.addColorStop(0, "rgba(29,140,248,0)"); //blue colors

    var myLabels = this.props.delivery.statistics.map(
      (delivery) => delivery.date
    );

    var myData = this.props.delivery.statistics.map(
      (delivery) => delivery.actualDriversCount
    );

    return {
      labels: myLabels,
      datasets: [
        {
          label: "Drivers",
          borderColor: "#1f8ef1",
          pointBorderColor: "#FFF",
          pointBackgroundColor: "#1f8ef1",
          pointHoverBackgroundColor: "#1f8ef1",
          pointBorderWidth: 2,
          pointHoverRadius: 4,
          pointHoverBorderWidth: 1,
          pointRadius: 4,
          fill: true,
          backgroundColor: gradientStroke,
          borderWidth: 2,
          data: myData,
        },
      ],
    };
  };

  data2 = (canvas) => {
    var ctx = canvas.getContext("2d");

    let gradientStroke = ctx.createLinearGradient(0, 230, 0, 50);
    gradientStroke.addColorStop(1, "rgba(66,134,121,0.15)");
    gradientStroke.addColorStop(0.4, "rgba(66,134,121,0.0)"); //green colors
    gradientStroke.addColorStop(0, "rgba(66,134,121,0)"); //green colors

    var myLabels = this.props.delivery.statistics.map(
      (delivery) => delivery.date
    );

    var myData = this.props.delivery.statistics.map(
      (delivery) => delivery.packagesCount
    );

    return {
      labels: myLabels,
      datasets: [
        {
          label: "Packages",
          borderColor: "#00d6b4",
          pointBorderColor: "#FFF",
          pointBackgroundColor: "#00d6b4",
          pointHoverBackgroundColor: "#00d6b4",
          pointBorderWidth: 2,
          pointHoverRadius: 4,
          pointHoverBorderWidth: 1,
          pointRadius: 4,
          fill: true,
          backgroundColor: gradientStroke,
          borderWidth: 2,
          data: myData,
        },
      ],
    };
  };

  data3 = (canvas) => {
    var ctx = canvas.getContext("2d");

    var gradientFill = ctx.createLinearGradient(0, 170, 0, 50);
    gradientFill.addColorStop(0, "rgba(128, 182, 244, 0)");
    gradientFill.addColorStop(1, "rgba(249, 99, 59, 0.40)");

    var myLabels = this.props.delivery.statistics.map(
      (delivery) => delivery.date
    );

    var myData = this.props.delivery.statistics.map(
      (delivery) => delivery.minimumDistanceToCover
    );

    return {
      labels: myLabels,
      datasets: [
        {
          label: "Minimum Distance",
          borderColor: "#f96332",
          pointBorderColor: "#FFF",
          pointBackgroundColor: "#f96332",
          pointBorderWidth: 2,
          pointHoverRadius: 4,
          pointHoverBorderWidth: 1,
          pointRadius: 4,
          fill: true,
          backgroundColor: gradientFill,
          borderWidth: 2,
          data: myData,
        },
      ],
    };
  };

  render() {
    return (
      <Fragment>
        <Card className="card-chart">
          <CardHeader>
            <CardTitle tag="h4">
              <i className="tim-icons icon-single-02 text-info" />
              Drivers assigned for delivery
            </CardTitle>
          </CardHeader>
          <CardBody>
            <div className="chart-area">
              <Line data={this.data1} options={options1} />
            </div>
          </CardBody>
        </Card>

        <Card className="card-chart">
          <CardHeader>
            <CardTitle tag="h4">
              <i className="tim-icons icon-app" style={{ color: "#00d6b4" }} />{" "}
              Packages delivered
            </CardTitle>
          </CardHeader>
          <CardBody>
            <div className="chart-area">
              <Line data={this.data2} options={options2} />
            </div>
          </CardBody>
        </Card>

        <Card className="card-chart">
          <CardHeader>
            <CardTitle tag="h4">
              <i
                className="tim-icons icon-world"
                style={{ color: "#f96332" }}
              />{" "}
              Minimum Distance covered (km)
            </CardTitle>
          </CardHeader>
          <CardBody>
            <div className="chart-area">
              <Line data={this.data3} options={options2} />
            </div>
          </CardBody>
        </Card>
      </Fragment>
    );
  }
}

const mapStateToProps = (state) => {
  return { delivery: state.delivery };
};

const mapDispatchToProps = (dispatch) => {
  return {
    ...bindActionCreators(
      { getDeliveryDataForLast30Days, setNotification },
      dispatch
    ),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Statistics);

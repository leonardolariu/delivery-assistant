import React from "react";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";
import { register } from "../actions/Actions";

import {
  Row,
  Col,
  FormGroup,
  Label,
  Input,
  Card,
  CardHeader,
  CardBody,
  CardTitle,
  Button,
} from "reactstrap";

class Registration extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      username: "",
      password: "",
    };
  }

  onChange = (fieldName, value) => {
    this.setState({
      [fieldName]: value,
    });
  };

  onChangeUsername = (event) => this.onChange("username", event.target.value);
  onChangePassword = (event) => this.onChange("password", event.target.value);

  isFormValid = () => {
    if (this.state.username === "" || this.state.password === "") return false;
    return true;
  };

  submitRegisterData = () => {
    this.props
      .register({
        username: this.state.username,
        password: this.state.password,
      })
      .then(() => {
        if (
          this.props.snackbar.options.message ===
          "Account created successfully."
        )
          this.props.history.push("/login");
      });
  };

  goTo = (page) => {
    this.props.history.push(page);
  };

  render() {
    return (
      <div className="content">
        <Row>
          <Col lg="4"></Col>
          <Col lg="4" style={{ margin: "auto" }}>
            <Card className="login-card">
              <CardHeader>
                <CardTitle tag="h1">
                  <span className="highlighted-text-primary">Delivery</span>
                  <span className="highlighted-text-default">Assistant</span>
                </CardTitle>
              </CardHeader>
              <CardBody>
                <form>
                  <FormGroup>
                    <Label for="Username">
                      <span className="highlighted-text-primary">Username</span>
                      <span> | Your unique identifier</span>
                    </Label>
                    <Input
                      type="username"
                      placeholder="Enter username"
                      value={this.state.username}
                      onChange={this.onChangeUsername}
                    />
                  </FormGroup>
                  <FormGroup>
                    <Label for="Password">
                      <span className="highlighted-text-primary">Password</span>
                      <span> | The strogest there is</span>
                    </Label>
                    <Input
                      type="password"
                      placeholder="Enter password"
                      value={this.state.password}
                      onChange={this.onChangePassword}
                    />
                  </FormGroup>
                  <div className="form-buttons-center">
                    {this.isFormValid() ? (
                      <Button
                        color="primary"
                        onClick={() => this.submitRegisterData()}
                      >
                        Register
                      </Button>
                    ) : (
                      <Button color="primary" disabled>
                        Register
                      </Button>
                    )}
                    <Button color="default" onClick={() => this.goTo("/login")}>
                      Login
                    </Button>
                  </div>
                </form>
              </CardBody>
            </Card>
          </Col>
          <Col lg="4"></Col>
        </Row>
      </div>
    );
  }
}

const mapStateToProps = (state) => {
  return {
    user: state.user,
    snackbar: state.snackbar,
  };
};

const mapDispatchToProps = (dispatch) => {
  return {
    ...bindActionCreators({ register }, dispatch),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Registration);

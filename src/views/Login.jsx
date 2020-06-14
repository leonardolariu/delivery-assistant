import React from "react";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";
import { login } from "../actions/Actions";
import { isLoggedIn } from "../actions/Utils";

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

class Login extends React.Component {
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

  submitLoginData = () => {
    this.props
      .login({
        username: this.state.username,
        password: this.state.password,
      })
      .then(() => {
        if (isLoggedIn()) this.props.history.push("/dashboard");
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
                        onClick={() => this.submitLoginData()}
                      >
                        Login
                      </Button>
                    ) : (
                      <Button color="primary" disabled>
                        Login
                      </Button>
                    )}
                    <Button
                      color="default"
                      onClick={() => this.goTo("/registration")}
                    >
                      Register
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
    ...bindActionCreators({ login }, dispatch),
  };
};

export default connect(mapStateToProps, mapDispatchToProps)(Login);

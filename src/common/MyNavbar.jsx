import React, { Fragment } from "react";
import { bindActionCreators } from "redux";
import { connect } from "react-redux";
import { withRouter } from "react-router-dom";

import {
  Navbar,
  Nav,
  NavItem,
  NavLink,
  Button,
  Modal,
  ModalBody,
  ModalFooter,
} from "reactstrap";

const PAGES_WITHOUT_NAVBAR = ["/login", "/registration"];

class MyNavbar extends React.Component {
  constructor(props) {
    super(props);

    this.state = {
      logoutModal: false,
    };

    this.toggleLogoutModal = this.toggleLogoutModal.bind(this);
    this.logoutAction = this.logoutAction.bind(this);
  }

  toggleLogoutModal() {
    this.setState({
      logoutModal: !this.state.logoutModal,
    });
  }

  logoutAction() {
    this.setState({
      logoutModal: !this.state.logoutModal,
    });

    localStorage.setItem("jwt", "");
    localStorage.setItem("role", "");
    this.props.history.push("/login");
  }

  goTo = (page) => {
    this.props.history.push(page);
  };

  render() {
    if (
      PAGES_WITHOUT_NAVBAR.filter(
        (path) => this.props.location.pathname === path
      ).length > 0
    ) {
      return null;
    }

    return (
      <Fragment>
        <Navbar>
          <Nav>
            <NavItem onClick={() => this.goTo("/dashboard")}>
              <NavLink href="#">Dashboard</NavLink>
            </NavItem>
            <NavItem onClick={() => this.goTo("/drivers")}>
              <NavLink href="#">Drivers</NavLink>
            </NavItem>
            <NavItem onClick={() => this.goTo("/delivery")}>
              <NavLink href="#">Delivery</NavLink>
            </NavItem>
            <NavItem onClick={() => this.goTo("/statistics")}>
              <NavLink href="#">Statistics</NavLink>
            </NavItem>
          </Nav>
          <Nav>
            <NavItem>
              <NavLink href="#" onClick={this.toggleLogoutModal}>
                Logout
              </NavLink>
            </NavItem>
          </Nav>
        </Navbar>

        <Modal
          isOpen={this.state.logoutModal}
          toggle={this.toggleLogoutModal}
          style={{ marginTop: "48px" }}
        >
          <div className="modal-header">
            <h5 className="modal-title" id="exampleModalLabel">
              Logout
            </h5>
            <button
              type="button"
              className="close"
              data-dismiss="modal"
              aria-hidden="true"
              onClick={this.toggleLogoutModal}
            >
              <i className="tim-icons icon-simple-remove" />
            </button>
          </div>
          <ModalBody>
            <p>Are you sure you want to leave?</p>
          </ModalBody>
          <ModalFooter>
            <Button color="secondary" onClick={this.toggleLogoutModal}>
              Close
            </Button>
            <Button color="primary" onClick={this.logoutAction}>
              Yes
            </Button>
          </ModalFooter>
        </Modal>
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
    ...bindActionCreators({}, dispatch),
  };
};

export default withRouter(
  connect(mapStateToProps, mapDispatchToProps)(MyNavbar)
);

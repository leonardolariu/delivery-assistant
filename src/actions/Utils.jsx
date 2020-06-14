import axios from "axios";
import _ from "lodash";

export const API_ENDPOINT =
  "https://backend-dot-delivery-assistant-278109.oa.r.appspot.com";
// "http://localhost:8080";

export const getUserRole = () => {
  return localStorage.getItem("role");
};

export const isLoggedIn = () => {
  // console.log(_.isNil(localStorage.getItem("jwt")));
  return (
    localStorage.getItem("jwt") != null && localStorage.getItem("jwt") !== ""
  );
};

export const isAdmin = () => {
  return localStorage.getItem("role").toUpperCase() === "ROLE_ADMIN";
};

export const isUser = () => {
  return localStorage.getItem("role").toUpperCase() === "ROLE_USER";
};

export const GET = (url, crossDomain = true) => {
  return axios.get(url, {
    crossDomain,
  });
};

export const POST = (url, data, crossDomain = true) => {
  return axios.post(url, data, {
    crossDomain,
  });
};

export const AUTHORIZED_GET = (url, crossDomain = true) => {
  return axios.get(url, {
    headers: { Authorization: `Bearer ${localStorage.getItem("jwt")}` },
    crossDomain,
  });
};

export const AUTHORIZED_POST = (url, data, crossDomain = true) => {
  return axios.post(url, data, {
    headers: { Authorization: `Bearer ${localStorage.getItem("jwt")}` },
    crossDomain,
  });
};

export const AUTHORIZED_PUT = (url, data, crossDomain = true) => {
  return axios.put(url, data, {
    headers: { Authorization: `Bearer ${localStorage.getItem("jwt")}` },
    crossDomain,
  });
};

export const AUTHORIZED_DELETE = (url, data, crossDomain = true) => {
  return axios.delete(url, data, {
    headers: { Authorization: `Bearer ${localStorage.getItem("jwt")}` },
    crossDomain,
  });
};

export const AUTHORIZED_DELETE_WITHOUT_BODY = (url, crossDomain = true) => {
  return axios.delete(url, {
    headers: { Authorization: `Bearer ${localStorage.getItem("jwt")}` },
    crossDomain,
  });
};

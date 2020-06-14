import React from "react";
import "./Marker.scss";

const Marker = (props) => {
  const { color, order } = props;

  return (
    <div
      className="marker"
      style={{ backgroundColor: color, cursor: "pointer" }}
    >
      <div className="marker-text">{order}</div>
    </div>
  );
};

export default Marker;

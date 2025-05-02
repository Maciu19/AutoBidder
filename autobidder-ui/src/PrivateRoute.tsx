import { Outlet } from "react-router-dom";
import KeycloakService from "./services/KeycloakService.tsx";

const PrivateRoute = () => {
  const keycloak = KeycloakService.getKeycloak();

  if (!keycloak.authenticated || keycloak.isTokenExpired()) {
    keycloak.login(); 
    return null;
  }

  return <Outlet />;
};

export default PrivateRoute;
import { useEffect } from "react";
import KeycloackService from "./services/KeycloakService";

function App() {
  useEffect(() => {
    const initKeycloak = async () => {
      await KeycloackService.getKeycloak().init();
    };

    initKeycloak();
  }, []);

  return <></>;
}

export default App;

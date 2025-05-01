import { useEffect } from "react";
import KeycloackService from "./services/KeycloakService";

function App() {
  useEffect(() => {
    const initKeycloak = async () => {
      await KeycloackService.init();
    };

    initKeycloak();
  }, []);

  return (
    <>
    </>
  )
}

export default App;

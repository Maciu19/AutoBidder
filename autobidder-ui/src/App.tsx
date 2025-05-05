import { useEffect } from "react";
import { getKeycloak } from "./auth";

function App() {
  useEffect(() => {
    const initKeycloak = async () => {
      await getKeycloak();
    };

    initKeycloak();
  }, []);

  return <></>
}

export default App;

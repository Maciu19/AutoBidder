import Keycloak from "keycloak-js";

class KeycloackService {
    private static instance: Keycloak | undefined;

    public static getKeycloak(): Keycloak {
        if (!KeycloackService.instance) {
            KeycloackService.instance = new Keycloak ({
                url: "http://localhost:9090",
                realm: "auto-bidder",
                clientId: "auto-bidder",
            });
        }

        return KeycloackService.instance;
    }

    public static async init(): Promise<void> {
        console.log("Auth the user...");
        const auth = await KeycloackService.getKeycloak().init({
            onLoad: 'login-required'
        });

        if (auth) {
            console.log("User auth succesfully");
        }
    }
}

export default KeycloackService;
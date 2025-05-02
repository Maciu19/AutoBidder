import Keycloak from "keycloak-js";
import { UserProfile } from "../model/user-profile";

class KeycloakService {
  private static instance: Keycloak | undefined;
  private static profile: UserProfile | undefined;
  private static initialized = false;


  public static getKeycloak(): Keycloak {
    if (!KeycloakService.instance) {
      KeycloakService.instance = new Keycloak({
        url: "http://localhost:9090",
        realm: "auto-bidder",
        clientId: "auto-bidder",
      });
    }

    return KeycloakService.instance;
  }

  public static getProfile(): UserProfile | undefined {
    return KeycloakService.profile;
  }

  public static getToken(): string | undefined {
    return KeycloakService.getKeycloak().token;
  }

  public static async init(): Promise<void> {
    if (KeycloakService.initialized) return;

    const auth = await KeycloakService.getKeycloak().init({
      onLoad: "login-required",
    });

    if (auth) {
      const keycloak = KeycloakService.getKeycloak();

      KeycloakService.profile = (keycloak.loadUserProfile()) as UserProfile;
      KeycloakService.initialized = true;
    }
  }

  public login(): void {
    KeycloakService.getKeycloak().login();
  }

  public logout(): void {
    KeycloakService.getKeycloak().logout({
      redirectUri: "http://localhost:5173",
    });
  }
}

export default KeycloakService;

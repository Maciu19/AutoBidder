import Keycloak from "keycloak-js";
import { UserProfile } from "../model/user-profile";

class KeycloackService {
  private static instance: Keycloak | undefined;
  private profile: UserProfile | undefined;

  public static getKeycloak(): Keycloak {
    if (!KeycloackService.instance) {
      KeycloackService.instance = new Keycloak({
        url: "http://localhost:9090",
        realm: "auto-bidder",
        clientId: "auto-bidder",
      });
    }

    return KeycloackService.instance;
  }

  public getProfile(): UserProfile | undefined {
    return this.profile;
  }

  public async init(): Promise<void> {
    const auth = await KeycloackService.getKeycloak().init({
      onLoad: "login-required",
    });

    if (auth) {
      this.profile =
        (await KeycloackService.getKeycloak().loadUserProfile()) as UserProfile;
      this.profile.token = KeycloackService.getKeycloak().token;
    }
  }

  public login(): void {
    KeycloackService.getKeycloak().login();
  }

  public logout(): void {
    KeycloackService.getKeycloak().logout({
      redirectUri: "http:localhost:5173",
    });
  }
}

export default KeycloackService;

import Keycloak, { KeycloakProfile } from "keycloak-js";

let keycloak: Keycloak | undefined;

export async function getKeycloak(): Promise<Keycloak> {
  if (keycloak) {
    return keycloak;
  }

  keycloak = new Keycloak({
    url: "http://localhost:9090",
    realm: "auto-bidder",
    clientId: "auto-bidder",
  });

  await keycloak.init({
    onLoad: "login-required",
    redirectUri: "http://localhost:5173",
  });

  return keycloak;
}

export async function getUserProfile(): Promise<KeycloakProfile | null> {
  const keycloak = await getKeycloak();

  if (keycloak.authenticated) {
    const profile: KeycloakProfile = await keycloak.loadUserProfile();
    return profile;
  }

  return null;
}

export async function withAuthCheck<T>(
  action: () => Promise<T>
): Promise<T | null> {
  if (keycloak === undefined) {
    return null;
  }

  try {
    const refreshed = await keycloak.updateToken(5);

    if (!refreshed && !keycloak.authenticated) {
      console.warn("User session expired. Logging out.");
      keycloak.logout();
      return null;
    }

    return await action();
  } catch (error) {
    console.error("Token check or action failed", error);
    keycloak.logout();
    return null;
  }
}

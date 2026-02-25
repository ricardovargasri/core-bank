import Keycloak from "keycloak-js";

const keycloakConfig = {
    url: "http://localhost:7080",
    realm: "BankaRealm",
    clientId: "banka-client",
};

const keycloak = new Keycloak(keycloakConfig);

export default keycloak;

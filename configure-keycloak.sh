#!/bin/bash
# Script to configure Keycloak BankaRealm settings via Admin API
# This is needed because Keycloak ignores realm-export.json when the realm already exists

KEYCLOAK_URL="http://localhost:7080"
MAX_RETRIES=30
RETRY_INTERVAL=3

echo "⏳ Esperando a que Keycloak esté listo..."

for i in $(seq 1 $MAX_RETRIES); do
    if curl -sf "$KEYCLOAK_URL/realms/master" > /dev/null 2>&1; then
        echo "✅ Keycloak está listo."
        break
    fi
    if [ "$i" -eq "$MAX_RETRIES" ]; then
        echo "❌ Keycloak no respondió después de $((MAX_RETRIES * RETRY_INTERVAL))s"
        exit 1
    fi
    sleep $RETRY_INTERVAL
done

# Obtain admin token
TOKEN=$(curl -sf -X POST "$KEYCLOAK_URL/realms/master/protocol/openid-connect/token" \
    -d "client_id=admin-cli" \
    -d "username=admin" \
    -d "password=admin" \
    -d "grant_type=password" | python3 -c "import sys,json; print(json.load(sys.stdin)['access_token'])")

if [ -z "$TOKEN" ]; then
    echo "❌ No se pudo obtener el token de admin."
    exit 1
fi

echo "🔧 Configurando BankaRealm (registro habilitado)..."

HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X PUT "$KEYCLOAK_URL/admin/realms/BankaRealm" \
    -H "Authorization: Bearer $TOKEN" \
    -H "Content-Type: application/json" \
    -d '{
        "registrationAllowed": true,
        "registrationEmailAsUsername": false,
        "resetPasswordAllowed": true,
        "rememberMe": true,
        "verifyEmail": false
    }')

if [ "$HTTP_CODE" = "204" ]; then
    echo "✅ Registro de usuarios habilitado correctamente."
else
    echo "❌ Error al configurar el realm (HTTP $HTTP_CODE)."
fi

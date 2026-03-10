#!/bin/bash

# Configuration
ACCOUNT_NUMBER="6372" # Ajustado a cuenta existente
ADMIN_USER="admin_banka"
ADMIN_PASS="admin123"
URL="http://localhost:8080/api/v1/transactions/deposit"

echo "🔑 Obteniendo token de acceso..."
TOKEN=$(curl -s -X POST "http://localhost:7080/realms/BankaRealm/protocol/openid-connect/token" \
     -H "Content-Type: application/x-www-form-urlencoded" \
     -d "grant_type=password" \
     -d "client_id=banka-client" \
     -d "username=$ADMIN_USER" \
     -d "password=$ADMIN_PASS" | jq -r '.access_token')

if [ "$TOKEN" == "null" ]; then
    echo "❌ Error: No se pudo obtener el token. Revisa las credenciales."
    exit 1
fi

echo "🚀 Insertando 1000 transacciones para la cuenta $ACCOUNT_NUMBER..."

for i in {1..1000}
do
   curl -s -X POST "$URL" \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d "{
              \"accountNumber\": \"$ACCOUNT_NUMBER\",
              \"amount\": 10,
              \"description\": \"Deposito de prueba $i\"
            }" > /dev/null
   
   if (( $i % 100 == 0 )); then
       echo "✅ $i transacciones insertadas..."
   fi
done

echo "✨ ¡Listo! Base de datos inflada con éxito."

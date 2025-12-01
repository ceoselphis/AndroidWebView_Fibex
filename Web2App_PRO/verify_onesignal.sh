#!/bin/bash

# Script de Verificación de OneSignal para Fibex Telecom
# Este script verifica que la configuración de OneSignal esté correcta

echo "🔍 Verificando configuración de OneSignal..."
echo ""

# Colores para output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Contador de errores
ERRORS=0
WARNINGS=0

# Función para verificar archivos
check_file() {
    if [ -f "$1" ]; then
        echo -e "${GREEN}✓${NC} Archivo encontrado: $1"
        return 0
    else
        echo -e "${RED}✗${NC} Archivo NO encontrado: $1"
        ERRORS=$((ERRORS + 1))
        return 1
    fi
}

# Función para verificar contenido en archivo
check_content() {
    if grep -q "$2" "$1"; then
        echo -e "${GREEN}✓${NC} $3"
        return 0
    else
        echo -e "${RED}✗${NC} $3"
        ERRORS=$((ERRORS + 1))
        return 1
    fi
}

# Función para verificar advertencias
check_warning() {
    if grep -q "$2" "$1"; then
        echo -e "${YELLOW}⚠${NC} $3"
        WARNINGS=$((WARNINGS + 1))
        return 1
    else
        echo -e "${GREEN}✓${NC} $3"
        return 0
    fi
}

echo "📁 Verificando estructura de archivos..."
echo ""

# Verificar archivos principales
check_file "build.gradle"
check_file "app/build.gradle"
check_file "app/src/main/AndroidManifest.xml"
check_file "app/src/main/java/com/oficina2/fibex_telecom/MyApplication.java"
check_file "app/src/main/java/com/oficina2/fibex_telecom/MainActivity.java"

echo ""
echo "📝 Verificando configuración en build.gradle (raíz)..."
echo ""

check_content "build.gradle" "onesignal-gradle-plugin" "Plugin de OneSignal agregado"

echo ""
echo "📝 Verificando configuración en app/build.gradle..."
echo ""

check_content "app/build.gradle" "com.onesignal.androidsdk.onesignal-gradle-plugin" "Plugin de OneSignal aplicado"
check_content "app/build.gradle" "com.onesignal:OneSignal" "Dependencia de OneSignal agregada"

echo ""
echo "📝 Verificando AndroidManifest.xml..."
echo ""

check_content "app/src/main/AndroidManifest.xml" "android.permission.POST_NOTIFICATIONS" "Permiso POST_NOTIFICATIONS agregado"
check_content "app/src/main/AndroidManifest.xml" "android.permission.VIBRATE" "Permiso VIBRATE agregado"
check_content "app/src/main/AndroidManifest.xml" "android:name=\".MyApplication\"" "MyApplication registrada"

echo ""
echo "📝 Verificando MyApplication.java..."
echo ""

check_content "app/src/main/java/com/oficina2/fibex_telecom/MyApplication.java" "OneSignal.initWithContext" "OneSignal inicializado"
check_warning "app/src/main/java/com/oficina2/fibex_telecom/MyApplication.java" "TU_ONESIGNAL_APP_ID_AQUI" "ADVERTENCIA: App ID aún no configurado (debes reemplazar TU_ONESIGNAL_APP_ID_AQUI)"

echo ""
echo "📝 Verificando MainActivity.java..."
echo ""

check_content "app/src/main/java/com/oficina2/fibex_telecom/MainActivity.java" "notification_url" "Soporte para URLs desde notificaciones agregado"

echo ""
echo "📚 Verificando documentación..."
echo ""

check_file "ONESIGNAL_SETUP_GUIDE.md"
check_file "NOTIFICATION_EXAMPLES.md"
check_file "README_ONESIGNAL.md"
check_file "CHECKLIST_ONESIGNAL.md"

echo ""
echo "═══════════════════════════════════════════════════════"
echo ""

if [ $ERRORS -eq 0 ] && [ $WARNINGS -eq 0 ]; then
    echo -e "${GREEN}✅ ¡Configuración completa y correcta!${NC}"
    echo ""
    echo "Próximos pasos:"
    echo "1. Obtén tu OneSignal App ID en: https://app.onesignal.com/"
    echo "2. Reemplaza 'TU_ONESIGNAL_APP_ID_AQUI' en MyApplication.java"
    echo "3. Compila e instala la app: ./gradlew installDebug"
    echo "4. Envía una notificación de prueba desde el dashboard"
elif [ $ERRORS -eq 0 ] && [ $WARNINGS -gt 0 ]; then
    echo -e "${YELLOW}⚠ Configuración completa con $WARNINGS advertencia(s)${NC}"
    echo ""
    echo "Debes completar:"
    echo "1. Reemplazar 'TU_ONESIGNAL_APP_ID_AQUI' en MyApplication.java con tu App ID real"
    echo "2. Obtén tu App ID en: https://app.onesignal.com/ → Settings → Keys & IDs"
else
    echo -e "${RED}❌ Se encontraron $ERRORS error(es) y $WARNINGS advertencia(s)${NC}"
    echo ""
    echo "Por favor, revisa los errores arriba y corrige la configuración."
    echo "Consulta ONESIGNAL_SETUP_GUIDE.md para más información."
fi

echo ""
echo "═══════════════════════════════════════════════════════"
echo ""

exit $ERRORS

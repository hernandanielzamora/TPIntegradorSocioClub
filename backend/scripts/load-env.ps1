# Compatible con Windows PowerShell 5.1. No evalua ni imprime valores del archivo.
& {
    $envFile = Join-Path (Split-Path -Parent $PSScriptRoot) '.env'
    if (-not (Test-Path -LiteralPath $envFile -PathType Leaf)) {
        throw 'Falta backend/.env. Copiar backend/.env.example y completar las claves locales.'
    }

    $required = @('DB_URL', 'DB_USERNAME', 'DB_PASSWORD', 'MYSQL_TEST_DB_URL',
        'MYSQL_TEST_DB_USERNAME', 'MYSQL_TEST_DB_PASSWORD', 'MYSQL_TEST_ENABLED')
    $allowed = $required + @('PORT', 'CORS_ALLOWED_ORIGINS')
    $values = @{}
    $lineNumber = 0
    foreach ($line in [System.IO.File]::ReadAllLines($envFile)) {
        $lineNumber++
        if ($line -match '^\s*(#|$)') { continue }
        # Separar solo por el primer =; nunca interpretar comentarios dentro del valor.
        $separator = $line.IndexOf('=')
        if ($separator -lt 1) { throw "Formato .env invalido en linea $lineNumber." }
        $name = $line.Substring(0, $separator).Trim()
        if ($name -cnotin $allowed) { throw "Variable no admitida en linea $lineNumber." }
        if ($values.ContainsKey($name)) { throw "Variable duplicada: $name." }
        $value = $line.Substring($separator + 1).Trim()
        # Comillas externas opcionales; el interior se conserva sin escapes ni expansion.
        if ($value.StartsWith('"') -or $value.StartsWith("'")) {
            if ($value.Length -lt 2 -or $value[$value.Length - 1] -ne $value[0]) {
                throw "Comillas sin cerrar en linea $lineNumber."
            }
            $value = $value.Substring(1, $value.Length - 2)
        }
        $values[$name] = $value
    }

    $missing = @($required | Where-Object {
        -not $values.ContainsKey($_) -or [string]::IsNullOrWhiteSpace($values[$_])
    })
    if ($missing.Count -gt 0) { throw ('Faltan variables en .env: ' + ($missing -join ', ')) }
    if ($values['DB_PASSWORD'] -eq 'COMPLETAR_CLAVE_DESARROLLO' -or
        $values['MYSQL_TEST_DB_PASSWORD'] -eq 'COMPLETAR_CLAVE_PRUEBAS') {
        throw 'Completar las dos claves locales en backend/.env antes de cargarlo.'
    }
    if ($values['DB_URL'] -cnotmatch '^jdbc:mysql://[^/]+/socioclub(\?.*)?$' -or
        $values['DB_USERNAME'] -cne 'socioclub_app' -or
        $values['MYSQL_TEST_DB_URL'] -cnotmatch '^jdbc:mysql://[^/]+/socioclub_test(\?.*)?$' -or
        $values['MYSQL_TEST_DB_USERNAME'] -cne 'socioclub_tests') {
        throw 'Se requieren socioclub/socioclub_app y socioclub_test/socioclub_tests separados.'
    }
    if ($values['MYSQL_TEST_ENABLED'] -notin @('true', 'false')) {
        throw 'MYSQL_TEST_ENABLED debe ser true o false.'
    }
    # Validar todo antes de cambiar variables; solo afecta este proceso y sus futuros hijos.
    foreach ($name in $values.Keys) {
        [System.Environment]::SetEnvironmentVariable($name, $values[$name], 'Process')
    }
    Write-Host 'Entorno local cargado en esta sesion (sin mostrar valores).'
}

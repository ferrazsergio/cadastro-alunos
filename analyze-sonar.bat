@echo off
echo === Iniciando analise SonarQube com modo detalhado ===

REM Verificar se o servidor está acessível
echo Verificando servidor SonarQube...
curl -s http://localhost:9000/api/system/status
if %ERRORLEVEL% NEQ 0 (
  echo ERRO: Servidor SonarQube nao esta acessivel em http://localhost:9000
  exit /b 1
)

REM Verificar se o token é válido
echo.
echo Verificando token SonarQube...
curl -s -u %SONAR_TOKEN%: http://localhost:9000/api/authentication/validate
echo.

REM Compilar o projeto (necessário para análise)
echo.
echo Compilando projeto...
mvn clean package -DskipTests

REM Executar a análise com logs detalhados
echo.
echo Executando analise SonarQube...
mvn sonar:sonar ^
  -Dsonar.host.url=http://localhost:9000 ^
  -Dsonar.login=sqp_ca102fd4aae7880e975ce4bd2da3357994c20cb5 ^
  -Dsonar.projectKey=ferrazsergio_cadastro-alunos ^
  -Dsonar.projectName="Cadastro Alunos" ^
  -Dsonar.sources=src/main/java ^
  -Dsonar.java.binaries=target/classes ^
  -Dsonar.verbose=true ^
  -X

echo.
echo === Processo concluido ===
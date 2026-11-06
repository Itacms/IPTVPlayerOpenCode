@echo off
setlocal EnableExtensions EnableDelayedExpansion

rem ============================================================
rem  emulador.cmd - abre o emulador Android (usado pelo VS Code)
rem
rem  Uso:
rem    emulador.cmd               -> abre o AVD de TV (ou o primeiro da lista)
rem    emulador.cmd <nome_do_avd> -> abre o AVD indicado
rem    emulador.cmd --cold        -> AVD padrao em cold boot
rem    emulador.cmd <avd> --cold  -> AVD indicado em cold boot
rem    emulador.cmd --list        -> apenas lista os AVDs
rem
rem  Variaveis opcionais:
rem    EMULATOR_EXE  caminho do emulator.exe
rem    DEFAULT_AVD   nome do AVD padrao (senao procura um que tenha "TV")
rem ============================================================

set "EMU=%EMULATOR_EXE%"
if "%EMU%"=="" set "EMU=%LOCALAPPDATA%\Android\Sdk\emulator\emulator.exe"
if not exist "%EMU%" (
  echo [ERRO] emulator.exe nao encontrado em "%EMU%"
  echo        Defina EMULATOR_EXE com o caminho completo.
  exit /b 3
)

set "SDK_ROOT=%EMU%\..\.."
set "ANDROID_SDK_ROOT=%SDK_ROOT%"
set "ANDROID_HOME=%SDK_ROOT%"

set "COLD="
set "AVD="
for %%A in (%*) do (
  if /i "%%~A"=="--cold" (
    set "COLD=1"
  ) else if /i "%%~A"=="--list" (
    "%EMU%" -list-avds
    exit /b 0
  ) else (
    set "AVD=%%~A"
  )
)

if not "%AVD%"=="" goto :tem_avd

rem -- sem AVD informado: usa DEFAULT_AVD, senao o primeiro AVD com "TV" no nome
if defined DEFAULT_AVD (
  set "AVD=%DEFAULT_AVD%"
  goto :tem_avd
)

set "PRIMEIRO="
set "TVAVD="
for /f "usebackq delims=" %%V in (`""%EMU%" -list-avds"`) do (
  if not defined PRIMEIRO set "PRIMEIRO=%%V"
  if not defined TVAVD (
    echo %%V | findstr /i "TV" >nul && set "TVAVD=%%V"
  )
)
if defined TVAVD (
  set "AVD=!TVAVD!"
) else (
  set "AVD=!PRIMEIRO!"
)

:tem_avd
if "%AVD%"=="" (
  echo [ERRO] Nenhum AVD encontrado.
  echo        Crie um emulador no Android Studio ^(Device Manager^).
  exit /b 2
)

set "ARGS=-avd %AVD%"
if defined COLD set "ARGS=-no-snapshot %ARGS%"

echo Abrindo emulador: %AVD%  (cold boot: %COLD% )
start "Android Emulator - %AVD%" "%EMU%" %ARGS%
exit /b 0

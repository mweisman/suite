@echo off

:: Check for ant on the path
call ant -version >NUL 2>NUL || (
  echo Requires Apache Ant ^(see http://ant.apache.org/^)
  exit /b 1
)

set COMMAND=
set APP_PATH=
set ANT_ARGS=
set NAME=%~n0

:: Find the full path of SDK_HOME 
pushd "%~dp0.."
set SDK_HOME="%cd%"
popd

:: Determine if proper arguments have been supplied
if "x%~1"=="x" goto Usage
if "%~1"=="create" goto Create
if "%~1"=="debug" goto Debug
if "%~1"=="deploy" goto Deploy
goto Usage


:Create
:: Create takes no arguments
if "x%~2"=="x" goto Usage
if "%~2"=="-h" goto UsageCreate
if "%~2"=="--help" goto UsageCreate
set COMMAND="%~1"
set APP_PATH="%~2"
goto Run


:Debug
if "x%~2"=="x" goto Usage
if "%~2"=="-h" goto UsageDebug
if "%~2"=="--help" goto UsageDebug
set COMMAND="%~1"
shift

:: Must be an even number of arguments
:: (Two for each command flag, one for command, one for app-path)
set /a ARGCOUNT = 0
for %%a in (%*) do set /a ARGCOUNT += 1
set /a ARGODD = "%ARGCOUNT% %% 2"
if %ARGODD% == 1 goto Usage
goto DebugFlagLoop

:DebugFlagLoop
:: Sets all of the command flags (ant arguments)
:: And whatever remains should be the app-path

:: Checking for a valid flag
rem TODO What about bad flags?
set flag=0
if "%~1"=="-l" set flag=l
if "%~1"=="--local-port" set flag=l
if "%~1"=="-g" set flag=g
if "%~1"=="--geoserver" set flag=g
if "%flag%"=="0" (
  :: Must be one arg remaining, otherwise fail
  rem TODO Edge case - Any valid flags after app-path are ignored
  rem but don't cause an error:
  rem Ex:  -p 9090 myapp -g http://geoserver 
  rem TODO The logic below seems exactly backwards, but works.
  if not "x%~1"=="%~1" (
    if not "x%~2"=="%~2" (
      goto DebugPath
    ) else (
      goto Usage
    )
  )
)

if "%flag%"=="l" (
  if "x%~2"=="x" goto Usage
  set ANT_ARGS=%ANT_ARGS% -Dapp.port=%2
  shift && shift
)

if "%flag%"=="g" (
  if "x%~2"=="x" goto Usage
  set ANT_ARGS=%ANT_ARGS% -Dapp.proxy.geoserver=%2
  shift && shift
)

:: Keep going until one arg left
goto DebugFlagLoop


:DebugPath
:: First argument is not a flag, so must assume that
:: it's the app-path.
set APP_PATH="%~1"
goto Run


:Deploy
if "x%~2"=="x" goto Usage
if "%~2"=="-h" goto UsageDeploy
if "%~2"=="--help" goto UsageDeploy
set COMMAND="%~1"
shift


:DeployFlagLoop
rem TODO: Same issues as in DebugFlagLoop

:: Checking for a valid flag
set flag=0
if "%~1"=="-d" set flag=d
if "%~1"=="--domain" set flag=d
if "%~1"=="-r" set flag=r
if "%~1"=="--remote-port" set flag=r
if "%~1"=="-u" set flag=u
if "%~1"=="--username" set flag=u
if "%~1"=="-p" set flag=p
if "%~1"=="--password" set flag=p
if "%~1"=="-c" set flag=c
if "%~1"=="--container" set flag=c
if "%flag%"=="0" (
  :: Must be one arg remaining, otherwise fail
  if not "x%~1"=="%~1" (
    if not "x%~2"=="%~2" (
      goto DeployPath
    ) else (
      goto Usage
    )
  )
)


:: TODO Figure out which of these are required!

if "%flag%"=="d" (
  if "x%~2"=="x" goto Usage
  set ANT_ARGS=%ANT_ARGS% -Dsuite.domain=%2
  shift && shift
)

if "%flag%"=="r" (
  if "x%~2"=="x" goto Usage
  set ANT_ARGS=%ANT_ARGS% -Dsuite.port=%2
  shift && shift
)

if "%flag%"=="u" (
  if "x%~2"=="x" goto Usage
  set ANT_ARGS=%ANT_ARGS% -Dsuite.username=%2
  shift && shift
)

if "%flag%"=="p" (
  if "x%~2"=="x" goto Usage
  set ANT_ARGS=%ANT_ARGS% -Dsuite.password=%2
  shift && shift
)

if "%flag%"=="c" (
  if "x%~2"=="x" goto Usage
  set ANT_ARGS=%ANT_ARGS% -Dsuite.container=%2
  shift && shift
)


:: Keep going until one arg left
goto DeployFlagLoop

:DeployPath
:: First argument is not a flag, so must assume that
:: it's the app-path.
set APP_PATH="%~1"
goto Run


:Usage
echo Usage: %NAME% ^<command^> ^<args^>
echo.
echo List of commands:
echo     create      Create a new application.
echo     debug       Run an existing application in debug mode.
echo     deploy      Deploy an application to a remote OpenGeo Suite instance.
echo.    
echo See '%NAME% ^<command^> --help' for more detail on a specific command.
echo.
exit /b

:UsageCreate
echo Usage: %NAME% create ^<app-path^>
echo.
echo Create a new application.  A new directory will be created using the ^<app-path^> 
echo argument (it must not already exist).
echo.
exit /b

:UsageDebug
echo Usage: %NAME% debug [^<options^>] ^<app-path^>
echo.
echo Debug an existing application.  The ^<app-path^> argument must be the path to an
echo existing application.
echo.
echo List of options:
echo.
echo     -l ^| --local-port   port    Port for the local debug server.  Default is 
echo                                 9080.
echo.
echo     -g ^| --geoserver    url     URL for a remote GeoServer to proxy.  The debug
echo                                 server will make the remote GeoServer available
echo                                 from the "/geoserver" path within the 
echo                                 application.
echo.
exit /b

:UsageDeploy
echo Usage: %NAME% deploy ^<options^> ^<app-path^>
echo.
echo Deploy an existing application.  The ^<app-path^> argument must be the path to an
echo existing application.
echo.
echo List of options:
echo.
echo     -d ^| --domain       name    Domain name for remote Suite container (for 
echo                                 example: yourdomain.com).  The domain name does
echo                                 not include the protocol (e.g. http), port, or
echo                                 any other part of the URL.  Domain must be 
echo                                 provided to deploy an app.
echo.
echo     -r ^| --remote-port  port    Port for the remote Suite container.  Default is
echo                                 8080.
echo.
echo     -u ^| --username     user    Username for manager of remote Suite container.
echo                                 Username must be provided to deploy an app.
echo.
echo     -p ^| --password     secret  Password for manager of remote Suite container.
echo                                 Password must be provided to deploy an app.
echo.
echo     -c ^| --container    type    Identifier for remote Suite container.  Default
echo                                 is "tomcat6x".  Possible values include 
echo                                 "jetty6x", "jboss7x", "weblogic9x".  See
echo                                 http://cargo.codehaus.org/ for details on
echo                                 supported containers.
echo.
exit /b


:Run
ant -e -f %SDK_HOME%\build.xml -Dsdk.home=%SDK_HOME% -Dbasedir=. %COMMAND% -Dapp.path=%APP_PATH% %ANT_ARGS%


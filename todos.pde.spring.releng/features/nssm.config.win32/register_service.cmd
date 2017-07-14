@ECHO OFF
GOTO CHECK_RIGHTS

:START
SET HOME=%~dp0
ECHO Installation directory is: "%HOME%"
ECHO.
SET /P CUSTOMER="Name of customer? "
IF NOT DEFINED CUSTOMER (
  SET CUSTOMER=ATRON
  ECHO Customer not set correctly.
)
SET PRODUCT_NAME=Todos
SET PRODUCT_EXE=Todos.exe
SET PRODUCT_DISPLAY_NAME=Todos
SET PRODUCT_DESCRIPTION=Todos Application

ECHO %CUSTOMER% will be used as customer.
"%HOME%nssm.exe" install "%CUSTOMER%%PRODUCT_NAME%" "%HOME%%PRODUCT_EXE%"
"%HOME%nssm.exe" set "%CUSTOMER%%PRODUCT_NAME%" DisplayName "%CUSTOMER% - %PRODUCT_DISPLAY_NAME%"
"%HOME%nssm.exe" set "%CUSTOMER%%PRODUCT_NAME%" Description "%CUSTOMER% - %PRODUCT_DESCRIPTION%"
ECHO @ECHO OFF > "%HOME%unregister_service.cmd"
ECHO GOTO CHECK_RIGHTS >> "%HOME%unregister_service.cmd"
ECHO. >> "%HOME%unregister_service.cmd"
ECHO :START >> "%HOME%unregister_service.cmd"
ECHO "%HOME%nssm.exe" remove "%CUSTOMER%%PRODUCT_NAME%" confirm >> "%HOME%unregister_service.cmd"
ECHO GOTO END >> "%HOME%unregister_service.cmd"
ECHO :CHECK_RIGHTS >> "%HOME%unregister_service.cmd"
ECHO   ECHO Administrative permissions required. Detecting permissions... >> "%HOME%unregister_service.cmd"
ECHO. >> "%HOME%unregister_service.cmd"
ECHO   NET SESSION ^>NUL 2^>^&1 >> "%HOME%unregister_service.cmd"
ECHO   IF %%ERRORLEVEL%% == 0 ^( >> "%HOME%unregister_service.cmd"
ECHO     ECHO Congratulations, you have admin rights. >> "%HOME%unregister_service.cmd"
ECHO     GOTO START >> "%HOME%unregister_service.cmd"
ECHO   ) ELSE ( >> "%HOME%unregister_service.cmd"
ECHO     ECHO Sorry, you need admin rights to do this. >> "%HOME%unregister_service.cmd"
ECHO     GOTO END >> "%HOME%unregister_service.cmd"
ECHO   ) >> "%HOME%unregister_service.cmd"
ECHO. >> "%HOME%unregister_service.cmd"
ECHO :END >> "%HOME%unregister_service.cmd"
ECHO PAUSE >> "%HOME%unregister_service.cmd"
GOTO END

:CHECK_RIGHTS
  ECHO Administrative permissions required. Detecting permissions...

  NET SESSION >NUL 2>&1
  IF %ERRORLEVEL% == 0 (
    ECHO Congratulations, you have admin rights.
    GOTO START
  ) ELSE (
    ECHO Sorry, you need admin rights to do this.
    GOTO END
  )

:END
PAUSE
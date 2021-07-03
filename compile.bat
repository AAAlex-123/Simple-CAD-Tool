dir /A-D /B /S src > .files

javac -d bin --release 8 @.files

del .files

pause

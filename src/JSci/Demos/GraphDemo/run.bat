echo off
cd classes.dir

dir GraphDemo.class

java -classpath .;..\;..\..\..\classes\xtra.jar;..\..\..\classes\core.jar;..\..\..\classes\sci.jar;..\..\..\classes\wavelet.jar;..\..\..\classes\JSciBeans.jar;.  -ms128m -mx256m GraphDemo

rem java -classpath .;..\;..\..\..\classes\xtra.jar;..\..\..\classes\core.jar;..\..\..\classes\sci.jar;..\..\..\classes\wavelet.jar;..\..\..\classes\JSciBeans.jar;.  -ms128m -mx256m ContourPlotDemo

cd ..
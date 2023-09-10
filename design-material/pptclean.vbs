Set argv = WScript.Arguments
if argv.Count < 1 then
WScript.Quit
end if
For Each argv In WScript.Arguments
filename = argv

Set pptApp = CreateObject("PowerPoint.Application")
Set MyPress = pptApp.Presentations.Open(filename)
MyPress.SaveCopyAs filename
pptApp.Quit
Next
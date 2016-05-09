#XLR Powershell Plugin#

##Introduction##

The XL Release Remote Script plugin allows *XL Release* to execute powershell commands on remote hosts. It does so by using the [Overthere](https://github.com/xebialabs/overthere) framework, a Java library for manipulating files and executing processes on remote hosts.

The **XLR Powershell Plugin** adds one more task to the *Remote Script group as follows:

* **Remote Script:Powershell:** Execute a powershell script on a Microsoft Windows host via WinRM


##Windows Powershell Properties##

| Property | Description |
| -------- | ----------- |
| Script | The shell script to execute on the remote host (required) |
| Remote Path | The path on the remote host where the script should be executed (required) |
| Temporary Directory Path | Where to store temporary files; this directory will be removed on connection close |
| Address and Port | Address Telnet or WinRM port of the remote host (required) |
| Username | The Unix host user log-in ID (required) |
| Password | The Unix host user password |
| Private key | The private key to use (verbatim) for authentication |
| Private key file and Passphrase | The file containing a private key, and an optional passphrase for the key it contains |
| CIFS Port | The port on which the CIFS (SMB) server runs |
| Windows path to Windows share mappings | Mapping from Windows paths to Windows share names; for example, `C:\IBM\WebSphere` &#8594; `WebSphereShare` |
| Timeout | The WinRM timeout in [XML schema duration format](http://www.w3.org/TR/xmlschema-2/#isoformats); the default value is `PT60.000S` |
| Enable HTTPS for WinRM | Check this if the remote Windows host supports encrypted connections |

## Using advanced Overthere functionality

XL Release uses the Overthere framework, which includes several connection options. While the most common options are available in the XL Release GUI, you can use other options as described in the [Overthere documentation](https://github.com/xebialabs/overthere/blob/master/README.md).

You can change the default values of options that are not exposed in the GUI in the `XLRELEASE_HOME/conf/deployit-defaults.properties` file.

If you need to use a different setting for a particular option per task, you can create a type modification in the `XLRELEASE_HOME/conf/synthetic.xml` file for the task types. In the modification, you can add the desired Overthere connection option as a property on the task. These task properties will automatically be used as connection options for Overthere if the name matches.

For example, to make the connection timeout configurable on each task, add this to `synthetic.xml`:

     <type-modification type="remoteScript.Powershell">
         <property name="scriptLocation" category="input" default="C:\Windows\SysWOW64\WindowsPowerShell\v1.0\powershell.exe"/>
     </type-modification>
     
     

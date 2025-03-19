/*
 THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
*/

package com.xebialabs.xlrelease.plugin.overthere;

import com.xebialabs.overthere.*;
import com.xebialabs.overthere.util.OverthereUtils;
import com.xebialabs.xlrelease.domain.PythonScript;

import static java.nio.charset.StandardCharsets.UTF_8;

public class PowershellScript extends RemoteScript {
    private static final String SCRIPT_NAME = "uploaded-script";

    private final String remotePath;
    private final String powerShellPath;
    private final String script;
    private final String errorActionPreference;
    private final String warningPreference;


    public PowershellScript(PythonScript remoteScript) {
        super(remoteScript);

        this.script = remoteScript.getProperty("script");
        this.remotePath = remoteScript.getProperty("remotePath");
        this.powerShellPath = remoteScript.getProperty("powerShellPath");
        this.errorActionPreference = remoteScript.getProperty("errorActionPreference");
        this.warningPreference = remoteScript.getProperty("warningPreference");
    }

    @Override
    public int doExecute(OverthereConnection connection, OutputHandler stdoutHandler, OutputHandler stderrHandler) {
        if (this.remotePath != null && !this.remotePath.isEmpty()) {
            connection.setWorkingDirectory(connection.getFile(this.remotePath));
        }

        OverthereFile targetFile = connection.getTempFile(SCRIPT_NAME, ".ps1");
        OverthereUtils.write(script.getBytes(UTF_8), targetFile);
        targetFile.setExecutable(true);

        CmdLine scriptCommand = CmdLine.build(this.powerShellPath, "-ExecutionPolicy", "Unrestricted", "-Inputformat", "None", "-NonInteractive", "-NoProfile", "-Command", "$ErrorActionPreference = '" + this.errorActionPreference + "'; $WarningPreference = '" + this.warningPreference + "';& " + targetFile.getPath() + "; if($LastExitCode) { Exit $LastExitCode; }");

        return connection.execute(stdoutHandler, stderrHandler, scriptCommand);
    }
}


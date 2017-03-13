/*
 THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY KIND, EITHER EXPRESSED OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS
 FOR A PARTICULAR PURPOSE. THIS CODE AND INFORMATION ARE NOT SUPPORTED BY XEBIALABS.
*/

package com.xebialabs.xlrelease.plugin.overthere;

import com.xebialabs.deployit.plugin.api.udm.ConfigurationItem;
import com.xebialabs.overthere.*;
import com.xebialabs.overthere.util.CapturingOverthereExecutionOutputHandler;
import com.xebialabs.overthere.util.OverthereUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

import static com.xebialabs.overthere.ConnectionOptions.*;
import static com.xebialabs.overthere.OperatingSystemFamily.UNIX;
import static com.xebialabs.overthere.util.CapturingOverthereExecutionOutputHandler.capturingHandler;
import static java.nio.charset.StandardCharsets.UTF_8;

public class PowershellScript extends RemoteScript {
    private static final String SCRIPT_NAME = "uploaded-script";

    private final ConnectionOptions options = new ConnectionOptions();
    private final String protocol;
    private final String remotePath;
    private final String powerShellPath;
    private final String script;
    private final String extension;
    private final String errorActionPreference;
    private final String warningPreference;

    private final CapturingOverthereExecutionOutputHandler stdout = capturingHandler();
    private final CapturingOverthereExecutionOutputHandler stderr = capturingHandler();

    public PowershellScript(ConfigurationItem remoteScript) {
        super( remoteScript );
        this.protocol = remoteScript.getProperty("protocol");
        copyPropertiesToConnectionOptions(options, remoteScript);

        this.script = remoteScript.getProperty("script");
        this.extension = options.get(OPERATING_SYSTEM, UNIX).getScriptExtension();
        this.remotePath = remoteScript.getProperty("remotePath");
        this.powerShellPath = remoteScript.getProperty("powerShellPath");
        this.errorActionPreference = remoteScript.getProperty("errorActionPreference");
        this.warningPreference = remoteScript.getProperty("warningPreference");
    }

    public int execute() {
        try (OverthereConnection connection = Overthere.getConnection(protocol, options)) {
            connection.setWorkingDirectory(connection.getFile(remotePath));

            // Upload and execute the script
            //OverthereFile targetFile = connection.getTempFile(SCRIPT_NAME, extension);
            OverthereFile targetFile = connection.getTempFile(SCRIPT_NAME, ".ps1");
            OverthereUtils.write(script.getBytes(UTF_8), targetFile);
            targetFile.setExecutable(true);

            CmdLine scriptCommand = CmdLine.build( this.powerShellPath, "-ExecutionPolicy", "Unrestricted", "-Inputformat", "None", "-NonInteractive", "-NoProfile", "-Command", "$ErrorActionPreference = '" + this.errorActionPreference + "'; $WarningPreference = '" + this.warningPreference + "';& " + targetFile.getPath() + "; if($LastExitCode) { Exit $LastExitCode; }" );

            return connection.execute(stdout, stderr, scriptCommand);
        } catch (Exception e) {
            StringWriter stacktrace = new StringWriter();
            PrintWriter writer = new PrintWriter(stacktrace, true);
            e.printStackTrace(writer);
            stderr.handleLine(stacktrace.toString());

            return 1;
        }

    }

    public String getStdout() {
        return stdout.getOutput();
    }

    public List<String> getStdoutLines() {
        return stdout.getOutputLines();
    }

    public String getStderr() {
        return stderr.getOutput();
    }

    public List<String> getStderrLines() {
        return stderr.getOutputLines();
    }

    private static Logger logger = LoggerFactory.getLogger(RemoteScript.class);

}


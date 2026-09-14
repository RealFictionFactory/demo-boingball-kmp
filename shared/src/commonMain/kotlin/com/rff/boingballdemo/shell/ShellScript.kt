package com.rff.boingballdemo.shell

import com.rff.boingballdemo.component.OSStyle

/** Token replaced with the current date when the command runs. */
const val SHELL_DATE_TOKEN = "%DATE%"

/** Token replaced with the current time when the command runs. */
const val SHELL_TIME_TOKEN = "%TIME%"

/**
 * A canned command with the output the shell "replies" with.
 *
 * @param promptAfter new prompt used from this command on (e.g. after `cd`).
 */
data class ShellCommand(
    val command: String,
    val output: List<String>,
    val promptAfter: String? = null,
)

fun promptFor(osStyle: OSStyle): String = when (osStyle) {
    OSStyle.AmigaOS13 -> "1.SYS:> "
    OSStyle.AmigaOS20 -> "1.Workbench3.0:> "
}

fun bannerFor(osStyle: OSStyle): List<String> = when (osStyle) {
    OSStyle.AmigaOS13 -> listOf("New CLI task 2", "")
    OSStyle.AmigaOS20 -> listOf("New Shell process 2", "")
}

fun scriptFor(osStyle: OSStyle): List<ShellCommand> = when (osStyle) {
    OSStyle.AmigaOS13 -> Os13Script
    OSStyle.AmigaOS20 -> Os30Script
}

private val Os13Script = listOf(
    ShellCommand(
        command = "dir SYS:",
        output = listOf(
            "c (dir)          devs (dir)",
            "l (dir)          libs (dir)",
            "s (dir)          fonts (dir)",
            "Utilities (dir)  System (dir)",
            "Empty (dir)      Trashcan (dir)",
            "Disk.info        Shell.info",
            "",
        ),
    ),
    ShellCommand(
        command = "version",
        output = listOf(
            "Kickstart version 34.5, Workbench version 34.20",
            "",
        ),
    ),
    ShellCommand(
        command = "date",
        output = listOf("$SHELL_DATE_TOKEN $SHELL_TIME_TOKEN", ""),
    ),
    ShellCommand(
        command = "avail",
        output = listOf(
            "Type  Available  In-Use  Maximum  Largest",
            "chip     359152  165944   525096   322016",
            "fast          0       0        0        0",
            "total    359152  165944   525096   322016",
            "",
        ),
    ),
    ShellCommand(
        command = "info df0:",
        output = listOf(
            "Unit     Size   Used   Free Full Errs   Status  Name",
            "DF0:     880K    791     89  90%    0   Read/Write  Workbench1.3",
            "",
        ),
    ),
    ShellCommand(
        command = "assign",
        output = listOf(
            "Volumes:",
            "  Workbench1.3 [Mounted]",
            "Directories:",
            "  S           Workbench1.3:s",
            "  L           Workbench1.3:l",
            "  C           Workbench1.3:c",
            "  FONTS       Workbench1.3:fonts",
            "  DEVS        Workbench1.3:devs",
            "  LIBS        Workbench1.3:libs",
            "  SYS         Workbench1.3:",
            "",
        ),
    ),
    ShellCommand(
        command = "status",
        output = listOf(
            "Process  1: Loaded as command: Workbench",
            "Process  2: Loaded as command: CLI",
            "",
        ),
    ),
    ShellCommand(
        command = "cd Utilities",
        output = emptyList(),
        promptAfter = "1.Utilities:> ",
    ),
    ShellCommand(
        command = "list",
        output = listOf(
            "Clock              12048 ----rwed $SHELL_DATE_TOKEN $SHELL_TIME_TOKEN",
            "Calculator          7256 ----rwed $SHELL_DATE_TOKEN $SHELL_TIME_TOKEN",
            "Notepad            18332 ----rwed $SHELL_DATE_TOKEN $SHELL_TIME_TOKEN",
            "More                9120 ----rwed $SHELL_DATE_TOKEN $SHELL_TIME_TOKEN",
            "4 files - 94 blocks used",
            "",
        ),
    ),
    ShellCommand(
        command = "cd SYS:",
        output = emptyList(),
        promptAfter = "1.SYS:> ",
    ),
    ShellCommand(
        command = "type s/startup-sequence",
        output = listOf(
            "echo \"Workbench Disk. Release 1.3\"",
            "SetPatch >NIL:",
            "AddBuffers df0: 15",
            "cd c:",
            "Path ram: c: sys:utilities sys:system s: add",
            "LoadWB delay",
            "endcli > nil:",
            "",
        ),
    ),
    ShellCommand(
        command = "echo \"Boing!\"",
        output = listOf("Boing!", ""),
    ),
    ShellCommand(
        command = "dir df1:",
        output = listOf(
            "Can't examine df1: - device not mounted",
            "",
        ),
    ),
    ShellCommand(
        command = "why",
        output = listOf(
            "Last command failed because volume df1: is not mounted",
            "",
        ),
    ),
)

private val Os30Script = listOf(
    ShellCommand(
        command = "dir SYS:",
        output = listOf(
            "C (dir)          Devs (dir)",
            "L (dir)          Libs (dir)",
            "S (dir)          Fonts (dir)",
            "Prefs (dir)      Storage (dir)",
            "Tools (dir)      WBStartup (dir)",
            "Disk.info        Shell.info",
            "",
        ),
    ),
    ShellCommand(
        command = "version",
        output = listOf(
            "Kickstart 40.68, Workbench 40.42",
            "",
        ),
    ),
    ShellCommand(
        command = "date",
        output = listOf("$SHELL_DATE_TOKEN $SHELL_TIME_TOKEN", ""),
    ),
    ShellCommand(
        command = "avail",
        output = listOf(
            "Type  Available  In-Use  Maximum  Largest",
            "chip    1572864  524288  2097152  1310720",
            "fast    6291456 2097152  8388608  4194304",
            "total   7864320 2621440 10485760  4194304",
            "",
        ),
    ),
    ShellCommand(
        command = "info DH0:",
        output = listOf(
            "Unit      Size   Used    Free Full Errs   Status  Name",
            "DH0:      100M  62381   37619  62%    0   Read/Write  Workbench",
            "",
        ),
    ),
    ShellCommand(
        command = "assign",
        output = listOf(
            "Volumes:",
            "  Workbench [Mounted]",
            "  RAM Disk [Mounted]",
            "Directories:",
            "  ENV         RAM Disk:env",
            "  CLIPS       RAM Disk:clipboards",
            "  T           RAM Disk:t",
            "  SYS         Workbench:",
            "  REXX        Workbench:rexx",
            "",
        ),
    ),
    ShellCommand(
        command = "status",
        output = listOf(
            "Process  1: Loaded as command: Workbench",
            "Process  2: Loaded as command: Shell",
            "Process  3: Loaded as command: AmigaGuide",
            "",
        ),
    ),
    ShellCommand(
        command = "cd SYS:Prefs",
        output = emptyList(),
        promptAfter = "1.Prefs:> ",
    ),
    ShellCommand(
        command = "list",
        output = listOf(
            "Env-Archive (dir)  ----rwed $SHELL_DATE_TOKEN $SHELL_TIME_TOKEN",
            "Printer            30124 ----rwed $SHELL_DATE_TOKEN $SHELL_TIME_TOKEN",
            "ScreenMode         22880 ----rwed $SHELL_DATE_TOKEN $SHELL_TIME_TOKEN",
            "WBPattern          25612 ----rwed $SHELL_DATE_TOKEN $SHELL_TIME_TOKEN",
            "3 files - 1 directory - 156 blocks used",
            "",
        ),
    ),
    ShellCommand(
        command = "cd SYS:",
        output = emptyList(),
        promptAfter = "1.Workbench3.0:> ",
    ),
    ShellCommand(
        command = "type S:Startup-Sequence",
        output = listOf(
            "C:SetPatch QUIET",
            "C:Version >NIL:",
            "C:AddBuffers >NIL: DF0: 15",
            "C:MakeDir RAM:T RAM:Clipboards RAM:ENV",
            "C:Assign T: RAM:T",
            "C:IPrefs",
            "C:LoadWB",
            "EndCLI >NIL:",
            "",
        ),
    ),
    ShellCommand(
        command = "echo \"Boing!\"",
        output = listOf("Boing!", ""),
    ),
    ShellCommand(
        command = "list RAM:",
        output = listOf(
            "ENV (dir)          ----rwed $SHELL_DATE_TOKEN $SHELL_TIME_TOKEN",
            "Clipboards (dir)   ----rwed $SHELL_DATE_TOKEN $SHELL_TIME_TOKEN",
            "T (dir)            ----rwed $SHELL_DATE_TOKEN $SHELL_TIME_TOKEN",
            "0 files - 3 directories - 6 blocks used",
            "",
        ),
    ),
    ShellCommand(
        command = "why",
        output = listOf(
            "Last command did not set a return code",
            "",
        ),
    ),
)

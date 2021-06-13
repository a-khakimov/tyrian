package tyrian.runtime

import tyrian.Cmd
import tyrian.Task

object CmdRunner:
  def runCmd[Msg](
    cmd: Cmd[Msg],
    callback: Msg => Unit,
    async: (=> Unit) => Unit
    ): Unit =
    val allCmds = {
      def loop(cmd: Cmd[Msg]): Unit =
        cmd match
          case Cmd.Empty =>
            ()

          case Cmd.RunTask(task, f) =>
            async(TaskRunner.execTask(task, f andThen callback))

          case Cmd.Combine(cmd1, cmd2) =>
            loop(cmd1)
            loop(cmd2)

          case Cmd.Batch(cmds) =>
            cmds.foreach(loop)

      loop(cmd)
    }
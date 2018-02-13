class DataPowerConfigurationManager {

  // TODO upload dir
  // TODO import object
  // TODO delete object
  // TODO check object status
  def importObject() {
    def scriptPath = new File('datapower-configuration-manager')
    def command = """
      ant -f deploy.ant.xml -Ddcm.dir=. \
        -Dhost=localhost -Ddomain=default \
        -Duid=admin -Dpwd=admin \
        -Dimport.file=../build/out/services/HelloWorld.zip
    """
    println command
    def sout = new StringBuilder(), serr = new StringBuilder()
    def proc = command.execute([], scriptPath)
    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(1000000000)
    println "out> $sout"
    println "err> $serr"
  }

  def importObject(String host, String domain, String user, String pwd, String file, String pathFile) {
    def scriptPath = new File('datapower-configuration-manager')
    println("host: $host, domain: $domain, user: $user, pwd: $pwd, pathFile: $pathFile, file: $file")
    def command = """
      ant -f deploy.ant.xml -Ddcm.dir=. \
        -Dhost=$host -Ddomain=$domain \
        -Duid=$user -Dpwd=$pwd \
        -Dimport.file=../$pathFile/$file
    """
    println command
    def sout = new StringBuilder(), serr = new StringBuilder()
    def proc = command.execute([], scriptPath)
    proc.consumeProcessOutput(sout, serr)
    proc.waitForOrKill(1000000000)
    println "out> $sout"
    println "err> $serr"
  }

}

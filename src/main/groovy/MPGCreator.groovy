import com.github.plecong.hogan.Hogan
import org.apache.commons.io.FileUtils
import com.github.plecong.hogan.HoganTemplate

/**
 * Creates DataPower Multi Protocol Gateways from DB stored procedures metadata.
 */
class MPGCreator {

  private static final String MPG_TEMPLATE_PATH = 'src/main/resources/templates/mpg'
  private static final String OUTPUT_PATH = 'build/out/services'

  //TODO change MPG name in the export.xml file

  void createMPGs(List procedures) {
    HoganTemplate template = Hogan.compile(new File("${MPG_TEMPLATE_PATH}/local/template.xslt").text)
    HoganTemplate templateExport = Hogan.compile(new File("${MPG_TEMPLATE_PATH}/export.xml").text)
    procedures.each { Map procedure -> createMPG(procedure, template, templateExport) }
  }

  void createMPG(Map procedure, HoganTemplate template, HoganTemplate templateExport) {
    File mpgPath = new File("$OUTPUT_PATH/${procedure.name}")
    FileUtils.copyDirectory(new File(MPG_TEMPLATE_PATH), mpgPath)
    new File("$mpgPath/local/template.xslt").newWriter('UTF-8').withWriter {
      it.write(template.render(procedure))
    }
    new File("$mpgPath/export.xml").newWriter('UTF-8').withWriter {
      it.write(templateExport.render(procedure))
    }
    zip(mpgPath, procedure.name)
  }

  void zip(File path, String zipFileName) {
    new AntBuilder().zip(basedir: path, destfile: "$OUTPUT_PATH/${zipFileName}.zip")
  }

}
